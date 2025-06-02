package com.gametester.dao;

import com.gametester.model.Projeto;
import com.gametester.model.Usuario;
import com.gametester.util.ConexaoDB;
// import org.mindrot.jbcrypt.BCrypt; // Descomente se usar no main para testes

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    public Projeto inserirProjeto(Projeto projeto) throws SQLException {
        String sql = "INSERT INTO Projeto (nome, descricao, data_criacao) VALUES (?, ?, ?) RETURNING id, data_criacao";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());

            Timestamp dataCriacaoAtual = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, dataCriacaoAtual);
            projeto.setDataCriacao(dataCriacaoAtual);

            rs = stmt.executeQuery();

            if (rs.next()) {
                projeto.setId(rs.getInt("id"));
                projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
            } else {
                throw new SQLException("Falha ao inserir projeto, não foi possível obter o ID ou data de criação gerados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir projeto: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e_stmt) {
                    System.err.println("DAO: Erro ao fechar PreparedStatement em inserirProjeto: " + e_stmt.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return projeto;
    }

    public Projeto buscarProjetoPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, data_criacao FROM Projeto WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Projeto projeto = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar projeto por ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar PreparedStatement em buscarProjetoPorId: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return projeto;
    }

    public List<Projeto> listarTodosProjetos(String campoOrdenacao, String direcaoOrdenacao) throws SQLException {
        List<Projeto> projetos = new ArrayList<>();

        String campoValido;
        switch (campoOrdenacao != null ? campoOrdenacao.toLowerCase() : "nome") {
            case "data_criacao":
                campoValido = "data_criacao";
                break;
            case "id":
                campoValido = "id";
                break;
            case "nome":
            default:
                campoValido = "nome";
                break;
        }

        String direcaoValida;
        switch (direcaoOrdenacao != null ? direcaoOrdenacao.toLowerCase() : "asc") {
            case "desc":
                direcaoValida = "DESC";
                break;
            case "asc":
            default:
                direcaoValida = "ASC";
                break;
        }

        String sql = "SELECT id, nome, descricao, data_criacao FROM Projeto ORDER BY " + campoValido + " " + direcaoValida + ", id " + direcaoValida;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
                projetos.add(projeto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar projetos ordenados: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarTodosProjetos (ordenado): " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return projetos;
    }

    public List<Projeto> listarTodosProjetos() throws SQLException {
        return listarTodosProjetos("nome", "ASC");
    }

    public boolean atualizarProjeto(Projeto projeto) throws SQLException {
        String sql = "UPDATE Projeto SET nome = ?, descricao = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setInt(3, projeto.getId());
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar projeto com ID " + projeto.getId() + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar PreparedStatement em atualizarProjeto: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean excluirProjeto(int id) throws SQLException {
        String sql = "DELETE FROM Projeto WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23503")) {
                throw new SQLException("Não é possível excluir o projeto (ID: " + id + ") pois ele possui registros associados (ex: sessões de teste ou membros).", e.getSQLState(), e);
            }
            System.err.println("Erro ao excluir projeto com ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e_stmt) {
                    System.err.println("DAO: Erro ao fechar PreparedStatement ao excluir projeto: " + e_stmt.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean adicionarMembroAoProjeto(int projetoId, int usuarioId) throws SQLException {
        String sql = "INSERT INTO projeto_membro (projeto_id, usuario_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projetoId);
            stmt.setInt(2, usuarioId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                System.err.println("Membro (ID: " + usuarioId + ") já existe no projeto (ID: " + projetoId + ").");
                return false;
            } else if (e.getSQLState() != null && e.getSQLState().equals("23503")) {
                System.err.println("Erro de chave estrangeira ao adicionar membro: Projeto (ID: " + projetoId + ") ou Usuário (ID: " + usuarioId + ") não existe. " + e.getMessage());
                throw new SQLException("Erro ao adicionar membro: Projeto ou Usuário especificado não existe.", e.getSQLState(), e);
            }
            System.err.println("Erro ao adicionar membro ao projeto: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em adicionarMembro: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean removerMembroDoProjeto(int projetoId, int usuarioId) throws SQLException {
        String sql = "DELETE FROM projeto_membro WHERE projeto_id = ? AND usuario_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projetoId);
            stmt.setInt(2, usuarioId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao remover membro (ID: " + usuarioId + ") do projeto (ID: " + projetoId + "): " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em removerMembro: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public List<Usuario> listarMembrosDoProjeto(int projetoId) throws SQLException {
        List<Usuario> membros = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.email, u.tipo_perfil " +
                "FROM Usuario u JOIN projeto_membro pm ON u.id = pm.usuario_id " +
                "WHERE pm.projeto_id = ? ORDER BY u.nome ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, projetoId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario membro = new Usuario();
                membro.setId(rs.getInt("id"));
                membro.setNome(rs.getString("nome"));
                membro.setEmail(rs.getString("email"));
                membro.setTipoPerfil(rs.getString("tipo_perfil"));
                membros.add(membro);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar membros do projeto ID " + projetoId + ": " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarMembros: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return membros;
    }

    public List<Projeto> listarProjetosPorMembro(int usuarioId, String campoOrdenacao, String direcaoOrdenacao) throws SQLException {
        List<Projeto> projetos = new ArrayList<>();

        String campoValido;
        switch (campoOrdenacao != null ? campoOrdenacao.toLowerCase() : "nome") {
            case "data_criacao": campoValido = "p.data_criacao"; break;
            case "id": campoValido = "p.id"; break;
            case "nome": default: campoValido = "p.nome"; break;
        }

        String direcaoValida;
        switch (direcaoOrdenacao != null ? direcaoOrdenacao.toLowerCase() : "asc") {
            case "desc": direcaoValida = "DESC"; break;
            case "asc": default: direcaoValida = "ASC"; break;
        }

        String sql = "SELECT p.id, p.nome, p.descricao, p.data_criacao " +
                "FROM Projeto p JOIN projeto_membro pm ON p.id = pm.projeto_id " +
                "WHERE pm.usuario_id = ? ORDER BY " + campoValido + " " + direcaoValida + ", p.id " + direcaoValida;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setDescricao(rs.getString("descricao"));
                projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
                projetos.add(projeto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar projetos (ordenados) para o membro ID " + usuarioId + ": " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarProjetosPorMembro (ordenado): " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return projetos;
    }

    public List<Projeto> listarProjetosPorMembro(int usuarioId) throws SQLException {
        return listarProjetosPorMembro(usuarioId, "nome", "ASC");
    }
}