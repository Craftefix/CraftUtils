package dev.craftefix.craftUtils;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for the CraftUtils plugin
 */
class CraftUtilsTest {
    
    private ServerMock server;
    private Main plugin;
    
    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
    }
    
    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    @DisplayName("Plugin should enable successfully")
    void testPluginEnable() {
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");
        assertNotNull(plugin.getDescription(), "Plugin description should not be null");
        assertEquals("CraftUtils", plugin.getDescription().getName(), "Plugin name should be CraftUtils");
    }
    
    @Test
    @DisplayName("Plugin should have correct version format")
    void testPluginVersion() {
        String version = plugin.getDescription().getVersion();
        assertNotNull(version, "Version should not be null");
        assertTrue(version.matches("\\d+\\.\\d+\\..*"), "Version should follow semantic versioning pattern");
    }
    
    @Test
    @DisplayName("Plugin should register commands")
    void testCommandsRegistered() {
        // Test that main command is registered
        assertNotNull(server.getCommandMap().getCommand("cu"), "Main command 'cu' should be registered");
        
        // Test some key commands are available
        assertNotNull(server.getCommandMap().getCommand("home"), "Home command should be registered");
        assertNotNull(server.getCommandMap().getCommand("warp"), "Warp command should be registered");
        assertNotNull(server.getCommandMap().getCommand("vault"), "Vault command should be registered");
    }
}