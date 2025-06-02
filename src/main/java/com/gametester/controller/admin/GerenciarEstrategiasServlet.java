package com.gametester.controller.admin;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;
import com.gametester.model.Usuario; // Para verificar o perfil do usuário

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/gerenciarEstrategias")
public class GerenciarEstrategiasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
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

        try {
            List<Estrategia> listaDeEstrategias = estrategiaDAO.listarTodasEstrategias();
            request.setAttribute("listaEstrategias", listaDeEstrategias);
        } catch (SQLException e) {
            e.printStackTrace(); // Registra o erro no log do servidor
            request.setAttribute("mensagemErro", "Erro ao carregar a lista de estratégias do banco de dados: " + e.getMessage());
        }

        // Encaminha para a página JSP que exibirá a interface de gerenciamento de estratégias
        request.getRequestDispatcher("gerenciarEstrategias.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Se houver ações de POST nesta página (ex: um filtro para a lista), trate aqui.
        // Por enquanto, apenas redireciona para o doGet.
        doGet(request, response);
    }
}