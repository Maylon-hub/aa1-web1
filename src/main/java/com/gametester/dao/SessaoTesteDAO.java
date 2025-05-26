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

    public int inserirSessaoTeste(SessaoTeste sessaoTeste) {
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
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // Data de criação

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir sessão de teste: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return idGerado;
    }

    public SessaoTeste buscarSessaoTestePorId(int id) {
        String sql = "SELECT st.id, st.projeto_id, st.testador_id, st.estrategia_id, st.tempo_sessao_minutos, " +
                "st.descricao, st.status, st.data_hora_criacao, st.data_hora_inicio, st.data_hora_fim, " +
                "p.nome AS projeto_nome, u.nome AS testador_nome, e.nome AS estrategia_nome " +
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

                // Carregar objetos relacionados para facilitar o uso na view
                sessaoTeste.setProjeto(new Projeto(sessaoTeste.getProjetoId(), rs.getString("projeto_nome"), null, null));
                sessaoTeste.setTestador(new Usuario(sessaoTeste.getTestadorId(), rs.getString("testador_nome"), null, null, null));
                sessaoTeste.setEstrategia(new Estrategia(sessaoTeste.getEstrategiaId(), rs.getString("estrategia_nome"), null, null, null));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar sessão de teste por ID: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return sessaoTeste;
    }

    public boolean atualizarSessaoTeste(SessaoTeste sessaoTeste) {
        String sql = "UPDATE SessaoTeste SET projeto_id = ?, testador_id = ?, estrategia_id = ?, " +
                "tempo_sessao_minutos = ?, descricao = ?, status = ?, data_hora_criacao = ?, " +
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
            stmt.setTimestamp(7, sessaoTeste.getDataHoraCriacao());
            stmt.setTimestamp(8, sessaoTeste.getDataHoraInicio());
            stmt.setTimestamp(9, sessaoTeste.getDataHoraFim());
            stmt.setInt(10, sessaoTeste.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar sessão de teste: " + e.getMessage());
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    // Métodos para controle do ciclo de vida (R8)
    public boolean iniciarSessao(int sessaoId) {
        String sql = "UPDATE SessaoTeste SET status = ?, data_hora_inicio = ? WHERE id = ? AND status = 'CRIADO'";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "EM_EXECUCAO");
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, sessaoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao iniciar sessão: " + e.getMessage());
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean finalizarSessao(int sessaoId) {
        String sql = "UPDATE SessaoTeste SET status = ?, data_hora_fim = ? WHERE id = ? AND status = 'EM_EXECUCAO'";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "FINALIZADO");
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, sessaoId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao finalizar sessão: " + e.getMessage());
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean excluirSessaoTeste(int id) {
        String sql = "DELETE FROM SessaoTeste WHERE id = ?";
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
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public List<SessaoTeste> listarSessoesPorProjeto(int projetoId, String statusFiltro, String ordenacao) {
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
        switch (ordenacao != null ? ordenacao.toUpperCase() : "") {
            case "TEMPO_ASC":
                sqlBuilder.append("st.tempo_sessao_minutos ASC");
                break;
            case "TEMPO_DESC":
                sqlBuilder.append("st.tempo_sessao_minutos DESC");
                break;
            case "STATUS_ASC":
                sqlBuilder.append("st.status ASC");
                break;
            case "STATUS_DESC":
                sqlBuilder.append("st.status DESC");
                break;
            case "DATA_CRIACAO_ASC":
                sqlBuilder.append("st.data_hora_criacao ASC");
                break;
            case "DATA_CRIACAO_DESC":
                sqlBuilder.append("st.data_hora_criacao DESC");
                break;
            default:
                sqlBuilder.append("st.data_hora_criacao DESC"); // Padrão
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
                stmt.setString(paramIndex++, statusFiltro);
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

                // Carregar objetos relacionados para facilitar o uso na view
                sessaoTeste.setProjeto(new Projeto(sessaoTeste.getProjetoId(), rs.getString("projeto_nome"), null, null));
                sessaoTeste.setTestador(new Usuario(sessaoTeste.getTestadorId(), rs.getString("testador_nome"), null, null, null));
                sessaoTeste.setEstrategia(new Estrategia(sessaoTeste.getEstrategiaId(), rs.getString("estrategia_nome"), null, null, null));

                sessoes.add(sessaoTeste);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar sessões por projeto: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }

    public List<SessaoTeste> listarTodasSessoes() {
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
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return sessoes;
    }

    public static void main(String[] args) {
        SessaoTesteDAO sessaoDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        // --- Pré-requisitos: Criar Projeto, Usuário (Testador) e Estratégia ---
        System.out.println("--- Criando pré-requisitos para Sessão de Teste ---");

        // Cria um projeto
        Projeto projetoTeste = new Projeto();
        projetoTeste.setNome("Projeto para Sessões");
        projetoTeste.setDescricao("Projeto de exemplo para testes de sessão.");
        int idProjeto = projetoDAO.inserirProjeto(projetoTeste);
        if (idProjeto == -1) {
            System.out.println("Erro ao criar projeto. Saindo do teste.");
            return;
        }
        projetoTeste.setId(idProjeto);
        System.out.println("Projeto criado com ID: " + idProjeto);

        // Cria um testador
        Usuario testadorSessao = new Usuario(0, "Testador de Sessão", "sessao@test.com", "senha", "TESTADOR");
        if (usuarioDAO.buscarUsuarioPorEmail(testadorSessao.getEmail()) == null) {
            usuarioDAO.inserirUsuario(testadorSessao);
            testadorSessao = usuarioDAO.buscarUsuarioPorEmail(testadorSessao.getEmail()); // Pega o ID
        } else {
            testadorSessao = usuarioDAO.buscarUsuarioPorEmail(testadorSessao.getEmail());
        }
        System.out.println("Testador de Sessão com ID: " + testadorSessao.getId());


        // Cria uma estratégia
        Estrategia estrategiaSessao = new Estrategia();
        estrategiaSessao.setNome("Estratégia de Descoberta");
        estrategiaSessao.setDescricao("Foco em explorar áreas desconhecidas.");
        int idEstrategia = estrategiaDAO.inserirEstrategia(estrategiaSessao);
        if (idEstrategia == -1) {
            System.out.println("Erro ao criar estratégia. Saindo do teste.");
            return;
        }
        estrategiaSessao.setId(idEstrategia);
        System.out.println("Estratégia criada com ID: " + idEstrategia);

        // --- Teste de Inserção de Sessão de Teste (Status 'CRIADO') ---
        System.out.println("\n--- Teste de Inserção de Sessão de Teste ---");
        SessaoTeste novaSessao = new SessaoTeste();
        novaSessao.setProjetoId(projetoTeste.getId());
        novaSessao.setTestadorId(testadorSessao.getId());
        novaSessao.setEstrategiaId(estrategiaSessao.getId());
        novaSessao.setTempoSessaoMinutos(60);
        novaSessao.setDescricao("Primeira sessão de teste para o Projeto para Sessões.");
        // Status e data de criação serão definidos pelo DAO no INSERT

        int idSessaoInserida = sessaoDAO.inserirSessaoTeste(novaSessao);
        if (idSessaoInserida != -1) {
            System.out.println("Sessão de Teste inserida com sucesso! ID: " + idSessaoInserida);
            novaSessao.setId(idSessaoInserida); // Atualiza o ID do objeto
        } else {
            System.out.println("Falha ao inserir sessão de teste.");
            return;
        }

        // --- Teste de Busca por ID ---
        System.out.println("\n--- Teste de Busca de Sessão de Teste por ID ---");
        SessaoTeste sessaoBuscada = sessaoDAO.buscarSessaoTestePorId(novaSessao.getId());
        if (sessaoBuscada != null) {
            System.out.println("Sessão encontrada: ID " + sessaoBuscada.getId());
            System.out.println("Projeto: " + sessaoBuscada.getProjeto().getNome());
            System.out.println("Testador: " + sessaoBuscada.getTestador().getNome());
            System.out.println("Estratégia: " + sessaoBuscada.getEstrategia().getNome());
            System.out.println("Status: " + sessaoBuscada.getStatus());
            System.out.println("Data de Criação: " + sessaoBuscada.getDataHoraCriacao());
        } else {
            System.out.println("Sessão com ID " + novaSessao.getId() + " não encontrada.");
        }

        // --- Teste de Iniciar Sessão (R8) ---
        System.out.println("\n--- Teste de Iniciar Sessão ---");
        if (sessaoBuscada != null && sessaoBuscada.getStatus().equals("CRIADO")) {
            if (sessaoDAO.iniciarSessao(sessaoBuscada.getId())) {
                System.out.println("Sessão iniciada com sucesso!");
                SessaoTeste sessaoIniciada = sessaoDAO.buscarSessaoTestePorId(sessaoBuscada.getId());
                System.out.println("Novo Status: " + sessaoIniciada.getStatus());
                System.out.println("Data de Início: " + sessaoIniciada.getDataHoraInicio());
            } else {
                System.out.println("Falha ao iniciar sessão.");
            }
        }

        // --- Teste de Finalizar Sessão (R8) ---
        System.out.println("\n--- Teste de Finalizar Sessão ---");
        if (sessaoBuscada != null && sessaoBuscada.getStatus().equals("EM_EXECUCAO")) {
            if (sessaoDAO.finalizarSessao(sessaoBuscada.getId())) {
                System.out.println("Sessão finalizada com sucesso!");
                SessaoTeste sessaoFinalizada = sessaoDAO.buscarSessaoTestePorId(sessaoBuscada.getId());
                System.out.println("Novo Status: " + sessaoFinalizada.getStatus());
                System.out.println("Data de Fim: " + sessaoFinalizada.getDataHoraFim());
            } else {
                System.out.println("Falha ao finalizar sessão.");
            }
        }

        // --- Teste de Listagem de Sessões por Projeto (R9) ---
        System.out.println("\n--- Teste de Listagem de Sessões por Projeto ---");
        List<SessaoTeste> sessoesDoProjeto = sessaoDAO.listarSessoesPorProjeto(projetoTeste.getId(), "TODOS", "DATA_CRIACAO_DESC");
        if (!sessoesDoProjeto.isEmpty()) {
            System.out.println("Sessões do Projeto '" + projetoTeste.getNome() + "':");
            for (SessaoTeste st : sessoesDoProjeto) {
                System.out.println("ID: " + st.getId() + ", Testador: " + st.getTestador().getNome() +
                        ", Estratégia: " + st.getEstrategia().getNome() + ", Status: " + st.getStatus());
            }
        } else {
            System.out.println("Nenhuma sessão encontrada para o projeto.");
        }

        // --- Teste de Listagem de Todas as Sessões (para Admin) ---
        System.out.println("\n--- Teste de Listagem de Todas as Sessões ---");
        List<SessaoTeste> todasAsSessoes = sessaoDAO.listarTodasSessoes();
        if (!todasAsSessoes.isEmpty()) {
            System.out.println("Todas as Sessões no sistema:");
            for (SessaoTeste st : todasAsSessoes) {
                System.out.println("ID: " + st.getId() + ", Projeto: " + st.getProjeto().getNome() +
                        ", Testador: " + st.getTestador().getNome() + ", Status: " + st.getStatus());
            }
        } else {
            System.out.println("Nenhuma sessão cadastrada no sistema.");
        }


        // --- Teste de Exclusão (CUIDADO!) ---
        // Descomente e use com cautela.
        // if (idSessaoInserida != -1) {
        //     System.out.println("\n--- Teste de Exclusão de Sessão de Teste ---");
        //     if (sessaoDAO.excluirSessaoTeste(idSessaoInserida)) {
        //         System.out.println("Sessão de Teste com ID " + idSessaoInserida + " excluída com sucesso!");
        //     } else {
        //         System.out.println("Falha ao excluir sessão de teste com ID " + idSessaoInserida);
        //     }
        // }

        // --- Limpeza dos pré-requisitos (Opcional, com cautela) ---
        // Descomente se quiser limpar os dados criados no teste
        // System.out.println("\n--- Limpando pré-requisitos ---");
        // if (idSessaoInserida != -1 && sessaoDAO.excluirSessaoTeste(idSessaoInserida)) {
        //     System.out.println("Sessão de Teste removida.");
        // }
        // if (idEstrategia != -1 && estrategiaDAO.excluirEstrategia(idEstrategia)) {
        //     System.out.println("Estratégia removida.");
        // }
        // // Cuidado: Se outros projetos/sessões usarem esses usuários, a exclusão falhará.
        // if (testadorSessao != null && usuarioDAO.excluirUsuario(testadorSessao.getId())) {
        //     System.out.println("Testador de Sessão removido.");
        // }
        // if (idProjeto != -1 && projetoDAO.excluirProjeto(idProjeto)) {
        //     System.out.println("Projeto removido.");
        // }
    }
}