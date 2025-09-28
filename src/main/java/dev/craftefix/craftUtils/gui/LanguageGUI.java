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
        String title = languageManager.getLegacy(player, "gui.language-title");
        CustomGUI gui = GUIBuilder.create(plugin, title, 1).build();
        
        // English flag with custom head from language file
        String englishHeadTexture = languageManager.getString(player, "gui.english-head");
        GUIItem englishItem = GUIItem.createCustomHeadButton(englishHeadTexture, 
            languageManager.getLegacy(player, "gui.english"), event -> {
                languageManager.setPlayerLanguage(player, "en");
                GUIFeedbackManager.playClickSound(player);
                player.sendMessage(languageManager.getMessage(player, "general.language-changed"));
                CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
            }).setLore(languageManager.getLegacy(player, "gui.english-desc"));
        gui.setItem(2, englishItem.getItemStack(), englishItem.getClickHandler());
            
        // German flag with custom head from language file
        String germanHeadTexture = languageManager.getString(player, "gui.german-head");
        GUIItem germanItem = GUIItem.createCustomHeadButton(germanHeadTexture,
            languageManager.getLegacy(player, "gui.german"), event -> {
                languageManager.setPlayerLanguage(player, "de");
                GUIFeedbackManager.playClickSound(player);
                player.sendMessage(languageManager.getMessage(player, "general.language-changed"));
                CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
            }).setLore(languageManager.getLegacy(player, "gui.german-desc"));
        gui.setItem(6, germanItem.getItemStack(), germanItem.getClickHandler());
            
        // Back button
        GUIItem backItem = GUIItem.createButton(Material.ARROW,
            languageManager.getLegacy(player, "gui.back"), event -> {
                GUIFeedbackManager.playClickSound(player);
                CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
            }).setLore(languageManager.getLegacy(player, "gui.back-desc"));
        gui.setItem(8, backItem.getItemStack(), backItem.getClickHandler());
        
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }

}