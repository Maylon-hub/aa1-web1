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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/gerenciarProjetos")
public class GerenciarProjetosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
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

        List<Projeto> listaDeProjetos = new ArrayList<>(); // Inicializa com lista vazia
        try {
            listaDeProjetos = projetoDAO.listarTodosProjetos();
            request.setAttribute("listaProjetos", listaDeProjetos);
        } catch (SQLException e) {
            e.printStackTrace(); // Registra o erro no log do servidor
            request.setAttribute("mensagemErro", "Erro ao carregar a lista de projetos do banco de dados: " + e.getMessage());
        }

        // Encaminha para a página JSP que exibirá a interface de gerenciamento de projetos
        request.getRequestDispatcher("/WEB-INF/jsp/admin/gerenciar-projetos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Se houver ações de POST nesta página (ex: um filtro para a lista), trate aqui.
        // Por enquanto, apenas redireciona para o doGet.
        doGet(request, response);
    }
}