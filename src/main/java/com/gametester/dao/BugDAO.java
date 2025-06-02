// src/main/java/com/gametester/dao/BugDAO.java
package com.gametester.dao;

import com.gametester.model.*;
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BugDAO {

    /**
     * Insere um novo bug no banco de dados.
     * A data de registro é definida automaticamente pelo banco de dados (DEFAULT CURRENT_TIMESTAMP).
     * O ID gerado é atualizado no objeto Bug passado como argumento.
     *
     * @param bug O objeto Bug a ser inserido (sem ID, ou ID será ignorado).
     * @return O ID gerado para o bug inserido, ou -1 em caso de falha na obtenção do ID (embora uma SQLException seja lançada antes disso se a inserção falhar).
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public int inserirBug(Bug bug) throws SQLException {
        // A query usa RETURNING id, específica do PostgreSQL para retornar o ID gerado.
        String sql = "INSERT INTO Bug (sessao_teste_id, descricao, severidade, screenshot_url) VALUES (?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idGerado = -1;
        try {
            conn = ConexaoDB.getConnection();
            // Statement.RETURN_GENERATED_KEYS é usado em conjunto com getGeneratedKeys().
            // Para PostgreSQL com RETURNING, o driver JDBC deve ser capaz de fornecer o ID via getGeneratedKeys().
            // Alternativamente, com RETURNING, poderia se usar stmt.executeQuery() e ler o ID do ResultSet diretamente.
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, bug.getSessaoTesteId());
            stmt.setString(2, bug.getDescricao());
            stmt.setString(3, bug.getSeveridade());
            stmt.setString(4, bug.getScreenshotUrl());
            // data_registro é definida pelo DEFAULT CURRENT_TIMESTAMP no esquema do BD.

            int rowsAffected = stmt.executeUpdate(); // Executa a inserção
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys(); // Obtém as chaves geradas (o ID retornado por RETURNING)
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    bug.setId(idGerado); // Atualiza o ID no objeto bug
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir bug: " + e.getMessage()); // Útil para debug rápido no console do DAO
            throw e; // Re-lança a exceção para ser tratada pela camada de serviço/servlet
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement em inserirBug: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return idGerado;
    }

    public Bug buscarBugPorId(int id) throws SQLException {
        String sql = "SELECT id, sessao_teste_id, descricao, severidade, data_registro, screenshot_url FROM Bug WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Bug bug = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                bug = new Bug();
                bug.setId(rs.getInt("id"));
                bug.setSessaoTesteId(rs.getInt("sessao_teste_id"));
                bug.setDescricao(rs.getString("descricao"));
                bug.setSeveridade(rs.getString("severidade"));
                bug.setDataRegistro(rs.getTimestamp("data_registro"));
                bug.setScreenshotUrl(rs.getString("screenshot_url"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar bug por ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt em buscarBugPorId: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return bug;
    }

    public boolean atualizarBug(Bug bug) throws SQLException {
        String sql = "UPDATE Bug SET sessao_teste_id = ?, descricao = ?, severidade = ?, screenshot_url = ? WHERE id = ?";
        // data_registro não é atualizada, pois é a data do registro original.
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bug.getSessaoTesteId());
            stmt.setString(2, bug.getDescricao());
            stmt.setString(3, bug.getSeveridade());
            stmt.setString(4, bug.getScreenshotUrl());
            stmt.setInt(5, bug.getId());
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar bug com ID " + bug.getId() + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt em atualizarBug: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public boolean excluirBug(int id) throws SQLException {
        String sql = "DELETE FROM Bug WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        int rowsAffected = 0;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir bug com ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt em excluirBug: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    public List<Bug> listarBugsPorSessaoTeste(int sessaoTesteId) throws SQLException {
        String sql = "SELECT id, sessao_teste_id, descricao, severidade, data_registro, screenshot_url FROM Bug WHERE sessao_teste_id = ? ORDER BY data_registro ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Bug> bugs = new ArrayList<>();
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sessaoTesteId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Bug bug = new Bug();
                bug.setId(rs.getInt("id"));
                bug.setSessaoTesteId(rs.getInt("sessao_teste_id"));
                bug.setDescricao(rs.getString("descricao"));
                bug.setSeveridade(rs.getString("severidade"));
                bug.setDataRegistro(rs.getTimestamp("data_registro"));
                bug.setScreenshotUrl(rs.getString("screenshot_url"));
                bugs.add(bug);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar bugs por sessão de teste (ID: " + sessaoTesteId + "): " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar stmt em listarBugsPorSessaoTeste: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return bugs;
    }

    // O método main já está bem estruturado para testes e trata SQLException.
    // Apenas garanta que os outros DAOs (ProjetoDAO, UsuarioDAO, etc.) também propaguem SQLException
    // e que seus métodos de inserção atualizem os IDs nos objetos passados ou retornem os IDs.
    public static void main(String[] args) {
        BugDAO bugDAO = new BugDAO();
        SessaoTesteDAO sessaoTesteDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        System.out.println("--- Iniciando Testes para BugDAO ---");
        Projeto projetoBug = null;
        Usuario testadorBug = null;
        Estrategia estrategiaBug = null;
        SessaoTeste sessaoParaBug = null;
        Bug novoBug = null; // Declarar aqui para usar no finally se necessário

        try {
            System.out.println("--- Criando pré-requisitos para Bug ---");

            projetoBug = new Projeto(0, "Projeto Bugs T Main", "Projeto para testar bugs no main do BugDAO", null);
            projetoDAO.inserirProjeto(projetoBug); // Assume que inserirProjeto atualiza o ID e dataCriacao no objeto projetoBug
            if (projetoBug.getId() == 0) throw new SQLException("Falha ao criar projeto para teste de bug.");
            System.out.println("Projeto de bug criado com ID: " + projetoBug.getId());

            testadorBug = new Usuario(0, "Testador Bug Main", "bugtestermain" + System.currentTimeMillis()%1000 + "@email.com", "senha", "TESTADOR");
            Usuario existingUser = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail());
            if (existingUser == null) {
                usuarioDAO.inserirUsuario(testadorBug); // Assume que inserirUsuario atualiza o ID
                if(testadorBug.getId() == 0) testadorBug = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail()); // Garante que temos o usuário com ID
            } else {
                testadorBug = existingUser;
            }
            if (testadorBug == null || testadorBug.getId() == 0) throw new SQLException("Falha ao criar/obter testador para bug.");
            System.out.println("Testador para bug obtido/criado com ID: " + testadorBug.getId());

            estrategiaBug = new Estrategia(0, "Estrategia Bugs Main", "Estratégia para bugs no main do BugDAO", "Exemplos...", "Dicas...");
            estrategiaDAO.inserirEstrategia(estrategiaBug); // Assume que atualiza o ID
            if (estrategiaBug.getId() == 0) throw new SQLException("Falha ao criar estratégia para bug.");
            System.out.println("Estratégia de bug criada com ID: " + estrategiaBug.getId());

            sessaoParaBug = new SessaoTeste();
            sessaoParaBug.setProjetoId(projetoBug.getId());
            sessaoParaBug.setTestadorId(testadorBug.getId());
            sessaoParaBug.setEstrategiaId(estrategiaBug.getId());
            sessaoParaBug.setTempoSessaoMinutos(30);
            sessaoParaBug.setDescricao("Sessão para registrar bugs (main do BugDAO).");
            sessaoParaBug.setDataHoraCriacao(new Timestamp(System.currentTimeMillis())); // DAO define CRIADO

            sessaoTesteDAO.inserirSessaoTeste(sessaoParaBug); // Assume que atualiza o ID e define status como CRIADO
            if (sessaoParaBug.getId() == 0) throw new SQLException("Falha ao criar sessão de teste para bug.");
            System.out.println("Sessão para bug criada com ID: " + sessaoParaBug.getId() + " e status: " + sessaoParaBug.getStatus());

            if (!"EM_EXECUCAO".equals(sessaoParaBug.getStatus())) { // Garante que a sessão está em execução
                sessaoTesteDAO.iniciarSessao(sessaoParaBug.getId());
                sessaoParaBug.setStatus("EM_EXECUCAO"); // Atualiza o status localmente para consistência no teste
                System.out.println("Sessão para bug iniciada. Novo status: " + sessaoParaBug.getStatus());
            }


            System.out.println("\n--- Teste de Inserção de Bug ---");
            novoBug = new Bug(); // Inicializa aqui
            novoBug.setSessaoTesteId(sessaoParaBug.getId());
            novoBug.setDescricao("Personagem atravessa parede na fase 1 (BugDAO main).");
            novoBug.setSeveridade("ALTA");
            novoBug.setScreenshotUrl("http://example.com/screenshot_bugdao_main.png");

            bugDAO.inserirBug(novoBug); // ID é setado no objeto novoBug
            if (novoBug.getId() > 0) {
                System.out.println("Bug inserido com sucesso! ID: " + novoBug.getId());
            } else {
                System.out.println("Falha ao inserir bug (ID não foi gerado/retornado).");
            }

            // Testes de buscar, atualizar, listar, excluir... (como já estavam)
            if (novoBug.getId() > 0) {
                System.out.println("\n--- Teste de Busca de Bug por ID ---");
                Bug bugBuscado = bugDAO.buscarBugPorId(novoBug.getId());
                if (bugBuscado != null) {
                    System.out.println("Bug encontrado: ID " + bugBuscado.getId() + ", Descrição: " + bugBuscado.getDescricao());

                    System.out.println("\n--- Teste de Atualização de Bug ---");
                    bugBuscado.setDescricao(bugBuscado.getDescricao() + " [ATUALIZADO]");
                    bugBuscado.setSeveridade("MEDIA");
                    if (bugDAO.atualizarBug(bugBuscado)) {
                        System.out.println("Bug atualizado com sucesso!");
                    } else {
                        System.out.println("Falha ao atualizar bug.");
                    }
                }
            }

            System.out.println("\n--- Teste de Listagem de Bugs por Sessão de Teste ---");
            List<Bug> bugsDaSessao = bugDAO.listarBugsPorSessaoTeste(sessaoParaBug.getId());
            System.out.println("Bugs encontrados para sessão ID " + sessaoParaBug.getId() + ": " + bugsDaSessao.size());
            for (Bug b : bugsDaSessao) {
                System.out.println("  -> Bug ID: " + b.getId() + ", Descrição: " + b.getDescricao());
            }


            if (novoBug != null && novoBug.getId() > 0) {
                System.out.println("\n--- Teste de Exclusão de Bug ---");
                if (bugDAO.excluirBug(novoBug.getId())) {
                    System.out.println("Bug com ID " + novoBug.getId() + " excluído com sucesso!");
                } else {
                    System.out.println("Falha ao excluir bug com ID " + novoBug.getId());
                }
            }


        } catch (SQLException e) {
            System.err.println("!!!!!! Erro de SQL no método main do BugDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n--- Limpando pré-requisitos (tentativa) ---");
            try {
                // Excluir bugs restantes da sessão antes de excluir a sessão (se não houver cascade)
                if (sessaoParaBug != null && sessaoParaBug.getId() > 0) {
                    List<Bug> bugsRestantes = bugDAO.listarBugsPorSessaoTeste(sessaoParaBug.getId());
                    for (Bug b : bugsRestantes) {
                        System.out.println("Excluindo bug restante ID: " + b.getId() + " da sessão " + sessaoParaBug.getId());
                        bugDAO.excluirBug(b.getId());
                    }
                    if (!"FINALIZADO".equals(sessaoParaBug.getStatus())) { // Finaliza apenas se não estiver já finalizada
                        sessaoTesteDAO.finalizarSessao(sessaoParaBug.getId());
                        System.out.println("Sessão finalizada: " + sessaoParaBug.getId());
                    }
                    if (sessaoTesteDAO.excluirSessaoTeste(sessaoParaBug.getId())) {
                        System.out.println("Sessão de Teste removida: " + sessaoParaBug.getId());
                    }
                }
                if (estrategiaBug != null && estrategiaBug.getId() > 0) {
                    if (estrategiaDAO.excluirEstrategia(estrategiaBug.getId())) {
                        System.out.println("Estratégia de Bug removida: " + estrategiaBug.getId());
                    }
                }
                if (projetoBug != null && projetoBug.getId() > 0) {
                    if (projetoDAO.excluirProjeto(projetoBug.getId())) { // Corrigido para usar getId()
                        System.out.println("Projeto de Bug removido: " + projetoBug.getId());
                    }
                }
                if (testadorBug != null && testadorBug.getId() > 0) {
                    // Cuidado ao excluir usuários que podem estar em outras sessões/projetos
                    // Para este teste, vamos comentar a exclusão do usuário para evitar falhas
                    // se ele for usado em outros testes ou se houver restrições de FK.
                    // if (usuarioDAO.excluirUsuario(testadorBug.getId())) {
                    //     System.out.println("Testador de Bug removido: " + testadorBug.getId());
                    // }
                }
            } catch (SQLException ex) {
                System.err.println("!!!!!! Erro de SQL durante a limpeza no main do BugDAO: " + ex.getMessage());
                ex.printStackTrace();
            }
            System.out.println("--- Testes para BugDAO Concluídos ---");
        }
    }
}