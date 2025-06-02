package com.gametester.controller.admin;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;
// Importe a biblioteca jBCrypt se for implementar o hashing de senha agora
import org.mindrot.jbcrypt.BCrypt;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/cadastrarUsuario")
public class CadastrarUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    // Exibe o formulário de cadastro de usuário
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem acessar
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        // Limpa atributos para um novo formulário
        request.removeAttribute("valorNomeUsuario");
        request.removeAttribute("valorEmailUsuario");
        request.removeAttribute("valorTipoPerfil");
        // Não repopulamos senha por segurança
        request.removeAttribute("mensagemErroUsuario");
        request.removeAttribute("mensagemSucessoUsuario");

        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-usuario.jsp").forward(request, response);
    }

    // Processa o envio do formulário de cadastro de usuário
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario adminLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Proteção: Somente administradores podem cadastrar
        if (adminLogado == null || !"ADMINISTRADOR".equals(adminLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nomeUsuario");
        String email = request.getParameter("emailUsuario");
        String senha = request.getParameter("senhaUsuario");
        String confirmaSenha = request.getParameter("confirmaSenhaUsuario");
        String tipoPerfil = request.getParameter("tipoPerfilUsuario");

        // Validações
        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.isEmpty() || // Senha não deve ser apenas espaços em branco
                confirmaSenha == null || confirmaSenha.isEmpty() ||
                tipoPerfil == null || tipoPerfil.trim().isEmpty()) {

            request.setAttribute("mensagemErroUsuario", "Todos os campos são obrigatórios.");
            repopularFormulario(request, nome, email, tipoPerfil); // Senha não é repopulada
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-usuario.jsp").forward(request, response);
            return;
        }

        if (!senha.equals(confirmaSenha)) {
            request.setAttribute("mensagemErroUsuario", "As senhas não coincidem.");
            repopularFormulario(request, nome, email, tipoPerfil);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-usuario.jsp").forward(request, response);
            return;
        }

        if (!"ADMINISTRADOR".equals(tipoPerfil) && !"TESTADOR".equals(tipoPerfil)) {
            request.setAttribute("mensagemErroUsuario", "Tipo de perfil inválido.");
            repopularFormulario(request, nome, email, tipoPerfil);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-usuario.jsp").forward(request, response);
            return;
        }

        // Hashing da senha (RECOMENDADO)
        String senhaComHash = BCrypt.hashpw(senha, BCrypt.gensalt());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome.trim());
        novoUsuario.setEmail(email.trim().toLowerCase()); // Padroniza email para minúsculas
        novoUsuario.setSenha(senhaComHash); // Salva a senha com hash
        novoUsuario.setTipoPerfil(tipoPerfil);

        try {
            usuarioDAO.inserirUsuario(novoUsuario); // O DAO preenche o ID no objeto
            request.setAttribute("mensagemSucessoUsuario", "Usuário '" + novoUsuario.getNome() + "' cadastrado com sucesso! ID: " + novoUsuario.getId());

            // Não repopula o formulário em caso de sucesso, para permitir novo cadastro
            // Os atributos valorNomeUsuario etc. não são definidos aqui

        } catch (SQLException e) {
            e.printStackTrace();
            // O UsuarioDAO já trata o erro de e-mail duplicado e relança uma SQLException com mensagem específica.
            request.setAttribute("mensagemErroUsuario", e.getMessage());
            repopularFormulario(request, nome, email, tipoPerfil);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-usuario.jsp").forward(request, response);
    }

    private void repopularFormulario(HttpServletRequest request, String nome, String email, String tipoPerfil) {
        request.setAttribute("valorNomeUsuario", nome);
        request.setAttribute("valorEmailUsuario", email);
        request.setAttribute("valorTipoPerfil", tipoPerfil);
        // Senhas não são repopuladas por segurança
    }
}