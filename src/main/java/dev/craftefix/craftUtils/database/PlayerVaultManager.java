package dev.craftefix.craftUtils.database;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

public class PlayerVaultManager {
    private final DatabaseManager databaseManager;
    
    public PlayerVaultManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public void saveVault(String playerUUID, int vaultNumber, ItemStack[] contents) {
        String query = "INSERT INTO player_vaults (owner_uuid, vault_number, contents) VALUES (?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE contents = VALUES(contents)";
        
        // For SQLite, we need a different approach since it doesn't support ON DUPLICATE KEY UPDATE
        if (databaseManager.getDatabaseType() == DatabaseManager.DatabaseType.SQLITE) {
            query = "INSERT OR REPLACE INTO player_vaults (owner_uuid, vault_number, contents) VALUES (?, ?, ?)";
        }
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setInt(2, vaultNumber);
            stmt.setString(3, itemsToBase64(contents));
            stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public Optional<ItemStack[]> getVault(String playerUUID, int vaultNumber) {
        String query = "SELECT contents FROM player_vaults WHERE owner_uuid = ? AND vault_number = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setInt(2, vaultNumber);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(itemsFromBase64(resultSet.getString("contents")));
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public void deleteVault(String playerUUID, int vaultNumber) {
        String query = "DELETE FROM player_vaults WHERE owner_uuid = ? AND vault_number = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUUID);
            stmt.setInt(2, vaultNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String itemsToBase64(ItemStack[] items) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeInt(items.length);
        for (ItemStack item : items) {
            dataOutput.writeObject(item);
        }
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    private ItemStack[] itemsFromBase64(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        int length = dataInput.readInt();
        ItemStack[] items = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            items[i] = (ItemStack) dataInput.readObject();
        }
        dataInput.close();
        return items;
    }
}