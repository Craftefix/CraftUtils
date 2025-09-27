package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.database.BackLocationManager;
import dev.craftefix.craftUtils.database.PlayerVaultManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UtilityCommands implements Listener {
    
    private final PlayerVaultManager vaultManager;
    private final BackLocationManager backLocationManager;
    private final Map<Inventory, VaultInfo> openVaults = new HashMap<>();
    private dev.craftefix.craftUtils.listeners.TeleportTrackingListener teleportListener;
    
    public UtilityCommands(PlayerVaultManager vaultManager, BackLocationManager backLocationManager) {
        this.vaultManager = vaultManager;
        this.backLocationManager = backLocationManager;
    }
    
    public void setTeleportListener(dev.craftefix.craftUtils.listeners.TeleportTrackingListener teleportListener) {
        this.teleportListener = teleportListener;
    }
    
    private static class VaultInfo {
        final String playerUUID;
        final int vaultNumber;
        
        VaultInfo(String playerUUID, int vaultNumber) {
            this.playerUUID = playerUUID;
            this.vaultNumber = vaultNumber;
        }
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
        
        openVaults.put(vaultInventory, new VaultInfo(actor.getUniqueId().toString(), vaultNumber));
        actor.openInventory(vaultInventory);
    }
    
    @Command({"craftingtable", "cu craftingtable", "ct"})
    @CommandPermission("CraftUtils.craftingtable")
    public void craftingtable(Player actor) {
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
        java.util.Optional<Location> lastLocationOpt = backLocationManager.getLocation(actor.getUniqueId());
        if (!lastLocationOpt.isPresent()) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Teleport ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("No previous location found.", NamedTextColor.RED)));
            return;
        }
        
        Location lastLocation = lastLocationOpt.get();
        // Mark this as a /back teleport to prevent tracking
        if (teleportListener != null) {
            teleportListener.markBackTeleport(actor.getUniqueId());
        }
        actor.teleport(lastLocation);
        
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
            
            if (item.getItemMeta() instanceof Damageable) {
                Damageable damageable = (Damageable) item.getItemMeta();
                if (damageable.hasDamage()) {
                    damageable.setDamage(0);
                    item.setItemMeta(damageable);
                    actor.sendMessage(Component.text()
                            .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                            .append(Component.text("Item repaired successfully.", NamedTextColor.GREEN)));
                } else {
                    actor.sendMessage(Component.text()
                            .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                            .append(Component.text("This item is already fully repaired.", NamedTextColor.YELLOW)));
                }
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
                if (item != null && item.getType() != Material.AIR && item.getItemMeta() instanceof Damageable) {
                    Damageable damageable = (Damageable) item.getItemMeta();
                    if (damageable.hasDamage()) {
                        damageable.setDamage(0);
                        item.setItemMeta(damageable);
                        repairedCount++;
                    }
                }
            }
            
            if (repairedCount > 0) {
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("Repaired " + repairedCount + " items.", NamedTextColor.GREEN)));
            } else {
                actor.sendMessage(Component.text()
                        .append(Component.text("Repair ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                        .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .append(Component.text("No damaged items found to repair.", NamedTextColor.YELLOW)));
            }
        }
    }
    

    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        VaultInfo vaultInfo = openVaults.get(inventory);
        
        if (vaultInfo != null) {
            ItemStack[] contents = inventory.getContents();
            vaultManager.saveVault(vaultInfo.playerUUID, vaultInfo.vaultNumber, contents);
            openVaults.remove(inventory);
        }
    }
}