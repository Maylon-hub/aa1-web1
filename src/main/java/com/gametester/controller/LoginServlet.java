package com.gametester.controller;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;
import org.mindrot.jbcrypt.BCrypt; // Importe a biblioteca jBCrypt

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException; // Importe SQLException

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String senhaDigitada = request.getParameter("senha"); // Renomeado para clareza
        String mensagemErro = null;
        Usuario usuario = null;

        if (email == null || email.trim().isEmpty() || senhaDigitada == null || senhaDigitada.isEmpty()) {
            mensagemErro = "E-mail e senha são obrigatórios.";
        } else {
            try {
                usuario = usuarioDAO.buscarUsuarioPorEmail(email.trim().toLowerCase()); // Busca email em minúsculas

                if (usuario != null) {
                    // Comparação da senha digitada com o hash armazenado no banco
                    if (BCrypt.checkpw(senhaDigitada, usuario.getSenha())) {
                        // Autenticação bem-sucedida: Criar Sessão
                        HttpSession session = request.getSession();
                        session.setAttribute("usuarioLogado", usuario);

                        // Redirecionar com base no tipo de perfil
                        String tipoPerfil = usuario.getTipoPerfil();
                        if ("ADMINISTRADOR".equals(tipoPerfil)) {
                            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                        } else if ("TESTADOR".equals(tipoPerfil)) {
                            response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");
                        } else { // Incluindo "VISITANTE" ou qualquer outro perfil não esperado para dashboard
                            // Visitantes normalmente não têm um dashboard após login.
                            // Se um visitante fizer login, talvez deva ir para a página inicial.
                            // Ou, se 'VISITANTE' como tipo de perfil não deveria fazer login, tratar como erro.
                            mensagemErro = "Tipo de perfil não tem acesso a um painel dedicado.";
                            // Para este caso, vamos redirecionar para a index se não for um erro de login.
                            // Se um visitante não deve logar, a lógica de erro seria diferente.
                            // response.sendRedirect(request.getContextPath() + "/index.jsp");
                        }

                        if (mensagemErro == null && response.isCommitted()) { // Se não houve erro de perfil e já redirecionou
                            return;
                        }
                    } else {
                        // Senha incorreta
                        mensagemErro = "E-mail ou senha inválidos.";
                    }
                } else {
                    // Usuário não encontrado
                    mensagemErro = "E-mail ou senha inválidos.";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mensagemErro = "Erro ao processar o login. Por favor, tente novamente mais tarde.";
            } catch (IllegalArgumentException e_bcrypt) {
                // BCrypt.checkpw pode lançar IllegalArgumentException se o hash armazenado for inválido
                e_bcrypt.printStackTrace();
                mensagemErro = "Erro na verificação da autenticação. Contate o suporte.";
            }
        }

        // Falha na autenticação, validação ou erro
        if (mensagemErro != null) {
            request.setAttribute("erroLogin", mensagemErro);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true");
        } else {
            // Simplesmente exibe a página de login
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}