package com.gametester.controller.testador;

import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.SessaoTesteDAO;
import com.gametester.model.Projeto;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testador/sessoesPorProjeto")
public class SessoesPorProjetoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessaoTesteDAO sessaoTesteDAO;
    private ProjetoDAO projetoDAO;

    @Override
    public void init() {
        sessaoTesteDAO = new SessaoTesteDAO();
        projetoDAO = new ProjetoDAO();
    }

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

        String projetoIdParam = request.getParameter("projetoId");
        if (projetoIdParam == null || projetoIdParam.isEmpty()) {
            // Se não tem ID do projeto, talvez redirecionar para a lista de projetos do testador
            session.setAttribute("mensagemErroMeusProjetos", "ID do Projeto não fornecido para visualizar sessões.");
            response.sendRedirect(request.getContextPath() + "/testador/meusProjetos");
            return;
        }

        try {
            int projetoId = Integer.parseInt(projetoIdParam);
            Projeto projeto = projetoDAO.buscarProjetoPorId(projetoId);

            if (projeto == null) {
                session.setAttribute("mensagemErroMeusProjetos", "Projeto com ID " + projetoId + " não encontrado.");
                response.sendRedirect(request.getContextPath() + "/testador/meusProjetos");
                return;
            }

            // Autorização: Verificar se o testador é membro deste projeto
            // (Assumindo que o admin também pode ver, ou ajustar a lógica)
            boolean ehMembro = false;
            if ("ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
                ehMembro = true; // Admin pode ver sessões de qualquer projeto
            } else {
                List<Projeto> projetosDoTestador = projetoDAO.listarProjetosPorMembro(usuarioLogado.getId());
                for (Projeto p : projetosDoTestador) {
                    if (p.getId() == projetoId) {
                        ehMembro = true;
                        break;
                    }
                }
            }

            if (!ehMembro) {
                session.setAttribute("mensagemErroMeusProjetos", "Você não tem permissão para visualizar sessões deste projeto.");
                response.sendRedirect(request.getContextPath() + "/testador/meusProjetos");
                return;
            }

            // Listar sessões para este projeto.
            // O método no DAO pode aceitar filtros de status e ordenação.
            // Por agora, vamos listar todas com ordenação padrão (data de criação descendente).
            List<SessaoTeste> sessoesDoProjeto = sessaoTesteDAO.listarSessoesPorProjeto(projetoId, "TODOS", "DATA_CRIACAO_DESC");

            request.setAttribute("projeto", projeto); // Para exibir o nome/descrição do projeto na página
            request.setAttribute("listaSessoesDoProjeto", sessoesDoProjeto);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/sessoes-por-projeto.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroMeusProjetos", "ID do Projeto inválido.");
            response.sendRedirect(request.getContextPath() + "/testador/meusProjetos");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroMeusProjetos", "Erro ao carregar sessões do projeto: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/testador/meusProjetos");
        }
    }
}