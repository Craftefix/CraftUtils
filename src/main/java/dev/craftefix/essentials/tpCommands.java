package dev.craftefix.essentials;

                import org.bukkit.Bukkit;
                import org.bukkit.Location;
                import org.bukkit.entity.Player;
                import revxrsal.commands.annotation.Command;
                import revxrsal.commands.annotation.Named;
                import revxrsal.commands.annotation.Optional;

                public class tpCommands {

                    @Command({"tp", "teleport"})
                    public void tp(Player actor, @Named("target") Player target, @Named("x") @Optional Double x, @Named("y") @Optional Double y, @Named("z") @Optional Double z) {
                        if (x != null && y != null && z != null) {
                            // /tp <player> <x> <y> <z>
                            Location location = new Location(target.getWorld(), x, y, z);
                            target.teleport(location);
                            actor.sendMessage("Teleported " + target.getName() + " to " + x + ", " + y + ", " + z);
                        } else {
                            // /tp <player> <another_player>
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
                        // /tp <x> <y> <z>
                        Location location = new Location(actor.getWorld(), x, y, z);
                        actor.teleport(location);
                        actor.sendMessage("Teleported to " + x + ", " + y + ", " + z);
                    }
                }