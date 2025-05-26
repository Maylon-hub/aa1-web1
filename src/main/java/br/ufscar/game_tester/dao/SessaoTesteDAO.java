package br.ufscar.game_tester.dao;

import br.ufscar.game_tester.model.SessaoTeste;
import br.ufscar.game_tester.model.Projeto; // Para popular o objeto Projeto
import br.ufscar.game_tester.model.Usuario; // Para popular o objeto Usuario (testador)
import br.ufscar.game_tester.model.Estrategia; // Para popular o objeto Estrategia
import br.ufscar.game_tester.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessaoTesteDAO {

    public SessaoTeste insert(SessaoTeste sessaoTeste) throws SQLException { // [cite: 14]
        String sql = "INSERT INTO SessoesDeTeste " +
                "(projeto_id, testador_id, estrategia_id, tempo_sessao_minutos, descricao_sessao, status, data_criacao_sessao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"; // [cite: 14, 15]

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, sessaoTeste.getProjeto().getId());
            pstmt.setInt(2, sessaoTeste.getTestador().getId());
            pstmt.setInt(3, sessaoTeste.getEstrategiaUtilizada().getId());
            pstmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            pstmt.setString(5, sessaoTeste.getDescricaoSessao());
            pstmt.setString(6, sessaoTeste.getStatus()); // ex: "CRIADO" [cite: 15]
            pstmt.setTimestamp(7, new Timestamp(sessaoTeste.getDataCriacaoSessao().getTime())); // [cite: 19]

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sessaoTeste.setId(generatedKeys.getInt(1)); // ID da sessão gerado pelo sistema [cite: 14]
                } else {
                    throw new SQLException("Falha ao obter o ID da sessão de teste.");
                }
            }
        }
        return sessaoTeste;
    }

    public SessaoTeste getById(int id) throws SQLException {
        String sql = "SELECT st.*, p.nome_projeto, u.nome as nome_testador, e.nome as nome_estrategia " +
                "FROM SessoesDeTeste st " +
                "JOIN Projetos p ON st.projeto_id = p.id " +
                "JOIN Usuarios u ON st.testador_id = u.id " +
                "JOIN Estrategias e ON st.estrategia_id = e.id " +
                "WHERE st.id = ?";
        SessaoTeste sessaoTeste = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    sessaoTeste = mapResultSetToSessaoTeste(rs);
                }
            }
        }
        return sessaoTeste;
    }

    public List<SessaoTeste> getByProjetoId(int projetoId) throws SQLException { // [cite: 31]
        List<SessaoTeste> sessoes = new ArrayList<>();
        String sql = "SELECT st.*, p.nome_projeto, u.nome as nome_testador, e.nome as nome_estrategia " +
                "FROM SessoesDeTeste st " +
                "JOIN Projetos p ON st.projeto_id = p.id " +
                "JOIN Usuarios u ON st.testador_id = u.id " +
                "JOIN Estrategias e ON st.estrategia_id = e.id " +
                "WHERE st.projeto_id = ? ORDER BY st.data_criacao_sessao DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projetoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessoes.add(mapResultSetToSessaoTeste(rs));
                }
            }
        }
        return sessoes;
    }

    public void update(SessaoTeste sessaoTeste) throws SQLException { // [cite: 24]
        String sql = "UPDATE SessoesDeTeste SET " +
                "projeto_id = ?, testador_id = ?, estrategia_id = ?, " +
                "tempo_sessao_minutos = ?, descricao_sessao = ?, status = ?, " +
                "data_criacao_sessao = ?, data_inicio_execucao = ?, data_finalizacao = ? " +
                "WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessaoTeste.getProjeto().getId());
            pstmt.setInt(2, sessaoTeste.getTestador().getId());
            pstmt.setInt(3, sessaoTeste.getEstrategiaUtilizada().getId());
            pstmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            pstmt.setString(5, sessaoTeste.getDescricaoSessao());
            pstmt.setString(6, sessaoTeste.getStatus());
            pstmt.setTimestamp(7, new Timestamp(sessaoTeste.getDataCriacaoSessao().getTime()));
            pstmt.setTimestamp(8, sessaoTeste.getDataInicioExecucao() != null ? new Timestamp(sessaoTeste.getDataInicioExecucao().getTime()) : null); // [cite: 16, 19]
            pstmt.setTimestamp(9, sessaoTeste.getDataFinalizacao() != null ? new Timestamp(sessaoTeste.getDataFinalizacao().getTime()) : null); // [cite: 17, 19]
            pstmt.setInt(10, sessaoTeste.getId());
            pstmt.executeUpdate();
        }
    }

    // Método específico para atualizar status e timestamps relacionados ao ciclo de vida [cite: 30]
    public void updateStatus(int sessaoId, String novoStatus, Date timestamp) throws SQLException { // [cite: 16, 17, 19]
        String campoData;
        switch (novoStatus) {
            case "EM_EXECUCAO": // [cite: 16]
                campoData = "data_inicio_execucao";
                break;
            case "FINALIZADO": // [cite: 17]
                campoData = "data_finalizacao";
                break;
            default: // Para "CRIADO" ou outros, não atualiza timestamp específico de início/fim aqui.
                campoData = null;
        }

        String sql = "UPDATE SessoesDeTeste SET status = ?";
        if (campoData != null) {
            sql += ", " + campoData + " = ?";
        }
        sql += " WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            pstmt.setString(paramIndex++, novoStatus);
            if (campoData != null) {
                pstmt.setTimestamp(paramIndex++, new Timestamp(timestamp.getTime()));
            }
            pstmt.setInt(paramIndex, sessaoId);
            pstmt.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException { // [cite: 24]
        String sql = "DELETE FROM SessoesDeTeste WHERE id = ?";
        // ATENÇÃO: "evitar a remoção de elementos em uso" (Bugs) [cite: 33]
        // Se a tabela Bugs tiver FK para SessoesDeTeste com ON DELETE CASCADE, ok. Senão, tratar.
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private SessaoTeste mapResultSetToSessaoTeste(ResultSet rs) throws SQLException {
        SessaoTeste st = new SessaoTeste();
        st.setId(rs.getInt("id"));
        st.setTempoSessaoMinutos(rs.getInt("tempo_sessao_minutos"));
        st.setDescricaoSessao(rs.getString("descricao_sessao"));
        st.setStatus(rs.getString("status"));
        st.setDataCriacaoSessao(rs.getTimestamp("data_criacao_sessao"));
        st.setDataInicioExecucao(rs.getTimestamp("data_inicio_execucao"));
        st.setDataFinalizacao(rs.getTimestamp("data_finalizacao"));

        // Popular objetos aninhados
        Projeto projeto = new Projeto();
        projeto.setId(rs.getInt("projeto_id"));
        projeto.setNomeProjeto(rs.getString("nome_projeto")); // Assumindo que o JOIN trouxe esse campo
        st.setProjeto(projeto);

        Usuario testador = new Usuario();
        testador.setId(rs.getInt("testador_id"));
        testador.setNome(rs.getString("nome_testador")); // Assumindo que o JOIN trouxe esse campo
        st.setTestador(testador);

        Estrategia estrategia = new Estrategia();
        estrategia.setId(rs.getInt("estrategia_id"));
        estrategia.setNome(rs.getString("nome_estrategia")); // Assumindo que o JOIN trouxe esse campo
        st.setEstrategiaUtilizada(estrategia);

        // Bugs seriam carregados em uma chamada separada, ex: bugDAO.getBySessaoId(st.getId())
        return st;
    }
}