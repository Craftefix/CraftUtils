package dev.craftefix.craftUtils.listeners;

import dev.craftefix.craftUtils.database.PlayerVaultManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class VaultEventListener implements Listener {
    
    private final PlayerVaultManager vaultManager;
    private final Map<Inventory, VaultInfo> openVaults = new HashMap<>();
    
    public VaultEventListener(PlayerVaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }
    
    public static class VaultInfo {
        final String playerUUID;
        final int vaultNumber;
        
        public VaultInfo(String playerUUID, int vaultNumber) {
            this.playerUUID = playerUUID;
            this.vaultNumber = vaultNumber;
        }
    }
    
    public void registerVault(Inventory inventory, String playerUUID, int vaultNumber) {
        openVaults.put(inventory, new VaultInfo(playerUUID, vaultNumber));
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        VaultInfo vaultInfo = openVaults.get(inventory);
        
        if (vaultInfo != null) {
            ItemStack[] contents = inventory.getContents();
            openVaults.remove(inventory);
            
            // Save vault contents asynchronously
            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("CraftUtils"), () -> {
                vaultManager.saveVault(vaultInfo.playerUUID, vaultInfo.vaultNumber, contents);
            });
        }
    }
}