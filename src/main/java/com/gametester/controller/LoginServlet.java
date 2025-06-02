package com.gametester.controller;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;

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
        String senha = request.getParameter("senha");
        String mensagemErro = null;
        Usuario usuario = null; // Declare o usuário aqui para acesso no bloco finally se necessário

        // 1. Validação básica
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            mensagemErro = "E-mail e senha são obrigatórios.";
        } else {
            try {
                // 2. Autenticar Usuário
                usuario = usuarioDAO.buscarUsuarioPorEmail(email.trim()); // Esta linha pode lançar SQLException

                if (usuario != null) {
                    // TODO: Implementar HASHING DE SENHA AQUI!
                    // Por enquanto, comparação direta (NÃO SEGURO PARA PRODUÇÃO)
                    if (senha.equals(usuario.getSenha())) { // CUIDADO! Comparação direta
                        // 3. Autenticação bem-sucedida: Criar Sessão
                        HttpSession session = request.getSession();
                        session.setAttribute("usuarioLogado", usuario);

                        // 4. Redirecionar com base no tipo de perfil
                        String tipoPerfil = usuario.getTipoPerfil();
                        if ("ADMINISTRADOR".equals(tipoPerfil)) {
                            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                        } else if ("TESTADOR".equals(tipoPerfil)) {
                            response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");
                        } else if ("VISITANTE".equals(tipoPerfil)) {
                            // Visitantes geralmente não fazem login para um dashboard, mas se for o caso:
                            response.sendRedirect(request.getContextPath() + "/index.jsp"); // Ou uma página inicial para visitantes
                        } else {
                            mensagemErro = "Tipo de perfil não autorizado para login.";
                        }
                        // Se houve redirecionamento bem-sucedido, não processar mais nada
                        if (response.isCommitted()) { // Verifica se o redirect já foi enviado
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
                e.printStackTrace(); // Loga o erro no console do servidor
                mensagemErro = "Erro ao processar o login. Por favor, tente novamente mais tarde.";
                // Opcional: você pode querer mostrar e.getMessage() se for seguro e útil para o admin,
                // mas para o usuário final, uma mensagem genérica é melhor.
            }
        }

        // 5. Falha na autenticação, validação ou erro de SQL
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
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}