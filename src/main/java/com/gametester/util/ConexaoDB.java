package com.gametester.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.postgresql.PGProperty.USER;

public class ConexaoDB {
    private static final String URL = "jdbc:postgresql://localhost:9091/sistema_testes_db";
    private static final String USER = "postgres"; // Ou o seu usuário do PostgreSQL
    private static final String PASSWORD = "admin"; // <<< PONTO CRÍTICO!

    /**
     * Retorna uma nova conexão com o banco de dados.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, String.valueOf(USER), PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado.", e);
        }
    }

    /**
     * Fecha a conexão com o banco de dados.
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

    public static void close(ResultSet rs) {
    }
}