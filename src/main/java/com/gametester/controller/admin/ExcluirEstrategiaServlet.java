package com.gametester.controller.admin; // Ou com.gametester.controller.admin

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/excluirEstrategia")
public class ExcluirEstrategiaServlet extends HttpServlet {
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
        // Proteção: Somente administradores podem excluir
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        int estrategiaId = 0;
        boolean sucesso = false;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                estrategiaId = Integer.parseInt(idParam);
                sucesso = estrategiaDAO.excluirEstrategia(estrategiaId); // O DAO já propaga SQLException

                if (sucesso) {
                    session.setAttribute("mensagemSucessoGerenciamento", "Estratégia com ID " + estrategiaId + " excluída com sucesso!");
                } else {
                    // Isso pode acontecer se o ID não existir ou se a exclusão falhar por outro motivo não excepcional (raro se o ID existe)
                    // Ou se houver uma restrição de FK (ex: estratégia em uso por uma sessão) e o DAO tratar isso sem lançar exceção (o nosso lança).
                    session.setAttribute("mensagemErroGerenciamento", "Não foi possível excluir a estratégia com ID " + estrategiaId + ". Verifique se ela não está em uso ou tente novamente.");
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamento", "ID da estratégia inválido.");
            } catch (SQLException e) {
                e.printStackTrace();
                // R33: "evitar a remoção de elementos em uso" - um erro de FK pode cair aqui.
                String mensagemEspecifica = "Erro ao excluir estratégia do banco de dados: " + e.getMessage();
                if (e.getMessage().toLowerCase().contains("foreign key constraint") ||
                        e.getMessage().toLowerCase().contains("referential integrity")) {
                    mensagemEspecifica = "Não é possível excluir a estratégia (ID: " + estrategiaId + ") pois ela está sendo utilizada em uma ou mais sessões de teste.";
                }
                session.setAttribute("mensagemErroGerenciamento", mensagemEspecifica);
            }
        } else {
            session.setAttribute("mensagemErroGerenciamento", "ID da estratégia não fornecido para exclusão.");
        }

        // Redireciona de volta para a página de gerenciamento de estratégias
        response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Para exclusão, geralmente se usa GET (se for um link simples) ou POST (se for um formulário)
        // Se você decidir usar um formulário com método POST para exclusão, a lógica iria aqui.
        // Por enquanto, nosso link é GET.
        doGet(request, response);
    }
}