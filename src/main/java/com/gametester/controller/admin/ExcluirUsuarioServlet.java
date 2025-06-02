package com.gametester.controller.admin;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/excluirUsuario")
public class ExcluirUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario adminLogado = null;

        // Proteção: Somente administradores podem excluir
        if (session != null && session.getAttribute("usuarioLogado") != null &&
                "ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            adminLogado = (Usuario) session.getAttribute("usuarioLogado");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        int usuarioIdParaExcluir = 0;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                usuarioIdParaExcluir = Integer.parseInt(idParam);

                // Impedir que o administrador se autoexclua
                if (adminLogado.getId() == usuarioIdParaExcluir) {
                    session.setAttribute("mensagemErroGerenciamentoUsuarios", "Não é permitido excluir a si mesmo.");
                    response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
                    return;
                }

                // TODO: Adicionar lógica para impedir a exclusão do último administrador (mais complexo)

                boolean sucesso = usuarioDAO.excluirUsuario(usuarioIdParaExcluir);

                if (sucesso) {
                    session.setAttribute("mensagemSucessoGerenciamentoUsuarios", "Usuário com ID " + usuarioIdParaExcluir + " excluído com sucesso!");
                } else {
                    session.setAttribute("mensagemErroGerenciamentoUsuarios", "Não foi possível excluir o usuário com ID " + usuarioIdParaExcluir + ". O usuário pode não existir.");
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário inválido para exclusão.");
            } catch (SQLException e) {
                e.printStackTrace();
                // O DAO já tenta identificar se é erro de FK
                session.setAttribute("mensagemErroGerenciamentoUsuarios", e.getMessage());
            }
        } else {
            session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário não fornecido para exclusão.");
        }

        // Redireciona de volta para a página de gerenciamento de usuários
        response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // A exclusão via link simples geralmente usa GET.
        doGet(request, response);
    }
}