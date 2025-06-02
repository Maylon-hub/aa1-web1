package com.gametester.controller.testador;

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

@WebServlet("/testador/meusProjetos")
public class MeusProjetosServlet extends HttpServlet {
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
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito.", "UTF-8"));
            return;
        }

        // Captura parâmetros de ordenação da requisição
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");

        // Define padrões se os parâmetros não forem fornecidos ou forem inválidos
        // Os nomes dos campos aqui devem corresponder aos casos no switch do DAO (sem o alias 'p.')
        if (sortField == null || !List.of("id", "nome", "data_criacao").contains(sortField.toLowerCase())) {
            sortField = "nome"; // Campo padrão de ordenação
        }
        if (sortOrder == null || !List.of("asc", "desc").contains(sortOrder.toLowerCase())) {
            sortOrder = "asc"; // Ordem padrão
        }

        List<Projeto> listaMeusProjetos = new ArrayList<>();
        try {
            // Chama o método do DAO com os parâmetros de ordenação
            listaMeusProjetos = projetoDAO.listarProjetosPorMembro(usuarioLogado.getId(), sortField, sortOrder);
            request.setAttribute("listaMeusProjetos", listaMeusProjetos);

            // Passa os parâmetros de ordenação atuais para o JSP
            request.setAttribute("currentSortField", sortField);
            request.setAttribute("currentSortOrder", sortOrder);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroMeusProjetos", "Erro ao carregar seus projetos: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/testador/meus-projetos.jsp").forward(request, response);
    }
}