package dev.craftefix.craftUtils.listeners;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.MuteManager;
import dev.craftefix.craftUtils.discord.DiscordWebhookManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    private final Main plugin;
    private final MuteManager muteManager;
    private final DiscordWebhookManager discordManager;

    public PlayerEventListener(Main plugin, MuteManager muteManager) {
        this.plugin = plugin;
        this.muteManager = muteManager;
        this.discordManager = new DiscordWebhookManager(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Send to Discord
        discordManager.sendPlayerJoin(event.getPlayer());
        
        // Custom join message
        String joinMessage = plugin.getConfig().getString("messages.join");
        if (joinMessage != null && !joinMessage.isEmpty()) {
            joinMessage = ChatColor.translateAlternateColorCodes('&', 
                joinMessage.replace("{player}", event.getPlayer().getName()));
            event.setJoinMessage(joinMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Send to Discord
        discordManager.sendPlayerLeave(event.getPlayer());
        
        // Custom leave message
        String leaveMessage = plugin.getConfig().getString("messages.leave");
        if (leaveMessage != null && !leaveMessage.isEmpty()) {
            leaveMessage = ChatColor.translateAlternateColorCodes('&', 
                leaveMessage.replace("{player}", event.getPlayer().getName()));
            event.setQuitMessage(leaveMessage);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (muteManager.isPlayerMuted(event.getPlayer().getUniqueId())) {
            MuteManager.MuteData muteData = muteManager.getMuteData(event.getPlayer().getUniqueId());
            if (muteData != null) {
                event.setCancelled(true);
                
                String message = ChatColor.RED + "You are muted and cannot speak.";
                if (muteData.reason != null && !muteData.reason.isEmpty()) {
                    message += "\nReason: " + muteData.reason;
                }
                if (!muteData.isPermanent()) {
                    long timeLeft = muteData.unmuteTime - System.currentTimeMillis();
                    if (timeLeft > 0) {
                        message += "\nTime remaining: " + formatTimeLeft(timeLeft / 1000);
                    }
                } else {
                    message += "\nThis mute is permanent.";
                }
                
                event.getPlayer().sendMessage(message);
            }
        }
    }
    
    private String formatTimeLeft(long seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " hours";
        } else {
            return (seconds / 86400) + " days";
        }
    }
}