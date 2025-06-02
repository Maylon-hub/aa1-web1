package com.gametester.controller.testador;

import com.gametester.dao.BugDAO;
import com.gametester.dao.SessaoTesteDAO; // Para verificar a sessão
import com.gametester.model.Bug;
import com.gametester.model.SessaoTeste; // Para verificar a sessão
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
// java.sql.Timestamp não é diretamente necessário aqui, pois o BD lida com data_registro

@WebServlet("/testador/registrarBug")
public class RegistrarBugServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BugDAO bugDAO;
    private SessaoTesteDAO sessaoTesteDAO; // Para verificar o status e dono da sessão

    @Override
    public void init() {
        bugDAO = new BugDAO();
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    // Exibe o formulário de registro de bug
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

        String sessaoIdParam = request.getParameter("sessaoId");
        if (sessaoIdParam == null || sessaoIdParam.isEmpty()) {
            request.setAttribute("mensagemErroBug", "ID da Sessão de Teste não fornecido.");
            // Idealmente, redirecionar para minhasSessoes com a mensagem na sessão
            session.setAttribute("mensagemErroSessaoOperacao", "ID da Sessão de Teste não fornecido para registrar bug.");
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
            return;
        }

        try {
            int sessaoId = Integer.parseInt(sessaoIdParam);
            SessaoTeste sessaoAtual = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);

            if (sessaoAtual == null) {
                session.setAttribute("mensagemErroSessaoOperacao", "Sessão de Teste (ID: " + sessaoId + ") não encontrada.");
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }
            if (sessaoAtual.getTestadorId() != usuarioLogado.getId() && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
                session.setAttribute("mensagemErroSessaoOperacao", "Você não tem permissão para registrar bugs para esta sessão.");
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }
            if (!"EM_EXECUCAO".equals(sessaoAtual.getStatus())) {
                session.setAttribute("mensagemErroSessaoOperacao", "Só é possível registrar bugs em sessões 'EM EXECUÇÃO'. Status atual: " + sessaoAtual.getStatus());
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }

            request.setAttribute("sessaoId", sessaoId); // Passa o ID da sessão para o formulário
            request.setAttribute("sessaoDescricao", sessaoAtual.getDescricao()); // Para exibir no form

        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroSessaoOperacao", "ID da Sessão de Teste inválido.");
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroSessaoOperacao", "Erro ao verificar sessão: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
    }

    // Processa o envio do formulário de registro de bug
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String sessaoIdParam = request.getParameter("sessaoId"); // Vem de um campo hidden no form
        String descricao = request.getParameter("descricaoBug");
        String severidade = request.getParameter("severidadeBug");
        String screenshotUrl = request.getParameter("screenshotUrlBug");

        int sessaoId = 0;
        // Tentar recuperar a descrição da sessão para reexibir o formulário em caso de erro
        String sessaoDescricaoOriginal = request.getParameter("sessaoDescricao");


        // Validações
        if (sessaoIdParam == null || sessaoIdParam.isEmpty()) {
            request.setAttribute("mensagemErroBug", "ID da Sessão de Teste não foi submetido com o formulário.");
            request.setAttribute("sessaoDescricao", sessaoDescricaoOriginal); // Para reexibir título
            repopularFormularioBug(request, descricao, severidade, screenshotUrl);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
            return;
        }

        try {
            sessaoId = Integer.parseInt(sessaoIdParam);
            // Revalidar se a sessão ainda está EM_EXECUCAO e pertence ao usuário
            SessaoTeste sessaoAtual = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);
            if (sessaoAtual == null || (sessaoAtual.getTestadorId() != usuarioLogado.getId() && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) || !"EM_EXECUCAO".equals(sessaoAtual.getStatus())) {
                session.setAttribute("mensagemErroSessaoOperacao", "Não é mais possível registrar bugs para esta sessão ou sessão inválida.");
                response.sendRedirect(request.getContextPath() + "/testador/minhasSessoes");
                return;
            }
            request.setAttribute("sessaoDescricao", sessaoAtual.getDescricao()); // Atualiza para reexibir

        } catch (NumberFormatException e) {
            request.setAttribute("mensagemErroBug", "ID da Sessão de Teste no formulário é inválido.");
            request.setAttribute("sessaoDescricao", sessaoDescricaoOriginal);
            repopularFormularioBug(request, descricao, severidade, screenshotUrl);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroBug", "Erro ao validar sessão antes de salvar bug: " + e.getMessage());
            request.setAttribute("sessaoId", sessaoId);
            request.setAttribute("sessaoDescricao", sessaoDescricaoOriginal);
            repopularFormularioBug(request, descricao, severidade, screenshotUrl);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
            return;
        }

        request.setAttribute("sessaoId", sessaoId); // Garante que o sessaoId está disponível para o JSP

        if (descricao == null || descricao.trim().isEmpty() || severidade == null || severidade.isEmpty()) {
            request.setAttribute("mensagemErroBug", "Descrição e Severidade do bug são obrigatórios.");
            repopularFormularioBug(request, descricao, severidade, screenshotUrl);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
            return;
        }

        Bug novoBug = new Bug();
        novoBug.setSessaoTesteId(sessaoId);
        novoBug.setDescricao(descricao.trim());
        novoBug.setSeveridade(severidade);
        novoBug.setScreenshotUrl(screenshotUrl != null && !screenshotUrl.trim().isEmpty() ? screenshotUrl.trim() : null);
        // data_registro é definida pelo banco (DEFAULT CURRENT_TIMESTAMP)

        try {
            bugDAO.inserirBug(novoBug); // DAO atualiza o objeto com ID
            request.setAttribute("mensagemSucessoBug", "Bug (ID: " + novoBug.getId() + ") registrado com sucesso para a sessão!");
            // Limpa os campos do formulário após o sucesso para permitir novo registro de bug na mesma sessão
            request.removeAttribute("valorDescricaoBug");
            request.removeAttribute("valorSeveridadeBug");
            request.removeAttribute("valorScreenshotUrlBug");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroBug", "Erro ao salvar bug no banco de dados: " + e.getMessage());
            repopularFormularioBug(request, descricao, severidade, screenshotUrl);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/testador/registrar-bug.jsp").forward(request, response);
    }

    private void repopularFormularioBug(HttpServletRequest request, String descricao, String severidade, String screenshotUrl) {
        request.setAttribute("valorDescricaoBug", descricao);
        request.setAttribute("valorSeveridadeBug", severidade);
        request.setAttribute("valorScreenshotUrlBug", screenshotUrl);
    }
}