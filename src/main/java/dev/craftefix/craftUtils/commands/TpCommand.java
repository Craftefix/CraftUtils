package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.parameters.EntitySelector;

@Command("cu tp")
public class TpCommand {
    private final YamlConfiguration lang;

    /**
     * Initializes the TpCommand and loads the language configuration for localized messages.
     */
    public TpCommand() {
        this.lang = Main.getInstance().getLang();
    }

    /**
     * Determines whether the specified location is within the world border.
     *
     * @param location the location to check
     * @return true if the location is inside the world border; false otherwise
     */
    private boolean isWithinWorldBorder(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        double x = location.getX();
        double z = location.getZ();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        return x >= center.getX() - size && x <= center.getX() + size
                && z >= center.getZ() - size && z <= center.getZ() + size;
    }

    /**
     * Checks if the specified Y-coordinate is within the allowed height limit.
     *
     * @param y the Y-coordinate to validate
     * @return true if y is less than or equal to 1000, false otherwise
     */
    private boolean isValidHeight(double y) {
        return y <= 1000;
    }

    /**
     * Teleports the command sender to the specified coordinates in their current world.
     *
     * Teleportation succeeds only if the target location is within the world border and below the height limit of 1000. Sends a localized success or error message to the sender.
     *
     * @param x the X coordinate to teleport to
     * @param y the Y coordinate to teleport to
     * @param z the Z coordinate to teleport to
     */
    @Subcommand("location")
    @CommandPermission("CraftUtils.teleport.location")
    public void teleportLocation(Player sender, @Named("x") double x, @Named("y") double y, @Named("z") double z) {
        Location location = new Location(sender.getWorld(), x, y, z);
        if (isWithinWorldBorder(location) && isValidHeight(y)) {
            sender.teleport(location);
            sender.sendMessage(Component.text(lang.getString("teleport.command.success.teleported", "Teleported successfully."), NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text(lang.getString("teleport.command.error.invalid-location", "Invalid location: outside world border or height exceeds 1000."), NamedTextColor.RED));
        }
    }

    /**
     * Teleports selected living entities to the specified coordinates in the sender's world.
     *
     * If the target location is within the world border and below the height limit, each selected entity is teleported and the sender receives a localized success message with the count of entities teleported. If the location is invalid, a localized error message is sent to the sender.
     *
     * @param target selector for the living entities to teleport
     * @param x X-coordinate of the destination
     * @param y Y-coordinate of the destination
     * @param z Z-coordinate of the destination
     */
    @Subcommand("others")
    @CommandPermission("CraftUtils.teleport.others")
    public void teleportOthers(Player sender, @Named("target") EntitySelector<LivingEntity> target, @Named("x") double x, @Named("y") double y, @Named("z") double z) {
        Location location = new Location(sender.getWorld(), x, y, z);
        if (isWithinWorldBorder(location) && isValidHeight(y)) {
            int count = 0;
            for (LivingEntity entity : target) {
                entity.teleport(location);
                count++;
            }
            String message = lang.getString("teleport.command.success.teleported-others", "Teleported {count} entities.")
                    .replace("{count}", String.valueOf(count));
            sender.sendMessage(Component.text(message, NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text(lang.getString("teleport.command.error.invalid-location", "Invalid location: outside world border or height exceeds 1000."), NamedTextColor.RED));
        }
    }

    /**
     * Teleports the selected living entities to the sender's current location.
     *
     * Sends a localized success message with the number of entities teleported, or an error message if the location is outside the world border or exceeds the height limit.
     */
    @Subcommand("here")
    @CommandPermission("CraftUtils.teleport.here")
    public void teleportHere(Player sender, @Named("target") EntitySelector<LivingEntity> target) {
        Location location = sender.getLocation();
        if (isWithinWorldBorder(location) && isValidHeight(location.getY())) {
            int count = 0;
            for (LivingEntity entity : target) {
                entity.teleport(sender);
                count++;
            }
            String message = lang.getString("teleport.command.success.teleported-others", "Teleported {count} entities.")
                    .replace("{count}", String.valueOf(count));
            sender.sendMessage(Component.text(message, NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text(lang.getString("teleport.command.error.invalid-location", "Invalid location: outside world border or height exceeds 1000."), NamedTextColor.RED));
        }
    }

    /**
     * Teleports the command sender to the location of the specified target entity if the location is within the world border and below the height limit.
     *
     * Sends a localized success message on successful teleportation, or an error message if the location is invalid.
     */
    @Subcommand("self")
    @CommandPermission("CraftUtils.teleport.self")
    public void teleportSelf(Player sender, @Named("target") Entity target) {
        Location location = target.getLocation();
        if (isWithinWorldBorder(location) && isValidHeight(location.getY())) {
            sender.teleport(target);
            sender.sendMessage(Component.text(lang.getString("teleport.command.success.teleported", "Teleported successfully."), NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text(lang.getString("teleport.command.error.invalid-location", "Invalid location: outside world border or height exceeds 1000."), NamedTextColor.RED));
        }
    }
}
