package dev.craftefix.craftUtils.language;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Language system for CraftUtils with per-player language settings
 */
public class LanguageManager {
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();
    private final Map<UUID, String> playerLanguages = new HashMap<>();
    private String defaultLanguage = "en";
    
    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeLanguages();
    }
    
    /**
     * Initialize language system and create default language files
     */
    private void initializeLanguages() {
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        
        // Create default language files
        createDefaultLanguageFile("en");
        createDefaultLanguageFile("de");
        
        // Load all language files
        loadLanguages();
    }
    
    /**
     * Create default language file if it doesn't exist
     */
    private void createDefaultLanguageFile(String lang) {
        File langFile = new File(plugin.getDataFolder(), "languages/" + lang + ".yml");
        if (!langFile.exists()) {
            try {
                // Try to copy from resources first
                try (InputStream resourceStream = plugin.getResource("languages/" + lang + ".yml")) {
                    if (resourceStream != null) {
                        Files.copy(resourceStream, langFile.toPath());
                        plugin.getLogger().info("Created language file: " + lang + ".yml from resources");
                    } else {
                        // Create default content
                        createDefaultLanguageContent(langFile, lang);
                        plugin.getLogger().info("Created default language file: " + lang + ".yml");
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create language file: " + lang + ".yml", e);
            }
        }
    }
    
    /**
     * Create default language content
     */
    private void createDefaultLanguageContent(File file, String lang) throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        
        if (lang.equals("en")) {
            // English translations
            config.set("general.prefix", "&6[CraftUtils] &r");
            config.set("general.no-permission", "&cYou don't have permission to use this command!");
            config.set("general.player-only", "&cThis command can only be used by players!");
            config.set("general.reload-success", "&aConfiguration reloaded successfully!");
            config.set("general.language-changed", "&aLanguage changed to English!");
            config.set("general.invalid-language", "&cInvalid language! Available: en, de");
            
            // GUI translations
            config.set("gui.main-title", "&6&lCraftUtils &8- &7Command GUI");
            config.set("gui.utilities", "&b&lUtilities");
            config.set("gui.utilities-desc", "&7Access utility commands");
            config.set("gui.abilities", "&a&lAbilities");
            config.set("gui.abilities-desc", "&7Manage player abilities");
            config.set("gui.gamemode", "&e&lGamemode");
            config.set("gui.gamemode-desc", "&7Change your gamemode");
            config.set("gui.homes-warps", "&d&lHomes & Warps");
            config.set("gui.homes-warps-desc", "&7Teleport to homes and warps");
            config.set("gui.admin", "&c&lAdmin");
            config.set("gui.admin-desc", "&7Access admin commands");
            config.set("gui.language", "&f&lLanguage");
            config.set("gui.language-desc", "&7Change your language");
            config.set("gui.sound-settings", "&e&lSound Settings");
            config.set("gui.sound-settings-desc", "&7Toggle GUI sounds");
            config.set("gui.back", "&7Back to Main Menu");
            config.set("gui.sounds-enabled", "&aGUI sounds enabled!");
            config.set("gui.sounds-disabled", "&cGUI sounds disabled!");
            
            // Language GUI
            config.set("gui.language-title", "&f&lLanguage Selection");
            config.set("gui.english", "&f&lEnglish");
            config.set("gui.english-desc", "&7Switch to English");
            config.set("gui.german", "&f&lDeutsch");
            config.set("gui.german-desc", "&7Switch to German");
            
        } else if (lang.equals("de")) {
            // German translations
            config.set("general.prefix", "&6[CraftUtils] &r");
            config.set("general.no-permission", "&cDu hast keine Berechtigung für diesen Befehl!");
            config.set("general.player-only", "&cDieser Befehl kann nur von Spielern verwendet werden!");
            config.set("general.reload-success", "&aKonfiguration erfolgreich neu geladen!");
            config.set("general.language-changed", "&aSprache zu Deutsch geändert!");
            config.set("general.invalid-language", "&cUngültige Sprache! Verfügbar: en, de");
            
            // GUI translations
            config.set("gui.main-title", "&6&lCraftUtils &8- &7Befehls GUI");
            config.set("gui.utilities", "&b&lHilfsmittel");
            config.set("gui.utilities-desc", "&7Zugriff auf Hilfsbefehle");
            config.set("gui.abilities", "&a&lFähigkeiten");
            config.set("gui.abilities-desc", "&7Spielerfähigkeiten verwalten");
            config.set("gui.gamemode", "&e&lSpielmodus");
            config.set("gui.gamemode-desc", "&7Ändere deinen Spielmodus");
            config.set("gui.homes-warps", "&d&lHeime & Warps");
            config.set("gui.homes-warps-desc", "&7Teleportiere zu Heimen und Warps");
            config.set("gui.admin", "&c&lAdmin");
            config.set("gui.admin-desc", "&7Zugriff auf Admin-Befehle");
            config.set("gui.language", "&f&lSprache");
            config.set("gui.language-desc", "&7Ändere deine Sprache");
            config.set("gui.sound-settings", "&e&lTon-Einstellungen");
            config.set("gui.sound-settings-desc", "&7GUI-Töne umschalten");
            config.set("gui.back", "&7Zurück zum Hauptmenü");
            config.set("gui.sounds-enabled", "&aGUI-Töne aktiviert!");
            config.set("gui.sounds-disabled", "&cGUI-Töne deaktiviert!");
            
            // Language GUI
            config.set("gui.language-title", "&f&lSprachauswahl");
            config.set("gui.english", "&f&lEnglish");
            config.set("gui.english-desc", "&7Zu Englisch wechseln");
            config.set("gui.german", "&f&lDeutsch");
            config.set("gui.german-desc", "&7Zu Deutsch wechseln");
        }
        
        config.save(file);
    }
    
    /**
     * Load all language files from the languages directory
     */
    public void loadLanguages() {
        languages.clear();
        File langDir = new File(plugin.getDataFolder(), "languages");
        
        if (!langDir.exists()) {
            return;
        }
        
        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langCode = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    languages.put(langCode, config);
                    plugin.getLogger().info("Loaded language: " + langCode);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Could not load language file: " + file.getName(), e);
                }
            }
        }
        
        if (languages.isEmpty()) {
            plugin.getLogger().warning("No language files loaded! Creating defaults...");
            initializeLanguages();
        }
    }
    
    /**
     * Get message for player in their preferred language
     */
    public Component getMessage(Player player, String key, Object... args) {
        String lang = getPlayerLanguage(player);
        return getMessage(lang, key, args);
    }
    
    /**
     * Get message in specific language
     */
    public Component getMessage(String lang, String key, Object... args) {
        YamlConfiguration config = languages.get(lang);
        if (config == null) {
            config = languages.get(defaultLanguage);
        }
        if (config == null) {
            return Component.text("Missing language: " + key, NamedTextColor.RED);
        }
        
        String message = config.getString(key, key);
        if (args.length > 0) {
            message = String.format(message, args);
        }
        
        // Convert color codes and return as Component
        return parseColorCodes(message);
    }
    
    /**
     * Parse color codes and return Adventure Component
     */
    private Component parseColorCodes(String text) {
        if (text == null) return Component.empty();
        
        // Replace Minecraft color codes with Adventure components
        text = text.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2")
                  .replace("&3", "§3").replace("&4", "§4").replace("&5", "§5")
                  .replace("&6", "§6").replace("&7", "§7").replace("&8", "§8")
                  .replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
                  .replace("&c", "§c").replace("&d", "§d").replace("&e", "§e")
                  .replace("&f", "§f").replace("&l", "§l").replace("&m", "§m")
                  .replace("&n", "§n").replace("&o", "§o").replace("&r", "§r");
        
        return Component.text(text);
    }
    
    /**
     * Get player's preferred language
     */
    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), defaultLanguage);
    }
    
    /**
     * Set player's preferred language
     */
    public void setPlayerLanguage(Player player, String language) {
        if (languages.containsKey(language)) {
            playerLanguages.put(player.getUniqueId(), language);
        }
    }
    
    /**
     * Get available languages
     */
    public Map<String, YamlConfiguration> getAvailableLanguages() {
        return new HashMap<>(languages);
    }
    
    /**
     * Check if language is available
     */
    public boolean isLanguageAvailable(String language) {
        return languages.containsKey(language);
    }
    
    /**
     * Get legacy string for player in their preferred language (for GUIs)
     */
    public String getLegacy(Player player, String key, Object... args) {
        String lang = getPlayerLanguage(player);
        return getLegacy(lang, key, args);
    }
    
    /**
     * Get legacy string in specific language (for GUIs)
     */
    public String getLegacy(String lang, String key, Object... args) {
        YamlConfiguration config = languages.get(lang);
        if (config == null) {
            config = languages.get(defaultLanguage);
        }
        if (config == null) {
            return key;
        }
        
        String message = config.getString(key, key);
        if (args.length > 0) {
            message = String.format(message, args);
        }
        
        // Convert & codes to § codes for legacy GUIs
        return message.replace("&0", "§0").replace("&1", "§1").replace("&2", "§2")
                     .replace("&3", "§3").replace("&4", "§4").replace("&5", "§5")
                     .replace("&6", "§6").replace("&7", "§7").replace("&8", "§8")
                     .replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
                     .replace("&c", "§c").replace("&d", "§d").replace("&e", "§e")
                     .replace("&f", "§f").replace("&l", "§l").replace("&m", "§m")
                     .replace("&n", "§n").replace("&o", "§o").replace("&r", "§r");
    }
    
    /**
     * Clear player language setting (cleanup on disconnect)
     */
    public void clearPlayer(Player player) {
        playerLanguages.remove(player.getUniqueId());
    }
}