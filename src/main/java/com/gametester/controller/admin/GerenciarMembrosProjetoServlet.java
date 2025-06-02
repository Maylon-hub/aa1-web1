package com.gametester.controller.admin;

import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Projeto;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/admin/gerenciarMembrosProjeto")
public class GerenciarMembrosProjetoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario adminLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (adminLogado == null || !"ADMINISTRADOR".equals(adminLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String projetoIdParam = request.getParameter("projetoId");
        if (projetoIdParam == null || projetoIdParam.isEmpty()) {
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto não fornecido para gerenciar membros.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            return;
        }

        try {
            int projetoId = Integer.parseInt(projetoIdParam);
            Projeto projeto = projetoDAO.buscarProjetoPorId(projetoId);

            if (projeto == null) {
                session.setAttribute("mensagemErroGerenciamentoProjetos", "Projeto com ID " + projetoId + " não encontrado.");
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
                return;
            }

            List<Usuario> membrosAtuais = projetoDAO.listarMembrosDoProjeto(projetoId);
            List<Usuario> todosUsuarios = usuarioDAO.listarTodosUsuarios();

            List<Integer> idsMembrosAtuais = membrosAtuais.stream().map(Usuario::getId).collect(Collectors.toList());
            List<Usuario> usuariosDisponiveis = todosUsuarios.stream()
                    .filter(u -> !idsMembrosAtuais.contains(u.getId()))
                    // Opcional: filtrar apenas por testadores aqui
                    // .filter(u -> "TESTADOR".equals(u.getTipoPerfil()))
                    .collect(Collectors.toList());

            request.setAttribute("projeto", projeto);
            request.setAttribute("membrosAtuais", membrosAtuais);
            request.setAttribute("usuariosDisponiveis", usuariosDisponiveis);

            request.getRequestDispatcher("/WEB-INF/jsp/admin/gerenciar-membros-projeto.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroGerenciamentoProjetos", "ID do projeto inválido.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroGerenciamentoProjetos", "Erro ao carregar dados para gerenciamento de membros: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario adminLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (adminLogado == null || !"ADMINISTRADOR".equals(adminLogado.getTipoPerfil())) {
            // Embora o doGet também verifique, é bom ter em ações POST também.
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Ação não autorizada.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");
        String projetoIdParam = request.getParameter("projetoId");
        // 'usuarioId' é usado pela ação de remover, 'usuarioIdAdd' pela ação de adicionar
        String usuarioIdParam = request.getParameter("usuarioId") != null ? request.getParameter("usuarioId") : request.getParameter("usuarioIdAdd");


        int projetoId = 0;
        int usuarioId = 0;

        if (projetoIdParam == null || projetoIdParam.isEmpty()) {
            session.setAttribute("mensagemErroMembros", "ID do projeto não foi especificado na ação.");
            // Tenta redirecionar para a lista geral de projetos se não tiver ID de projeto
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            return;
        }

        try {
            projetoId = Integer.parseInt(projetoIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroMembros", "ID do projeto inválido na ação.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarProjetos");
            return;
        }

        if (usuarioIdParam == null || usuarioIdParam.isEmpty()){
            session.setAttribute("mensagemErroMembros", "ID do usuário não foi especificado para a ação.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarMembrosProjeto?projetoId=" + projetoId);
            return;
        }

        try {
            usuarioId = Integer.parseInt(usuarioIdParam);

            if ("adicionarMembro".equals(acao)) {
                boolean sucesso = projetoDAO.adicionarMembroAoProjeto(projetoId, usuarioId);
                if (sucesso) {
                    session.setAttribute("mensagemSucessoMembros", "Membro (ID: " + usuarioId + ") adicionado ao projeto (ID: " + projetoId + ") com sucesso!");
                } else {
                    session.setAttribute("mensagemErroMembros", "Não foi possível adicionar o membro. Ele pode já ser membro ou o ID do usuário/projeto é inválido.");
                }
            } else if ("removerMembro".equals(acao)) {
                boolean sucesso = projetoDAO.removerMembroDoProjeto(projetoId, usuarioId);
                if (sucesso) {
                    session.setAttribute("mensagemSucessoMembros", "Membro (ID: " + usuarioId + ") removido do projeto (ID: " + projetoId + ") com sucesso!");
                } else {
                    session.setAttribute("mensagemErroMembros", "Não foi possível remover o membro. Ele pode não ser membro deste projeto.");
                }
            } else {
                session.setAttribute("mensagemErroMembros", "Ação desconhecida ou não especificada.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroMembros", "ID de usuário fornecido para a ação é inválido.");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroMembros", "Erro no banco de dados ao gerenciar membros: " + e.getMessage());
        }

        // Redireciona de volta para a página de gerenciamento de membros do projeto específico
        response.sendRedirect(request.getContextPath() + "/admin/gerenciarMembrosProjeto?projetoId=" + projetoId);
    }
}