package com.gametester.controller; // Ou com.gametester.controller.publico

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/estrategias-publicas") // Este caminho deve estar em CAMINHOS_PUBLICOS_VISITANTE no filtro
public class PublicEstrategiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Estrategia> listaEstrategias = estrategiaDAO.listarTodasEstrategias();
            request.setAttribute("listaEstrategias", listaEstrategias);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/listaEstrategias.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            // Tratar exceção, talvez redirecionar para uma página de erro genérica
            e.printStackTrace(); // Logar o erro
            request.setAttribute("mensagemErro", "Erro ao carregar estratégias.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/paginaErroPublica.jsp"); // Crie esta página
            dispatcher.forward(request, response);
        }
    }
}