package dev.craftefix.craftUtils.migration.migrations;

import dev.craftefix.craftUtils.migration.DatabaseMigration;
import dev.craftefix.craftUtils.migration.MigrationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddUserPreferencesMigration extends DatabaseMigration {

    public AddUserPreferencesMigration() {
        super("001_add_user_preferences", "Add user preferences table for storing player settings");
    }

    @Override
    public void up() throws MigrationException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS craftutils_user_preferences (
                player_uuid VARCHAR(36) PRIMARY KEY,
                language VARCHAR(10) DEFAULT 'en',
                gui_sound_enabled BOOLEAN DEFAULT TRUE,
                teleport_effects_enabled BOOLEAN DEFAULT TRUE,
                auto_accept_tpa BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            
            stmt.executeUpdate();
            logger.info("Created craftutils_user_preferences table");
            
        } catch (SQLException e) {
            throw new MigrationException("Failed to create user preferences table", e);
        }
    }

    @Override
    public void down() throws MigrationException {
        String dropTableSQL = "DROP TABLE IF EXISTS craftutils_user_preferences";

        try (Connection conn = getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(dropTableSQL)) {
            
            stmt.executeUpdate();
            logger.info("Dropped craftutils_user_preferences table");
            
        } catch (SQLException e) {
            throw new MigrationException("Failed to drop user preferences table", e);
        }
    }
}