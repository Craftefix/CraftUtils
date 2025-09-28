package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.language.LanguageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Language selection GUI with flag-based player heads
 */
public class LanguageGUI {
    
    public static void openLanguageGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        String currentLang = languageManager.getPlayerLanguage(player);
        
        CustomGUI gui = GUIBuilder.create(plugin, languageManager.getLegacy(player, "gui.language-title"), 1)
            // English flag (US flag pattern using player head)
            .setButton(2, Material.PLAYER_HEAD, 
                languageManager.getLegacy(player, "gui.english"),
                event -> {
                    languageManager.setPlayerLanguage(player, "en");
                    GUIFeedbackManager.playClickSound(player);
                    player.sendMessage(languageManager.getMessage(player, "general.language-changed"));
                    CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
                })
            // German flag (German flag pattern using player head)
            .setButton(6, Material.PLAYER_HEAD,
                languageManager.getLegacy(player, "gui.german"),
                event -> {
                    languageManager.setPlayerLanguage(player, "de");
                    GUIFeedbackManager.playClickSound(player);
                    player.sendMessage(languageManager.getMessage(player, "general.language-changed"));
                    CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
                })
            // Back button
            .setButton(8, Material.ARROW,
                languageManager.getLegacy(player, "gui.back"),
                event -> {
                    GUIFeedbackManager.playClickSound(player);
                    CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
                })
            .build();
            
        // Set custom player heads for flags - temporarily disabled due to compilation issues
        // TODO: Fix PlayerProfile type compatibility
        // setFlagHead(gui, 2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQzOTNmZmE5NDhhNmNjNzUyN2ZkNmUzODY2ZTY1YzE2NmM2Mzc5NTc2ZWYzYmY4ZmJhOTU1NjY4Yjc5ZSJ9fX0="); // US flag
        // setFlagHead(gui, 6, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY5NjQxOGVkNGU2ZDg0OTQzNWZhMTg0ZjFiMiJ9fX0="); // German flag
        
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }
    
    /**
     * Set custom player head texture using base64 encoded texture
     */
    private static void setFlagHead(CustomGUI gui, int slot, String texture) {
        try {
            var item = gui.getInventory().getItem(slot);
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                var meta = item.getItemMeta();
                if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
                    // Create player profile with custom texture (Paper API)
                    var profile = org.bukkit.Bukkit.getServer().createProfile(java.util.UUID.randomUUID(), "Flag");
                    var textures = profile.getTextures();
                    try {
                        textures.setSkin(new java.net.URL("http://textures.minecraft.net/texture/" + texture));
                        profile.setTextures(textures);
                        skullMeta.setPlayerProfile(profile);
                        item.setItemMeta(skullMeta);
                    } catch (java.net.MalformedURLException e) {
                        // Fallback to default heads if texture fails
                    }
                }
            }
        } catch (Exception e) {
            // Ignore texture setting errors
        }
    }
}