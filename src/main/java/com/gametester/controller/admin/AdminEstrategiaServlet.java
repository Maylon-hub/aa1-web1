package com.gametester.controller.admin; // Pacote para controllers de admin

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/estrategias") // URL base para as ações de estratégia
public class AdminEstrategiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Para tratar acentos corretamente
        String action = request.getParameter("action");
        if (action == null) {
            action = "listar"; // Ação padrão
        }

        try {
            switch (action) {
                case "salvar": // Salvar nova estratégia ou atualizar existente
                    salvarEstrategia(request, response);
                    break;
                default:
                    listarEstrategias(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "listar"; // Ação padrão
        }

        try {
            switch (action) {
                case "novo":
                    mostrarFormularioNovaEstrategia(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditarEstrategia(request, response);
                    break;
                case "excluir":
                    excluirEstrategia(request, response);
                    break;
                case "listar":
                default:
                    listarEstrategias(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listarEstrategias(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Estrategia> listaEstrategias = estrategiaDAO.listarTodasEstrategias();
        request.setAttribute("listaEstrategias", listaEstrategias);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/estrategia/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioNovaEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("estrategia", new Estrategia()); // Objeto vazio para o formulário
        request.setAttribute("action", "salvar");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioEditarEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Estrategia estrategiaExistente = estrategiaDAO.buscarEstrategiaPorId(id);
        request.setAttribute("estrategia", estrategiaExistente);
        request.setAttribute("action", "salvar"); // Reutiliza o mesmo método POST para salvar/atualizar
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void salvarEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {
        String idParam = request.getParameter("id");
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String exemplos = request.getParameter("exemplos");
        String dicas = request.getParameter("dicas");

        Estrategia estrategia = new Estrategia();
        estrategia.setNome(nome);
        estrategia.setDescricao(descricao);
        estrategia.setExemplos(exemplos);
        estrategia.setDicas(dicas);

        if (idParam == null || idParam.isEmpty() || "0".equals(idParam)) { // Nova Estratégia (ID 0 ou nulo)
            estrategiaDAO.inserirEstrategia(estrategia);
            request.getSession().setAttribute("mensagemSucesso", "Estratégia cadastrada com sucesso!");
        } else { // Atualizando Estratégia Existente
            estrategia.setId(Integer.parseInt(idParam));
            estrategiaDAO.atualizarEstrategia(estrategia);
            request.getSession().setAttribute("mensagemSucesso", "Estratégia atualizada com sucesso!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/estrategias?action=listar");
    }

    private void excluirEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean excluiu = estrategiaDAO.excluirEstrategia(id);
        if (excluiu) {
            request.getSession().setAttribute("mensagemSucesso", "Estratégia excluída com sucesso!");
        } else {
            // Adicionar tratamento para falha na exclusão (ex: estratégia em uso)
            request.getSession().setAttribute("mensagemErro", "Falha ao excluir estratégia. Pode estar em uso.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/estrategias?action=listar");
    }
}