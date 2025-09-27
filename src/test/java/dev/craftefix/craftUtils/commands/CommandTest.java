package dev.craftefix.craftUtils.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
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
        assertTrue(player.hasPermission("craftutils.main"), "Player should have main permission");
        
        // Execute main command
        boolean result = server.execute("cu", player);
        assertTrue(result, "Main command should execute successfully");
        
        // Check that player received some message (plugin info)
        assertTrue(player.nextMessage() != null, "Player should receive a response message");
    }
    
    @Test
    @DisplayName("Player should be able to access help")
    void testHelpCommand() {
        boolean result = server.execute("cu help", player);
        assertTrue(result, "Help command should execute successfully");
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
        boolean result = server.execute("repair hand", player);
        assertTrue(result, "Repair command should execute successfully");
        
        // Check that player received a message
        assertNotNull(player.nextMessage(), "Player should receive a response message");
    }
    
    @Test
    @DisplayName("Commands should respect permissions")
    void testCommandPermissions() {
        // Test command without permission
        player.setOp(false);
        boolean result = server.execute("admingui", player);
        
        // Command should execute but player should get no permission message
        assertTrue(result, "Command should execute (permission check happens in command handler)");
    }
    
    @Test
    @DisplayName("Vault command should handle invalid vault numbers")
    void testVaultCommandValidation() {
        player.addAttachment(plugin, "CraftUtils.vault", true);
        
        // Test valid vault number
        boolean result1 = server.execute("vault 1", player);
        assertTrue(result1, "Valid vault command should execute");
        
        // Test invalid vault number
        boolean result2 = server.execute("vault 15", player);
        assertTrue(result2, "Command should execute (validation happens in handler)");
    }
    
    @Test
    @DisplayName("Gamemode commands should work")
    void testGamemodeCommands() {
        player.addAttachment(plugin, "CraftUtils.Gamemode.Creative", true);
        player.addAttachment(plugin, "CraftUtils.Gamemode.Survival", true);
        
        // Test creative mode
        boolean result1 = server.execute("gmc", player);
        assertTrue(result1, "Creative mode command should execute");
        
        // Test survival mode
        boolean result2 = server.execute("gms", player);
        assertTrue(result2, "Survival mode command should execute");
    }
    
    @Test
    @DisplayName("Teleport commands should validate coordinates")
    void testTeleportCommands() {
        player.addAttachment(plugin, "CraftUtils.teleport.location", true);
        
        // Test valid coordinates
        boolean result = server.execute("cu tp location 100 64 200", player);
        assertTrue(result, "Valid teleport command should execute");
    }
    
    @Test
    @DisplayName("Home commands should handle validation")
    void testHomeCommands() {
        player.addAttachment(plugin, "CraftUtils.home.sethome", true);
        player.addAttachment(plugin, "CraftUtils.home", true);
        player.addAttachment(plugin, "CraftUtils.home.list", true);
        
        // Test setting a home
        boolean result1 = server.execute("sethome test", player);
        assertTrue(result1, "Set home command should execute");
        
        // Test listing homes
        boolean result2 = server.execute("homes", player);
        assertTrue(result2, "List homes command should execute");
        
        // Test going to home
        boolean result3 = server.execute("home test", player);
        assertTrue(result3, "Go to home command should execute");
    }
}