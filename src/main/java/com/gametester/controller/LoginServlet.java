package com.gametester.controller; // Ou o seu pacote de controllers

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login") // Define a URL que acionará este Servlet
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String mensagemErro = null;

        // 1. Validação básica
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            mensagemErro = "E-mail e senha são obrigatórios.";
        } else {
            // 2. Autenticar Usuário
            Usuario usuario = usuarioDAO.buscarUsuarioPorEmail(email.trim());

            if (usuario != null) {
                // TODO: Implementar HASHING DE SENHA AQUI!
                // Por enquanto, comparação direta (NÃO SEGURO PARA PRODUÇÃO)
                if (senha.equals(usuario.getSenha())) { // << CUIDADO! Comparação direta
                    // 3. Autenticação bem-sucedida: Criar Sessão
                    HttpSession session = request.getSession();
                    session.setAttribute("usuarioLogado", usuario); // Armazena o objeto Usuario na sessão

                    // 4. Redirecionar com base no tipo de perfil
                    String tipoPerfil = usuario.getTipoPerfil();
                    if ("ADMINISTRADOR".equals(tipoPerfil)) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp"); // Exemplo de URL
                    } else if ("TESTADOR".equals(tipoPerfil)) {
                        response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp"); // Exemplo de URL
                    } else {
                        // Perfil desconhecido ou não autorizado para login direto (ex: VISITANTE)
                        mensagemErro = "Tipo de perfil não autorizado para login.";
                        request.setAttribute("erroLogin", mensagemErro);
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                    }
                    return; // Importante para não processar mais nada após o redirect
                } else {
                    // Senha incorreta
                    mensagemErro = "E-mail ou senha inválidos.";
                }
            } else {
                // Usuário não encontrado
                mensagemErro = "E-mail ou senha inválidos.";
            }
        }

        // 5. Falha na autenticação ou validação
        if (mensagemErro != null) {
            request.setAttribute("erroLogin", mensagemErro);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // O GET para /login pode simplesmente exibir a página de login
        // Ou, pode ser usado para invalidar a sessão (logout), se você adicionar um parâmetro "action=logout"
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false); // Não cria uma nova sessão se não existir
            if (session != null) {
                session.invalidate(); // Invalida a sessão
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true"); // Redireciona para login com msg opcional
        } else {
            // Por padrão, mostra a página de login
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}