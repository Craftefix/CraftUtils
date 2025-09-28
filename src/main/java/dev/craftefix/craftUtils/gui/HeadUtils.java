package dev.craftefix.craftUtils.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Utility class for creating custom player heads with textures
 */
public class HeadUtils {
    
    /**
     * Creates a player head with a custom texture from base64 encoded texture data
     * @param base64Texture The base64 encoded texture data
     * @return ItemStack with custom head texture
     */
    public static ItemStack createCustomHead(String base64Texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
        if (base64Texture == null || base64Texture.isEmpty()) {
            return head; // Return default head if no texture provided
        }
        
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            try {
                // Create a player profile with random UUID
                PlayerProfile profile = org.bukkit.Bukkit.createProfile(UUID.randomUUID(), "CustomHead");
                
                // Set the texture using the base64 texture data
                profile.getProperties().add(new ProfileProperty("textures", base64Texture));
                
                skullMeta.setPlayerProfile(profile);
                head.setItemMeta(skullMeta);
            } catch (Exception e) {
                // Fallback to default head if texture setting fails
                System.err.println("Failed to set custom head texture: " + e.getMessage());
            }
        }
        
        return head;
    }
    
    /**
     * Creates a player head with a custom texture from texture URL
     * @param textureUrl The URL to the texture
     * @return ItemStack with custom head texture
     */
    public static ItemStack createCustomHeadFromUrl(String textureUrl) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
        if (textureUrl == null || textureUrl.isEmpty()) {
            return head; // Return default head if no URL provided
        }
        
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            try {
                // Create a player profile with random UUID
                PlayerProfile profile = org.bukkit.Bukkit.createProfile(UUID.randomUUID(), "CustomHead");
                
                // Set the skin texture URL
                profile.getTextures().setSkin(new URL(textureUrl));
                
                skullMeta.setPlayerProfile(profile);
                head.setItemMeta(skullMeta);
            } catch (MalformedURLException e) {
                // Fallback to default head if URL is invalid
                System.err.println("Invalid texture URL: " + textureUrl);
            } catch (Exception e) {
                // Fallback to default head if texture setting fails
                System.err.println("Failed to set custom head texture from URL: " + e.getMessage());
            }
        }
        
        return head;
    }
}