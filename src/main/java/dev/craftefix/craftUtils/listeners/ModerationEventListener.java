package dev.craftefix.craftUtils.listeners;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.discord.DiscordWebhookManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ModerationEventListener implements Listener {
    private final Main plugin;
    private final DiscordWebhookManager discordManager;

    public ModerationEventListener(Main plugin) {
        this.plugin = plugin;
        this.discordManager = DiscordWebhookManager.getInstance(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) return;
        
        // Check if this is a ban (kick reasons often contain "Banned" or similar)
        String reason = event.getReason();
        if (reason != null && (reason.toLowerCase().contains("banned") || reason.toLowerCase().contains("ban"))) {
            discordManager.sendPlayerBan(event.getPlayer().getName(), reason, "Server");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        if (command.startsWith("ban ")) {
            handleBanCommand(command, "Console");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/ban ")) {
            handleBanCommand(command.substring(1), event.getPlayer().getName());
        }
    }
    
    private void handleBanCommand(String command, String executor) {
        try {
            String[] parts = command.split(" ", 3);
            if (parts.length >= 2) {
                String playerName = parts[1];
                String reason = parts.length > 2 ? parts[2] : "No reason provided";
                
                // Send to Discord after a small delay to ensure the ban is processed
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    discordManager.sendPlayerBan(playerName, reason, executor);
                }, 20L); // 1 second delay
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
    }
}