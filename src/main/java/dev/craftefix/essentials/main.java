package dev.craftefix.essentials;

                        import org.bukkit.plugin.java.JavaPlugin;
                        import revxrsal.commands.bukkit.BukkitLamp;

                        public final class main extends JavaPlugin {

                            @Override
                            public void onEnable() {
                                try {
                                    System.out.println("trying to save conf");
                                    saveDefaultConfig();

                                    System.out.println("loaded config.yml, trying to register commands");
                                    var lamp = BukkitLamp.builder(this).build();
                                    messageCommands msgCommands = new messageCommands();
                                    msgCommands.loadConfig(this);
                                    lamp.register(new tpCommands());
                                    lamp.register(msgCommands);
                                    System.out.println("Loaded commands, trying to check for debug state");

                                    if (getConfig().getBoolean("developer-settings.enable-logging")) {
                                        getLogger().info("Debugging is enabled!");
                                    }
                                } catch (Exception e) {
                                    getLogger().severe("Error at onEnable: " + e.getMessage());
                                    for (StackTraceElement element : e.getStackTrace()) {
                                        getLogger().severe(element.toString());
                                    }
                                }
                            }

                            @Override
                            public void onDisable() {
                                try {
                                    getLogger().info("Plugin is deactivating");
                                } catch (Exception e) {
                                    getLogger().severe("Error at onDisable: " + e.getMessage());
                                    for (StackTraceElement element : e.getStackTrace()) {
                                        getLogger().severe(element.toString());
                                    }
                                }
                            }
                        }