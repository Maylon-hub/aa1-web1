package com.gametester.dao;

import com.gametester.model.Estrategia;
import com.gametester.model.Projeto;
import com.gametester.model.SessaoTeste;
import com.gametester.model.Usuario;
import com.gametester.util.ConexaoDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessaoTesteDAO {

    public SessaoTeste inserirSessaoTeste(SessaoTeste sessaoTeste) throws SQLException {
        String sql = "INSERT INTO SessaoTeste (projeto_id, testador_id, estrategia_id, tempo_sessao_minutos, " +
                "descricao, status, data_hora_criacao) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, sessaoTeste.getProjetoId());
            stmt.setInt(2, sessaoTeste.getTestadorId());
            stmt.setInt(3, sessaoTeste.getEstrategiaId());
            stmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            stmt.setString(5, sessaoTeste.getDescricao());
            stmt.setString(6, "CRIADO"); // R15: status "criado" [cite: 15]

            Timestamp dataCriacao = sessaoTeste.getDataHoraCriacao() != null ? sessaoTeste.getDataHoraCriacao() : new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(7, dataCriacao);
            sessaoTeste.setDataHoraCriacao(dataCriacao);
            sessaoTeste.setStatus("CRIADO");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    sessaoTeste.setId(rs.getInt(1));
                } else {
                    System.err.println("DAO: Inserção de SessaoTeste afetou " + rowsAffected + " linha(s), mas não foi possível obter o ID gerado.");
                    throw new SQLException("Falha ao inserir SessaoTeste: ID não pôde ser recuperado.");
                }
            } else {
                System.err.println("DAO: Nenhuma linha afetada pela inserção da SessaoTeste para o projeto ID: " + sessaoTeste.getProjetoId());
                throw new SQLException("Falha ao inserir SessaoTeste: Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir sessão de teste: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em inserirSessaoTeste: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return sessaoTeste;
    }

    public SessaoTeste buscarSessaoTestePorId(int id) throws SQLException {
        String sql = "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, p.descricao AS projeto_descricao_alias, p.data_criacao AS projeto_data_criacao_alias, " + // Renomeado alias para evitar conflito de nomes se 'descricao' ou 'data_criacao' existirem em SessaoTeste
                "u.nome AS testador_nome, u.email AS testador_email, u.tipo_perfil AS testador_tipo_perfil_alias, " + // Renomeado alias
                "e.nome AS estrategia_nome, e.descricao AS estrategia_descricao_alias, e.exemplos AS estrategia_exemplos, e.dicas AS estrategia_dicas " + // Renomeado alias
                "FROM SessaoTeste st " +
                "JOIN Projeto p ON st.projeto_id = p.id " +
                "JOIN Usuario u ON st.testador_id = u.id " +
                "JOIN Estrategia e ON st.estrategia_id = e.id " +
                "WHERE st.id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        SessaoTeste sessaoTeste = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                sessaoTeste = new SessaoTeste();
                sessaoTeste.setId(rs.getInt("id"));
                sessaoTeste.setProjetoId(rs.getInt("projeto_id"));
                sessaoTeste.setTestadorId(rs.getInt("testador_id"));
                sessaoTeste.setEstrategiaId(rs.getInt("estrategia_id"));
                sessaoTeste.setTempoSessaoMinutos(rs.getInt("tempo_sessao_minutos"));
                sessaoTeste.setDescricao(rs.getString("descricao"));
                sessaoTeste.setStatus(rs.getString("status"));
                sessaoTeste.setDataHoraCriacao(rs.getTimestamp("data_hora_criacao"));
                sessaoTeste.setDataHoraInicio(rs.getTimestamp("data_hora_inicio"));
                sessaoTeste.setDataHoraFim(rs.getTimestamp("data_hora_fim"));

                Projeto projeto = new Projeto(rs.getInt("projeto_id"), rs.getString("projeto_nome"), rs.getString("projeto_descricao_alias"), rs.getTimestamp("projeto_data_criacao_alias"));
                sessaoTeste.setProjeto(projeto);

                Usuario testador = new Usuario(rs.getInt("testador_id"), rs.getString("testador_nome"), rs.getString("testador_email"), null, rs.getString("testador_tipo_perfil_alias"));
                sessaoTeste.setTestador(testador);

                Estrategia estrategia = new Estrategia(rs.getInt("estrategia_id"), rs.getString("estrategia_nome"), rs.getString("estrategia_descricao_alias"), rs.getString("estrategia_exemplos"), rs.getString("estrategia_dicas"));
                sessaoTeste.setEstrategia(estrategia);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessão de teste por ID ("+id+"): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em buscarSessaoTestePorId: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return sessaoTeste;
    }

    public boolean atualizarSessaoTeste(SessaoTeste sessaoTeste) throws SQLException {
        String sql = "UPDATE SessaoTeste SET projeto_id = ?, testador_id = ?, estrategia_id = ?, " +
                "tempo_sessao_minutos = ?, descricao = ?, status = ?, " +
                "data_hora_inicio = ?, data_hora_fim = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sessaoTeste.getProjetoId());
            stmt.setInt(2, sessaoTeste.getTestadorId());
            stmt.setInt(3, sessaoTeste.getEstrategiaId());
            stmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            stmt.setString(5, sessaoTeste.getDescricao());
            stmt.setString(6, sessaoTeste.getStatus());
            stmt.setTimestamp(7, sessaoTeste.getDataHoraInicio());
            stmt.setTimestamp(8, sessaoTeste.getDataHoraFim());
            stmt.setInt(9, sessaoTeste.getId());
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar sessão de teste ID (" + sessaoTeste.getId() + "): " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em atualizarSessaoTeste: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean iniciarSessao(int sessaoId) throws SQLException {
        String sql = "UPDATE SessaoTeste SET status = 'EM_EXECUCAO', data_hora_inicio = ? WHERE id = ? AND status = 'CRIADO'"; // R16, R19 [cite: 16, 19]
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, sessaoId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao iniciar sessão ID (" + sessaoId + "): " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em iniciarSessao: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean finalizarSessao(int sessaoId) throws SQLException {
        String sql = "UPDATE SessaoTeste SET status = 'FINALIZADO', data_hora_fim = ? WHERE id = ? AND status = 'EM_EXECUCAO'"; // R17, R19 [cite: 17, 19]
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, sessaoId);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao finalizar sessão ID (" + sessaoId + "): " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em finalizarSessao: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean excluirSessaoTeste(int id) throws SQLException {
        String sql = "DELETE FROM SessaoTeste WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23503")) { // Foreign key violation no PostgreSQL
                throw new SQLException("Não é possível excluir a sessão de teste (ID: " + id + ") pois ela possui bugs associados ou outros registros dependentes.", e.getSQLState(), e);
            }
            System.err.println("Erro ao excluir sessão de teste ID (" + id + "): " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em excluirSessaoTeste: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public List<SessaoTeste> listarSessoesPorTestador(int testadorId) throws SQLException {
        List<SessaoTeste> sessoes = new ArrayList<>();
        String sql = "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, e.nome AS estrategia_nome " +
                "FROM SessaoTeste st " +
                "JOIN Projeto p ON st.projeto_id = p.id " +
                "JOIN Estrategia e ON st.estrategia_id = e.id " +
                "WHERE st.testador_id = ? ORDER BY st.data_hora_criacao DESC, st.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, testadorId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                SessaoTeste sessaoTeste = new SessaoTeste();
                sessaoTeste.setId(rs.getInt("id"));
                sessaoTeste.setProjetoId(rs.getInt("projeto_id"));
                sessaoTeste.setTestadorId(rs.getInt("testador_id"));
                sessaoTeste.setEstrategiaId(rs.getInt("estrategia_id"));
                sessaoTeste.setTempoSessaoMinutos(rs.getInt("tempo_sessao_minutos"));
                sessaoTeste.setDescricao(rs.getString("descricao"));
                sessaoTeste.setStatus(rs.getString("status"));
                sessaoTeste.setDataHoraCriacao(rs.getTimestamp("data_hora_criacao"));
                sessaoTeste.setDataHoraInicio(rs.getTimestamp("data_hora_inicio"));
                sessaoTeste.setDataHoraFim(rs.getTimestamp("data_hora_fim"));

                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("projeto_id"));
                projeto.setNome(rs.getString("projeto_nome"));
                sessaoTeste.setProjeto(projeto);

                Estrategia estrategia = new Estrategia();
                estrategia.setId(rs.getInt("estrategia_id"));
                estrategia.setNome(rs.getString("estrategia_nome"));
                sessaoTeste.setEstrategia(estrategia);
                sessoes.add(sessaoTeste);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar sessões por testador (ID: " + testadorId + "): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarSessoesPorTestador: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }


    public List<SessaoTeste> listarSessoesPorProjeto(int projetoId, String statusFiltro, String ordenacao) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                        "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                        "p.nome AS projeto_nome, u.nome AS testador_nome, e.nome AS estrategia_nome " + // Adicionado u.nome
                        "FROM SessaoTeste st " +
                        "JOIN Projeto p ON st.projeto_id = p.id " +
                        "JOIN Usuario u ON st.testador_id = u.id " + // Adicionado JOIN com Usuario
                        "JOIN Estrategia e ON st.estrategia_id = e.id " +
                        "WHERE st.projeto_id = ?"
        );

        if (statusFiltro != null && !statusFiltro.trim().isEmpty() && !statusFiltro.equalsIgnoreCase("TODOS")) {
            sqlBuilder.append(" AND st.status = ?");
        }

        sqlBuilder.append(" ORDER BY ");
        String ordenacaoUpper = (ordenacao != null && !ordenacao.trim().isEmpty()) ? ordenacao.trim().toUpperCase() : "DATA_CRIACAO_DESC";

        switch (ordenacaoUpper) {
            case "TEMPO_ASC": sqlBuilder.append("st.tempo_sessao_minutos ASC, st.id DESC"); break;
            case "TEMPO_DESC": sqlBuilder.append("st.tempo_sessao_minutos DESC, st.id DESC"); break;
            case "STATUS_ASC": sqlBuilder.append("st.status ASC, st.id DESC"); break;
            case "STATUS_DESC": sqlBuilder.append("st.status DESC, st.id DESC"); break;
            case "DATA_CRIACAO_ASC": sqlBuilder.append("st.data_hora_criacao ASC, st.id DESC"); break;
            default: sqlBuilder.append("st.data_hora_criacao DESC, st.id DESC"); break;
        }

        List<SessaoTeste> sessoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sqlBuilder.toString());
            stmt.setInt(1, projetoId);

            if (statusFiltro != null && !statusFiltro.trim().isEmpty() && !statusFiltro.equalsIgnoreCase("TODOS")) {
                stmt.setString(2, statusFiltro.toUpperCase());
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                SessaoTeste sessaoTeste = new SessaoTeste();
                sessaoTeste.setId(rs.getInt("id"));
                sessaoTeste.setProjetoId(rs.getInt("projeto_id"));
                sessaoTeste.setTestadorId(rs.getInt("testador_id"));
                sessaoTeste.setEstrategiaId(rs.getInt("estrategia_id"));
                sessaoTeste.setTempoSessaoMinutos(rs.getInt("tempo_sessao_minutos"));
                sessaoTeste.setDescricao(rs.getString("descricao"));
                sessaoTeste.setStatus(rs.getString("status"));
                sessaoTeste.setDataHoraCriacao(rs.getTimestamp("data_hora_criacao"));
                sessaoTeste.setDataHoraInicio(rs.getTimestamp("data_hora_inicio"));
                sessaoTeste.setDataHoraFim(rs.getTimestamp("data_hora_fim"));

                sessaoTeste.setProjeto(new Projeto(sessaoTeste.getProjetoId(), rs.getString("projeto_nome"), null, null));
                // Adicionado o objeto Testador (Usuario)
                sessaoTeste.setTestador(new Usuario(sessaoTeste.getTestadorId(), rs.getString("testador_nome"), null, null, null));
                sessaoTeste.setEstrategia(new Estrategia(sessaoTeste.getEstrategiaId(), rs.getString("estrategia_nome"), null, null, null));
                sessoes.add(sessaoTeste);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar sessões por projeto ("+projetoId+"): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarSessoesPorProjeto: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }

    public List<SessaoTeste> listarTodasSessoes() throws SQLException {
        String sql = "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, u.nome AS testador_nome, e.nome AS estrategia_nome " +
                "FROM SessaoTeste st " +
                "JOIN Projeto p ON st.projeto_id = p.id " +
                "JOIN Usuario u ON st.testador_id = u.id " +
                "JOIN Estrategia e ON st.estrategia_id = e.id ORDER BY st.data_hora_criacao DESC, st.id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SessaoTeste> sessoes = new ArrayList<>();
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                SessaoTeste sessaoTeste = new SessaoTeste();
                sessaoTeste.setId(rs.getInt("id"));
                sessaoTeste.setProjetoId(rs.getInt("projeto_id"));
                sessaoTeste.setTestadorId(rs.getInt("testador_id"));
                sessaoTeste.setEstrategiaId(rs.getInt("estrategia_id"));
                sessaoTeste.setTempoSessaoMinutos(rs.getInt("tempo_sessao_minutos"));
                sessaoTeste.setDescricao(rs.getString("descricao"));
                sessaoTeste.setStatus(rs.getString("status"));
                sessaoTeste.setDataHoraCriacao(rs.getTimestamp("data_hora_criacao"));
                sessaoTeste.setDataHoraInicio(rs.getTimestamp("data_hora_inicio"));
                sessaoTeste.setDataHoraFim(rs.getTimestamp("data_hora_fim"));

                sessaoTeste.setProjeto(new Projeto(sessaoTeste.getProjetoId(), rs.getString("projeto_nome"), null, null));
                sessaoTeste.setTestador(new Usuario(sessaoTeste.getTestadorId(), rs.getString("testador_nome"), null, null, null));
                sessaoTeste.setEstrategia(new Estrategia(sessaoTeste.getEstrategiaId(), rs.getString("estrategia_nome"), null, null, null));
                sessoes.add(sessaoTeste);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as sessões: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e_stmt) { System.err.println("DAO: Erro ao fechar stmt em listarTodasSessoes: " + e_stmt.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }

    // Main para testes (ajustado e simplificado)
    public static void main(String[] args) {
        System.out.println("--- Iniciando Testes para SessaoTesteDAO ---");
        SessaoTesteDAO sessaoDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        Projeto projetoTeste = null;
        Usuario testadorSessao = null;
        Estrategia estrategiaSessao = null;
        SessaoTeste sessaoTesteObj = null;

        try {
            // Setup: Criar um projeto, um testador e uma estratégia
            System.out.println("Criando dados de pré-requisito...");
            projetoTeste = new Projeto(0, "Projeto Teste Main SessaoDAO", "Descrição", null);
            projetoDAO.inserirProjeto(projetoTeste); // Assume que ID e data são setados no objeto

            testadorSessao = new Usuario(0, "Testador Main SessaoDAO", "testadorsessao@example.com", BCrypt.hashpw("senha123", BCrypt.gensalt()), "TESTADOR");
            usuarioDAO.inserirUsuario(testadorSessao); // Assume que ID é setado

            estrategiaSessao = new Estrategia(0, "Estratégia Teste Main SessaoDAO", "Descrição", "Exemplos", "Dicas");
            estrategiaDAO.inserirEstrategia(estrategiaSessao); // Assume que ID é setado

            if (projetoTeste.getId() == 0 || testadorSessao.getId() == 0 || estrategiaSessao.getId() == 0) {
                throw new SQLException("Falha ao criar pré-requisitos (IDs não gerados).");
            }
            System.out.println("Pré-requisitos criados: ProjetoID=" + projetoTeste.getId() + ", TestadorID=" + testadorSessao.getId() + ", EstrategiaID=" + estrategiaSessao.getId());

            // 1. Testar Inserir Sessão
            System.out.println("\nTestando inserirSessaoTeste...");
            sessaoTesteObj = new SessaoTeste();
            sessaoTesteObj.setProjetoId(projetoTeste.getId());
            sessaoTesteObj.setTestadorId(testadorSessao.getId());
            sessaoTesteObj.setEstrategiaId(estrategiaSessao.getId());
            sessaoTesteObj.setTempoSessaoMinutos(45);
            sessaoTesteObj.setDescricao("Sessão de teste criada via main do SessaoTesteDAO");

            sessaoDAO.inserirSessaoTeste(sessaoTesteObj);
            System.out.println("Sessão Inserida: ID=" + sessaoTesteObj.getId() + ", Status=" + sessaoTesteObj.getStatus() + ", Criada=" + sessaoTesteObj.getDataHoraCriacao());

            if (sessaoTesteObj.getId() == 0) {
                throw new SQLException("ID da sessão não foi gerado após inserção.");
            }

            // 2. Testar buscarSessaoTestePorId
            System.out.println("\nTestando buscarSessaoTestePorId...");
            SessaoTeste buscada = sessaoDAO.buscarSessaoTestePorId(sessaoTesteObj.getId());
            if (buscada != null) {
                System.out.println("Sessão Buscada: ID=" + buscada.getId() + ", Projeto=" + buscada.getProjeto().getNome());
            } else {
                System.out.println("Erro: Sessão não encontrada após inserção.");
            }

            // 3. Testar iniciarSessao
            System.out.println("\nTestando iniciarSessao...");
            if (buscada != null && "CRIADO".equals(buscada.getStatus())) {
                if (sessaoDAO.iniciarSessao(buscada.getId())) {
                    System.out.println("Sessão iniciada.");
                    SessaoTeste iniciada = sessaoDAO.buscarSessaoTestePorId(buscada.getId());
                    System.out.println("Novo Status: " + iniciada.getStatus() + ", Início: " + iniciada.getDataHoraInicio());
                    buscada = iniciada; // Atualiza referência
                } else {
                    System.out.println("Falha ao iniciar sessão.");
                }
            } else {
                System.out.println("Sessão não está no estado CRIADO para iniciar.");
            }


            // 4. Testar finalizarSessao
            System.out.println("\nTestando finalizarSessao...");
            if (buscada != null && "EM_EXECUCAO".equals(buscada.getStatus())) {
                if (sessaoDAO.finalizarSessao(buscada.getId())) {
                    System.out.println("Sessão finalizada.");
                    SessaoTeste finalizada = sessaoDAO.buscarSessaoTestePorId(buscada.getId());
                    System.out.println("Novo Status: " + finalizada.getStatus() + ", Fim: " + finalizada.getDataHoraFim());
                    buscada = finalizada; // Atualiza referência
                } else {
                    System.out.println("Falha ao finalizar sessão.");
                }
            } else {
                System.out.println("Sessão não está no estado EM_EXECUCAO para finalizar.");
            }


            // 5. Testar listarTodasSessoes
            System.out.println("\nTestando listarTodasSessoes...");
            List<SessaoTeste> todas = sessaoDAO.listarTodasSessoes();
            System.out.println("Total de sessões no sistema: " + todas.size());
            for(SessaoTeste st : todas) {
                System.out.println("  ID: " + st.getId() + ", Projeto: " + st.getProjeto().getNome() + ", Testador: " + st.getTestador().getNome());
            }

        } catch (SQLException e) {
            System.err.println("Erro no método main de SessaoTesteDAO: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("--- Testes para SessaoTesteDAO Concluídos ---");
    }
}