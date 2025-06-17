package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.database.WarpManager.Warp;
import dev.craftefix.craftUtils.suggestions.WarpSuggestionProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand {
    private final WarpManager warpManager;
    private final YamlConfiguration lang;

    /**
     * Constructs a WarpCommand instance with the specified WarpManager and loads localized language configuration.
     *
     * @param warpManager the manager responsible for warp data operations
     */
    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
        this.lang = Main.getInstance().getLang();
    }

    /**
     * Teleports the player to the specified warp location.
     *
     * If the warp with the given name exists, the player is teleported there and receives a localized success message. If the warp does not exist, a localized error message is sent instead. The default warp name is "spawm" if none is provided.
     *
     * @param name the name of the warp to teleport to (defaults to "spawm" if not specified)
     */
    @Command({"warp", "cu warp"})
    @CommandPermission("CraftUtils.warp")
    public void warp(Player actor, @SuggestWith(WarpSuggestionProvider.class) @Optional @Default("spawm") String name) {
        java.util.Optional<Warp> warpOpt = warpManager.getWarp(name);
        if (warpOpt.isPresent()) {
            Warp warp = warpOpt.get();
            actor.teleport(new Location(warp.getWorld(), warp.getX(), warp.getY(), warp.getZ()));
            String message = lang.getString("warps.success.teleported", "Teleported to warp {name}")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        } else {
            String message = lang.getString("warps.error.not-found", "Warp {name} does not exist")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
        }
    }

    /**
     * Creates a new warp at the player's current location with the specified name and visibility.
     *
     * If a warp with the given name already exists, notifies the player and does not create a new warp.
     * The warp name defaults to "spawn" if not provided or is null. The warp can be marked as hidden.
     *
     * @param name the name for the new warp; defaults to "spawn" if not specified or null
     * @param hidden whether the warp should be hidden from public listings
     */
    @Command({"setwarp", "cu setwarp"})
    @CommandPermission("CraftUtils.warp.setwarp")
    public void setWarp(Player actor, @Named("warp") @Default("spawn") String name, @Optional @Named("hidden") boolean hidden) {
        if (name == null) name = "spawm";
        List<Warp> warps = warpManager.getAllWarps();
        String finalName = name;

        if (warps.stream().anyMatch(h -> h.getWarpName().equalsIgnoreCase(finalName))) {
            String message = lang.getString("warps.error.already-exists", "A warp with that name already exists")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
            return;
        }

        Location loc = actor.getLocation();
        warpManager.createWarp(actor.getUniqueId().toString(), name, loc.getX(), loc.getY(), loc.getZ(), hidden ? 1 : 0, loc.getWorld());
        String message = lang.getString("warps.success.created", "Warp {name} created successfully")
                .replace("{name}", name);
        actor.sendMessage(Component.text()
                .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text(message, NamedTextColor.GRAY)));
    }

    /**
     * Deletes a warp with the specified name if it exists.
     *
     * Sends a localized success message if the warp is deleted, or an error message if the warp does not exist.
     */
    @Command({"delwarp", "cu delwarp"})
    @CommandPermission("CraftUtils.warp.delete")
    public void delWarp(Player actor, @SuggestWith(WarpSuggestionProvider.class) String name) {
        if (warpManager.getWarp(name).isPresent()) {
            warpManager.deleteWarp(name);
            String message = lang.getString("warps.success.deleted", "Warp {name} deleted successfully")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        } else {
            String message = lang.getString("warps.error.not-found", "Warp {name} does not exist")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
        }
    }

    /**
     * Sends the player a localized list of all available warps, or a message if no warps exist.
     *
     * @param actor the player requesting the list of warps
     */
    @Command({"warps", "cu warps"})
    @CommandPermission("CraftUtils.warp.list")
    public void listWarps(Player actor) {
        List<Warp> warps = warpManager.getAllWarps();
        if (warps.isEmpty()) {
            String message = lang.getString("warps.list.empty", "No warps available");
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps: \n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.DARK_GRAY)));
        } else {
            String header = lang.getString("warps.list.header", "Available warps:");
            String warpList = warps.stream()
                    .map(warp -> lang.getString("warps.list.entry", "- {name}")
                            .replace("{name}", warp.getWarpName()))
                    .collect(Collectors.joining("\n"));

            actor.sendMessage(Component.text()
                    .append(Component.text(header + "\n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text(warpList, NamedTextColor.YELLOW)));
        }
    }
}