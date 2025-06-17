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

    public TpCommand() {
        this.lang = Main.getInstance().getLang();
    }

    private boolean isWithinWorldBorder(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        double x = location.getX();
        double z = location.getZ();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        return x >= center.getX() - size && x <= center.getX() + size
                && z >= center.getZ() - size && z <= center.getZ() + size;
    }

    private boolean isValidHeight(double y) {
        return y <= 1000;
    }

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
