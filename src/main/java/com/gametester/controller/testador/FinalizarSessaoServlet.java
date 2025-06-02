package com.gametester.controller.testador;

import com.gametester.dao.SessaoTesteDAO;
import com.gametester.model.SessaoTeste;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/testador/finalizarSessao")
public class FinalizarSessaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessaoTesteDAO sessaoTesteDAO;

    @Override
    public void init() {
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Proteção: Somente Testador ou Administrador podem finalizar sessões
        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        int sessaoId = 0;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                sessaoId = Integer.parseInt(idParam);

                // Verificação de autorização: O testador só pode finalizar suas próprias sessões.
                SessaoTeste sessaoParaFinalizar = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);

                if (sessaoParaFinalizar == null) {
                    session.setAttribute("mensagemErroSessaoOperacao", "Sessão de teste com ID " + sessaoId + " não encontrada.");
                } else if (sessaoParaFinalizar.getTestadorId() != usuarioLogado.getId() && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
                    session.setAttribute("mensagemErroSessaoOperacao", "Você não tem permissão para finalizar esta sessão de teste.");
                } else if (!"EM_EXECUCAO".equals(sessaoParaFinalizar.getStatus())) {
                    session.setAttribute("mensagemErroSessaoOperacao", "A sessão de teste (ID: " + sessaoId + ") não pode ser finalizada pois seu status é '" + sessaoParaFinalizar.getStatus() + "'.");
                } else {
                    // Tenta finalizar a sessão
                    boolean sucesso = sessaoTesteDAO.finalizarSessao(sessaoId); // DAO atualiza status e data_hora_fim
                    if (sucesso) {
                        session.setAttribute("mensagemSucessoSessaoOperacao", "Sessão de teste (ID: " + sessaoId + ") finalizada com sucesso!");
                    } else {
                        session.setAttribute("mensagemErroSessaoOperacao", "Não foi possível finalizar a sessão de teste (ID: " + sessaoId + "). Verifique seu status.");
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessaoOperacao", "ID da sessão inválido.");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessaoOperacao", "Erro ao tentar finalizar a sessão de teste: " + e.getMessage());
            }
        } else {
            session.setAttribute("mensagemErroSessaoOperacao", "ID da sessão não fornecido.");
        }

        // Redireciona de volta para a página "Minhas Sessões"
        response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Ação simples via GET
    }
}