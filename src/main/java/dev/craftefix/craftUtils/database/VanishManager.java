package dev.craftefix.craftUtils.database;

import dev.craftefix.craftUtils.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private static VanishManager instance;
    private final Main plugin;
    private final Set<UUID> vanishedPlayers;
    
    private VanishManager(Main plugin) {
        this.plugin = plugin;
        this.vanishedPlayers = new HashSet<>();
    }
    
    public static VanishManager getInstance(Main plugin) {
        if (instance == null) {
            instance = new VanishManager(plugin);
        }
        return instance;
    }
    
    public static VanishManager getInstance() {
        return instance;
    }
    
    public boolean isVanished(UUID playerId) {
        return vanishedPlayers.contains(playerId);
    }
    
    public boolean isVanished(Player player) {
        return isVanished(player.getUniqueId());
    }
    
    public void setVanished(Player player, boolean vanished) {
        UUID playerId = player.getUniqueId();
        
        if (vanished) {
            if (!vanishedPlayers.contains(playerId)) {
                vanishedPlayers.add(playerId);
                
                // Make player invisible to others
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    if (!otherPlayer.equals(player) && !otherPlayer.hasPermission("CraftUtils.vanish.see")) {
                        otherPlayer.hidePlayer(plugin, player);
                    }
                }
                
                // Send fake leave message
                sendFakeLeaveMessage(player);
                
                // Notify vanished player
                String vanishOnMessage = plugin.getConfig().getString("messages.vanish-on", 
                    "&7You are now &cinvisible&7.");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', vanishOnMessage));
                
                plugin.getLogger().info(player.getName() + " entered vanish mode");
            }
        } else {
            if (vanishedPlayers.contains(playerId)) {
                vanishedPlayers.remove(playerId);
                
                // Make player visible to others
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                    if (!otherPlayer.equals(player)) {
                        otherPlayer.showPlayer(plugin, player);
                    }
                }
                
                // Send fake join message
                sendFakeJoinMessage(player);
                
                // Notify unvanished player
                String vanishOffMessage = plugin.getConfig().getString("messages.vanish-off", 
                    "&7You are now &avisible&7.");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', vanishOffMessage));
                
                plugin.getLogger().info(player.getName() + " exited vanish mode");
            }
        }
    }
    
    public void toggleVanish(Player player) {
        setVanished(player, !isVanished(player));
    }
    
    private void sendFakeLeaveMessage(Player player) {
        String leaveMessage = plugin.getConfig().getString("messages.leave");
        if (leaveMessage != null && !leaveMessage.isEmpty()) {
            leaveMessage = ChatColor.translateAlternateColorCodes('&', 
                leaveMessage.replace("{player}", player.getName()));
                
            // Send to all players who can't see vanished players
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.equals(player) && !otherPlayer.hasPermission("CraftUtils.vanish.see")) {
                    otherPlayer.sendMessage(leaveMessage);
                }
            }
        }
    }
    
    private void sendFakeJoinMessage(Player player) {
        String joinMessage = plugin.getConfig().getString("messages.join");
        if (joinMessage != null && !joinMessage.isEmpty()) {
            joinMessage = ChatColor.translateAlternateColorCodes('&', 
                joinMessage.replace("{player}", player.getName()));
                
            // Send to all players
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.equals(player)) {
                    otherPlayer.sendMessage(joinMessage);
                }
            }
        }
    }
    
    public void handlePlayerJoin(Player player) {
        // Hide vanished players from joining player
        for (UUID vanishedId : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedId);
            if (vanishedPlayer != null && vanishedPlayer.isOnline() && 
                !player.hasPermission("CraftUtils.vanish.see")) {
                player.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }
    
    public void handlePlayerQuit(Player player) {
        // Remove from vanish list when player quits
        vanishedPlayers.remove(player.getUniqueId());
    }
    
    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }
    
    public int getVanishedCount() {
        return vanishedPlayers.size();
    }
}