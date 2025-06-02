package com.gametester.controller.admin;

import com.gametester.dao.SessaoTesteDAO;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/excluirSessao")
public class ExcluirSessaoAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessaoTesteDAO sessaoTesteDAO;

    @Override
    public void init() {
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem excluir
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        int sessaoId = 0;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                sessaoId = Integer.parseInt(idParam);
                // O método excluirSessaoTeste no DAO já verifica se a sessão tem bugs associados (FK)
                // e lança uma SQLException com mensagem específica se não puder excluir.
                boolean sucesso = sessaoTesteDAO.excluirSessaoTeste(sessaoId);

                if (sucesso) {
                    session.setAttribute("mensagemSucessoSessoesAdmin", "Sessão de teste com ID " + sessaoId + " excluída com sucesso!");
                } else {
                    // Esta condição é menos provável se o ID existir e não houver exceção,
                    // mas pode ocorrer se o ID não for encontrado para exclusão.
                    session.setAttribute("mensagemErroSessoesAdmin", "Não foi possível excluir a sessão de teste com ID " + sessaoId + ". Sessão não encontrada.");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessoesAdmin", "ID da sessão inválido para exclusão.");
            } catch (SQLException e) {
                e.printStackTrace();
                // A mensagem de e.getMessage() já deve ser amigável vinda do DAO em caso de FK violation
                session.setAttribute("mensagemErroSessoesAdmin", "Erro ao excluir sessão de teste: " + e.getMessage());
            }
        } else {
            session.setAttribute("mensagemErroSessoesAdmin", "ID da sessão não fornecido para exclusão.");
        }

        // Redireciona de volta para a página de visualização de sessões do admin
        response.sendRedirect(request.getContextPath() + "/admin/sessoes");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Ação de exclusão via link GET é comum, mas POST seria mais robusto para operações destrutivas.
        // Por simplicidade, mantemos o doGet.
        doGet(request, response);
    }
}