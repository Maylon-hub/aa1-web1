package com.gametester.dao;

import com.gametester.model.Projeto;
import com.gametester.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    /**
     * Insere um novo projeto no banco de dados.
     * A data de criação é definida automaticamente no momento da inserção.
     * O ID e a data de criação são atualizados no objeto Projeto passado como argumento.
     *
     * @param projeto O objeto Projeto a ser inserido (sem ID, ou ID será ignorado).
     * @return O objeto Projeto atualizado com o ID e data de criação definidos pelo banco.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public Projeto inserirProjeto(Projeto projeto) throws SQLException {
        String sql = "INSERT INTO Projeto (nome, descricao, data_criacao) VALUES (?, ?, ?) RETURNING id, data_criacao";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoDB.getConnection();
            stmt = conn.prepareStatement(sql); // Com RETURNING, não precisamos de Statement.RETURN_GENERATED_KEYS explicitamente para o PreparedStatement

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());

            Timestamp dataCriacaoAtual = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(3, dataCriacaoAtual);
            // Atualiza o objeto projeto com a data de criação que será enviada ao banco
            projeto.setDataCriacao(dataCriacaoAtual);

            rs = stmt.executeQuery(); // Usar executeQuery() com RETURNING para obter o ResultSet

            if (rs.next()) {
                projeto.setId(rs.getInt("id"));
                // Atualiza o objeto projeto com a data de criação exata retornada pelo banco (pode ter precisão diferente ou default do BD)
                projeto.setDataCriacao(rs.getTimestamp("data_criacao"));
            } else {
                // Isso seria inesperado se a inserção ocorreu e RETURNING foi usado.
                throw new SQLException("Falha ao inserir projeto, não foi possível obter o ID ou data de criação gerados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir projeto: " + e.getMessage());
            throw e; // Re-lança a exceção para ser tratada pela camada chamadora
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement em inserirProjeto: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return projeto; // Retorna o objeto projeto com ID e data de criação preenchidos
    }

    /**
     * Busca um projeto pelo seu ID.
     * @param id O ID do projeto a ser buscado.
     * @return O objeto Projeto encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
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
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement em buscarProjetoPorId: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return projeto;
    }

    /**
     * Lista todos os projetos cadastrados, ordenados pelo nome.
     * @return Uma lista de objetos Projeto.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public List<Projeto> listarTodosProjetos() throws SQLException {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, data_criacao FROM Projeto ORDER BY nome ASC";
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
            System.err.println("Erro ao listar projetos: " + e.getMessage());
            throw e;
        } finally {
            ConexaoDB.close(rs);
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement em listarTodosProjetos: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return projetos;
    }

    /**
     * Atualiza os dados de um projeto existente no banco de dados.
     * A data de criação não é alterada.
     * @param projeto O objeto Projeto com os dados atualizados (deve conter o ID do projeto a ser atualizado).
     * @return true se a atualização foi bem-sucedida (pelo menos uma linha afetada), false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    public boolean atualizarProjeto(Projeto projeto) throws SQLException {
        String sql = "UPDATE Projeto SET nome = ?, descricao = ? WHERE id = ?";
        // data_criacao geralmente não é atualizada.
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
            if (stmt != null) { try { stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar PreparedStatement em atualizarProjeto: " + e.getMessage());} }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    /**
     * Exclui um projeto do banco de dados pelo seu ID.
     * @param id O ID do projeto a ser excluído.
     * @return true se a exclusão foi bem-sucedida (pelo menos uma linha afetada), false caso contrário.
     * @throws SQLException Se ocorrer um erro de banco de dados (ex: violação de chave estrangeira se o projeto estiver em uso).
     */
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
            System.err.println("Erro ao excluir projeto com ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar PreparedStatement ao excluir projeto: " + e.getMessage());
                }
            }
            ConexaoDB.closeConnection(conn);
        }
        return rowsAffected > 0;
    }

    // Método main para testes rápidos do DAO (opcional)
    public static void main(String[] args) {
        ProjetoDAO projetoDAO = new ProjetoDAO();
        System.out.println("--- Testando ProjetoDAO ---");

        // Teste de Inserção
        System.out.println("\n--- Tentando Inserir Projeto ---");
        Projeto novoProjeto = new Projeto();
        novoProjeto.setNome("Projeto Alpha de Teste DAO");
        novoProjeto.setDescricao("Descrição do Projeto Alpha para testes via DAO.");
        try {
            projetoDAO.inserirProjeto(novoProjeto); // O ID e dataCriacao serão preenchidos no objeto novoProjeto
            if (novoProjeto.getId() > 0) {
                System.out.println("Projeto inserido com sucesso! ID: " + novoProjeto.getId() + ", Nome: " + novoProjeto.getNome() + ", Criado em: " + novoProjeto.getDataCriacao());
            } else {
                System.out.println("Falha ao inserir projeto (ID não foi gerado).");
            }

            // Teste de Busca por ID (usando o ID do projeto recém-inserido)
            if (novoProjeto.getId() > 0) {
                System.out.println("\n--- Tentando Buscar Projeto ID: " + novoProjeto.getId() + " ---");
                Projeto projetoBuscado = projetoDAO.buscarProjetoPorId(novoProjeto.getId());
                if (projetoBuscado != null) {
                    System.out.println("Projeto encontrado: " + projetoBuscado);

                    // Teste de Atualização
                    System.out.println("\n--- Tentando Atualizar Projeto ID: " + projetoBuscado.getId() + " ---");
                    projetoBuscado.setNome("Projeto Alpha (Atualizado via DAO)");
                    projetoBuscado.setDescricao("Descrição atualizada do Projeto Alpha.");
                    boolean atualizado = projetoDAO.atualizarProjeto(projetoBuscado);
                    if (atualizado) {
                        System.out.println("Projeto atualizado com sucesso.");
                        Projeto projetoRebuscado = projetoDAO.buscarProjetoPorId(projetoBuscado.getId());
                        System.out.println("Dados após atualização: " + projetoRebuscado);
                    } else {
                        System.out.println("Falha ao atualizar projeto.");
                    }
                } else {
                    System.out.println("Projeto com ID " + novoProjeto.getId() + " não encontrado para busca.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro durante o teste de inserção/busca/atualização no main: " + e.getMessage());
            e.printStackTrace();
        }

        // Teste de Listagem
        System.out.println("\n--- Tentando Listar Todos os Projetos ---");
        try {
            List<Projeto> todosProjetos = projetoDAO.listarTodosProjetos();
            if (todosProjetos.isEmpty()) {
                System.out.println("Nenhum projeto encontrado.");
            } else {
                System.out.println("Total de projetos encontrados: " + todosProjetos.size());
                for (Projeto p : todosProjetos) {
                    System.out.println(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar projetos no main: " + e.getMessage());
            e.printStackTrace();
        }

        // Teste de Exclusão (use com cautela e com um ID que possa ser excluído)
        // Lembre-se que a exclusão pode falhar se houver sessões de teste associadas a este projeto (chave estrangeira)
        /*
        if (novoProjeto != null && novoProjeto.getId() > 0) {
            System.out.println("\n--- Tentando Excluir Projeto ID: " + novoProjeto.getId() + " ---");
            try {
                boolean excluido = projetoDAO.excluirProjeto(novoProjeto.getId());
                if (excluido) {
                    System.out.println("Projeto com ID " + novoProjeto.getId() + " excluído com sucesso.");
                } else {
                    System.out.println("Não foi possível excluir o projeto com ID " + novoProjeto.getId() + " (pode não existir ou estar em uso).");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao excluir projeto no main: " + e.getMessage());
                e.printStackTrace();
            }
        }
        */
        System.out.println("\n--- Testes do ProjetoDAO Concluídos ---");
    }
}