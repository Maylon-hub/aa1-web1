package br.ufscar.game_tester.dao;

import br.ufscar.game_tester.model.Usuario;
import br.ufscar.game_tester.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario insert(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuarios (nome, email, senha, tipo_perfil) VALUES (?, ?, ?, ?)"; //

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha()); // Lembre-se: HASH da senha
            pstmt.setString(4, usuario.getTipoPerfil());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao obter o ID do usuário, nenhum ID obtido.");
                }
            }
        }
        return usuario;
    }

    public Usuario getByEmail(String email) throws SQLException { //
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuarios WHERE email = ?";
        Usuario usuario = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = mapResultSetToUsuario(rs);
                }
            }
        }
        return usuario;
    }

    public Usuario getById(int id) throws SQLException {
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuarios WHERE id = ?";
        Usuario usuario = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = mapResultSetToUsuario(rs);
                }
            }
        }
        return usuario;
    }

    public List<Usuario> getAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuarios ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    public List<Usuario> getAllByType(String tipoPerfil) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha, tipo_perfil FROM Usuarios WHERE tipo_perfil = ? ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipoPerfil);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        }
        return usuarios;
    }

    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuarios SET nome = ?, email = ?, senha = ?, tipo_perfil = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha()); // Lembre-se: HASH da senha
            pstmt.setString(4, usuario.getTipoPerfil());
            pstmt.setInt(5, usuario.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException { // (para R1 e R2)
        String sql = "DELETE FROM Usuarios WHERE id = ?";
        // ATENÇÃO: A lógica de "evitar a remoção de elementos em uso"
        // deve ser tratada na camada de serviço ou aqui, verificando dependências
        // ou tratando a SQLException de restrição de FK.
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha")); // Não ideal para carregar a senha assim, mas para consistência do objeto
        usuario.setTipoPerfil(rs.getString("tipo_perfil"));
        return usuario;
    }
}