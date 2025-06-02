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
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");

        // Define padrões se os parâmetros não forem fornecidos ou forem inválidos
        if (sortField == null || !List.of("id", "nome", "data_criacao").contains(sortField.toLowerCase())) {
            sortField = "nome";
        }
        if (sortOrder == null || !List.of("asc", "desc").contains(sortOrder.toLowerCase())) {
            sortOrder = "asc";
        }

        List<Projeto> listaDeProjetos = new ArrayList<>();
        try {
            listaDeProjetos = projetoDAO.listarTodosProjetos(sortField, sortOrder);
            request.setAttribute("listaProjetos", listaDeProjetos);

            request.setAttribute("currentSortField", sortField);
            request.setAttribute("currentSortOrder", sortOrder);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErro", "Erro ao carregar a lista de projetos: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin/gerenciar-projetos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Ações de POST não são esperadas aqui, apenas recarrega a lista.
    }
}