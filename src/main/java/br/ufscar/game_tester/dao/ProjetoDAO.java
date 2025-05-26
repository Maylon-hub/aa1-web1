package br.ufscar.game_tester.dao;

import br.ufscar.game_tester.model.Projeto;
import br.ufscar.game_tester.model.Usuario; // Para carregar membros
import br.ufscar.game_tester.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    // Insere um projeto e seus membros iniciais (se houver)
    public Projeto insert(Projeto projeto, List<Integer> membrosIds) throws SQLException { // [cite: 13]
        String sqlProjeto = "INSERT INTO Projetos (nome_projeto, descricao, data_criacao) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmtProjeto = null;
        PreparedStatement pstmtMembro = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // Insere o Projeto
            pstmtProjeto = conn.prepareStatement(sqlProjeto, Statement.RETURN_GENERATED_KEYS);
            pstmtProjeto.setString(1, projeto.getNomeProjeto());
            pstmtProjeto.setString(2, projeto.getDescricao());
            pstmtProjeto.setTimestamp(3, new Timestamp(projeto.getDataCriacao().getTime())); // [cite: 13]
            pstmtProjeto.executeUpdate();

            try (ResultSet generatedKeys = pstmtProjeto.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    projeto.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao obter o ID do projeto.");
                }
            }

            // Insere os Membros Permitidos (se houver) [cite: 13]
            if (membrosIds != null && !membrosIds.isEmpty()) {
                String sqlMembro = "INSERT INTO MembrosDoProjeto (projeto_id, usuario_id) VALUES (?, ?)";
                pstmtMembro = conn.prepareStatement(sqlMembro);
                for (Integer membroId : membrosIds) {
                    pstmtMembro.setInt(1, projeto.getId());
                    pstmtMembro.setInt(2, membroId);
                    pstmtMembro.addBatch();
                }
                pstmtMembro.executeBatch();
            }

            conn.commit(); // Finaliza transação com sucesso
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz em caso de erro
                } catch (SQLException ex) {
                    // Log ex
                }
            }
            throw e; // Relança a exceção original
        } finally {
            if (pstmtProjeto != null) pstmtProjeto.close();
            if (pstmtMembro != null) pstmtMembro.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Restaura auto-commit
                conn.close();
            }
        }
        return projeto;
    }

    public Projeto getById(int id) throws SQLException {
        String sql = "SELECT id, nome_projeto, descricao, data_criacao FROM Projetos WHERE id = ?";
        Projeto projeto = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    projeto = mapResultSetToProjeto(rs);
                    // Carregar membros separadamente se necessário, ou criar um método getByIdWithMembros
                    projeto.setMembrosPermitidos(getMembrosByProjetoId(projeto.getId()));
                }
            }
        }
        return projeto;
    }

    public List<Projeto> getAll() throws SQLException { // [cite: 27]
        List<Projeto> projetos = new ArrayList<>();
        // A ordenação pode ser adicionada aqui ou na chamada do método [cite: 28]
        String sql = "SELECT id, nome_projeto, descricao, data_criacao FROM Projetos ORDER BY nome_projeto";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Projeto projeto = mapResultSetToProjeto(rs);
                // Opcional: Carregar membros para cada projeto, pode ser custoso
                // projeto.setMembrosPermitidos(getMembrosByProjetoId(projeto.getId()));
                projetos.add(projeto);
            }
        }
        return projetos;
    }

    public void update(Projeto projeto) throws SQLException { // [cite: 24]
        String sql = "UPDATE Projetos SET nome_projeto = ?, descricao = ? WHERE id = ?";
        // Atualizar membros é uma operação separada (add/remove)
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projeto.getNomeProjeto());
            pstmt.setString(2, projeto.getDescricao());
            pstmt.setInt(3, projeto.getId());
            pstmt.executeUpdate();
        }
    }

    public void addMembroToProjeto(int projetoId, int usuarioId) throws SQLException {
        String sql = "INSERT INTO MembrosDoProjeto (projeto_id, usuario_id) VALUES (?, ?) ON CONFLICT DO NOTHING"; // Evita erro se já for membro
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projetoId);
            pstmt.setInt(2, usuarioId);
            pstmt.executeUpdate();
        }
    }

    public void removeMembroFromProjeto(int projetoId, int usuarioId) throws SQLException {
        String sql = "DELETE FROM MembrosDoProjeto WHERE projeto_id = ? AND usuario_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projetoId);
            pstmt.setInt(2, usuarioId);
            pstmt.executeUpdate();
        }
    }

    public List<Usuario> getMembrosByProjetoId(int projetoId) throws SQLException {
        List<Usuario> membros = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.email, u.tipo_perfil " +
                "FROM Usuarios u JOIN MembrosDoProjeto mp ON u.id = mp.usuario_id " +
                "WHERE mp.projeto_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projetoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario membro = new Usuario();
                    membro.setId(rs.getInt("id"));
                    membro.setNome(rs.getString("nome"));
                    membro.setEmail(rs.getString("email"));
                    membro.setTipoPerfil(rs.getString("tipo_perfil"));
                    // Não carregar senha aqui
                    membros.add(membro);
                }
            }
        }
        return membros;
    }


    public void delete(int id) throws SQLException { // [cite: 24]
        String sql = "DELETE FROM Projetos WHERE id = ?";
        // ATENÇÃO: "evitar a remoção de elementos em uso" (MembrosDoProjeto, SessoesDeTeste) [cite: 33]
        // A constraint ON DELETE CASCADE para MembrosDoProjeto pode cuidar disso.
        // Para SessoesDeTeste, a constraint é RESTRICT, então uma exceção será lançada se houver sessões.
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Projeto mapResultSetToProjeto(ResultSet rs) throws SQLException {
        Projeto projeto = new Projeto();
        projeto.setId(rs.getInt("id"));
        projeto.setNomeProjeto(rs.getString("nome_projeto"));
        projeto.setDescricao(rs.getString("descricao"));
        projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
        return projeto;
    }
}