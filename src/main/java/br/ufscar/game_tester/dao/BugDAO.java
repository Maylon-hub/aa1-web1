package br.ufscar.game_tester.dao;

import br.ufscar.game_tester.model.Bug;
import br.ufscar.game_tester.model.SessaoTeste; // Para popular o objeto SessaoTeste
import br.ufscar.game_tester.util.ConnectionFactory;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BugDAO {

    public Bug insert(Bug bug) throws SQLException { // Baseado na funcionalidade "o testador pode registrar bugs identificados" [cite: 17]
        String sql = "INSERT INTO Bugs (sessao_id, descricao_bug, data_registro_bug, severidade, status_bug, screenshot_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, bug.getSessaoTeste().getId());
            pstmt.setString(2, bug.getDescricaoBug());
            pstmt.setTimestamp(3, new Timestamp(bug.getDataRegistroBug().getTime()));
            pstmt.setString(4, bug.getSeveridade());
            pstmt.setString(5, bug.getStatusBug());
            pstmt.setString(6, bug.getScreenshotPath());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bug.setId(generatedKeys.getInt(1));
                }
            }
        }
        return bug;
    }

    public Bug getById(int id) throws SQLException {
        String sql = "SELECT * FROM Bugs WHERE id = ?";
        Bug bug = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bug = mapResultSetToBug(rs, conn); // Passando conn para carregar SessaoTeste
                }
            }
        }
        return bug;
    }

    public List<Bug> getBySessaoId(int sessaoId) throws SQLException {
        List<Bug> bugs = new ArrayList<>();
        String sql = "SELECT * FROM Bugs WHERE sessao_id = ? ORDER BY data_registro_bug DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessaoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Para evitar N+1 queries, não carregaremos o objeto SessaoTeste completo aqui
                    // a menos que seja estritamente necessário para a listagem.
                    // Se necessário, você pode fazer um JOIN ou carregar depois.
                    Bug bug = new Bug();
                    bug.setId(rs.getInt("id"));
                    // Criar um objeto SessaoTeste apenas com o ID para o bug, se não for fazer JOIN
                    SessaoTeste sessaoResumida = new SessaoTeste();
                    sessaoResumida.setId(rs.getInt("sessao_id"));
                    bug.setSessaoTeste(sessaoResumida);

                    bug.setDescricaoBug(rs.getString("descricao_bug"));
                    bug.setDataRegistroBug(rs.getTimestamp("data_registro_bug"));
                    bug.setSeveridade(rs.getString("severidade"));
                    bug.setStatusBug(rs.getString("status_bug"));
                    bug.setScreenshotPath(rs.getString("screenshot_path"));
                    bugs.add(bug);
                }
            }
        }
        return bugs;
    }

    public void update(Bug bug) throws SQLException {
        String sql = "UPDATE Bugs SET sessao_id = ?, descricao_bug = ?, data_registro_bug = ?, " +
                "severidade = ?, status_bug = ?, screenshot_path = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bug.getSessaoTeste().getId());
            pstmt.setString(2, bug.getDescricaoBug());
            pstmt.setTimestamp(3, new Timestamp(bug.getDataRegistroBug().getTime()));
            pstmt.setString(4, bug.getSeveridade());
            pstmt.setString(5, bug.getStatusBug());
            pstmt.setString(6, bug.getScreenshotPath());
            pstmt.setInt(7, bug.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Bugs WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Bug mapResultSetToBug(ResultSet rs, Connection connForSessaoLoad) throws SQLException {
        Bug bug = new Bug();
        bug.setId(rs.getInt("id"));
        bug.setDescricaoBug(rs.getString("descricao_bug"));
        bug.setDataRegistroBug(rs.getTimestamp("data_registro_bug"));
        bug.setSeveridade(rs.getString("severidade"));
        bug.setStatusBug(rs.getString("status_bug"));
        bug.setScreenshotPath(rs.getString("screenshot_path"));

        // Carregar o objeto SessaoTeste associado
        int sessaoId = rs.getInt("sessao_id");
        // Para evitar dependência cíclica direta, idealmente o SessaoTesteDAO seria injetado ou
        // uma versão resumida da SessaoTeste seria carregada.
        // Por simplicidade aqui, vamos assumir que o SessaoTesteDAO está acessível ou
        // que carregamos apenas o ID. Para um objeto completo, você precisaria de uma instância de SessaoTesteDAO.
        // Exemplo simplificado:
        SessaoTeste sessao = new SessaoTesteDAO().getById(sessaoId); // Isso pode ser problemático (nova conexão, etc)
        // Melhor seria passar SessaoTesteDAO como dependência.
        // Ou, como feito em getBySessaoId, apenas setar um objeto com ID.
        if (sessao == null) { // Criar um objeto parcial se não encontrado, para não quebrar o Bug.
            sessao = new SessaoTeste();
            sessao.setId(sessaoId);
        }
        bug.setSessaoTeste(sessao);
        return bug;
    }
}