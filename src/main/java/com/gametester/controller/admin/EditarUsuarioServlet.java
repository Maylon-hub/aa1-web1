package com.gametester.controller.admin;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;
import org.mindrot.jbcrypt.BCrypt; // Para hashing de nova senha

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/editarUsuario")
public class EditarUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    // doGet: Carrega os dados do usuário para o formulário de edição
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem editar
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int usuarioId = Integer.parseInt(idParam);
                Usuario usuarioParaEditar = usuarioDAO.buscarUsuarioPorId(usuarioId);

                if (usuarioParaEditar != null) {
                    // Não passamos a senha (hash) para o formulário por segurança!!!
                    // O formulário terá campos para "nova senha" se desejado.
                    usuarioParaEditar.setSenha(null); // Limpa a senha antes de enviar para o JSP
                    request.setAttribute("usuarioParaEditar", usuarioParaEditar);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-usuario.jsp").forward(request, response);
                } else {
                    session.setAttribute("mensagemErroGerenciamentoUsuarios", "Usuário com ID " + usuarioId + " não encontrado.");
                    response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário inválido para edição.");
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamentoUsuarios", "Erro ao buscar usuário para edição: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
            }
        } else {
            session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário não fornecido para edição.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
        }
    }

    // doPost: Processa os dados do formulário de edição e atualiza o usuário
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario adminLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (adminLogado == null || !"ADMINISTRADOR".equals(adminLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idUsuario");
        String nome = request.getParameter("nomeUsuario");
        String email = request.getParameter("emailUsuario");
        String tipoPerfil = request.getParameter("tipoPerfilUsuario");
        String novaSenha = request.getParameter("novaSenhaUsuario");
        String confirmaNovaSenha = request.getParameter("confirmaNovaSenhaUsuario");

        int usuarioId = 0;

        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário não fornecido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
            return;
        }

        try {
            usuarioId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroGerenciamentoUsuarios", "ID do usuário inválido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
            return;
        }

        // Validação dos campos obrigatórios (nome, email, tipoPerfil)
        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                tipoPerfil == null || tipoPerfil.isEmpty()) {

            request.setAttribute("mensagemErroFormUsuario", "Nome, E-mail e Tipo de Perfil são obrigatórios.");
            repopularFormularioEdicao(request, usuarioId, nome, email, tipoPerfil); // Repopula o formulário
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-usuario.jsp").forward(request, response);
            return;
        }

        // Validação da nova senha (se fornecida)
        boolean atualizarSenha = novaSenha != null && !novaSenha.isEmpty();
        if (atualizarSenha && (confirmaNovaSenha == null || !novaSenha.equals(confirmaNovaSenha))) {
            request.setAttribute("mensagemErroFormUsuario", "As novas senhas não coincidem.");
            repopularFormularioEdicao(request, usuarioId, nome, email, tipoPerfil);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-usuario.jsp").forward(request, response);
            return;
        }

        if (!"ADMINISTRADOR".equals(tipoPerfil) && !"TESTADOR".equals(tipoPerfil)) {
            request.setAttribute("mensagemErroFormUsuario", "Tipo de perfil inválido.");
            repopularFormularioEdicao(request, usuarioId, nome, email, tipoPerfil);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-usuario.jsp").forward(request, response);
            return;
        }

        // Impedir que um admin mude seu próprio perfil para Testador se for o único admin
        // Ou que mude o perfil do último admin para testador.
        // Esta lógica pode ser mais complexa e ficar no DAO ou numa camada de serviço.
        // Por agora, uma verificação simples: não permitir rebaixar a si mesmo se for admin.
        if (adminLogado.getId() == usuarioId && "ADMINISTRADOR".equals(adminLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(tipoPerfil)) {
            request.setAttribute("mensagemErroFormUsuario", "Um administrador não pode rebaixar seu próprio perfil.");
            repopularFormularioEdicao(request, usuarioId, nome, email, adminLogado.getTipoPerfil()); // Mantém o perfil original
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-usuario.jsp").forward(request, response);
            return;
        }


        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(usuarioId);
        usuarioAtualizado.setNome(nome.trim());
        usuarioAtualizado.setEmail(email.trim().toLowerCase());
        usuarioAtualizado.setTipoPerfil(tipoPerfil);

        if (atualizarSenha) {
            String senhaComHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            usuarioAtualizado.setSenha(senhaComHash); // Define a nova senha (com hash) para atualização
        } else {
            usuarioAtualizado.setSenha(null); // Indica ao DAO para não atualizar a senha
        }

        try {
            boolean sucesso = usuarioDAO.atualizarUsuario(usuarioAtualizado);
            if (sucesso) {
                session.setAttribute("mensagemSucessoGerenciamentoUsuarios", "Usuário '" + usuarioAtualizado.getNome() + "' (ID: " + usuarioId + ") atualizado com sucesso!");
            } else {
                session.setAttribute("mensagemErroGerenciamentoUsuarios", "Não foi possível atualizar o usuário com ID " + usuarioId + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // O DAO já tenta identificar erro de email duplicado
            session.setAttribute("mensagemErroGerenciamentoUsuarios", "Erro ao atualizar usuário: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/gerenciarUsuarios");
    }

    private void repopularFormularioEdicao(HttpServletRequest request, int id, String nome, String email, String tipoPerfil) {
        // Para repopular o formulário de edição, precisamos do objeto usuário completo (ou pelo menos dos campos)
        // Se o objeto original não estiver mais no request, podemos criar um temporário.
        // A melhor prática seria buscar o objeto original do DAO novamente no doGet se a validação do POST falhar,
        // mas para simplificar, vamos repopular com os dados submetidos.
        Usuario usuarioParaRepopular = new Usuario();
        usuarioParaRepopular.setId(id);
        usuarioParaRepopular.setNome(nome);
        usuarioParaRepopular.setEmail(email);
        usuarioParaRepopular.setTipoPerfil(tipoPerfil);
        // Senha não é repopulada
        request.setAttribute("usuarioParaEditar", usuarioParaRepopular);
    }
}