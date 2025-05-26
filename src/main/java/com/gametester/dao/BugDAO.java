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

    public int inserirBug(Bug bug) throws SQLException {
        String sql = "INSERT INTO Bug (sessao_teste_id, descricao, severidade, screenshot_url) VALUES (?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idGerado = -1;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, bug.getSessaoTesteId());
            stmt.setString(2, bug.getDescricao());
            stmt.setString(3, bug.getSeveridade());
            stmt.setString(4, bug.getScreenshotUrl());
            // data_registro usa DEFAULT CURRENT_TIMESTAMP no DB

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    bug.setId(idGerado); // Set ID on the bug object
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir bug: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
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
            System.err.println("Erro ao buscar bug por ID: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return bug;
    }

    public boolean atualizarBug(Bug bug) throws SQLException {
        String sql = "UPDATE Bug SET sessao_teste_id = ?, descricao = ?, severidade = ?, screenshot_url = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bug.getSessaoTesteId());
            stmt.setString(2, bug.getDescricao());
            stmt.setString(3, bug.getSeveridade());
            stmt.setString(4, bug.getScreenshotUrl());
            stmt.setInt(5, bug.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar bug: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean excluirBug(int id) throws SQLException {
        String sql = "DELETE FROM Bug WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir bug: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
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
            System.err.println("Erro ao listar bugs por sessão de teste: " + e.getMessage());
            throw e; // Re-throw
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return bugs;
    }

    // Corrected main method
    public static void main(String[] args) { // Removed "throws SQLException" to handle internally
        BugDAO bugDAO = new BugDAO();
        // It's better if DAO main methods don't depend on other DAOs directly for their core testing,
        // or if they do, those DAOs should also be robust or mocked.
        // For now, we assume these DAOs will also be updated to throw SQLException from their methods.
        SessaoTesteDAO sessaoTesteDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        System.out.println("--- Iniciando Testes para BugDAO ---");
        Projeto projetoBug = null;
        Usuario testadorBug = null;
        Estrategia estrategiaBug = null;
        SessaoTeste sessaoParaBug = null;
        int idBugInserido = -1;
        int idSessaoParaBug = -1;

        try {
            System.out.println("--- Criando pré-requisitos para Bug ---");

            // 1. Criar Projeto
            projetoBug = new Projeto(0, "Projeto Bugs T", "Projeto para testar bugs", new Timestamp(System.currentTimeMillis()));
            int idProjetoBug = projetoDAO.inserirProjeto(projetoBug); // Assumes throws SQLException
            projetoBug.setId(idProjetoBug);
            System.out.println("Projeto de bug criado com ID: " + idProjetoBug);

            // 2. Criar/Obter Usuário (Testador)
            testadorBug = new Usuario(0, "Testador Bug Main", "bugtestermain@email.com", "senha", "TESTADOR");
            Usuario existingUser = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail()); // Assumes throws SQLException
            if (existingUser == null) {
                usuarioDAO.inserirUsuario(testadorBug); // Assumes throws SQLException
                testadorBug = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail());
            } else {
                testadorBug = existingUser;
            }
            if (testadorBug == null || testadorBug.getId() == 0) throw new SQLException("Falha ao criar/obter testador.");
            System.out.println("Testador para bug obtido/criado com ID: " + testadorBug.getId());

            // 3. Criar Estratégia
            estrategiaBug = new Estrategia(0, "Estrategia Bugs Main", "Estratégia para bugs no main", "Exemplos...", "Dicas...");
            int idEstrategiaBug = estrategiaDAO.inserirEstrategia(estrategiaBug); // THIS throws SQLException
            estrategiaBug.setId(idEstrategiaBug);
            System.out.println("Estratégia de bug criada com ID: " + idEstrategiaBug);

            // 4. Criar SessaoTeste
            sessaoParaBug = new SessaoTeste();
            sessaoParaBug.setProjetoId(projetoBug.getId());
            sessaoParaBug.setTestadorId(testadorBug.getId());
            sessaoParaBug.setEstrategiaId(estrategiaBug.getId());
            sessaoParaBug.setTempoSessaoMinutos(30);
            sessaoParaBug.setDescricao("Sessão para registrar bugs (main).");
            sessaoParaBug.setStatus("CRIADO");
            sessaoParaBug.setDataHoraCriacao(new Timestamp(System.currentTimeMillis()));

            idSessaoParaBug = sessaoTesteDAO.inserirSessaoTeste(sessaoParaBug); // Assumes throws SQLException
            if (idSessaoParaBug == -1) throw new SQLException("Falha ao criar sessão de teste.");
            sessaoParaBug.setId(idSessaoParaBug);
            System.out.println("Sessão para bug criada com ID: " + idSessaoParaBug);

            sessaoTesteDAO.iniciarSessao(idSessaoParaBug); // Assumes throws SQLException
            System.out.println("Sessão para bug iniciada.");

            // --- Teste de Inserção de Bug ---
            System.out.println("\n--- Teste de Inserção de Bug ---");
            Bug novoBug = new Bug();
            novoBug.setSessaoTesteId(sessaoParaBug.getId());
            novoBug.setDescricao("Personagem atravessa parede na fase 1 (main).");
            novoBug.setSeveridade("ALTA");
            novoBug.setScreenshotUrl("http://example.com/screenshot1_main.png");

            idBugInserido = bugDAO.inserirBug(novoBug); // This method now throws SQLException
            if (idBugInserido != -1) {
                System.out.println("Bug inserido com sucesso! ID: " + novoBug.getId()); // Use novoBug.getId()
            } else {
                System.out.println("Falha ao inserir bug (ID -1 retornado, mas exceção deveria ter ocorrido).");
            }

            // Inserir outro bug
            Bug outroBug = new Bug();
            outroBug.setSessaoTesteId(sessaoParaBug.getId());
            outroBug.setDescricao("Texto incorreto em item de inventário (main).");
            outroBug.setSeveridade("MEDIA");
            outroBug.setScreenshotUrl("http://example.com/screenshot2_main.png");
            bugDAO.inserirBug(outroBug);
            System.out.println("Outro bug inserido com ID: " + outroBug.getId());


            // --- Teste de Busca por ID ---
            System.out.println("\n--- Teste de Busca de Bug por ID ---");
            if (novoBug.getId() > 0) {
                Bug bugBuscado = bugDAO.buscarBugPorId(novoBug.getId());
                if (bugBuscado != null) {
                    System.out.println("Bug encontrado: ID " + bugBuscado.getId());
                    System.out.println("Descrição: " + bugBuscado.getDescricao());

                    // --- Teste de Atualização ---
                    System.out.println("\n--- Teste de Atualização de Bug ---");
                    bugBuscado.setDescricao("Personagem atravessa parede na fase 1 (corrigido no main).");
                    bugBuscado.setSeveridade("BAIXA");
                    if (bugDAO.atualizarBug(bugBuscado)) {
                        System.out.println("Bug atualizado com sucesso!");
                        Bug bugAtualizado = bugDAO.buscarBugPorId(bugBuscado.getId());
                        if (bugAtualizado != null) {
                            System.out.println("Nova Descrição: " + bugAtualizado.getDescricao());
                            System.out.println("Nova Severidade: " + bugAtualizado.getSeveridade());
                        }
                    } else {
                        System.out.println("Falha ao atualizar bug.");
                    }
                } else {
                    System.out.println("Bug com ID " + novoBug.getId() + " não encontrado após inserção.");
                }
            }


            // --- Teste de Listagem de Bugs por Sessão ---
            System.out.println("\n--- Teste de Listagem de Bugs por Sessão de Teste ---");
            List<Bug> bugsDaSessao = bugDAO.listarBugsPorSessaoTeste(sessaoParaBug.getId());
            if (!bugsDaSessao.isEmpty()) {
                System.out.println("Bugs da Sessão ID " + sessaoParaBug.getId() + ":");
                for (Bug b : bugsDaSessao) {
                    System.out.println("ID: " + b.getId() + ", Descrição: " + b.getDescricao() + ", Severidade: " + b.getSeveridade());
                }
            } else {
                System.out.println("Nenhum bug encontrado para esta sessão.");
            }

            // --- Teste de Exclusão ---
            if (novoBug.getId() > 0) {
                System.out.println("\n--- Teste de Exclusão de Bug ---");
                if (bugDAO.excluirBug(novoBug.getId())) {
                    System.out.println("Bug com ID " + novoBug.getId() + " excluído com sucesso!");
                    idBugInserido = -1; // Mark as deleted for cleanup
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
                // Excluir bugs restantes da sessão se não foram excluídos individualmente
                if (idSessaoParaBug != -1) {
                    List<Bug> bugsRestantes = bugDAO.listarBugsPorSessaoTeste(idSessaoParaBug);
                    for (Bug b : bugsRestantes) {
                        System.out.println("Excluindo bug restante ID: " + b.getId());
                        bugDAO.excluirBug(b.getId());
                    }
                }

                if (idSessaoParaBug != -1) {
                    sessaoTesteDAO.finalizarSessao(idSessaoParaBug);
                    System.out.println("Sessão finalizada: " + idSessaoParaBug);
                    if (sessaoTesteDAO.excluirSessaoTeste(idSessaoParaBug)) {
                        System.out.println("Sessão de Teste removida: " + idSessaoParaBug);
                    }
                }
                if (estrategiaBug != null && estrategiaBug.getId() > 0) {
                    if (estrategiaDAO.excluirEstrategia(estrategiaBug.getId())) {
                        System.out.println("Estratégia de Bug removida: " + estrategiaBug.getId());
                    }
                }
                if (testadorBug != null && testadorBug.getId() > 0) {
                    // Verifique se o usuário não está associado a outras sessões/projetos antes de excluir,
                    // ou se a exclusão em cascata está configurada adequadamente.
                    // For testing, direct deletion is fine if it's a dedicated test user.
                    // System.out.println("Tentando excluir usuário: " + testadorBug.getId());
                    // if (usuarioDAO.excluirUsuario(testadorBug.getId())) {
                    //     System.out.println("Testador de Bug removido: " + testadorBug.getId());
                    // } else {
                    //    System.out.println("Não foi possível remover o testador ou já removido: " + testadorBug.getId());
                    // }
                }
                if (projetoBug != null && projetoBug.getId() > 0) {
                    if (projetoDAO.excluirProjeto(projetoBug.getId())) {
                        System.out.println("Projeto de Bug removido: " + projetoBug.getId());
                    }
                }
            } catch (SQLException ex) {
                System.err.println("!!!!!! Erro de SQL durante a limpeza no main do BugDAO: " + ex.getMessage());
                ex.printStackTrace();
            }
            System.out.println("--- Testes para BugDAO Concluídos ---");
        }
    }
}