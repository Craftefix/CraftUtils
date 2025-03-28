package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.database.WarpManager;
import dev.craftefix.craftUtils.database.WarpManager.Warp;
import dev.craftefix.craftUtils.suggestions.WarpSuggestionProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.stream.Collectors;

public class  WarpCommand{
    WarpManager warpManager;

    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Command({"warp", "cu warp"})
    @CommandPermission("CraftUtils.warp")
    public void warp(Player actor, @SuggestWith(WarpSuggestionProvider.class) @Optional @Default("spawm") String name) {
        java.util.Optional<Warp> warpOpt = warpManager.getWarp(name);
        if (warpOpt.isPresent()) {
            Warp warp = warpOpt.get();
            actor.teleport(new Location(warp.getWorld(), warp.getX(), warp.getY(), warp.getZ()));
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Teleported to your warp.", NamedTextColor.GRAY)));
        } else {
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Couldn't find this warp.", NamedTextColor.LIGHT_PURPLE)));
        }
    }

    @Command({"setwarp", "cu setwarp"})
    @CommandPermission("CraftUtils.warp.setwarp")
    public void setWarp(Player actor, @Named("warp") @Default("spawn") String name, @Optional @Named("hidden") boolean hidden) {
        if (name == null) name = "spawm";
        List<Warp> warps = warpManager.getAllWarps();

        String finalName = name;

        if (warps.stream().anyMatch(h -> h.getWarpName().equalsIgnoreCase(finalName))) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Warp already exists, "))
                    .append(Component.text("'/cu delwarp <warp>'", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text( " to delete", NamedTextColor.GRAY)).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));
            return;
        }


        Location loc = actor.getLocation();
        warpManager.createWarp(actor.getUniqueId().toString(), name, loc.getX(), loc.getY(), loc.getZ(), hidden ? 1 : 0 ,loc.getWorld());
        actor.sendMessage(Component.text()
                .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Warp ", NamedTextColor.GRAY))
                .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" set", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
    }

    @Command({"delwarp", "cu delwarp"})
    @CommandPermission("CraftUtils.warp.delete")
    public void delWarp(Player actor, @SuggestWith(WarpSuggestionProvider.class) String name) {
        if (warpManager.getWarp(name).isPresent()) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Warp ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" deleted", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
        } else {
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Warp ", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" not found.", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
        }
    }

    @Command({"warps", "cu warps"})
    @CommandPermission("CraftUtils.warp.list")
    public void listWarps(Player actor) {
        List<Warp> warps = warpManager.getAllWarps();
        if (warps.isEmpty()) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps: \n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("No Warps exist, use ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("'/cu setwarp <warp>'", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text( " to create your first warp", NamedTextColor.GRAY)).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));
        } else {
            String warpList = warps.stream().map(Warp::getWarpName).collect(Collectors.joining(", "));
            actor.sendMessage(Component.text()
                    .append(Component.text("Warps: \n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Your warps are: \n", NamedTextColor.GRAY))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(warpList, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }
    }
}