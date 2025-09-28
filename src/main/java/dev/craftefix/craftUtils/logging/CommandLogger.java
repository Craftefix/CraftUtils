package dev.craftefix.craftUtils.logging;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.discord.DiscordWebhookManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class CommandLogger implements Listener {
    private static CommandLogger instance;
    private final Main plugin;
    private final boolean commandLoggingEnabled;
    private final boolean latestLogEnabled;
    private final DateTimeFormatter timestampFormatter;
    private final DiscordWebhookManager discordManager;
    private File latestLogFile;
    
    private CommandLogger(Main plugin) {
        this.plugin = plugin;
        this.commandLoggingEnabled = plugin.getConfig().getBoolean("logging.command-logging", false);
        this.latestLogEnabled = plugin.getConfig().getBoolean("logging.create-latest-log", false);
        this.timestampFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.discordManager = DiscordWebhookManager.getInstance(plugin);
        
        if (latestLogEnabled) {
            initializeLatestLog();
        }
    }
    
    public static CommandLogger getInstance(Main plugin) {
        if (instance == null) {
            instance = new CommandLogger(plugin);
        }
        return instance;
    }
    
    public static CommandLogger getInstance() {
        return instance;
    }
    
    private void initializeLatestLog() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        latestLogFile = new File(dataFolder, "latest.log");
        
        // Delete existing log file on restart
        if (latestLogFile.exists()) {
            boolean deleted = latestLogFile.delete();
            if (!deleted) {
                plugin.getLogger().warning("Failed to delete existing latest.log file");
            }
        }
        
        // Create new log file
        try {
            boolean created = latestLogFile.createNewFile();
            if (created) {
                plugin.getLogger().info("Created latest.log file");
                logToFile("SERVER", "CraftUtils logging system initialized");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create latest.log file", e);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        String command = event.getMessage();
        
        logCommand(player.getName(), command, "PLAYER");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent event) {
        if (event.isCancelled()) return;
        
        String command = "/" + event.getCommand();
        logCommand("CONSOLE", command, "CONSOLE");
    }
    
    private void logCommand(String executor, String command, String executorType) {
        String timestamp = LocalDateTime.now().format(timestampFormatter);
        String logMessage = String.format("[%s] %s executed: %s", timestamp, executor, command);
        
        // Log to console if command logging is enabled
        if (commandLoggingEnabled) {
            plugin.getLogger().info("COMMAND: " + executor + " executed " + command);
        }
        
        // Log to latest.log if enabled
        if (latestLogEnabled) {
            logToFile(executorType, executor + " executed: " + command);
        }
        
        // Send to Discord if enabled and it's an admin command
        if (isAdminCommand(command)) {
            discordManager.sendLogMessage("🔧 **" + executor + "** executed admin command: `" + command + "`");
        }
    }
    
    private void logToFile(String level, String message) {
        if (latestLogFile == null) return;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(latestLogFile, true))) {
            String timestamp = LocalDateTime.now().format(timestampFormatter);
            writer.println(String.format("[%s] [%s] %s", timestamp, level, message));
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to write to latest.log", e);
        }
    }
    
    private boolean isAdminCommand(String command) {
        String cmd = command.toLowerCase();
        return cmd.startsWith("/ban ") || 
               cmd.startsWith("/kick ") || 
               cmd.startsWith("/mute ") || 
               cmd.startsWith("/pardon ") || 
               cmd.startsWith("/unmute ") || 
               cmd.startsWith("/op ") || 
               cmd.startsWith("/deop ") || 
               cmd.startsWith("/gamemode ") ||
               cmd.startsWith("/reload") ||
               cmd.startsWith("/stop") ||
               cmd.startsWith("/restart");
    }
    
    public void logServerEvent(String event) {
        if (latestLogEnabled) {
            logToFile("SERVER", event);
        }
    }
    
    public void logPlayerEvent(String playerName, String event) {
        if (latestLogEnabled) {
            logToFile("PLAYER", playerName + " " + event);
        }
    }
}