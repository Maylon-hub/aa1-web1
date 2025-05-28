package com.gametester.controller;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/estrategias") // Mapeia este servlet para o URL /estrategias
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

        // O Requisito R6: "Listagem de todas as estratégias (não requer login)" [cite: 2]
        // significa que não precisamos verificar a sessão de um usuário específico aqui.

        try {
            List<Estrategia> listaEstrategias = estrategiaDAO.listarTodasEstrategias();
            request.setAttribute("listaEstrategias", listaEstrategias); // Passa a lista para o JSP
        } catch (SQLException e) {
            e.printStackTrace(); // Registra o erro no log do servidor
            // Você pode definir uma mensagem de erro mais amigável para o usuário aqui, se desejar
            request.setAttribute("mensagemErro", "Erro ao buscar estratégias do banco de dados: " + e.getMessage());
        }

        // Encaminha para a página JSP que exibirá as estratégias
        // Certifique-se de que este caminho para o JSP está correto e o arquivo JSP existe.
        request.getRequestDispatcher("/WEB-INF/jsp/estrategias/listar-estrategias.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Normalmente, a listagem é feita via GET.
        // Se houver necessidade de POST (ex: para aplicar filtros enviados por um formulário),
        // você implementaria essa lógica aqui. Por enquanto, apenas redireciona para o doGet.
        doGet(request, response);
    }
}