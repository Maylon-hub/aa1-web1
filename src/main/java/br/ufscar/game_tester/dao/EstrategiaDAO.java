package br.ufscar.game_tester.dao;

import br.ufscar.game_tester.model.Estrategia;
import br.ufscar.game_tester.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaDAO {

    public Estrategia insert(Estrategia estrategia) throws SQLException { //
        String sql = "INSERT INTO Estrategias (nome, descricao, exemplos, dicas, imagem_path) VALUES (?, ?, ?, ?, ?)"; //
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, estrategia.getNome());
            pstmt.setString(2, estrategia.getDescricao());
            pstmt.setString(3, estrategia.getExemplos());
            pstmt.setString(4, estrategia.getDicas());
            pstmt.setString(5, estrategia.getImagemPath());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    estrategia.setId(generatedKeys.getInt(1));
                }
            }
        }
        return estrategia;
    }

    public Estrategia getById(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, exemplos, dicas, imagem_path FROM Estrategias WHERE id = ?";
        Estrategia estrategia = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    estrategia = mapResultSetToEstrategia(rs);
                }
            }
        }
        return estrategia;
    }

    public List<Estrategia> getAll() throws SQLException { //
        List<Estrategia> estrategias = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, exemplos, dicas, imagem_path FROM Estrategias ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                estrategias.add(mapResultSetToEstrategia(rs));
            }
        }
        return estrategias;
    }

    public void update(Estrategia estrategia) throws SQLException { //
        String sql = "UPDATE Estrategias SET nome = ?, descricao = ?, exemplos = ?, dicas = ?, imagem_path = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estrategia.getNome());
            pstmt.setString(2, estrategia.getDescricao());
            pstmt.setString(3, estrategia.getExemplos());
            pstmt.setString(4, estrategia.getDicas());
            pstmt.setString(5, estrategia.getImagemPath());
            pstmt.setInt(6, estrategia.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException { //
        String sql = "DELETE FROM Estrategias WHERE id = ?";
        // ATENÇÃO: "evitar a remoção de elementos em uso"
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Estrategia mapResultSetToEstrategia(ResultSet rs) throws SQLException {
        Estrategia estrategia = new Estrategia();
        estrategia.setId(rs.getInt("id"));
        estrategia.setNome(rs.getString("nome"));
        estrategia.setDescricao(rs.getString("descricao"));
        estrategia.setExemplos(rs.getString("exemplos"));
        estrategia.setDicas(rs.getString("dicas"));
        estrategia.setImagemPath(rs.getString("imagem_path"));
        return estrategia;
    }
}