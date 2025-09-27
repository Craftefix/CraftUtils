package dev.craftefix.craftUtils.discord;

import dev.craftefix.craftUtils.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class DiscordWebhookManager {
    private final Main plugin;
    private final FileConfiguration config;
    private final boolean enabled;

    public DiscordWebhookManager(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.enabled = config.getBoolean("discord.enabled", false);
    }

    public void sendPlayerJoin(Player player) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.joins");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        String message = String.format("🟢 **%s** joined the server", player.getName());
        sendWebhookMessage(webhookUrl, message, "Player Join");
    }

    public void sendPlayerLeave(Player player) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.leaves");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        String message = String.format("🔴 **%s** left the server", player.getName());
        sendWebhookMessage(webhookUrl, message, "Player Leave");
    }

    public void sendPlayerBan(String playerName, String reason, String bannedBy) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.bans");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        String message = String.format("🔨 **%s** was banned by **%s**\nReason: %s", 
            playerName, bannedBy, reason != null ? reason : "No reason provided");
        sendWebhookMessage(webhookUrl, message, "Player Ban");
    }

    public void sendPlayerMute(String playerName, String reason, String mutedBy, long duration) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.mutes");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        String durationText = duration > 0 ? formatDuration(duration) : "Permanent";
        String message = String.format("🔇 **%s** was muted by **%s** for **%s**\nReason: %s", 
            playerName, mutedBy, durationText, reason != null ? reason : "No reason provided");
        sendWebhookMessage(webhookUrl, message, "Player Mute");
    }

    public void sendPlayerUnmute(String playerName, String unmutedBy) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.unmutes");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        String message = String.format("🔊 **%s** was unmuted by **%s**", playerName, unmutedBy);
        sendWebhookMessage(webhookUrl, message, "Player Unmute");
    }

    public void sendLogMessage(String logMessage) {
        if (!enabled) return;
        String webhookUrl = config.getString("discord.webhooks.logs");
        if (webhookUrl == null || webhookUrl.contains("YOUR_WEBHOOK")) return;
        
        sendWebhookMessage(webhookUrl, logMessage, "Server Log");
    }

    private void sendWebhookMessage(String webhookUrl, String message, String username) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String jsonPayload = String.format(
                    "{\"username\":\"%s\",\"content\":\"%s\",\"embeds\":[{\"description\":\"%s\",\"timestamp\":\"%s\",\"color\":5814783}]}", 
                    username, message, message.replace("\"", "\\\""), timestamp
                );

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 200 && responseCode != 204) {
                    plugin.getLogger().warning("Discord webhook failed with response code: " + responseCode);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }

    private String formatDuration(long seconds) {
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