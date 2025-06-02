package com.gametester.controller.admin;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/cadastrarEstrategia") // R5: Mapeamento do servlet
public class CadastrarEstrategiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    // Exibe o formulário de cadastro
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Verifica se o usuário é administrador
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            // Se não for admin, redireciona para login com mensagem de erro
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }
        // Encaminha para o JSP do formulário de cadastro de estratégia
        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
    }

    // Processa o envio do formulário
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

        request.setCharacterEncoding("UTF-8"); // Define o encoding para tratar caracteres especiais

        // Obtém os parâmetros do formulário
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String exemplosOriginal = request.getParameter("exemplos"); // Conteúdo original do campo exemplos
        String dicas = request.getParameter("dicas");
        String imagemPath = request.getParameter("imagemPath"); // Caminho da imagem vindo do formulário

        // Validação simples (campos obrigatórios: nome e descrição)
        if (nome == null || nome.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Nome e Descrição são campos obrigatórios.");
            // Repopula os campos para o usuário não perder o que digitou
            request.setAttribute("param", request.getParameterMap()); // Envia todos os parâmetros de volta
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
            return;
        }

        Estrategia novaEstrategia = new Estrategia();
        novaEstrategia.setNome(nome.trim());
        novaEstrategia.setDescricao(descricao.trim());

        String exemplosFinais = exemplosOriginal != null ? exemplosOriginal.trim() : "";

        if (imagemPath != null && !imagemPath.trim().isEmpty()) {
            if (!exemplosFinais.isEmpty()) {
                exemplosFinais += "\n"; // Adiciona uma nova linha se já houver exemplos
            }
            exemplosFinais += "[Imagem: " + imagemPath.trim() + "]";

        }

        novaEstrategia.setExemplos(exemplosFinais.isEmpty() ? null : exemplosFinais);
        novaEstrategia.setDicas(dicas != null ? dicas.trim() : null);


        try {

            estrategiaDAO.inserirEstrategia(novaEstrategia);


            request.setAttribute("mensagemSucesso", "Estratégia '" + novaEstrategia.getNome() + "' cadastrada com sucesso! ID: " + novaEstrategia.getId());

            request.removeAttribute("param"); // Garante que dados antigos não sejam repopulados em caso de sucesso
            request.removeAttribute("mensagemErro");

            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace(); // Importante para debug no console do servidor
            // Em caso de erro no banco, define mensagem de erro e repopula o formulário
            request.setAttribute("mensagemErro", "Erro ao salvar estratégia no banco de dados: " + e.getMessage());
            request.setAttribute("param", request.getParameterMap()); // Repopula
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
        }
    }
}