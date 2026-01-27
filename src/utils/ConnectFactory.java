package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectFactory {
    private static String URL = "jdbc:mysql://localhost:3306/LibraryProject";
    private static String USER = "root";
    private static String PASSWORD = "java@123";

    private ConnectFactory() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: Erro ao conectar o banco de dados.", e);
        }
    }
}
