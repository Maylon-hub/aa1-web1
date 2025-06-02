package com.gametester.controller.testador;

import com.gametester.dao.BugDAO;
import com.gametester.dao.SessaoTesteDAO;
import com.gametester.model.Bug;
import com.gametester.model.SessaoTeste;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testador/visualizarBugsSessao")
public class VisualizarBugsSessaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BugDAO bugDAO;
    private SessaoTesteDAO sessaoTesteDAO;

    @Override
    public void init() {
        bugDAO = new BugDAO();
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito.", "UTF-8"));
            return;
        }

        String sessaoIdParam = request.getParameter("sessaoId");
        if (sessaoIdParam == null || sessaoIdParam.isEmpty()) {
            session.setAttribute("mensagemErroSessaoOperacao", "ID da Sessão de Teste não fornecido para visualizar bugs.");
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
            return;
        }

        try {
            int sessaoId = Integer.parseInt(sessaoIdParam);
            SessaoTeste sessaoAtual = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);

            if (sessaoAtual == null) {
                session.setAttribute("mensagemErroSessaoOperacao", "Sessão de Teste (ID: " + sessaoId + ") não encontrada.");
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }
            // Autorização: Testador só vê bugs de suas próprias sessões (Admin pode ver todas)
            if (sessaoAtual.getTestadorId() != usuarioLogado.getId() && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
                session.setAttribute("mensagemErroSessaoOperacao", "Você não tem permissão para visualizar bugs desta sessão.");
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }

            List<Bug> listaBugs = bugDAO.listarBugsPorSessaoTeste(sessaoId);

            request.setAttribute("sessaoAtual", sessaoAtual); // Passa o objeto SessaoTeste completo
            request.setAttribute("listaBugsDaSessao", listaBugs);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/visualizar-bugs-sessao.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroSessaoOperacao", "ID da Sessão de Teste inválido.");
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroSessaoOperacao", "Erro ao carregar bugs da sessão: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
        }
    }
}