package dev.craftefix.craftUtils;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestCommands implements Listener {

    private final Map<UUID, Player> openInventories = new HashMap<>();


    @Command({"enderchest", "ec", "cc enderchest"})
    @CommandPermission("CraftUtils.enderchest")
    public void enderchest(Player player, @Optional @Named("target") Player target) {
        if (target == null) {
            // Open the player's own ender chest
            target = player;
        } else if (!player.hasPermission("CraftUtils.enderchest.others")) {
            player.sendMessage("You do not have permission to view others' ender chests.");
            return;
        }

        if (!target.isOnline()) {
            player.sendMessage("Player not found or not online.");
            return;
        }

        Inventory enderChest = target.getEnderChest();
        player.openInventory(enderChest);
        openInventories.put(target.getUniqueId(), player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player target = event.getPlayer();
        Player viewer = openInventories.remove(target.getUniqueId());
        if (viewer != null) {
            viewer.closeInventory();
        }
    }
}