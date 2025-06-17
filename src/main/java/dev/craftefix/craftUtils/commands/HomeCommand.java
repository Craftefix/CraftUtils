package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.HomeManager.Home;
import dev.craftefix.craftUtils.suggestions.HomeSuggestionProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomeCommand {
    private final HomeManager homeManager;
    private final YamlConfiguration lang;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
        this.lang = Main.getInstance().getLang();
    }

    @Command({"home", "cu home"})
    @CommandPermission("CraftUtils.home")
    public void home(Player actor, @SuggestWith(HomeSuggestionProvider.class)@revxrsal.commands.annotation.Optional @Default("default") String name) {
        Optional<Home> homeOpt = homeManager.getHome(actor.getUniqueId().toString(), name);
        if (homeOpt.isPresent()) {
            Home home = homeOpt.get();
            actor.teleport(new Location(home.getWorld(), home.getX(), home.getY(), home.getZ()));

            String message = lang.getString("homes.success.teleported", "Teleported to home {name}")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.GRAY)));
        } else {
            String message = lang.getString("homes.error.not-found", "Home {name} does not exist")
                    .replace("{name}", name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.DARK_RED)));
        }
    }

    @Command({"sethome", "cu sethome"})
    @CommandPermission("CraftUtils.home.sethome")
    public void setHome(Player actor, @Named("home") @Default("default") String name) {
        if (name == null) name = "default";
        List<Home> homes = homeManager.getAllHomes(actor.getUniqueId().toString());
        int homeLimit = getHomeLimit(actor);

        String finalName = name;
        if (homes.size() >= homeLimit && homes.stream().noneMatch(h -> h.getHomeName().equalsIgnoreCase(finalName))) {
            String message = lang.getString("homes.error.limit-reached", "You have reached your homes limit")
                    .replace("{limit}", String.valueOf(homeLimit));
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text(message, NamedTextColor.DARK_RED)));
            return;
        } else if (homes.size() == 0) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Homes are not allowed with your permissions, contact your server administrator", NamedTextColor.DARK_RED)));
        }

        String finalName1 = name;
        if (homes.stream().anyMatch(h -> h.getHomeName().equalsIgnoreCase(finalName1))) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Home already exists, "))
                    .append(Component.text("'/cu delhome <home>'", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text( " to delete", NamedTextColor.GRAY)).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));
            return;
        }

        Location loc = actor.getLocation();
        homeManager.createHome(actor.getUniqueId().toString(), name, loc.getX(), loc.getY(), loc.getZ(), actor.getWorld());
        actor.sendMessage(Component.text()
                .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Home ", NamedTextColor.GRAY))
                .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                .append(Component.text(" set", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
    }

    @Command({"delhome", "cu delhome"})
    @CommandPermission("CraftUtils.home.delete")
    public void delHome(Player actor, @SuggestWith(HomeSuggestionProvider.class) String name) {
        if (homeManager.getHome(actor.getUniqueId().toString(), name).isPresent()) {
            homeManager.deleteHome(actor.getUniqueId().toString(), name);
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Home ", NamedTextColor.GRAY))
                    .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" deleted", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
        } else {
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes ", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Home ", NamedTextColor.DARK_RED))
                    .append(Component.text(name, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text(" not found.", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
        }
    }

    @Command({"homes", "cu homes"})
    @CommandPermission("CraftUtils.home.list")
    public void listHomes(Player actor) {
        List<Home> homes = homeManager.getAllHomes(actor.getUniqueId().toString());
        if (homes.isEmpty()) {
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes: \n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("No Homes exist, use ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("'/cu sethome <home>'", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    .append(Component.text( " to create your first home", NamedTextColor.GRAY)).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));
        } else {
            String homeList = homes.stream().map(Home::getHomeName).collect(Collectors.joining(", "));
            actor.sendMessage(Component.text()
                    .append(Component.text("Homes: \n", NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                    .append(Component.text("Your homes are: \n", NamedTextColor.GRAY))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(homeList, NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
        }
    }

    private int getHomeLimit(Player player) {
        if (player.hasPermission("craftutils.homes.unlimited")) return Integer.MAX_VALUE;
        for (int i = 20; i > 0; i--) {
            if (player.hasPermission("craftutils.homes." + i)) return i;
        }
        return 0;
    }
}