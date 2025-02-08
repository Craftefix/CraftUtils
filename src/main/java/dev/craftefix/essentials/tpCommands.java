package dev.craftefix.essentials;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.parameters.EntitySelector;

public class tpCommands {

    private boolean isWithinWorldBorder(Location location) {
        WorldBorder border = location.getWorld().getWorldBorder();
        double x = location.getX();
        double z = location.getZ();
        double size = border.getSize() / 2;
        Location center = border.getCenter();
        return x >= center.getX() - size && x <= center.getX() + size &&
                z >= center.getZ() - size && z <= center.getZ() + size;
    }

    @Command({"teleport", "tp"})
    public void teleport(Player sender, double x, double y, double z) {
        Location location = new Location(sender.getWorld(), x, y, z);
        sender.teleport(location);
    }
    @Command({"teleport", "tp"})
    public void teleport(Player sender, EntitySelector<LivingEntity> target, double x, double y, double z) {
        Location location = new Location(sender.getWorld(), x, y, z);
        for (LivingEntity entity : target)
            entity.teleport(location);
    }
    @Command("teleport <target> here")
    public void teleportHere(Player sender, EntitySelector<LivingEntity> target) {
        for (LivingEntity entity : target)
            entity.teleport(sender);
    }
    @Command({"teleport", "tp"})
    public void teleport(Player sender, Entity target) {
        sender.teleport(target);
    }


}