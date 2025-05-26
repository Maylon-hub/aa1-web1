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

    public int inserirBug(Bug bug) {
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
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir bug: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return idGerado;
    }

    public Bug buscarBugPorId(int id) {
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
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return bug;
    }

    public boolean atualizarBug(Bug bug) {
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
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean excluirBug(int id) {
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
            return false;
        } finally {
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public List<Bug> listarBugsPorSessaoTeste(int sessaoTesteId) {
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
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close((ResultSet) stmt);
            ConexaoDB.closeConnection(conn);
        }
        return bugs;
    }

    public static void main(String[] args) {
        BugDAO bugDAO = new BugDAO();
        SessaoTesteDAO sessaoTesteDAO = new SessaoTesteDAO();
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        // --- Pré-requisito: Criar uma Sessão de Teste ---
        System.out.println("--- Criando pré-requisito para Bug ---");
        // Criar projeto, testador e estratégia para a sessão
        Projeto projetoBug = new Projeto(0, "Projeto Bugs", "Projeto para testar bugs", new Timestamp(System.currentTimeMillis()));
        int idProjetoBug = projetoDAO.inserirProjeto(projetoBug);
        projetoBug.setId(idProjetoBug);

        Usuario testadorBug = new Usuario(0, "Testador Bug", "bugtester@email.com", "senha", "TESTADOR");
        if (usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail()) == null) {
            usuarioDAO.inserirUsuario(testadorBug);
            testadorBug = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail());
        } else {
            testadorBug = usuarioDAO.buscarUsuarioPorEmail(testadorBug.getEmail());
        }

        Estrategia estrategiaBug = new Estrategia(0, "Estrategia Bugs", "Estratégia para bugs", "...", "...");
        int idEstrategiaBug = estrategiaDAO.inserirEstrategia(estrategiaBug);
        estrategiaBug.setId(idEstrategiaBug);

        SessaoTeste sessaoParaBug = new SessaoTeste();
        sessaoParaBug.setProjetoId(projetoBug.getId());
        sessaoParaBug.setTestadorId(testadorBug.getId());
        sessaoParaBug.setEstrategiaId(estrategiaBug.getId());
        sessaoParaBug.setTempoSessaoMinutos(30);
        sessaoParaBug.setDescricao("Sessão para registrar bugs.");
        sessaoParaBug.setStatus("EM_EXECUCAO"); // Deve estar em execução para registrar bugs
        sessaoParaBug.setDataHoraCriacao(new Timestamp(System.currentTimeMillis()));
        sessaoParaBug.setDataHoraInicio(new Timestamp(System.currentTimeMillis()));

        int idSessaoParaBug = sessaoTesteDAO.inserirSessaoTeste(sessaoParaBug);
        if (idSessaoParaBug != -1) {
            System.out.println("Sessão para bug criada com ID: " + idSessaoParaBug);
            sessaoParaBug.setId(idSessaoParaBug);
            // Iniciar a sessão após a criação
            sessaoTesteDAO.iniciarSessao(idSessaoParaBug);
        } else {
            System.out.println("Falha ao criar sessão para bug. Saindo do teste.");
            return;
        }

        // --- Teste de Inserção de Bug ---
        System.out.println("\n--- Teste de Inserção de Bug ---");
        Bug novoBug = new Bug();
        novoBug.setSessaoTesteId(sessaoParaBug.getId());
        novoBug.setDescricao("Personagem atravessa parede na fase 1.");
        novoBug.setSeveridade("ALTA");
        novoBug.setScreenshotUrl("http://example.com/screenshot1.png");

        int idBugInserido = bugDAO.inserirBug(novoBug);
        if (idBugInserido != -1) {
            System.out.println("Bug inserido com sucesso! ID: " + idBugInserido);
            novoBug.setId(idBugInserido); // Atualiza o ID
        } else {
            System.out.println("Falha ao inserir bug.");
        }

        // Inserir outro bug
        Bug outroBug = new Bug();
        outroBug.setSessaoTesteId(sessaoParaBug.getId());
        outroBug.setDescricao("Texto incorreto em item de inventário.");
        outroBug.setSeveridade("MEDIA");
        outroBug.setScreenshotUrl("http://example.com/screenshot2.png");
        bugDAO.inserirBug(outroBug);

        // --- Teste de Busca por ID ---
        System.out.println("\n--- Teste de Busca de Bug por ID ---");
        Bug bugBuscado = bugDAO.buscarBugPorId(novoBug.getId());
        if (bugBuscado != null) {
            System.out.println("Bug encontrado: ID " + bugBuscado.getId());
            System.out.println("Descrição: " + bugBuscado.getDescricao());
            System.out.println("Severidade: " + bugBuscado.getSeveridade());
            System.out.println("URL Screenshot: " + bugBuscado.getScreenshotUrl());
        } else {
            System.out.println("Bug com ID " + novoBug.getId() + " não encontrado.");
        }

        // --- Teste de Atualização ---
        System.out.println("\n--- Teste de Atualização de Bug ---");
        if (bugBuscado != null) {
            bugBuscado.setDescricao("Personagem atravessa parede na fase 1 (corrigir colisão).");
            bugBuscado.setSeveridade("BAIXA"); // Mudando a severidade
            if (bugDAO.atualizarBug(bugBuscado)) {
                System.out.println("Bug atualizado com sucesso!");
                Bug bugAtualizado = bugDAO.buscarBugPorId(bugBuscado.getId());
                System.out.println("Nova Descrição: " + bugAtualizado.getDescricao());
                System.out.println("Nova Severidade: " + bugAtualizado.getSeveridade());
            } else {
                System.out.println("Falha ao atualizar bug.");
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

        // --- Teste de Exclusão (CUIDADO!) ---
        // Descomente e use com cautela.
        // if (idBugInserido != -1) {
        //     System.out.println("\n--- Teste de Exclusão de Bug ---");
        //     if (bugDAO.excluirBug(idBugInserido)) {
        //         System.out.println("Bug com ID " + idBugInserido + " excluído com sucesso!");
        //     } else {
        //         System.out.println("Falha ao excluir bug com ID " + idBugInserido);
        //     }
        // }

        // --- Limpeza dos pré-requisitos (Opcional, com cautela) ---
        System.out.println("\n--- Limpando pré-requisitos ---");
        // Finaliza a sessão antes de tentar excluir.
        sessaoTesteDAO.finalizarSessao(idSessaoParaBug);

        // Exclua a sessão de teste (que por CASCADE deve excluir os bugs associados, se configurado)
        if (idSessaoParaBug != -1 && sessaoTesteDAO.excluirSessaoTeste(idSessaoParaBug)) {
            System.out.println("Sessão de Teste removida.");
        }
        if (idProjetoBug != -1 && projetoDAO.excluirProjeto(idProjetoBug)) {
            System.out.println("Projeto removido.");
        }
        if (testadorBug != null && usuarioDAO.excluirUsuario(testadorBug.getId())) {
            System.out.println("Testador de Bug removido.");
        }
        if (idEstrategiaBug != -1 && estrategiaDAO.excluirEstrategia(idEstrategiaBug)) {
            System.out.println("Estratégia de Bug removida.");
        }
    }
}

