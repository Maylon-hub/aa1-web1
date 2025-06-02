package com.gametester.controller.perfil;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/perfil/editar")
public class EditarPerfilServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO; // Adicionado como membro da classe

    @Override
    public void init() { // init() já deve existir, apenas garanta que o DAO seja inicializado
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Por favor, faça login para editar seu perfil.", "UTF-8"));
            return;
        }

        // Limpa mensagens da sessão que podem ter sido definidas pelo POST antes do redirect
        if (session.getAttribute("mensagemSucessoPerfil") != null) {
            request.setAttribute("mensagemSucessoPerfil", session.getAttribute("mensagemSucessoPerfil"));
            session.removeAttribute("mensagemSucessoPerfil");
        }
        if (session.getAttribute("mensagemErroPerfil") != null) {
            request.setAttribute("mensagemErroPerfil", session.getAttribute("mensagemErroPerfil"));
            session.removeAttribute("mensagemErroPerfil");
        }

        // Não precisamos repopular "valorNome" etc. aqui, pois pegamos direto do usuarioLogado
        request.setAttribute("usuarioParaEditar", usuarioLogado);
        request.getRequestDispatcher("/WEB-INF/jsp/perfil/editar-perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Sessão expirada. Por favor, faça login novamente.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nomeUsuario");
        String email = request.getParameter("emailUsuario");
        String mensagemParaJSP = null; // Para erros de validação que usam forward

        // Validação básica
        if (nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            mensagemParaJSP = "Nome e E-mail são obrigatórios.";
        } else if (!email.trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // Validação de formato de e-mail
            mensagemParaJSP = "Formato de e-mail inválido.";
        }

        if (mensagemParaJSP != null) {
            request.setAttribute("mensagemErroPerfil", mensagemParaJSP);
            // Repopula com os dados que o usuário tentou submeter
            Usuario dadosComErro = new Usuario(usuarioLogado.getId(), nome, email, null, usuarioLogado.getTipoPerfil());
            request.setAttribute("usuarioParaEditar", dadosComErro);
            request.getRequestDispatcher("/WEB-INF/jsp/perfil/editar-perfil.jsp").forward(request, response);
            return;
        }

        try {
            // Verificar se o e-mail foi alterado e se o novo e-mail já existe para outro usuário
            if (!email.trim().equalsIgnoreCase(usuarioLogado.getEmail())) {
                Usuario usuarioExistenteComNovoEmail = usuarioDAO.buscarUsuarioPorEmail(email.trim());
                if (usuarioExistenteComNovoEmail != null && usuarioExistenteComNovoEmail.getId() != usuarioLogado.getId()) {
                    mensagemParaJSP = "Este e-mail já está em uso por outra conta.";
                    request.setAttribute("mensagemErroPerfil", mensagemParaJSP);
                    Usuario dadosComErro = new Usuario(usuarioLogado.getId(), nome, email, null, usuarioLogado.getTipoPerfil());
                    request.setAttribute("usuarioParaEditar", dadosComErro);
                    request.getRequestDispatcher("/WEB-INF/jsp/perfil/editar-perfil.jsp").forward(request, response);
                    return;
                }
            }

            Usuario usuarioParaAtualizar = new Usuario();
            usuarioParaAtualizar.setId(usuarioLogado.getId());
            usuarioParaAtualizar.setNome(nome.trim());
            usuarioParaAtualizar.setEmail(email.trim().toLowerCase());
            usuarioParaAtualizar.setTipoPerfil(usuarioLogado.getTipoPerfil());
            usuarioParaAtualizar.setSenha(null); // Indica ao DAO para NÃO atualizar a senha

            boolean sucesso = usuarioDAO.atualizarUsuario(usuarioParaAtualizar);

            if (sucesso) {
                usuarioLogado.setNome(usuarioParaAtualizar.getNome());
                usuarioLogado.setEmail(usuarioParaAtualizar.getEmail());
                session.setAttribute("usuarioLogado", usuarioLogado);
                session.setAttribute("mensagemSucessoPerfil", "Seu cadastro foi atualizado com sucesso!");
            } else {
                // Se o DAO.atualizarUsuario retornar false sem lançar SQLException (ex: ID não encontrado no update)
                session.setAttribute("mensagemErroPerfil", "Não foi possível atualizar seu cadastro. Tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("mensagemErroPerfil", "Erro ao atualizar cadastro: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/perfil/editar");
    }
}