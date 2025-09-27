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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class KitManager {
    
    public void createKit(String kitName, ItemStack[] items, int cooldownSeconds, String permission) {
        String query = "INSERT INTO kits (kit_name, items, cooldown, permission) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, kitName);
            stmt.setString(2, itemsToBase64(items));
            stmt.setInt(3, cooldownSeconds);
            stmt.setString(4, permission);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Kit creation failed: Duplicate kit name.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public Optional<Kit> getKit(String kitName) {
        String query = "SELECT * FROM kits WHERE kit_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, kitName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Kit(
                            resultSet.getString("kit_name"),
                            itemsFromBase64(resultSet.getString("items")),
                            resultSet.getInt("cooldown"),
                            resultSet.getString("permission")
                    ));
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Kit> getAllKits() {
        List<Kit> kits = new ArrayList<>();
        String query = "SELECT * FROM kits";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    kits.add(new Kit(
                            resultSet.getString("kit_name"),
                            itemsFromBase64(resultSet.getString("items")),
                            resultSet.getInt("cooldown"),
                            resultSet.getString("permission")
                    ));
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return kits;
    }
    
    public void deleteKit(String kitName) {
        String query = "DELETE FROM kits WHERE kit_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, kitName);
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
    
    public static class Kit {
        private String kitName;
        private ItemStack[] items;
        private int cooldownSeconds;
        private String permission;
        
        public Kit(String kitName, ItemStack[] items, int cooldownSeconds, String permission) {
            this.kitName = kitName;
            this.items = items;
            this.cooldownSeconds = cooldownSeconds;
            this.permission = permission;
        }
        
        public String getKitName() { return kitName; }
        public ItemStack[] getItems() { return items; }
        public int getCooldownSeconds() { return cooldownSeconds; }
        public String getPermission() { return permission; }
    }
}