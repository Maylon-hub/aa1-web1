package com.gametester.controller.admin;

import com.gametester.dao.SessaoTesteDAO;
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

@WebServlet("/admin/sessoes") // Ou /admin/visualizarSessoes, conforme o link no dashboard
public class VisualizarSessoesAdminServlet extends HttpServlet {
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
        // Proteção: Somente administradores podem acessar
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        List<SessaoTeste> listaDeSessoes = new ArrayList<>();
        try {
            // O método listarTodasSessoes() no DAO já busca informações de projeto, testador e estratégia
            listaDeSessoes = sessaoTesteDAO.listarTodasSessoes();
            request.setAttribute("listaSessoes", listaDeSessoes);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroSessoes", "Erro ao carregar a lista de sessões de teste: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin/visualizar-sessoes.jsp").forward(request, response);
    }
}