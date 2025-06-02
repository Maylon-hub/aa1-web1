package com.gametester.controller.admin;

import com.gametester.dao.ProjetoDAO;
import com.gametester.model.Projeto;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
// A data de criação será gerada pelo sistema (no DAO ou aqui)
// import java.sql.Timestamp;

@WebServlet("/admin/cadastrarProjeto")
public class CadastrarProjetoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
    }

    // Exibe o formulário de cadastro de projeto
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem acessar
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        // Limpa atributos para um novo formulário
        request.removeAttribute("valorNomeProjeto");
        request.removeAttribute("valorDescricaoProjeto");
        request.removeAttribute("mensagemErroProjeto");
        request.removeAttribute("mensagemSucessoProjeto");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-projeto.jsp").forward(request, response);
    }

    // Processa o envio do formulário de cadastro de projeto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Proteção: Somente administradores podem cadastrar
        if (usuarioLogado == null || !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nomeProjeto");
        String descricao = request.getParameter("descricaoProjeto");
        // Membros permitidos será uma funcionalidade futura ou simplificada inicialmente

        // Validação simples
        if (nome == null || nome.trim().isEmpty()) {
            request.setAttribute("mensagemErroProjeto", "O nome do projeto é obrigatório.");
            request.setAttribute("valorNomeProjeto", nome); // Repopula o que foi digitado
            request.setAttribute("valorDescricaoProjeto", descricao);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-projeto.jsp").forward(request, response);
            return;
        }

        Projeto novoProjeto = new Projeto();
        novoProjeto.setNome(nome.trim());
        novoProjeto.setDescricao(descricao != null ? descricao.trim() : null);
        // A data de criação é definida no DAO ao inserir (ou pelo BD com DEFAULT)
        // A gestão de membros não será tratada neste momento inicial.

        try {
            projetoDAO.inserirProjeto(novoProjeto); // O DAO preencherá o ID e dataCriacao no objeto novoProjeto
            request.setAttribute("mensagemSucessoProjeto", "Projeto '" + novoProjeto.getNome() + "' cadastrado com sucesso! ID: " + novoProjeto.getId());

            // Limpa os campos para um novo cadastro
            request.removeAttribute("valorNomeProjeto");
            request.removeAttribute("valorDescricaoProjeto");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroProjeto", "Erro ao salvar projeto no banco de dados: " + e.getMessage());
            // Repopula os campos em caso de erro no banco
            request.setAttribute("valorNomeProjeto", nome);
            request.setAttribute("valorDescricaoProjeto", descricao);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-projeto.jsp").forward(request, response);
    }
}