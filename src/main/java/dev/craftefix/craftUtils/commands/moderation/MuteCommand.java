package dev.craftefix.craftUtils.commands.moderation;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.MuteManager;
import dev.craftefix.craftUtils.discord.DiscordWebhookManager;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MuteCommand {
    private final Main plugin;
    private final DiscordWebhookManager discordManager;
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhd])");

    public MuteCommand(Main plugin) {
        this.plugin = plugin;
        this.discordManager = new DiscordWebhookManager(plugin);
    }

    @Command("mute")
    @Description("Mute a player")
    @CommandPermission("craftutils.mute")
    public void mute(CommandSender sender, @Named("player") String playerName, 
                    @Optional @Named("duration") String duration, 
                    @Optional @Named("reason") String reason) {
        
        Player target = Bukkit.getPlayer(playerName);
        UUID targetUuid;
        String targetName;
        
        if (target != null) {
            targetUuid = target.getUniqueId();
            targetName = target.getName();
        } else {
            // Try to get offline player data
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player " + playerName + " has never joined the server.");
                return;
            }
            targetUuid = offlinePlayer.getUniqueId();
            targetName = offlinePlayer.getName();
        }
        
        if (MuteManager.isPlayerMuted(targetUuid)) {
            sender.sendMessage(ChatColor.RED + targetName + " is already muted.");
            return;
        }
        
        Long durationSeconds = null;
        if (duration != null && !duration.isEmpty()) {
            durationSeconds = parseDuration(duration);
            if (durationSeconds == null) {
                sender.sendMessage(ChatColor.RED + "Invalid duration format. Use: 1s, 5m, 2h, 1d");
                return;
            }
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            reason = "No reason provided";
        }
        
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        
        // Mute the player
        MuteManager.mutePlayer(targetUuid, targetName, senderName, reason, durationSeconds);
        
        // Send messages
        String durationText = durationSeconds != null ? formatDuration(durationSeconds) : "permanently";
        sender.sendMessage(ChatColor.GREEN + "Successfully muted " + targetName + " for " + durationText + ".");
        
        if (target != null && target.isOnline()) {
            target.sendMessage(ChatColor.RED + "You have been muted " + durationText + ".");
            target.sendMessage(ChatColor.RED + "Reason: " + reason);
        }
        
        // Broadcast to staff
        String broadcastMessage = ChatColor.YELLOW + senderName + " muted " + targetName + " for " + durationText + ". Reason: " + reason;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("craftutils.mute.notify")) {
                player.sendMessage(broadcastMessage);
            }
        }
        
        // Send to Discord
        discordManager.sendPlayerMute(targetName, reason, senderName, durationSeconds != null ? durationSeconds : -1);
    }

    @Command("unmute")
    @Description("Unmute a player")
    @CommandPermission("craftutils.unmute")
    public void unmute(CommandSender sender, @Named("player") String playerName) {
        MuteManager.MuteData muteData = MuteManager.getMuteDataByName(playerName);
        
        if (muteData == null || !muteData.active) {
            sender.sendMessage(ChatColor.RED + playerName + " is not muted.");
            return;
        }
        
        MuteManager.unmutePlayerByName(playerName);
        
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        sender.sendMessage(ChatColor.GREEN + "Successfully unmuted " + playerName + ".");
        
        Player target = Bukkit.getPlayer(playerName);
        if (target != null && target.isOnline()) {
            target.sendMessage(ChatColor.GREEN + "You have been unmuted by " + senderName + ".");
        }
        
        // Broadcast to staff
        String broadcastMessage = ChatColor.YELLOW + senderName + " unmuted " + playerName + ".";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("craftutils.mute.notify")) {
                player.sendMessage(broadcastMessage);
            }
        }
        
        // Send to Discord
        discordManager.sendPlayerUnmute(playerName, senderName);
    }

    @Command("pardon")
    @Description("Pardon (unban) a player")
    @CommandPermission("craftutils.pardon")
    public void pardon(CommandSender sender, @Named("player") String playerName) {
        @SuppressWarnings("deprecation")
        org.bukkit.BanList banList = Bukkit.getBanList(org.bukkit.BanList.Type.NAME);
        
        if (!banList.isBanned(playerName)) {
            sender.sendMessage(ChatColor.RED + playerName + " is not banned.");
            return;
        }
        
        banList.pardon(playerName);
        
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        sender.sendMessage(ChatColor.GREEN + "Successfully pardoned " + playerName + ".");
        
        // Broadcast to staff
        String broadcastMessage = ChatColor.YELLOW + senderName + " pardoned " + playerName + ".";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("craftutils.pardon.notify")) {
                player.sendMessage(broadcastMessage);
            }
        }
    }
    
    private Long parseDuration(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration.toLowerCase());
        if (!matcher.matches()) {
            return null;
        }
        
        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        
        return switch (unit) {
            case "s" -> amount;
            case "m" -> amount * 60;
            case "h" -> amount * 3600;
            case "d" -> amount * 86400;
            default -> null;
        };
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