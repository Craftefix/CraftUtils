package dev.craftefix.essentials;

        import net.kyori.adventure.text.Component;
        import net.kyori.adventure.text.format.NamedTextColor;
        import org.bukkit.Location;
        import org.bukkit.WorldBorder;
        import org.bukkit.entity.Entity;
        import org.bukkit.entity.LivingEntity;
        import org.bukkit.entity.Player;
        import revxrsal.commands.annotation.Command;
        import revxrsal.commands.annotation.Named;
        import revxrsal.commands.annotation.Subcommand;
        import revxrsal.commands.bukkit.annotation.CommandPermission;
        import revxrsal.commands.bukkit.parameters.EntitySelector;

        @Command("etp")
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

            private boolean isValidHeight(double y) {
                return y <= 1000;
            }

            @Subcommand("location")
            @CommandPermission("CraftNet.essentials.teleport.location")
            public void teleportLocation(Player sender, @Named("x") double x, @Named("y") double y, @Named("z") double z) {
                Location location = new Location(sender.getWorld(), x, y, z);
                if (isWithinWorldBorder(location) && isValidHeight(y)) {
                    sender.teleport(location);
                    sender.sendMessage(Component.text("Teleported to location: ", NamedTextColor.GREEN)
                            .append(Component.text(x + ", " + y + ", " + z, NamedTextColor.YELLOW)));
                } else {
                    sender.sendMessage(Component.text("Invalid location: outside world border or height exceeds 1000.", NamedTextColor.RED));
                }
            }

            @CommandPermission("CraftNet.essentials.teleport.location")
            public void teleportLocation(Player sender) {
                Location location = sender.getLocation();
                if (isWithinWorldBorder(location) && isValidHeight(location.getY())) {
                    sender.teleport(location);
                    sender.sendMessage(Component.text("Teleported to your current location.", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Invalid location: outside world border or height exceeds 1000.", NamedTextColor.RED));
                }
            }

            @Subcommand("others")
            @CommandPermission("CraftNet.essentials.teleport.others")
            public void teleportOthers(Player sender, @Named("target") EntitySelector<LivingEntity> target, @Named("x") double x, @Named("y") double y, @Named("z") double z) {
                Location location = new Location(sender.getWorld(), x, y, z);
                if (isWithinWorldBorder(location) && isValidHeight(y)) {
                    for (LivingEntity entity : target) {
                        entity.teleport(location);
                    }
                    sender.sendMessage(Component.text("Teleported others to location: ", NamedTextColor.GREEN)
                            .append(Component.text(x + ", " + y + ", " + z, NamedTextColor.YELLOW)));
                } else {
                    sender.sendMessage(Component.text("Invalid location: outside world border or height exceeds 1000.", NamedTextColor.RED));
                }
            }

            @Subcommand("here")
            @CommandPermission("CraftNet.essentials.teleport.here")
            public void teleportHere(Player sender, @Named("target") EntitySelector<LivingEntity> target) {
                Location location = sender.getLocation();
                if (isWithinWorldBorder(location) && isValidHeight(location.getY())) {
                    for (LivingEntity entity : target) {
                        entity.teleport(sender);
                    }
                    sender.sendMessage(Component.text("Teleported others to your location.", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Invalid location: outside world border or height exceeds 1000.", NamedTextColor.RED));
                }
            }

            @Subcommand("self")
            @CommandPermission("CraftNet.essentials.teleport.self")
            public void teleportSelf(Player sender, @Named("target") Entity target) {
                Location location = target.getLocation();
                if (isWithinWorldBorder(location) && isValidHeight(location.getY())) {
                    sender.teleport(target);
                    sender.sendMessage(Component.text("Teleported to target.", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Invalid location: outside world border or height exceeds 1000.", NamedTextColor.RED));
                }
            }
        }