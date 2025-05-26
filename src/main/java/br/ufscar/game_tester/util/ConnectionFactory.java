
        package br.ufscar.game_tester.util; // Certifique-se que o pacote está correto

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/game_test_support_db"; // Verifique o nome do seu banco e a porta
    private static final String USER = "postgres"; // Substitua pelo seu usuário do PostgreSQL
    private static final String PASSWORD = "212199"; // Substitua pela sua senha

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver"); // Garante que o driver JDBC está carregado
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL não encontrado!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados!", e);
        }
    }
}