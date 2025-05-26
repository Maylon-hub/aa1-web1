// src/main/java/com/gametester/util/ConexaoDB.java
package com.gametester.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_testes_db";
    private static final String USER = "postgres"; // Usuário do PostgreSQL
    private static final String PASSWORD = "admin"; // Senha do Postgres!

    /**
     * Retorna uma nova conexão com o banco de dados.
     * @return Connection
     * @throws SQLException se ocorrer um erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver JDBC do PostgreSQL
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver PostgreSQL não encontrado. Verifique suas dependências.");
            throw new SQLException("Driver PostgreSQL não encontrado.", e);
        }
    }

    /**
     * Fecha a conexão com o banco de dados.
     * @param connection a conexão a ser fechada
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    // Você pode adicionar métodos para fechar Statement e ResultSet aqui também
}