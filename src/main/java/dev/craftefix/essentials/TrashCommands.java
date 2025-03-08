package dev.craftefix.essentials;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

    public class TrashCommands {

        @Command({"trash", "cc trash"})
        @CommandPermission("CraftNet.essentials.trash")
        public void trash(Player player) {
            Inventory trashInventory = Bukkit.createInventory(null, InventoryType.CHEST, NamedTextColor.RED + "Trash");
            player.openInventory(trashInventory);
        }
    }