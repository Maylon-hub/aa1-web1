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
import java.sql.Timestamp; // Necessário se for lidar com data de criação na atualização

@WebServlet("/admin/editarProjeto")
public class EditarProjetoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
    }

    // doGet: Carrega os dados do projeto para o formulário de edição
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem editar
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int projetoId = Integer.parseInt(idParam);
                Projeto projetoParaEditar = projetoDAO.buscarProjetoPorId(projetoId); // Método já existente no DAO

                if (projetoParaEditar != null) {
                    request.setAttribute("projeto", projetoParaEditar); // Passa o objeto para o JSP
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-projeto.jsp").forward(request, response);
                } else {
                    session.setAttribute("mensagemErroGerenciamentoProjetos", "Projeto com ID " + projetoId + " não encontrado.");
                    response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto inválido para edição.");
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoProjetos", "Erro ao buscar projeto para edição: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            }
        } else {
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto não fornecido para edição.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
        }
    }

    // doPost: Processa os dados do formulário de edição e atualiza o projeto
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem salvar edições
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idProjeto"); // Nome do campo oculto no formulário
        String nome = request.getParameter("nomeProjeto");
        String descricao = request.getParameter("descricaoProjeto");
        // A data de criação não é editável pelo usuário.
        // A gestão de membros não será tratada nesta atualização básica.

        int projetoId = 0;

        // Validação do ID
        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto não fornecido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            return;
        }

        try {
            projetoId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto inválido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            return;
        }

        // Validação dos campos obrigatórios
        if (nome == null || nome.trim().isEmpty()) {
            request.setAttribute("mensagemErroFormProjeto", "O nome do projeto é obrigatório.");

            // Precisamos recriar o objeto Projeto com os dados submetidos (e o ID) para repopular
            // e também a data de criação original, pois ela não vem do formulário de edição
            Projeto projetoComErro = null;
            try {
                projetoComErro = projetoDAO.buscarProjetoPorId(projetoId); // Busca o original para pegar a data de criação
                if (projetoComErro != null) {
                    projetoComErro.setNome(nome); // Atualiza com o nome (potencialmente inválido) para repopular
                    projetoComErro.setDescricao(descricao);
                }
            } catch (SQLException e_fetch) {
                e_fetch.printStackTrace();
                // Se falhar ao buscar, pelo menos repopula com o que tem
                projetoComErro = new Projeto(projetoId, nome, descricao, null);
            }
            request.setAttribute("projeto", projetoComErro != null ? projetoComErro : new Projeto(projetoId, nome, descricao, null));

            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-projeto.jsp").forward(request, response);
            return;
        }

        Projeto projetoAtualizado = new Projeto();
        projetoAtualizado.setId(projetoId);
        projetoAtualizado.setNome(nome.trim());
        projetoAtualizado.setDescricao(descricao != null ? descricao.trim() : null);
        // A data de criação não é modificada aqui; o método atualizarProjeto no DAO não deve alterá-la.

        try {
            boolean sucesso = projetoDAO.atualizarProjeto(projetoAtualizado); // Método já existente no DAO
            if (sucesso) {
                session.setAttribute("mensagemSucessoGerenciamentoProjetos", "Projeto '" + projetoAtualizado.getNome() + "' (ID: " + projetoId + ") atualizado com sucesso!");
            } else {
                session.setAttribute("mensagemErroGerenciamentoProjetos", "Não foi possível atualizar o projeto com ID " + projetoId + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroGerenciamentoProjetos", "Erro ao atualizar projeto no banco de dados: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
    }
}