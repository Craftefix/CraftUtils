package dev.craftefix.essentials;

                                import org.bukkit.Bukkit;
                                import org.bukkit.Location;
                                import org.bukkit.WorldBorder;
                                import org.bukkit.entity.Player;
                                import revxrsal.commands.annotation.Command;
                                import revxrsal.commands.annotation.Named;
                                import revxrsal.commands.annotation.Optional;

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

                                    @Command({"tp", "teleport"})
                                    public void tp(Player actor, @Named("target") Player target, @Named("x") @Optional Double x, @Named("y") @Optional Double y, @Named("z") @Optional Double z) {
                                        if (x != null && y != null && z != null) {
                                            if (y > 1000) {
                                                actor.sendMessage("Maximum teleport height is 1000.");
                                                return;
                                            }
                                            Location location = new Location(target.getWorld(), x, y, z);
                                            if (!isWithinWorldBorder(location)) {
                                                actor.sendMessage("Coordinates are outside the world border.");
                                                return;
                                            }
                                            target.teleport(location);
                                            actor.sendMessage("Teleported " + target.getName() + " to " + x + ", " + y + ", " + z);
                                        } else {
                                            Player anotherPlayer = Bukkit.getPlayer(target.getName());
                                            if (anotherPlayer != null) {
                                                target.teleport(anotherPlayer);
                                                actor.sendMessage("Teleported " + target.getName() + " to " + anotherPlayer.getName());
                                            } else {
                                                actor.sendMessage("Player " + target.getName() + " not found.");
                                            }
                                        }
                                    }

                                    @Command({"tppos", "teleportcords"})
                                    public void tp(Player actor, @Named("x") Double x, @Named("y") Double y, @Named("z") Double z) {
                                        if (y > 1000) {
                                            actor.sendMessage("Maximum teleport height is 1000.");
                                            return;
                                        }
                                        Location location = new Location(actor.getWorld(), x, y, z);
                                        if (!isWithinWorldBorder(location)) {
                                            actor.sendMessage("Coordinates are outside the world border.");
                                            return;
                                        }
                                        actor.teleport(location);
                                        actor.sendMessage("Teleported to " + x + ", " + y + ", " + z);
                                    }
                                    @Command({"tpp", "teleportplayer", "tpplayer"})
                                    public void tpp(Player actor, @Named("player") Player player, @Named("target") Player target) {
                                        if (target == null) {
                                            actor.sendMessage("Player not found.");
                                            return;
                                        }
                                        if (target.getWorld() != player.getWorld()) {
                                            actor.sendMessage("Player is in another world, use /tpworld");
                                            return;
                                        }
                                        if (!isWithinWorldBorder(target.getLocation()) && !isWithinWorldBorder(player.getLocation())) {
                                            actor.sendMessage("Player is outside the world border, please report to an admin.");
                                            return;
                                        }
                                        player.teleport(target);
                                        actor.sendMessage("Teleported to " + target.getName());
                                    }
                                }