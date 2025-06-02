package com.gametester.dao;

import com.gametester.model.Usuario;
import com.gametester.util.ConexaoDB;
// Adicione esta importação se ainda não existir
import org.mindrot.jbcrypt.BCrypt; // Para hashing de senha (opcional nesta fase, mas recomendado)


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Método para verificar senha com hash (se você implementar hashing)
    // public boolean verificarSenha(String senhaPlana, String hashDaSenha) {
    //     return BCrypt.checkpw(senhaPlana, hashDaSenha);
    // }


    // Seu método inserirUsuario precisa ser capaz de lidar com o tipo de perfil
    // e idealmente faria o hash da senha.
    public Usuario inserirUsuario(Usuario usuario) throws SQLException {
        // Nota: O ideal é fazer o hash da senha ANTES de chegar no DAO, ou aqui dentro.
        // String senhaComHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        // Para simplificar por enquanto, vamos assumir que a senha já está pronta para o BD
        // ou que o hash será adicionado depois.
        // Se não estiver usando RETURNING para todos os campos, o objeto pode não ser totalmente atualizado aqui.

        String sql = "INSERT INTO Usuario (nome, email, senha, tipo_perfil) VALUES (?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha()); // Idealmente, aqui seria o hash da senha
            stmt.setString(4, usuario.getTipoPerfil());

            // Para PostgreSQL com RETURNING id:
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario.setId(rs.getInt("id"));
            } else {
                // Fallback para getGeneratedKeys se RETURNING não for usado ou falhar em popular o ID
                // (Pouco provável com RETURNING id e executeQuery)
                System.err.println("Não foi possível obter o ID do usuário via RETURNING. Verifique a query ou o driver.");
                // throw new SQLException("Falha ao inserir usuário, não foi possível obter o ID gerado.");
            }

        } catch (SQLException e) {
            // R33: tratar todos os erros possíveis (cadastro duplicado [email])
            if (e.getMessage().toLowerCase().contains("unique constraint") || e.getMessage().toLowerCase().contains("duplicate key")) {
                throw new SQLException("Erro ao inserir usuário: O e-mail '" + usuario.getEmail() + "' já está cadastrado.", e);
            }
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return usuario; // Retorna o usuário com o ID preenchido
    }

    public Usuario buscarUsuarioPorEmail(String email) throws SQLException {
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
                usuario.setSenha(rs.getString("senha")); // Lembre-se que esta é a senha do BD (hash, idealmente)
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    public Usuario buscarUsuarioPorId(int id) throws SQLException {
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
                usuario.setSenha(rs.getString("senha")); // Senha do BD
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return usuario;
    }

    public List<Usuario> listarTodosUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, tipo_perfil FROM Usuario ORDER BY nome ASC"; // Não buscamos a senha na listagem
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
                // Não setamos a senha aqui por segurança e porque não foi selecionada
                usuario.setTipoPerfil(rs.getString("tipo_perfil"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return usuarios;
    }

    public boolean atualizarUsuario(Usuario usuario) throws SQLException {
        // Decide se a senha será atualizada ou não.
        // Se usuario.getSenha() for nulo ou vazio, não atualizamos a senha.
        // Se uma nova senha for fornecida, ela deve ser hashada antes de ser salva.
        String sql;
        boolean atualizandoSenha = usuario.getSenha() != null && !usuario.getSenha().isEmpty();

        if (atualizandoSenha) {
            sql = "UPDATE Usuario SET nome = ?, email = ?, senha = ?, tipo_perfil = ? WHERE id = ?";
        } else {
            sql = "UPDATE Usuario SET nome = ?, email = ?, tipo_perfil = ? WHERE id = ?";
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());

            int paramIndex = 3;
            if (atualizandoSenha) {
                // String senhaComHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
                // stmt.setString(paramIndex++, senhaComHash);
                stmt.setString(paramIndex++, usuario.getSenha()); // Sem hash por enquanto
            }
            stmt.setString(paramIndex++, usuario.getTipoPerfil());
            stmt.setInt(paramIndex++, usuario.getId());

            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique constraint") || e.getMessage().toLowerCase().contains("duplicate key")) {
                throw new SQLException("Erro ao atualizar usuário: O e-mail '" + usuario.getEmail() + "' já está cadastrado para outro usuário.", e);
            }
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean excluirUsuario(int id) throws SQLException {
        // Cuidado: O administrador não deve poder excluir a si mesmo se for o último admin.
        // E um usuário não pode ser excluído se tiver dados relacionados (ex: sessões de teste como testador).
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
            if (e.getMessage().toLowerCase().contains("foreign key constraint") ||
                    e.getMessage().toLowerCase().contains("referential integrity")) {
                throw new SQLException("Não é possível excluir o usuário (ID: " + id + ") pois ele possui registros associados (ex: sessões de teste).", e);
            }
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { e_stmt.printStackTrace(); } }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }
}