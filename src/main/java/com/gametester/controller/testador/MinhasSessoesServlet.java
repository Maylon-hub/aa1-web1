package com.gametester.controller.testador;

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

@WebServlet("/testador/minhasSessoes")
public class MinhasSessoesServlet extends HttpServlet {
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
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a testadores ou administradores.", "UTF-8"));
            return;
        }

        List<SessaoTeste> minhasSessoes = new ArrayList<>();
        try {
            // O método listarSessoesPorTestador já faz JOIN com Projeto e Estrategia para pegar nomes
            minhasSessoes = sessaoTesteDAO.listarSessoesPorTestador(usuarioLogado.getId());
            request.setAttribute("listaMinhasSessoes", minhasSessoes);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroMinhasSessoes", "Erro ao carregar suas sessões de teste: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/testador/minhas-sessoes.jsp").forward(request, response);
    }
}