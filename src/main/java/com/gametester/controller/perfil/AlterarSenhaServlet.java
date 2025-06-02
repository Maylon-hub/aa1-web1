package com.gametester.controller.perfil;

import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Usuario;
import org.mindrot.jbcrypt.BCrypt; // Para hashing e verificação de senha

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/perfil/alterarSenha")
public class AlterarSenhaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Por favor, faça login para alterar sua senha.", "UTF-8"));
            return;
        }
        // Limpar mensagens de erro/sucesso de POST anterior
        request.removeAttribute("mensagemErroSenha");
        request.removeAttribute("mensagemSucessoSenha");

        request.getRequestDispatcher("/WEB-INF/jsp/perfil/alterar-senha.jsp").forward(request, response);
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

        String senhaAtual = request.getParameter("senhaAtual");
        String novaSenha = request.getParameter("novaSenha");
        String confirmaNovaSenha = request.getParameter("confirmaNovaSenha");

        // Validações
        if (senhaAtual == null || senhaAtual.isEmpty() ||
                novaSenha == null || novaSenha.isEmpty() ||
                confirmaNovaSenha == null || confirmaNovaSenha.isEmpty()) {
            request.setAttribute("mensagemErroSenha", "Todos os campos de senha são obrigatórios.");
            request.getRequestDispatcher("/WEB-INF/jsp/perfil/alterar-senha.jsp").forward(request, response);
            return;
        }

        if (!novaSenha.equals(confirmaNovaSenha)) {
            request.setAttribute("mensagemErroSenha", "A nova senha e a confirmação não coincidem.");
            request.getRequestDispatcher("/WEB-INF/jsp/perfil/alterar-senha.jsp").forward(request, response);
            return;
        }

        // Adicionar validação de complexidade para novaSenha se desejado (ex: tamanho mínimo)
        if (novaSenha.length() < 6) { // Exemplo de validação de tamanho mínimo
            request.setAttribute("mensagemErroSenha", "A nova senha deve ter pelo menos 6 caracteres.");
            request.getRequestDispatcher("/WEB-INF/jsp/perfil/alterar-senha.jsp").forward(request, response);
            return;
        }


        try {
            // 1. Buscar o usuário do banco para pegar o hash da senha atual (importante!)
            // Não confie apenas no objeto usuarioLogado da sessão para a senha atual,
            // pois ele pode não ter o hash ou pode estar desatualizado em cenários raros.
            // No entanto, para simplificar e assumir que a senha no usuarioLogado é o hash correto:
            String hashSenhaAtualBanco = usuarioLogado.getSenha();
            // Se o objeto na sessão não tiver o hash, você precisaria buscar do DAO:
            // Usuario usuarioDoBanco = usuarioDAO.buscarUsuarioPorId(usuarioLogado.getId());
            // String hashSenhaAtualBanco = usuarioDoBanco.getSenha();


            // 2. Verificar se a "Senha Atual" digitada corresponde à senha armazenada
            if (BCrypt.checkpw(senhaAtual, hashSenhaAtualBanco)) {
                // Senha atual está correta, pode prosseguir para atualizar para a nova senha

                String novaSenhaComHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
                boolean sucesso = usuarioDAO.atualizarSenhaUsuario(usuarioLogado.getId(), novaSenhaComHash);

                if (sucesso) {
                    // IMPORTANTE: Atualizar o hash da senha no objeto da sessão também!
                    usuarioLogado.setSenha(novaSenhaComHash);
                    session.setAttribute("usuarioLogado", usuarioLogado);

                    request.setAttribute("mensagemSucessoSenha", "Senha alterada com sucesso!");
                } else {
                    request.setAttribute("mensagemErroSenha", "Não foi possível alterar a senha. Tente novamente.");
                }
            } else {
                // Senha atual incorreta
                request.setAttribute("mensagemErroSenha", "A 'Senha Atual' está incorreta.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroSenha", "Erro no banco de dados ao tentar alterar a senha: " + e.getMessage());
        } catch (IllegalArgumentException e_bcrypt) {
            e_bcrypt.printStackTrace();
            request.setAttribute("mensagemErroSenha", "Erro na verificação da senha atual. Formato de senha armazenado pode ser inválido.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/perfil/alterar-senha.jsp").forward(request, response);
    }
}