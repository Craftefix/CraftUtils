package dev.craftefix.craftUtils.gui;

import dev.craftefix.craftUtils.language.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abilities GUI with fly, heal, feed, and trash commands
 */
public class AbilitiesGUI implements RefreshableGUI {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;
    private final CooldownManager cooldownManager;
    private CustomGUI gui;
    
    private AbilitiesGUI(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.cooldownManager = CooldownManager.getInstance();
    }
    
    public static void openAbilitiesGUI(Player player, JavaPlugin plugin, LanguageManager languageManager) {
        AbilitiesGUI abilitiesGUI = new AbilitiesGUI(plugin, languageManager);
        abilitiesGUI.buildAndOpen(player);
    }
    
    private void buildAndOpen(Player player) {
        String title = languageManager != null ? 
            languageManager.getLegacy(player, "gui.abilities") : 
            "§a§lAbilities";
            
        this.gui = GUIBuilder.create(plugin, title, 1).build();
        buildItems(player);
            
        gui.open(player);
        GUIFeedbackManager.playClickSound(player);
    }

    @Override
    public void refreshAndReopen(Player player) {
        if (gui != null) {
            gui.refresh();
            buildItems(player);
        }
    }
    
    private void buildItems(Player player) {
        int slot = 1;
        
        // Fly command with status
        if (player.hasPermission("CraftUtils.fly")) {
            boolean canFly = player.getAllowFlight();
            Material material = canFly ? Material.ELYTRA : Material.FEATHER;
            String status = canFly ? 
                languageManager.getLegacy(player, "gui.fly-on") : 
                languageManager.getLegacy(player, "gui.fly-off");
            
            GUIItem flyItem = GUIItem.createButton(material, 
                languageManager.getLegacy(player, "gui.fly") + status, event -> {
                if (cooldownManager.isOnCooldown(player, "fly")) {
                    long remaining = cooldownManager.getRemainingCooldown(player, "fly");
                    String timeStr = cooldownManager.formatRemainingTime(remaining);
                    String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                        "&cYou must wait {time} before using this again.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        cooldownMsg.replace("{time}", timeStr)));
                    return;
                }
                GUIFeedbackManager.playClickSound(player);
                cooldownManager.setCooldown(player, "fly");
                player.performCommand("fly");
                // Refresh the GUI after a short delay to show updated status
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    this.refreshAndReopen(player);
                }, 2L);
            }).setLore(languageManager.getLegacy(player, "gui.fly-lore")).setGlint(canFly);
            gui.setItem(slot++, flyItem.getItemStack(), flyItem.getClickHandler());
        }
        
        // Heal command
        if (player.hasPermission("CraftUtils.heal")) {
            GUIItem healItem = GUIItem.createButton(Material.GOLDEN_APPLE, 
                languageManager.getLegacy(player, "gui.heal"), event -> {
                if (cooldownManager.isOnCooldown(player, "heal")) {
                    long remaining = cooldownManager.getRemainingCooldown(player, "heal");
                    String timeStr = cooldownManager.formatRemainingTime(remaining);
                    String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                        "&cYou must wait {time} before using this again.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        cooldownMsg.replace("{time}", timeStr)));
                    return;
                }
                GUIFeedbackManager.playClickSound(player);
                cooldownManager.setCooldown(player, "heal");
                player.performCommand("heal");
            }).setLore(languageManager.getLegacy(player, "gui.heal-lore"));
            gui.setItem(slot++, healItem.getItemStack(), healItem.getClickHandler());
        }
        
        // Feed command
        if (player.hasPermission("CraftUtils.feed")) {
            GUIItem feedItem = GUIItem.createButton(Material.COOKED_BEEF, 
                languageManager.getLegacy(player, "gui.feed"), event -> {
                if (cooldownManager.isOnCooldown(player, "feed")) {
                    long remaining = cooldownManager.getRemainingCooldown(player, "feed");
                    String timeStr = cooldownManager.formatRemainingTime(remaining);
                    String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                        "&cYou must wait {time} before using this again.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        cooldownMsg.replace("{time}", timeStr)));
                    return;
                }
                GUIFeedbackManager.playClickSound(player);
                cooldownManager.setCooldown(player, "feed");
                player.performCommand("feed");
            }).setLore(languageManager.getLegacy(player, "gui.feed-lore"));
            gui.setItem(slot++, feedItem.getItemStack(), feedItem.getClickHandler());
        }
        
        // Trash command
        if (player.hasPermission("CraftUtils.trash")) {
            GUIItem trashItem = GUIItem.createButton(Material.LAVA_BUCKET, 
                languageManager.getLegacy(player, "gui.trash"), event -> {
                if (cooldownManager.isOnCooldown(player, "trash")) {
                    long remaining = cooldownManager.getRemainingCooldown(player, "trash");
                    String timeStr = cooldownManager.formatRemainingTime(remaining);
                    String cooldownMsg = plugin.getConfig().getString("messages.cooldown-message", 
                        "&cYou must wait {time} before using this again.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        cooldownMsg.replace("{time}", timeStr)));
                    return;
                }
                GUIFeedbackManager.playClickSound(player);
                cooldownManager.setCooldown(player, "trash");
                player.performCommand("trash");
            }).setLore(languageManager.getLegacy(player, "gui.trash-lore"));
            gui.setItem(slot++, trashItem.getItemStack(), trashItem.getClickHandler());
        }
        
        // Back to main menu
        String backText = languageManager != null ? 
            languageManager.getLegacy(player, "gui.back") : 
            "§7Back to Main Menu";
            
        GUIItem backItem = GUIItem.createButton(Material.ARROW, backText, event -> {
            GUIFeedbackManager.playClickSound(player);
            CraftUtilsMainGUI.openMainGUI(player, plugin, languageManager);
        }).setLore(languageManager.getLegacy(player, "gui.back-desc"));
        gui.setItem(8, backItem.getItemStack(), backItem.getClickHandler());
    }
}