package dev.craftefix.craftUtils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

        private static HikariDataSource dataSource;

        // Initialize the HikariCP DataSource
        static {
            Main plugin = Main.getInstance();
            FileConfiguration config = plugin.getConfig();
            String username = config.getString("database.username");
            String host = config.getString("database.host");
            int port = config.getInt("database.port");
            String password = config.getString("database.password");
            String database = config.getString("database.database");

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setMaximumPoolSize(10);  // Adjust the pool size as needed

            dataSource = new HikariDataSource(hikariConfig);
        }

        // Get a connection from the pool
        public static Connection getConnection() throws SQLException {
            return dataSource.getConnection();
        }

        // Close the data source
        public static void close() {
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }