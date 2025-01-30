package dev.craftefix.essentials;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.UUID;

public class messageCommands extends JavaPlugin {

    private final HashMap<UUID, UUID> messagers = new HashMap<>();
    private NamedTextColor actorColor;
    private NamedTextColor messageColor;
    private NamedTextColor errorColor;
    private boolean enableLogging;

    @Override
    public void onEnable() {
        // Save default config.yml if it doesn't exist
        saveDefaultConfig();
        // Load and process the config
        loadConfig();
    }

    public void loadConfig() {
        // Load colors from the "msg.colors" category
        try {
            actorColor = NamedTextColor.NAMES.value(getConfig().getString("msg.colors.actor", "YELLOW"));
            messageColor = NamedTextColor.NAMES.value(getConfig().getString("msg.colors.message", "GRAY"));
            errorColor = NamedTextColor.NAMES.value(getConfig().getString("msg.colors.error", "RED"));
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid color found in config.yml under 'msg.colors'. Falling back to default colors.");
            actorColor = NamedTextColor.YELLOW;
            messageColor = NamedTextColor.GRAY;
            errorColor = NamedTextColor.RED;
        }

        // Load developer settings
        enableLogging = getConfig().getBoolean("developer-settings.enable-logging", false);
    }

    /**
     * Logs debug messages if developer logging is enabled in the config.
     *
     * @param message The debug message to log.
     */
    private void logDebug(String message) {
        if (enableLogging) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    @Command({"msg", "message", "tell"})
    @CommandPermission("CraftNet.essentials.message")
    public void msg(Player actor, Player target, String message) {
        // Log debug information
        logDebug("Command /msg executed by: " + actor.getName());
        logDebug("Message target: " + target.getName());
        logDebug("Message content: " + message);

        // Send messages using the defined colors
        actor.sendMessage("You to " + actorColor + target.getName() + ": " + messageColor + message);
        target.sendMessage(actorColor + actor.getName() + " to you: " + messageColor + message);

        if (actor.isOnline() && target.isOnline()) {
            messagers.put(actor.getUniqueId(), target.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Log the player's quit event if debugging is enabled
        logDebug("Player quit: " + e.getPlayer().getName());

        // Remove the player from the messagers map on quit
        messagers.remove(e.getPlayer().getUniqueId());
    }

    @Command({"r", "reply"})
    @CommandPermission("CraftNet.essentials.reply")
    public void reply(Player actor, String message) {
        // Log debug information
        logDebug("Command /reply executed by: " + actor.getName());
        logDebug("Reply message content: " + message);

        UUID targetUUID = messagers.get(actor.getUniqueId()); // Check if the actor has messaged someone last
        if (targetUUID != null) { // If a target exists
            Player target = getServer().getPlayer(targetUUID); // Retrieve the player from the server
            if (target != null && target.isOnline()) {
                // Send the message to the target
                target.sendMessage(actorColor + actor.getName() + " to you: " + messageColor + message);
                // Also send the message to the actor (as confirmation)
                actor.sendMessage("You to " + actorColor + target.getName() + ": " + messageColor + message);
            } else {
                // Log debug if the player to reply to is offline
                logDebug("The player being replied to is offline: UUID=" + targetUUID);

                // Notify the actor if the target is offline
                actor.sendMessage(errorColor + "The player you are replying to is no longer online.");
            }
        } else {
            // Log debug if no previous message target exists
            logDebug("Player attempted to reply but has no recent message target.");

            // Notify the actor if there is no previous message target
            actor.sendMessage(errorColor + "You don't have anyone to reply to.");
        }
    }

}