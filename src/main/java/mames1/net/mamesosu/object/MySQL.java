package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL {

    final String host;
    final int port;
    final String user;
    final String password;
    final String name;

    public MySQL() {
        Dotenv dotenv = Dotenv.configure().load();
        host = dotenv.get("HOST");
        port = Integer.parseInt(dotenv.get("PORT"));
        user = dotenv.get("USER");
        password = dotenv.get("PASSWORD");
        name = dotenv.get("NAME");
    }

    public Connection getConnection() {

        try {

            AppLogger.log("MySQLに接続します...", LogLevel.INFO);

            return DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + name + "?autoReconnect=true",
                    user,
                    password
            );
        } catch (Exception e) {
            AppLogger.log("MySQLへの接続に失敗しました: " + e.getMessage(), LogLevel.ERROR);
            return null;
        }
    }
}
