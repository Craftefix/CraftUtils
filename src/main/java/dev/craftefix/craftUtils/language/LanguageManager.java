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

    /**
     * Initializes the LanguageManager, setting up language file management and player language preferences.
     *
     * Loads the default and available language files, validates them against the default, and creates the database table for storing player language settings.
     */
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

    /****
     * Creates the `player_languages` table in the database if it does not already exist.
     * The table stores player UUIDs and their associated language codes.
     * Logs a severe error if the table creation fails.
     */
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

    /**
     * Loads a YAML language configuration file for the specified language code.
     *
     * If the language file does not exist in the plugin's `lang` directory, it is copied from the plugin resources.
     *
     * @param langCode the language code (e.g., "en_US") to load
     * @return the loaded YamlConfiguration for the specified language
     */
    private YamlConfiguration loadLanguageFile(String langCode) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + langCode + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langCode + ".yml", false);
        }
        return YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Loads all language YAML files from the plugin's `lang` directory into memory and updates the list of available language codes.
     *
     * Creates the `lang` directory if it does not exist. Only files with a `.yml` extension are loaded. Logs an error if file loading fails.
     */
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

    /**
     * Checks all loaded language files for missing translation keys compared to the default language.
     *
     * Logs warnings for any language file that does not contain all keys present in the default language ("en_US").
     */
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

    /**
     * Retrieves all non-section keys from the given YAML configuration.
     *
     * @param config the YAML configuration to extract keys from
     * @return a list of all keys that do not represent configuration sections
     */
    private List<String> getAllKeys(YamlConfiguration config) {
        List<String> keys = new ArrayList<>();
        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) continue;
            keys.add(key);
        }
        return keys;
    }

    /**
     * Sets the language preference for a player and persists it to the database.
     *
     * @param player the player whose language is being set
     * @param langCode the language code to assign to the player
     * @throws IllegalArgumentException if the specified language code is not available
     */
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

    /**
     * Retrieves the language code associated with the specified player.
     *
     * @param player the player whose language code is to be retrieved
     * @return the player's language code, or "en_US" if none is set
     */
    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), "en_US");
    }

    /**
     * Loads a player's language preference from the database and updates the in-memory mapping.
     * <p>
     * If the player has no language set or if a database error occurs, defaults to "en_US".
     *
     * @param player the player whose language preference should be loaded
     */
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

    /**
     * Returns a list of all available language codes.
     *
     * @return a list containing the codes of all loaded languages
     */
    public List<String> getAvailableLanguages() {
        return new ArrayList<>(availableLanguages);
    }

    /**
     * Retrieves the language configuration for the specified player.
     *
     * Returns the loaded language configuration corresponding to the player's language code,
     * or the default language configuration if the player's language is not found.
     *
     * @param player the player whose language configuration is requested
     * @return the appropriate YamlConfiguration for the player
     */
    public YamlConfiguration getPlayerLangConfig(Player player) {
        String langCode = getPlayerLanguage(player);
        return loadedLanguages.getOrDefault(langCode, defaultLang);
    }
}
