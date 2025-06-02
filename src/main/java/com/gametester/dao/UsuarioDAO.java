package com.gametester.dao;

import com.gametester.model.Usuario;
import com.gametester.util.ConexaoDB;
// import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Insere um novo usuário no banco de dados.
     * A senha fornecida no objeto Usuario já deve estar "hasheada" (ex: com BCrypt).
     * O ID gerado é atualizado no objeto Usuario passado como argumento.
     *
     * @param usuario O objeto Usuario a ser inserido com a senha já hasheada.
     * @return O objeto Usuario atualizado com o ID definido pelo banco.
     * @throws SQLException Se ocorrer um erro de banco de dados, incluindo e-mail duplicado.
     */
    public Usuario inserirUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario (nome, email, senha, tipo_perfil) VALUES (?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail().toLowerCase()); // Armazena email em minúsculas
            stmt.setString(3, usuario.getSenha()); // Senha com HASH
            stmt.setString(4, usuario.getTipoPerfil());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    usuario.setId(rs.getInt(1));
                } else {
                    System.err.println("DAO: Inserção de Usuario afetou " + rowsAffected + " linha(s), mas não foi possível obter o ID gerado.");
                    throw new SQLException("Falha ao inserir usuário: ID não pôde ser recuperado após a inserção.");
                }
            } else {
                System.err.println("DAO: Nenhuma linha afetada pela inserção do usuário: " + usuario.getEmail());
                throw new SQLException("Falha ao inserir usuário: Nenhuma linha foi afetada no banco de dados.");
            }
        } catch (SQLException e) {
            // Código de erro padrão do PostgreSQL para unique_violation é 23505
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                throw new SQLException("Erro ao inserir usuário: O e-mail '" + usuario.getEmail() + "' já está cadastrado.", e.getSQLState(), e);
            }
            System.err.println("DAO: SQLException ao inserir usuário " + usuario.getEmail() + " - Mensagem: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e_stmt) {
                    System.err.println("DAO: Erro ao fechar PreparedStatement em inserirUsuario: " + e_stmt.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    public Usuario buscarUsuarioPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuario WHERE lower(email) = lower(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.toLowerCase());
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha")); // Esta é a senha com HASH do banco
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email ("+ email +"): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em buscarUsuarioPorEmail: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    public Usuario buscarUsuarioPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email, tipo_perfil FROM Usuario WHERE id = ?";
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
                // A senha não é carregada aqui intencionalmente
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID ("+id+"): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em buscarUsuarioPorId: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    public List<Usuario> listarTodosUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, tipo_perfil FROM Usuario ORDER BY nome ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarTodosUsuarios: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return usuarios;
    }

    public boolean atualizarUsuario(Usuario usuario) throws SQLException {
        String sql;

        boolean atualizandoSenha = usuario.getSenha() != null && !usuario.getSenha().isEmpty();

        if (atualizandoSenha) {
            sql = "UPDATE Usuario SET nome = ?, email = lower(?), senha = ?, tipo_perfil = ? WHERE id = ?";
        } else {
            sql = "UPDATE Usuario SET nome = ?, email = lower(?), tipo_perfil = ? WHERE id = ?";
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail().toLowerCase());

            int paramIndex = 3;
            if (atualizandoSenha) {
                stmt.setString(paramIndex++, usuario.getSenha());
            }
            stmt.setString(paramIndex++, usuario.getTipoPerfil());
            stmt.setInt(paramIndex++, usuario.getId());

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) { // Email duplicado
                throw new SQLException("Erro ao atualizar usuário: O e-mail '" + usuario.getEmail() + "' já está cadastrado para outro usuário.", e.getSQLState(), e);
            }
            System.err.println("Erro ao atualizar usuário ID " + usuario.getId() + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em atualizarUsuario: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    /**
     * Atualiza apenas a senha de um usuário específico.
     * @param usuarioId O ID do usuário.
     * @param novaSenhaComHash A nova senha já processada com hash.
     * @return true se a senha foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean atualizarSenhaUsuario(int usuarioId, String novaSenhaComHash) throws SQLException {
        String sql = "UPDATE Usuario SET senha = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, novaSenhaComHash);
            stmt.setInt(2, usuarioId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha para o usuário ID " + usuarioId + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar PreparedStatement em atualizarSenhaUsuario: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean excluirUsuario(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            // Código de erro para foreign_key_violation no PostgreSQL é 23503
            if (e.getSQLState() != null && e.getSQLState().equals("23503")) {
                throw new SQLException("Não é possível excluir o usuário (ID: " + id + ") pois ele possui registros associados (ex: sessões de teste).", e.getSQLState(), e);
            }
            System.err.println("Erro ao excluir usuário ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em excluirUsuario: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }


}