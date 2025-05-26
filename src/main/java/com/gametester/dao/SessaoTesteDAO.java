// src/main/java/com/gametester/dao/SessaoTesteDAO.java
package com.gametester.dao;

import com.gametester.model.SessaoTeste;
import com.gametester.model.Projeto; // Para carregar o objeto Projeto
import com.gametester.model.Usuario; // Para carregar o objeto Usuario
import com.gametester.model.Estrategia; // Para carregar o objeto Estrategia
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SessaoTesteDAO {

    public int inserirSessaoTeste(SessaoTeste sessaoTeste) throws SQLException {
        String sql = "INSERT INTO SessaoTeste (projeto_id, testador_id, estrategia_id, tempo_sessao_minutos, " +
                "descricao, status, data_hora_criacao) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idGerado = -1;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, sessaoTeste.getProjetoId());
            stmt.setInt(2, sessaoTeste.getTestadorId());
            stmt.setInt(3, sessaoTeste.getEstrategiaId());
            stmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            stmt.setString(5, sessaoTeste.getDescricao());
            stmt.setString(6, "CRIADO"); // Status inicial
            // Se data_hora_criacao for passada no objeto, usar, senão gerar nova.
            // Para consistência com o que foi passado no main original, vamos usar o do objeto se não for null.
            // No entanto, o método original gerava um novo timestamp aqui.
            // Requisito R19: "Cada mudança de status deve registrar a data e a hora."
            // A criação é o primeiro "status".
            stmt.setTimestamp(7, sessaoTeste.getDataHoraCriacao() != null ? sessaoTeste.getDataHoraCriacao() : new Timestamp(System.currentTimeMillis()));


            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    sessaoTeste.setId(idGerado); // Atualiza o ID no objeto
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir sessão de teste: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
        return idGerado;
    }

    public SessaoTeste buscarSessaoTestePorId(int id) throws SQLException {
        String sql = "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, p.descricao AS projeto_descricao, p.data_criacao AS projeto_data_criacao, " + // Adicionado para popular Projeto
                "u.nome AS testador_nome, u.email AS testador_email, u.tipo_perfil AS testador_tipo, " + // Adicionado para popular Usuario
                "e.nome AS estrategia_nome, e.descricao AS estrategia_descricao, e.exemplos AS estrategia_exemplos, e.dicas AS estrategia_dicas " + // Adicionado para popular Estrategia
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

                Projeto projeto = new Projeto(rs.getInt("projeto_id"), rs.getString("projeto_nome"), rs.getString("projeto_descricao"), rs.getTimestamp("projeto_data_criacao"));
                sessaoTeste.setProjeto(projeto);

                Usuario testador = new Usuario(rs.getInt("testador_id"), rs.getString("testador_nome"), rs.getString("testador_email"), null, rs.getString("testador_tipo"));
                sessaoTeste.setTestador(testador);

                Estrategia estrategia = new Estrategia(rs.getInt("estrategia_id"), rs.getString("estrategia_nome"), rs.getString("estrategia_descricao"), rs.getString("estrategia_exemplos"), rs.getString("estrategia_dicas"));
                sessaoTeste.setEstrategia(estrategia);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessão de teste por ID: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
        return sessaoTeste;
    }

    public boolean atualizarSessaoTeste(SessaoTeste sessaoTeste) throws SQLException {
        // Note: data_hora_criacao is generally not updated after creation.
        // Status, data_hora_inicio, data_hora_fim are typically updated by specific lifecycle methods.
        // This method allows general update, use with caution.
        String sql = "UPDATE SessaoTeste SET projeto_id = ?, testador_id = ?, estrategia_id = ?, " +
                "tempo_sessao_minutos = ?, descricao = ?, status = ?, " + // data_hora_criacao removida da atualização
                "data_hora_inicio = ?, data_hora_fim = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sessaoTeste.getProjetoId());
            stmt.setInt(2, sessaoTeste.getTestadorId());
            stmt.setInt(3, sessaoTeste.getEstrategiaId());
            stmt.setInt(4, sessaoTeste.getTempoSessaoMinutos());
            stmt.setString(5, sessaoTeste.getDescricao());
            stmt.setString(6, sessaoTeste.getStatus());
            // stmt.setTimestamp(7, sessaoTeste.getDataHoraCriacao()); // data_hora_criacao should not change
            stmt.setTimestamp(7, sessaoTeste.getDataHoraInicio()); // Index adjusted
            stmt.setTimestamp(8, sessaoTeste.getDataHoraFim());   // Index adjusted
            stmt.setInt(9, sessaoTeste.getId());                 // Index adjusted
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar sessão de teste: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
    }

    // Métodos para controle do ciclo de vida (R8)
    public boolean iniciarSessao(int sessaoId) throws SQLException { // [cite: 30]
        String sql = "UPDATE SessaoTeste SET status = ?, data_hora_inicio = ? WHERE id = ? AND (status = 'CRIADO' OR status IS NULL)"; // Allow if status is null for some legacy reason
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "EM_EXECUCAO");
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // R19: Cada mudança de status deve registrar a data e a hora [cite: 19]
            stmt.setInt(3, sessaoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao iniciar sessão: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean finalizarSessao(int sessaoId) throws SQLException { // [cite: 30]
        String sql = "UPDATE SessaoTeste SET status = ?, data_hora_fim = ? WHERE id = ? AND status = 'EM_EXECUCAO'";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "FINALIZADO");
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // R19: Cada mudança de status deve registrar a data e a hora [cite: 19]
            stmt.setInt(3, sessaoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao finalizar sessão: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean excluirSessaoTeste(int id) throws SQLException {
        String sql = "DELETE FROM SessaoTeste WHERE id = ?";
        // Considerar cascade delete para Bugs associados ou excluir bugs manualmente antes.
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir sessão de teste: " + e.getMessage());
            // R33: "evitar a remoção de elementos em uso". Se bugs dependem, pode dar erro de FK. [cite: 33]
            throw e; // Re-throw
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
    }

    public List<SessaoTeste> listarSessoesPorProjeto(int projetoId, String statusFiltro, String ordenacao) throws SQLException { // Para R9 [cite: 31]
        StringBuilder sqlBuilder = new StringBuilder("SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, u.nome AS testador_nome, e.nome AS estrategia_nome " +
                "FROM SessaoTeste st " +
                "JOIN Projeto p ON st.projeto_id = p.id " +
                "JOIN Usuario u ON st.testador_id = u.id " +
                "JOIN Estrategia e ON st.estrategia_id = e.id " +
                "WHERE st.projeto_id = ?");

        if (statusFiltro != null && !statusFiltro.isEmpty() && !statusFiltro.equalsIgnoreCase("TODOS")) {
            sqlBuilder.append(" AND st.status = ?");
        }

        sqlBuilder.append(" ORDER BY ");
        switch (ordenacao != null ? ordenacao.toUpperCase() : "DATA_CRIACAO_DESC") { // Default a valor válido
            case "TEMPO_ASC":
                sqlBuilder.append("st.tempo_sessao_minutos ASC");
                break;
            case "TEMPO_DESC":
                sqlBuilder.append("st.tempo_sessao_minutos DESC");
                break;
            case "STATUS_ASC":
                sqlBuilder.append("st.status ASC, st.data_hora_criacao DESC"); // Secondary sort for consistency
                break;
            case "STATUS_DESC":
                sqlBuilder.append("st.status DESC, st.data_hora_criacao DESC");
                break;
            case "DATA_CRIACAO_ASC":
                sqlBuilder.append("st.data_hora_criacao ASC");
                break;
            // Default case
            case "DATA_CRIACAO_DESC":
            default:
                sqlBuilder.append("st.data_hora_criacao DESC");
                break;
        }

        List<SessaoTeste> sessoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sqlBuilder.toString());
            stmt.setInt(1, projetoId);

            int paramIndex = 2;
            if (statusFiltro != null && !statusFiltro.isEmpty() && !statusFiltro.equalsIgnoreCase("TODOS")) {
                stmt.setString(paramIndex++, statusFiltro.toUpperCase()); // Consistent status case
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
                sessaoTeste.setTestador(new Usuario(sessaoTeste.getTestadorId(), rs.getString("testador_nome"), null, null, null));
                sessaoTeste.setEstrategia(new Estrategia(sessaoTeste.getEstrategiaId(), rs.getString("estrategia_nome"), null, null, null));

                sessoes.add(sessaoTeste);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar sessões por projeto: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
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
                "JOIN Estrategia e ON st.estrategia_id = e.id ORDER BY st.data_hora_criacao DESC";
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
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt: " + e.getMessage());}
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }


    public static void main(String[] args) {
        SessaoTesteDAO sessaoDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO(); // Assume these DAOs also throw SQLException correctly
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        System.out.println("--- Iniciando Testes para SessaoTesteDAO ---");
        Projeto projetoTeste = null;
        Usuario testadorSessao = null;
        Estrategia estrategiaSessao = null;
        SessaoTeste novaSessao = null;
        int idSessaoInserida = -1;

        try {
            System.out.println("--- Criando pré-requisitos para Sessão de Teste ---");

            projetoTeste = new Projeto();
            projetoTeste.setNome("Projeto Sessao Main " + System.currentTimeMillis() % 10000);
            projetoTeste.setDescricao("Projeto de exemplo para testes de sessão no main.");
            projetoDAO.inserirProjeto(projetoTeste); // Assumes ID is set in object by DAO
            if (projetoTeste.getId() == 0) throw new SQLException("Falha ao criar projeto para teste.");
            System.out.println("Projeto criado com ID: " + projetoTeste.getId());

            testadorSessao = new Usuario(0, "Testador Sessao Main", "sessaomain" + System.currentTimeMillis()%10000 + "@test.com", "senha", "TESTADOR");
            Usuario existingUser = usuarioDAO.buscarUsuarioPorEmail(testadorSessao.getEmail());
            if (existingUser == null) {
                usuarioDAO.inserirUsuario(testadorSessao); // Assumes ID is set in object by DAO
                if (testadorSessao.getId() == 0) testadorSessao = usuarioDAO.buscarUsuarioPorEmail(testadorSessao.getEmail()); // re-fetch if ID not set
            } else {
                testadorSessao = existingUser;
            }
            if (testadorSessao == null || testadorSessao.getId() == 0) throw new SQLException("Falha ao criar/obter testador para teste.");
            System.out.println("Testador de Sessão com ID: " + testadorSessao.getId());

            estrategiaSessao = new Estrategia();
            estrategiaSessao.setNome("Estrategia Sessao Main " + System.currentTimeMillis() % 10000);
            estrategiaSessao.setDescricao("Foco em explorar áreas desconhecidas no main.");
            estrategiaDAO.inserirEstrategia(estrategiaSessao); // Assumes ID is set in object by DAO
            if (estrategiaSessao.getId() == 0) throw new SQLException("Falha ao criar estrategia para teste.");
            System.out.println("Estratégia criada com ID: " + estrategiaSessao.getId());

            System.out.println("\n--- Teste de Inserção de Sessão de Teste ---");
            novaSessao = new SessaoTeste();
            novaSessao.setProjetoId(projetoTeste.getId());
            novaSessao.setTestadorId(testadorSessao.getId());
            novaSessao.setEstrategiaId(estrategiaSessao.getId());
            novaSessao.setTempoSessaoMinutos(60);
            novaSessao.setDescricao("Primeira sessão de teste para o Projeto no Main.");
            novaSessao.setDataHoraCriacao(new Timestamp(System.currentTimeMillis())); // Set creation time explicitly

            idSessaoInserida = sessaoDAO.inserirSessaoTeste(novaSessao); // ID is also set in novaSessao object
            if (idSessaoInserida != -1) {
                System.out.println("Sessão de Teste inserida com sucesso! ID: " + novaSessao.getId());
            } else {
                throw new SQLException("Falha ao inserir sessão de teste (ID -1 retornado).");
            }

            System.out.println("\n--- Teste de Busca de Sessão de Teste por ID ---");
            SessaoTeste sessaoBuscada = sessaoDAO.buscarSessaoTestePorId(novaSessao.getId());
            if (sessaoBuscada != null) {
                System.out.println("Sessão encontrada: ID " + sessaoBuscada.getId());
                System.out.println("Projeto: " + sessaoBuscada.getProjeto().getNome());
                System.out.println("Testador: " + sessaoBuscada.getTestador().getNome());
                System.out.println("Estratégia: " + sessaoBuscada.getEstrategia().getNome());
                System.out.println("Status: " + sessaoBuscada.getStatus()); // CRIADO
                System.out.println("Data de Criação: " + sessaoBuscada.getDataHoraCriacao());
            } else {
                throw new SQLException("Sessão com ID " + novaSessao.getId() + " não encontrada após inserção.");
            }

            System.out.println("\n--- Teste de Iniciar Sessão ---");
            if (sessaoBuscada != null && "CRIADO".equals(sessaoBuscada.getStatus())) {
                if (sessaoDAO.iniciarSessao(sessaoBuscada.getId())) {
                    System.out.println("Sessão iniciada com sucesso!");
                    SessaoTeste sessaoIniciada = sessaoDAO.buscarSessaoTestePorId(sessaoBuscada.getId());
                    System.out.println("Novo Status: " + sessaoIniciada.getStatus()); // EM_EXECUCAO
                    System.out.println("Data de Início: " + sessaoIniciada.getDataHoraInicio());
                    sessaoBuscada = sessaoIniciada; // Update reference
                } else {
                    System.out.println("Falha ao iniciar sessão (DAO retornou false).");
                }
            }

            System.out.println("\n--- Teste de Finalizar Sessão ---");
            if (sessaoBuscada != null && "EM_EXECUCAO".equals(sessaoBuscada.getStatus())) {
                if (sessaoDAO.finalizarSessao(sessaoBuscada.getId())) {
                    System.out.println("Sessão finalizada com sucesso!");
                    SessaoTeste sessaoFinalizada = sessaoDAO.buscarSessaoTestePorId(sessaoBuscada.getId());
                    System.out.println("Novo Status: " + sessaoFinalizada.getStatus()); // FINALIZADO
                    System.out.println("Data de Fim: " + sessaoFinalizada.getDataHoraFim());
                } else {
                    System.out.println("Falha ao finalizar sessão (DAO retornou false).");
                }
            }

            System.out.println("\n--- Teste de Listagem de Sessões por Projeto ---");
            List<SessaoTeste> sessoesDoProjeto = sessaoDAO.listarSessoesPorProjeto(projetoTeste.getId(), "TODOS", "DATA_CRIACAO_DESC");
            if (!sessoesDoProjeto.isEmpty()) {
                System.out.println("Sessões do Projeto '" + projetoTeste.getNome() + "':");
                for (SessaoTeste st : sessoesDoProjeto) {
                    System.out.println("ID: " + st.getId() + ", Testador: " + st.getTestador().getNome() +
                            ", Status: " + st.getStatus());
                }
            } else {
                System.out.println("Nenhuma sessão encontrada para o projeto.");
            }

            System.out.println("\n--- Teste de Listagem de Todas as Sessões ---");
            List<SessaoTeste> todasAsSessoes = sessaoDAO.listarTodasSessoes();
            if (!todasAsSessoes.isEmpty()) {
                System.out.println("Algumas das Sessões no sistema:");
                for (int i = 0; i < Math.min(todasAsSessoes.size(), 5); i++) { // Print first 5
                    SessaoTeste st = todasAsSessoes.get(i);
                    System.out.println("ID: " + st.getId() + ", Projeto: " + st.getProjeto().getNome() +
                            ", Testador: " + st.getTestador().getNome() + ", Status: " + st.getStatus());
                }
            } else {
                System.out.println("Nenhuma sessão cadastrada no sistema.");
            }

        } catch (SQLException e) {
            System.err.println("!!!!!! Erro de SQL no método main do SessaoTesteDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Limpando pré-requisitos (tentativa) ---");
            try {
                if (idSessaoInserida != -1) {
                    // Delete bugs associated with this session first if not handled by CASCADE
                    // BugDAO bugDAO = new BugDAO();
                    // List<Bug> bugs = bugDAO.listarBugsPorSessaoTeste(idSessaoInserida);
                    // for (Bug b : bugs) { bugDAO.excluirBug(b.getId()); }

                    if (sessaoDAO.excluirSessaoTeste(idSessaoInserida)) {
                        System.out.println("Sessão de Teste removida: " + idSessaoInserida);
                    }
                }
                if (estrategiaSessao != null && estrategiaSessao.getId() != 0) {
                    if (estrategiaDAO.excluirEstrategia(estrategiaSessao.getId())) {
                        System.out.println("Estratégia removida: " + estrategiaSessao.getId());
                    }
                }
                // Be careful deleting shared users/projects unless they are exclusive to this test run.
                if (testadorSessao != null && testadorSessao.getId() != 0) {
                    // Check if this user is used elsewhere before deleting.
                    // For this test, we assume it can be deleted.
                    // if (usuarioDAO.excluirUsuario(testadorSessao.getId())) {
                    //     System.out.println("Testador de Sessão removido: " + testadorSessao.getId());
                    // }
                }
                if (projetoTeste != null && projetoTeste.getId() != 0) {
                    if (projetoDAO.excluirProjeto(projetoTeste.getId())) {
                        System.out.println("Projeto removido: " + projetoTeste.getId());
                    }
                }
            } catch (SQLException ex) {
                System.err.println("!!!!!! Erro de SQL durante a limpeza no main do SessaoTesteDAO: " + ex.getMessage());
                ex.printStackTrace();
            }
            System.out.println("--- Testes para SessaoTesteDAO Concluídos ---");
        }
    }
}