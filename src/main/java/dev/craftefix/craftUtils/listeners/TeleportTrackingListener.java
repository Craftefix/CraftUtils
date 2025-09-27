package dev.craftefix.craftUtils.listeners;

import dev.craftefix.craftUtils.database.BackLocationManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportTrackingListener implements Listener {
    private final BackLocationManager backLocationManager;
    private final java.util.Set<java.util.UUID> activeBackTeleports = new java.util.HashSet<>();

    public TeleportTrackingListener(BackLocationManager backLocationManager) {
        this.backLocationManager = backLocationManager;
    }
    
    public void markBackTeleport(java.util.UUID playerUUID) {
        activeBackTeleports.add(playerUUID);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        // Only track if teleport is successful (not cancelled)
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        // Don't track if teleporting within the same location (prevents spam from minor position changes)
        if (to != null && isSameLocation(from, to)) {
            return;
        }

        // Don't track /back teleports to prevent infinite loops
        if (activeBackTeleports.contains(player.getUniqueId())) {
            activeBackTeleports.remove(player.getUniqueId());
            return;
        }

        // Store the location they're teleporting FROM
        backLocationManager.storeLocation(player.getUniqueId(), from);
    }

    private boolean isSameLocation(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            return false;
        }
        
        // Consider locations the same if they're within 1 block of each other
        return Math.abs(loc1.getX() - loc2.getX()) < 1.0 &&
               Math.abs(loc1.getY() - loc2.getY()) < 1.0 &&
               Math.abs(loc1.getZ() - loc2.getZ()) < 1.0;
    }
}