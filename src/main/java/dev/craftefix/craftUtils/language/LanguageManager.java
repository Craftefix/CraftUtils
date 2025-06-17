package dev.craftefix.craftUtils.language;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.DatabaseManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

public class LanguageManager {
    private final Main plugin;
    private final DatabaseManager databaseManager;
    private final Map<String, YamlConfiguration> loadedLanguages;
    private final Map<UUID, String> playerLanguages;
    private final YamlConfiguration defaultLang;
    private final List<String> availableLanguages;

    public LanguageManager(Main plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.loadedLanguages = new HashMap<>();
        this.playerLanguages = new HashMap<>();
        this.availableLanguages = new ArrayList<>();

        // Initialize database table
        createLanguageTable();

        // Load the default language first
        this.defaultLang = loadLanguageFile("en_US");
        loadAllLanguages();
        validateAllLanguages();
    }

    private void createLanguageTable() {
        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS player_languages (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "language_code VARCHAR(10) NOT NULL" +
                ");"
            );
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create language table", e);
        }
    }

    private YamlConfiguration loadLanguageFile(String langCode) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + langCode + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langCode + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(langFile);
    }

    public void loadAllLanguages() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists() || !langDir.isDirectory()) {
            langDir.mkdirs();
            return;
        }

        try (Stream<Path> paths = Files.walk(langDir.toPath())) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".yml"))
                 .forEach(path -> {
                     String fileName = path.getFileName().toString();
                     String langCode = fileName.substring(0, fileName.length() - 4);
                     loadedLanguages.put(langCode, loadLanguageFile(langCode));
                     availableLanguages.add(langCode);
                 });
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load language files", e);
        }
    }

    public void validateAllLanguages() {
        YamlConfiguration defaultConfig = loadedLanguages.get("en_US");
        if (defaultConfig == null) {
            plugin.getLogger().severe("Default language (en_US) is missing!");
            return;
        }

        List<String> defaultKeys = getAllKeys(defaultConfig);

        for (Map.Entry<String, YamlConfiguration> entry : loadedLanguages.entrySet()) {
            if (entry.getKey().equals("en_US")) continue;

            List<String> missingKeys = new ArrayList<>();
            List<String> langKeys = getAllKeys(entry.getValue());

            for (String key : defaultKeys) {
                if (!langKeys.contains(key)) {
                    missingKeys.add(key);
                }
            }

            if (!missingKeys.isEmpty()) {
                plugin.getLogger().warning("Language file " + entry.getKey() + " is missing the following keys:");
                missingKeys.forEach(key -> plugin.getLogger().warning("- " + key));
            }
        }
    }

    private List<String> getAllKeys(YamlConfiguration config) {
        List<String> keys = new ArrayList<>();
        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;
            keys.add(key);
        }
        return keys;
    }

    public void setPlayerLanguage(Player player, String langCode) {
        if (!availableLanguages.contains(langCode)) {
            throw new IllegalArgumentException("Language " + langCode + " is not available");
        }

        playerLanguages.put(player.getUniqueId(), langCode);

        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO player_languages (uuid, language_code) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE language_code = ?;"
            );
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, langCode);
            stmt.setString(3, langCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player language", e);
        }
    }

    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), "en_US");
    }

    public void loadPlayerLanguage(Player player) {
        try (Connection conn = databaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT language_code FROM player_languages WHERE uuid = ?;"
            );
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String langCode = rs.getString("language_code");
                playerLanguages.put(player.getUniqueId(), langCode);
            } else {
                playerLanguages.put(player.getUniqueId(), "en_US");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load player language", e);
            playerLanguages.put(player.getUniqueId(), "en_US");
        }
    }

    public List<String> getAvailableLanguages() {
        return new ArrayList<>(availableLanguages);
    }

    public YamlConfiguration getPlayerLangConfig(Player player) {
        String langCode = getPlayerLanguage(player);
        return loadedLanguages.getOrDefault(langCode, defaultLang);
    }
}
