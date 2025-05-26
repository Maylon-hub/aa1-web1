package com.gametester.dao;// src/main/java/com/gametester/dao/UsuarioDAO.java

import com.gametester.model.Usuario;
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Insere um novo usuário no banco de dados.
     * @param usuario O objeto Usuario a ser inserido.
     * @return true se o usuário foi inserido com sucesso, false caso contrário.
     */
    public boolean inserirUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nome, email, senha, tipo_perfil) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha()); // Lembre-se de hashear a senha em um sistema real!
            stmt.setString(4, usuario.getTipoPerfil());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
            // Para depuração, você pode lançar a exceção ou logar com mais detalhes
            return false;
        } finally {
            ConexaoDB.closeConnection(conn);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
        }
    }

    /**
     * Busca um usuário pelo ID.
     * @param id O ID do usuário.
     * @return O objeto Usuario se encontrado, ou null.
     */
    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuario WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { /* Ignorar */ } }
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    /**
     * Busca um usuário pelo email (útil para login).
     * @param email O email do usuário.
     * @return O objeto Usuario se encontrado, ou null.
     */
    public Usuario buscarUsuarioPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuario WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { /* Ignorar */ } }
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    /**
     * Atualiza um usuário existente no banco de dados.
     * @param usuario O objeto Usuario com os dados atualizados.
     * @return true se o usuário foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuario SET nome = ?, email = ?, senha = ?, tipo_perfil = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoPerfil());
            stmt.setInt(5, usuario.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        } finally {
            ConexaoDB.closeConnection(conn);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
        }
    }

    /**
     * Exclui um usuário do banco de dados pelo ID.
     * @param id O ID do usuário a ser excluído.
     * @return true se o usuário foi excluído com sucesso, false caso contrário.
     */
    public boolean excluirUsuario(int id) {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            // Exceção de integridade referencial: tratar aqui ou no Controller
            return false;
        } finally {
            ConexaoDB.closeConnection(conn);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
        }
    }

    /**
     * Lista todos os usuários.
     * @return Uma lista de objetos Usuario.
     */
    public List<Usuario> listarTodosUsuarios() {
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuario ORDER BY nome";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Usuario> usuarios = new ArrayList<>();
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { /* Ignorar */ } }
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { /* Ignorar */ } }
            ConexaoDB.closeConnection(conn);
        }
        return usuarios;
    }

    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();

        // 1. Testar Inserção
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Admin Teste");
        novoUsuario.setEmail("admin@test.com");
        novoUsuario.setSenha("senha123"); // Em produção, hash a senha!
        novoUsuario.setTipoPerfil("ADMINISTRADOR");

        if (dao.inserirUsuario(novoUsuario)) {
            System.out.println("Usuário inserido com sucesso!");
        } else {
            System.out.println("Falha ao inserir usuário.");
        }

        // 2. Testar Busca por Email
        Usuario usuarioEncontrado = dao.buscarUsuarioPorEmail("admin@test.com");
        if (usuarioEncontrado != null) {
            System.out.println("Usuário encontrado: " + usuarioEncontrado.getNome() + " - " + usuarioEncontrado.getTipoPerfil());

            // 3. Testar Atualização
            usuarioEncontrado.setNome("Administrador Principal");
            if (dao.atualizarUsuario(usuarioEncontrado)) {
                System.out.println("Usuário atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar usuário.");
            }
        } else {
            System.out.println("Usuário não encontrado.");
        }

        // 4. Testar Listagem
        List<Usuario> todosUsuarios = dao.listarTodosUsuarios();
        System.out.println("\nTodos os usuários:");
        for (Usuario u : todosUsuarios) {
            System.out.println("- " + u.getNome() + " (" + u.getEmail() + ") - " + u.getTipoPerfil());
        }

        // 5. Testar Exclusão (CUIDADO ao usar em dados importantes!)
        // int idParaExcluir = usuarioEncontrado != null ? usuarioEncontrado.getId() : -1;
        // if (idParaExcluir != -1 && dao.excluirUsuario(idParaExcluir)) {
        //     System.out.println("Usuário excluído com sucesso!");
        // } else {
        //     System.out.println("Falha ao excluir usuário ou ID não encontrado.");
        // }
    }
}

// Adicione isso temporariamente no final da classe UsuarioDAO
