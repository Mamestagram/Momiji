package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    String host;
    String port;
    String user;
    String password;
    String database;

    public DataBase() {
        Dotenv dotenv = Dotenv.configure().load();
        this.host = dotenv.get("DB_HOST");
        this.port = dotenv.get("DB_PORT");
        this.user = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");
        this.database = dotenv.get("DB_DATABASE");
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database +
                    "?autoReconnect=true",
                    user,
                    password
            );
        } catch (SQLException e) {
            e.fillInStackTrace();
            return null;
        }
    }
}
