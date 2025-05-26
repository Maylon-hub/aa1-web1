// src/main/java/com/gametestingtool/util/ConexaoDB.java
package com.gametester.util;

import java.sql.*;

public class ConexaoDB {
    // URL de conexão com o seu banco de dados PostgreSQL
    // Lembre-se que o nome do banco de dados que você criou é 'sistema_testes_db'
    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_testes_db";
    private static final String USER = "postgres"; // Seu usuário do PostgreSQL
    private static final String PASSWORD = "admin"; // **ATENÇÃO: SUBSTITUA PELA SUA SENHA DO POSTGRESQL**

    /**
     * Retorna uma nova conexão com o banco de dados.
     * @return Objeto Connection para interagir com o DB.
     * @throws SQLException se ocorrer um erro na conexão ou carregamento do driver.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver JDBC do PostgreSQL.
            // Para versões mais recentes do JDBC e Java, isso pode não ser estritamente necessário,
            // mas é uma boa prática para garantir.
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver PostgreSQL não encontrado. Verifique se o JAR está no classpath.");
            // Lança uma SQLException para que quem chamar o método possa tratar.
            throw new SQLException("Driver PostgreSQL não encontrado.", e);
        }
    }

    /**
     * Fecha a conexão com o banco de dados.
     * É crucial para liberar recursos.
     * @param connection a conexão a ser fechada.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                // Em um sistema real, você logaria isso em um logger.
            }
        }
    }

    // Métodos auxiliares para fechar Statement e ResultSet para evitar duplicação de código
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
            }
        }
    }

    public static void close(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar PreparedStatement: " + e.getMessage());
            }
        }
    }
}