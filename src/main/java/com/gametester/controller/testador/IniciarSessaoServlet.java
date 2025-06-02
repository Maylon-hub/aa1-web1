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

@WebServlet("/testador/iniciarSessao")
public class IniciarSessaoServlet extends HttpServlet {
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

        // Proteção: Somente Testador ou Administrador podem iniciar sessões
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

                // Verificação de autorização: O testador só pode iniciar suas próprias sessões.
                // Um admin poderia iniciar qualquer uma, mas vamos manter a lógica de "minhas sessões" para o testador.
                SessaoTeste sessaoParaIniciar = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);

                if (sessaoParaIniciar == null) {
                    session.setAttribute("mensagemErroSessaoOperacao", "Sessão de teste com ID " + sessaoId + " não encontrada.");
                } else if (sessaoParaIniciar.getTestadorId() != usuarioLogado.getId() && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
                    // Se não for admin, e a sessão não pertencer ao testador logado
                    session.setAttribute("mensagemErroSessaoOperacao", "Você não tem permissão para iniciar esta sessão de teste.");
                } else if (!"CRIADO".equals(sessaoParaIniciar.getStatus())) {
                    session.setAttribute("mensagemErroSessaoOperacao", "A sessão de teste (ID: " + sessaoId + ") não pode ser iniciada pois seu status é '" + sessaoParaIniciar.getStatus() + "'.");
                } else {
                    // Tenta iniciar a sessão
                    boolean sucesso = sessaoTesteDAO.iniciarSessao(sessaoId); // DAO já atualiza status e data_hora_inicio
                    if (sucesso) {
                        session.setAttribute("mensagemSucessoSessaoOperacao", "Sessão de teste (ID: " + sessaoId + ") iniciada com sucesso!");
                    } else {
                        // Isso pode acontecer se, por exemplo, o status mudou entre a leitura e a tentativa de update.
                        session.setAttribute("mensagemErroSessaoOperacao", "Não foi possível iniciar a sessão de teste (ID: " + sessaoId + "). Verifique seu status.");
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessaoOperacao", "ID da sessão inválido.");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessaoOperacao", "Erro ao tentar iniciar a sessão de teste: " + e.getMessage());
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
        // A ação de iniciar é geralmente um link GET simples.
        // Se fosse um formulário mais complexo, poderia usar POST.
        doGet(request, response);
    }
}