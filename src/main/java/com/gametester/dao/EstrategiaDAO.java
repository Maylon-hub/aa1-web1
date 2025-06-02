// src/main/java/com/gametester/dao/EstrategiaDAO.java
package com.gametester.dao;

import com.gametester.model.Estrategia;
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Ensure this is imported
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaDAO {

    public int inserirEstrategia(Estrategia estrategia) throws SQLException {
        String sql = "INSERT INTO Estrategia (nome, descricao, exemplos, dicas) VALUES (?, ?, ?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idGerado = -1;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, estrategia.getNome());
            stmt.setString(2, estrategia.getDescricao());
            stmt.setString(3, estrategia.getExemplos());
            stmt.setString(4, estrategia.getDicas());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    estrategia.setId(idGerado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir estratégia: " + e.getMessage());
            throw e;
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

    public Estrategia buscarEstrategiaPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, exemplos, dicas FROM Estrategia WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Estrategia estrategia = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                estrategia = new Estrategia();
                estrategia.setId(rs.getInt("id"));
                estrategia.setNome(rs.getString("nome"));
                estrategia.setDescricao(rs.getString("descricao"));
                estrategia.setExemplos(rs.getString("exemplos"));
                estrategia.setDicas(rs.getString("dicas"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar estratégia por ID: " + e.getMessage());
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
        return estrategia;
    }

    public boolean atualizarEstrategia(Estrategia estrategia) throws SQLException {
        String sql = "UPDATE Estrategia SET nome = ?, descricao = ?, exemplos = ?, dicas = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, estrategia.getNome());
            stmt.setString(2, estrategia.getDescricao());
            stmt.setString(3, estrategia.getExemplos());
            stmt.setString(4, estrategia.getDicas());
            stmt.setInt(5, estrategia.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estratégia: " + e.getMessage());
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

    public boolean excluirEstrategia(int id) throws SQLException {
        String sql = "DELETE FROM Estrategia WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir estratégia: " + e.getMessage());
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

    public List<Estrategia> listarTodasEstrategias() throws SQLException {
        String sql = "SELECT id, nome, descricao, exemplos, dicas FROM Estrategia ORDER BY nome ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Estrategia> estrategias = new ArrayList<>();
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Estrategia estrategia = new Estrategia();
                estrategia.setId(rs.getInt("id"));
                estrategia.setNome(rs.getString("nome"));
                estrategia.setDescricao(rs.getString("descricao"));
                estrategia.setExemplos(rs.getString("exemplos"));
                estrategia.setDicas(rs.getString("dicas"));
                estrategias.add(estrategia);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estratégias: " + e.getMessage());
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
        return estrategias;
    }

    public static void main(String[] args) {
        EstrategiaDAO estrategiaDAO = new EstrategiaDAO();

        // --- Teste de Inserção ---
        System.out.println("--- Teste de Inserção de Estratégia ---");
        Estrategia novaEstrategia = new Estrategia();
        novaEstrategia.setNome("Estratégia de Empatia Teste DAO");
        novaEstrategia.setDescricao("Testar o jogo do ponto de vista de um jogador iniciante, via DAO test.");
        novaEstrategia.setExemplos("Exemplo DAO: 'Como um jogador que nunca jogou isso reagiria ao tutorial?'");
        novaEstrategia.setDicas("Dica DAO: Evite pular diálogos ou instruções iniciais.");

        try {
            int idEstrategiaInserida = estrategiaDAO.inserirEstrategia(novaEstrategia);
            if (idEstrategiaInserida != -1) {
                System.out.println("Estratégia inserida com sucesso! ID: " + idEstrategiaInserida);
            } else {
                System.out.println("Falha ao inserir estratégia (ID -1 retornado).");
            }

            //  Teste de Busca por ID
            if (novaEstrategia.getId() > 0) { // Check if ID was set
                System.out.println("\n--- Teste de Busca de Estratégia por ID ---");
                Estrategia estrategiaBuscada = estrategiaDAO.buscarEstrategiaPorId(novaEstrategia.getId());
                if (estrategiaBuscada != null) {
                    System.out.println("Estratégia encontrada: " + estrategiaBuscada.getNome());
                    System.out.println("Descrição: " + estrategiaBuscada.getDescricao());

                    //  Teste de Atualização
                    System.out.println("\n--- Teste de Atualização de Estratégia ---");
                    estrategiaBuscada.setNome("Estratégia de Empatia (Revisada DAO)");
                    estrategiaBuscada.setDicas("Dica DAO: Preste atenção nos sinais visuais e sonoros.");
                    if (estrategiaDAO.atualizarEstrategia(estrategiaBuscada)) {
                        System.out.println("Estratégia atualizada com sucesso!");
                        Estrategia estrategiaAtualizada = estrategiaDAO.buscarEstrategiaPorId(estrategiaBuscada.getId());
                        if(estrategiaAtualizada != null) {
                            System.out.println("Nome atualizado: " + estrategiaAtualizada.getNome());
                            System.out.println("Dicas atualizadas: " + estrategiaAtualizada.getDicas());
                        }
                    } else {
                        System.out.println("Falha ao atualizar estratégia.");
                    }
                } else {
                    System.out.println("Estratégia com ID " + novaEstrategia.getId() + " não encontrada.");
                }
            }


            //  Teste de Listagem
            System.out.println("\n--- Teste de Listagem de Estratégias ---");
            List<Estrategia> todasEstrategias = estrategiaDAO.listarTodasEstrategias();
            if (!todasEstrategias.isEmpty()) {
                System.out.println("Estratégias no sistema:");
                for (Estrategia e : todasEstrategias) {
                    System.out.println("ID: " + e.getId() + ", Nome: " + e.getNome());
                }
            } else {
                System.out.println("Nenhuma estratégia cadastrada.");
            }

        } catch (SQLException e) {
            System.err.println("Erro no método main do DAO durante teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}