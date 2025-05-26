// src/main/java/com/gametester/dao/ProjetoDAO.java
package com.gametester.dao;

import com.gametester.model.Projeto;
import com.gametester.model.Usuario; // Para gerenciar
import com.gametester.dao.UsuarioDAO;
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjetoDAO {

    public int inserirProjeto(Projeto projeto) {
        String sql = "INSERT INTO Projeto (nome, descricao) VALUES (?, ?) RETURNING id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idGerado = -1;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    // Opcional: Adicionar membros aqui se o projeto já tiver uma lista de membros ao ser criado
                    if (projeto.getMembrosPermitidos() != null && !projeto.getMembrosPermitidos().isEmpty()) {
                        adicionarMembrosAoProjeto(idGerado, projeto.getMembrosPermitidos(), conn);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir projeto: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close(stmt);
            ConexaoDB.closeConnection(conn);
        }
        return idGerado;
    }

    public Projeto buscarProjetoPorId(int id) {
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
                // Opcional: Carregar membros permitidos
                projeto.setMembrosPermitidos(buscarMembrosDoProjeto(id, conn));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar projeto por ID: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close(stmt);
            ConexaoDB.closeConnection(conn);
        }
        return projeto;
    }

    public boolean atualizarProjeto(Projeto projeto) {
        String sql = "UPDATE Projeto SET nome = ?, descricao = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transação

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setInt(3, projeto.getId());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Se o projeto for atualizado, também atualiza os membros
                limparMembrosDoProjeto(projeto.getId(), conn);
                if (projeto.getMembrosPermitidos() != null && !projeto.getMembrosPermitidos().isEmpty()) {
                    adicionarMembrosAoProjeto(projeto.getId(), projeto.getMembrosPermitidos(), conn);
                }
                conn.commit(); // Confirmar transação
                return true;
            } else {
                conn.rollback(); // Reverter transação
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar projeto: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro ao reverter transação: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Restaurar auto-commit
            } catch (SQLException ex) { /* Ignorar */ }
            ConexaoDB.close(stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public boolean excluirProjeto(int id) {
        String sql = "DELETE FROM Projeto WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir projeto: " + e.getMessage());
            // Tratar ConstraintViolationException (usuário em uso)
            return false;
        } finally {
            ConexaoDB.close(stmt);
            ConexaoDB.closeConnection(conn);
        }
    }

    public List<Projeto> listarTodosProjetos() {
        // Inclui ordenação e pode ser filtrado/ordenado por parâmetros no controller
        String sql = "SELECT id, nome, descricao, data_criacao FROM Projeto ORDER BY nome ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Projeto> projetos = new ArrayList<>();
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
                // Opcional: Carregar membros para cada projeto. Pode ser custoso para muitos projetos.
                // projeto.setMembrosPermitidos(buscarMembrosDoProjeto(projeto.getId(), conn));
                projetos.add(projeto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar projetos: " + e.getMessage());
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close(stmt);
            ConexaoDB.closeConnection(conn);
        }
        return projetos;
    }

    /**
     * Adiciona membros à tabela de associação Projeto_Membro.
     * @param projetoId ID do projeto.
     * @param membros Lista de usuários a serem adicionados como membros.
     * @param conn Conexão JDBC existente (para transação).
     * @throws SQLException
     */
    private void adicionarMembrosAoProjeto(int projetoId, List<Usuario> membros, Connection conn) throws SQLException {
        String sql = "INSERT INTO Projeto_Membro (projeto_id, usuario_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Usuario membro : membros) {
                stmt.setInt(1, projetoId);
                stmt.setInt(2, membro.getId());
                stmt.addBatch(); // Adiciona ao lote para execução em massa
            }
            stmt.executeBatch(); // Executa todas as inserções em lote
        }
    }

    /**
     * Remove todos os membros de um projeto.
     * @param projetoId ID do projeto.
     * @param conn Conexão JDBC existente (para transação).
     * @throws SQLException
     */
    private void limparMembrosDoProjeto(int projetoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM Projeto_Membro WHERE projeto_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projetoId);
            stmt.executeUpdate();
        }
    }

    /**
     * Busca os membros de um projeto específico.
     * @param projetoId ID do projeto.
     * @param conn Conexão JDBC existente (para transação ou reuso).
     * @return Lista de usuários membros.
     * @throws SQLException
     */
    public List<Usuario> buscarMembrosDoProjeto(int projetoId, Connection conn) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.email, u.tipo_perfil FROM Usuario u " +
                "JOIN Projeto_Membro pm ON u.id = pm.usuario_id WHERE pm.projeto_id = ?";
        List<Usuario> membros = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
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
        } finally {
            ConexaoDB.close(rs);
            ConexaoDB.close(stmt);
        }
        return membros;
    }

    // Sobrecarga para buscarMembrosDoProjeto se não houver conexão aberta
    public List<Usuario> buscarMembrosDoProjeto(int projetoId) {
        Connection conn = null;
        List<Usuario> membros = new ArrayList<>();
        try {
            conn = ConexaoDB.getConnection();
            membros = buscarMembrosDoProjeto(projetoId, conn);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar membros do projeto: " + e.getMessage());
        } finally {
            ConexaoDB.closeConnection(conn);
        }
        return membros;
    }

    public static void main(String[] args) {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO(); // Precisamos de usuários para testar membros

        // --- Teste de Inserção de Usuários para Membros ---
        System.out.println("--- Teste de Usuários para Membros ---");
        Usuario testador1 = new Usuario(0, "Testador A", "testadorA@email.com", "senha", "TESTADOR");
        Usuario testador2 = new Usuario(0, "Testador B", "testadorB@email.com", "senha", "TESTADOR");

        // Inserir se não existirem
        Usuario t1 = usuarioDAO.buscarUsuarioPorEmail(testador1.getEmail());
        if (t1 == null) {
            usuarioDAO.inserirUsuario(testador1);
            t1 = usuarioDAO.buscarUsuarioPorEmail(testador1.getEmail()); // Busca para obter o ID gerado
            System.out.println("Testador A inserido com ID: " + t1.getId());
        } else {
            System.out.println("Testador A já existe com ID: " + t1.getId());
        }

        Usuario t2 = usuarioDAO.buscarUsuarioPorEmail(testador2.getEmail());
        if (t2 == null) {
            usuarioDAO.inserirUsuario(testador2);
            t2 = usuarioDAO.buscarUsuarioPorEmail(testador2.getEmail());
            System.out.println("Testador B inserido com ID: " + t2.getId());
        } else {
            System.out.println("Testador B já existe com ID: " + t2.getId());
        }

        // Atualiza os objetos locais com os IDs do banco, caso tenham sido inseridos agora
        testador1.setId(t1.getId());
        testador2.setId(t2.getId());

        // --- Teste de Inserção de Projeto ---
        System.out.println("\n--- Teste de Inserção de Projeto ---");
        Projeto novoProjeto = new Projeto();
        novoProjeto.setNome("Projeto Alpha - Testes");
        novoProjeto.setDescricao("Descrição detalhada do Projeto Alpha para testes exploratórios.");
        // Não defina dataCriacao, o banco de dados cuidará disso
        novoProjeto.setMembrosPermitidos(Arrays.asList(testador1, testador2)); // Adiciona membros

        int idProjetoInserido = projetoDAO.inserirProjeto(novoProjeto);
        if (idProjetoInserido != -1) {
            System.out.println("Projeto inserido com sucesso! ID: " + idProjetoInserido);
            novoProjeto.setId(idProjetoInserido); // Atualiza o ID do objeto para testes futuros
        } else {
            System.out.println("Falha ao inserir projeto.");
        }

        // --- Teste de Busca por ID ---
        System.out.println("\n--- Teste de Busca de Projeto por ID ---");
        Projeto projetoBuscado = projetoDAO.buscarProjetoPorId(novoProjeto.getId());
        if (projetoBuscado != null) {
            System.out.println("Projeto encontrado: " + projetoBuscado.getNome());
            System.out.println("Descrição: " + projetoBuscado.getDescricao());
            System.out.println("Data de Criação: " + projetoBuscado.getDataCriacao());
            System.out.println("Membros: ");
            if (projetoBuscado.getMembrosPermitidos() != null && !projetoBuscado.getMembrosPermitidos().isEmpty()) {
                for (Usuario membro : projetoBuscado.getMembrosPermitidos()) {
                    System.out.println("  - " + membro.getNome() + " (" + membro.getEmail() + ")");
                }
            } else {
                System.out.println("  Nenhum membro associado.");
            }
        } else {
            System.out.println("Projeto com ID " + novoProjeto.getId() + " não encontrado.");
        }

        // --- Teste de Atualização de Projeto ---
        System.out.println("\n--- Teste de Atualização de Projeto ---");
        if (projetoBuscado != null) {
            projetoBuscado.setNome("Projeto Alpha - Atualizado");
            projetoBuscado.setDescricao("Nova descrição do Projeto Alpha.");
            // Remove um membro e adiciona outro
            List<Usuario> novosMembros = new ArrayList<>(Arrays.asList(testador1)); // Mantém testador1
            // novosMembros.add(novoUsuarioParaMembro); // Adicionaria um novo, se tivesse um novo usuário
            projetoBuscado.setMembrosPermitidos(novosMembros);


            if (projetoDAO.atualizarProjeto(projetoBuscado)) {
                System.out.println("Projeto atualizado com sucesso!");
                Projeto projetoAtualizado = projetoDAO.buscarProjetoPorId(projetoBuscado.getId());
                System.out.println("Nome atualizado: " + projetoAtualizado.getNome());
                System.out.println("Membros atualizados: ");
                if (projetoAtualizado.getMembrosPermitidos() != null) {
                    for (Usuario membro : projetoAtualizado.getMembrosPermitidos()) {
                        System.out.println("  - " + membro.getNome() + " (" + membro.getEmail() + ")");
                    }
                }
            } else {
                System.out.println("Falha ao atualizar projeto.");
            }
        }

        // --- Teste de Listagem de Projetos ---
        System.out.println("\n--- Teste de Listagem de Projetos ---");
        List<Projeto> todosProjetos = projetoDAO.listarTodosProjetos();
        if (!todosProjetos.isEmpty()) {
            System.out.println("Projetos no sistema:");
            for (Projeto p : todosProjetos) {
                System.out.println("ID: " + p.getId() + ", Nome: " + p.getNome() + ", Data Criação: " + p.getDataCriacao());
            }
        } else {
            System.out.println("Nenhum projeto cadastrado.");
        }

        // --- Teste de Exclusão de Projeto (CUIDADO!) ---
        // Descomente e use com cautela, pois excluirá o projeto e suas associações de membros.
        // if (idProjetoInserido != -1) {
        //     System.out.println("\n--- Teste de Exclusão de Projeto ---");
        //     if (projetoDAO.excluirProjeto(idProjetoInserido)) {
        //         System.out.println("Projeto com ID " + idProjetoInserido + " excluído com sucesso!");
        //     } else {
        //         System.out.println("Falha ao excluir projeto com ID " + idProjetoInserido);
        //     }
        // }
    }
}