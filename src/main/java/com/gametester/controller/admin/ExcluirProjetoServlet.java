package com.gametester.controller.admin;

import com.gametester.dao.ProjetoDAO;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/excluirProjeto")
public class ExcluirProjetoServlet extends HttpServlet {
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
        // Proteção: Somente administradores podem excluir
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        int projetoId = 0;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                projetoId = Integer.parseInt(idParam);
                boolean sucesso = projetoDAO.excluirProjeto(projetoId);

                if (sucesso) {
                    session.setAttribute("mensagemSucessoGerenciamentoProjetos", "Projeto com ID " + projetoId + " excluído com sucesso!");
                } else {
                    // Isso pode acontecer se o ID não existir ou se a exclusão falhar por outro motivo não excepcional.
                    session.setAttribute("mensagemErroGerenciamentoProjetos", "Não foi possível excluir o projeto com ID " + projetoId + ". Verifique se ele não está em uso ou tente novamente.");
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto inválido.");
            } catch (SQLException e) {
                e.printStackTrace();
                String mensagemEspecifica = "Erro ao excluir projeto do banco de dados: " + e.getMessage();
                // Verifica se o erro é por causa de uma chave estrangeira (projeto em uso)
                if (e.getMessage().toLowerCase().contains("foreign key constraint") ||
                        e.getMessage().toLowerCase().contains("referential integrity") ||
                        e.getMessage().toLowerCase().contains("violates foreign key constraint")) { // Adicionando mais uma verificação comum
                    mensagemEspecifica = "Não é possível excluir o projeto (ID: " + projetoId + ") pois ele está sendo utilizado em uma ou mais sessões de teste.";
                }
                session.setAttribute("mensagemErroGerenciamentoProjetos", mensagemEspecifica);
            }
        } else {
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto não fornecido para exclusão.");
        }

        // Redireciona de volta para a página de gerenciamento de projetos
        response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // A exclusão via link simples geralmente usa GET.
        // Se fosse um formulário de exclusão, poderia usar POST.
        doGet(request, response);
    }
}