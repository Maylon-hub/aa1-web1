package br.ufscar.game_tester.controller;

import br.ufscar.game_tester.dao.EstrategiaDAO;
import br.ufscar.game_tester.model.Estrategia;

// Imports atualizados para jakarta
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/estrategias")
public class ListarEstrategiasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Estrategia> listaEstrategias = estrategiaDAO.getAll();
            request.setAttribute("listaEstrategias", listaEstrategias);
            request.getRequestDispatcher("/WEB-INF/jsp/listar-estrategias.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Erro ao acessar o banco de dados para listar estratégias", e);
        }
    }
}