package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.database.KitManager;
import dev.craftefix.craftUtils.database.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UtilityCommands {
    
    private final KitManager kitManager = new KitManager();
    private final PlayerVaultManager vaultManager = new PlayerVaultManager();
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    
    @Command({"kit", "cu kit"})
    @CommandPermission("CraftUtils.kit")
    public void kit(Player actor, String kitName) {
        KitManager.Kit kit = kitManager.getKit(kitName).orElse(null);
        if (kit == null) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Kit ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Kit '" + kitName + "' not found.", NamedTextColor.RED)));
            return;
        }
        
        if (kit.getPermission() != null && !actor.hasPermission(kit.getPermission())) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Kit ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("You don't have permission to use this kit.", NamedTextColor.RED)));
            return;
        }
        
        for (ItemStack item : kit.getItems()) {
            if (item != null) {
                if (actor.getInventory().firstEmpty() == -1) {
                    actor.getWorld().dropItem(actor.getLocation(), item);
                } else {
                    actor.getInventory().addItem(item);
                }
            }
        }
        
        actor.sendMessage(Component.text()
                .append(Component.text("Kit ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("You received the '" + kitName + "' kit.", NamedTextColor.GREEN)));
    }
    
    @Command({"vault", "cu vault", "pv"})
    @CommandPermission("CraftUtils.vault")
    public void vault(Player actor, @Optional Integer vaultNumber) {
        if (vaultNumber == null) vaultNumber = 1;
        
        if (vaultNumber < 1 || vaultNumber > 10) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Vault ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Vault number must be between 1 and 10.", NamedTextColor.RED)));
            return;
        }
        
        Inventory vaultInventory = Bukkit.createInventory(null, 54, Component.text("Vault #" + vaultNumber).color(NamedTextColor.DARK_PURPLE));
        
        ItemStack[] contents = vaultManager.getVault(actor.getUniqueId().toString(), vaultNumber).orElse(new ItemStack[54]);
        vaultInventory.setContents(contents);
        
        actor.openInventory(vaultInventory);
    }
    
    @Command({"workbench", "cu workbench", "wb"})
    @CommandPermission("CraftUtils.workbench")
    public void workbench(Player actor) {
        actor.openWorkbench(null, true);
        actor.sendMessage(Component.text()
                .append(Component.text("Utility ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Opened crafting table.", NamedTextColor.GRAY)));
    }
    
    @Command({"anvil", "cu anvil"})
    @CommandPermission("CraftUtils.anvil")
    public void anvil(Player actor) {
        Inventory anvilInventory = Bukkit.createInventory(null, InventoryType.ANVIL, Component.text("Anvil"));
        actor.openInventory(anvilInventory);
        actor.sendMessage(Component.text()
                .append(Component.text("Utility ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Opened anvil.", NamedTextColor.GRAY)));
    }
    
    @Command({"enderchest", "cu enderchest", "ec"})
    @CommandPermission("CraftUtils.enderchest")
    public void enderchest(Player actor, @Optional Player target) {
        if (target == null) {
            actor.openInventory(actor.getEnderChest());
            actor.sendMessage(Component.text()
                    .append(Component.text("Utility ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Opened your ender chest.", NamedTextColor.GRAY)));
        } else {
            if (actor.hasPermission("CraftUtils.enderchest.others")) {
                actor.openInventory(target.getEnderChest());
                actor.sendMessage(Component.text()
                        .append(Component.text("Utility ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Opened " + target.getName() + "'s ender chest.", NamedTextColor.GRAY)));
            } else {
                actor.sendMessage(Component.text()
                        .append(Component.text("Utility ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You don't have permission to open other players' ender chests.", NamedTextColor.RED)));
            }
        }
    }
    
    @Command({"back", "cu back"})
    @CommandPermission("CraftUtils.back")
    public void back(Player actor) {
        Location lastLocation = lastLocations.get(actor.getUniqueId());
        if (lastLocation == null) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Teleport ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("No previous location found.", NamedTextColor.RED)));
            return;
        }
        
        // Store current location before teleporting
        Location currentLocation = actor.getLocation();
        actor.teleport(lastLocation);
        lastLocations.put(actor.getUniqueId(), currentLocation);
        
        actor.sendMessage(Component.text()
                .append(Component.text("Teleport ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Teleported to your previous location.", NamedTextColor.GREEN)));
    }
    
    @Command({"repair", "cu repair", "fix"})
    @CommandPermission("CraftUtils.repair")
    public void repair(Player actor, @Optional String target) {
        if (target == null || target.equalsIgnoreCase("hand")) {
            ItemStack item = actor.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You must hold an item to repair.", NamedTextColor.RED)));
                return;
            }
            
            if (item.getItemMeta() != null && item.getItemMeta().hasEnchants()) {
                item.setDurability((short) 0);
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Item repaired successfully.", NamedTextColor.GREEN)));
            } else {
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("This item cannot be repaired.", NamedTextColor.RED)));
            }
        } else if (target.equalsIgnoreCase("all")) {
            if (!actor.hasPermission("CraftUtils.repair.all")) {
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("You don't have permission to repair all items.", NamedTextColor.RED)));
                return;
            }
            
            int repairedCount = 0;
            for (ItemStack item : actor.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR && item.getItemMeta() != null && item.getItemMeta().hasEnchants()) {
                    item.setDurability((short) 0);
                    repairedCount++;
                }
            }
            
            actor.sendMessage(Component.text()
                    .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Repaired " + repairedCount + " items.", NamedTextColor.GREEN)));
        }
    }
    
    // Store last location when player teleports
    public void storeLastLocation(Player player, Location location) {
        lastLocations.put(player.getUniqueId(), location);
    }
}