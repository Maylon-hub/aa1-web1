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

@WebServlet("/estrategias") // Mantém o mapeamento original
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

        String estrategiaIdParam = request.getParameter("id");

        try {
            if (estrategiaIdParam != null && !estrategiaIdParam.isEmpty()) {
                // Se um ID foi fornecido, busca e exibe detalhes dessa estratégia
                int estrategiaId = Integer.parseInt(estrategiaIdParam);
                Estrategia estrategia = estrategiaDAO.buscarEstrategiaPorId(estrategiaId); // Método já existente no DAO

                if (estrategia != null) {
                    request.setAttribute("estrategiaDetalhes", estrategia);
                    request.getRequestDispatcher("/WEB-INF/jsp/estrategias/detalhes-estrategia.jsp").forward(request, response);
                } else {
                    request.setAttribute("mensagemErro", "Estratégia com ID " + estrategiaId + " não encontrada.");
                    // Encaminha para a lista geral se o ID não for encontrado ou for inválido
                    listarTodas(request, response);
                }
            } else {
                // Se nenhum ID foi fornecido, lista todas as estratégias (comportamento original do R6)
                listarTodas(request, response);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErro", "ID da estratégia inválido.");
            listarTodas(request, response); // Mostra a lista geral em caso de ID inválido
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErro", "Erro ao buscar estratégias: " + e.getMessage());
            // Tenta exibir o JSP de listagem mesmo com erro, para mostrar a mensagem
            request.getRequestDispatcher("/WEB-INF/jsp/estrategias/listar-estrategias.jsp").forward(request, response);
        }
    }

    // Método auxiliar para não repetir a lógica de listar todas
    private void listarTodas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Estrategia> listaEstrategias = estrategiaDAO.listarTodasEstrategias();
            request.setAttribute("listaEstrategias", listaEstrategias);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErro", (request.getAttribute("mensagemErro") != null ? request.getAttribute("mensagemErro") + "<br/>" : "") + "Erro ao carregar a lista de estratégias: " + e.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/jsp/estrategias/listar-estrategias.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}