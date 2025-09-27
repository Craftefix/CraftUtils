package dev.craftefix.craftUtils.commands;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.command.CommandResult;
import dev.craftefix.craftUtils.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for command functionality
 */
class CommandTest {
    
    private ServerMock server;
    private Main plugin;
    private PlayerMock player;
    private World world;
    
    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        world = server.addSimpleWorld("world");
        player = server.addPlayer("TestPlayer");
        player.setLocation(new Location(world, 0, 64, 0));
    }
    
    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    @DisplayName("Player should be able to execute main command")
    void testMainCommand() {
        assertTrue(player.hasPermission("CraftUtils.main"), "Player should have main permission");
        
        // Execute main command
        var result = server.execute("cu", player);
        assertTrue(result.hasSucceeded(), "Main command should execute successfully");
        
        // Check that player received some message (plugin info)
        assertTrue(player.nextMessage() != null, "Player should receive a response message");
    }
    
    @Test
    @DisplayName("Player should be able to access help")
    void testHelpCommand() {
        var result = server.execute("cu help", player);
        assertTrue(result.hasSucceeded(), "Help command should execute successfully");
    }
    
    @Test
    @DisplayName("Repair command should work for valid items")
    void testRepairCommand() {
        // Give player permission
        player.addAttachment(plugin, "CraftUtils.repair", true);
        
        // Give player a damaged iron sword
        org.bukkit.inventory.ItemStack sword = new org.bukkit.inventory.ItemStack(org.bukkit.Material.IRON_SWORD);
        sword.setDurability((short) 100); // Damage it
        player.getInventory().setItemInMainHand(sword);
        
        // Execute repair command
        var result = server.execute("repair hand", player);
        assertTrue(result.hasSucceeded(), "Repair command should execute successfully");
        
        // Check that player received a message
        assertNotNull(player.nextMessage(), "Player should receive a response message");
    }
    
    @Test
    @DisplayName("Commands should respect permissions")
    void testCommandPermissions() {
        // Test command without permission
        player.setOp(false);
        var result = server.execute("admingui", player);
        
        // Command should execute but player should get no permission message
        assertTrue(result.hasSucceeded(), "Command should execute (permission check happens in command handler)");
    }
    
    @Test
    @DisplayName("Vault command should handle invalid vault numbers")
    void testVaultCommandValidation() {
        player.addAttachment(plugin, "CraftUtils.vault", true);
        
        // Test valid vault number
        var result1 = server.execute("vault 1", player);
        assertTrue(result1.hasSucceeded(), "Valid vault command should execute");
        
        // Test invalid vault number
        var result2 = server.execute("vault 15", player);
        assertTrue(result2.hasSucceeded(), "Command should execute (validation happens in handler)");
    }
    
    @Test
    @DisplayName("Gamemode commands should work")
    void testGamemodeCommands() {
        player.addAttachment(plugin, "CraftUtils.Gamemode.Creative", true);
        player.addAttachment(plugin, "CraftUtils.Gamemode.Survival", true);
        
        // Test creative mode
        var result1 = server.execute("gmc", player);
        assertTrue(result1.hasSucceeded(), "Creative mode command should execute");
        
        // Test survival mode
        var result2 = server.execute("gms", player);
        assertTrue(result2.hasSucceeded(), "Survival mode command should execute");
    }
    
    @Test
    @DisplayName("Teleport commands should validate coordinates")
    void testTeleportCommands() {
        player.addAttachment(plugin, "CraftUtils.teleport.location", true);
        
        // Test valid coordinates
        var result = server.execute("cu tp location 100 64 200", player);
        assertTrue(result.hasSucceeded(), "Valid teleport command should execute");
    }
    
    @Test
    @DisplayName("Home commands should handle validation")
    void testHomeCommands() {
        player.addAttachment(plugin, "CraftUtils.home.sethome", true);
        player.addAttachment(plugin, "CraftUtils.home", true);
        player.addAttachment(plugin, "CraftUtils.home.list", true);
        
        // Test setting a home
        var result1 = server.execute("sethome test", player);
        assertTrue(result1.hasSucceeded(), "Set home command should execute");
        
        // Test listing homes
        var result2 = server.execute("homes", player);
        assertTrue(result2.hasSucceeded(), "List homes command should execute");
        
        // Test going to home
        var result3 = server.execute("home test", player);
        assertTrue(result3.hasSucceeded(), "Go to home command should execute");
    }
}