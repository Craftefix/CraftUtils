package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.database.HomeManager;
import dev.craftefix.craftUtils.database.HomeManager.Home;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomeCommand {
    HomeManager homeManager;
    private final int defaultHomeLimit = 3;
    private final boolean requireConfirmation = true;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }
    // Change to false to disable confirmation

    @Command({"home", "cu home"})
    @CommandPermission("CraftUtils.home")
    public void home(Player player, @revxrsal.commands.annotation.Optional String name) {
        if (name == null) name = "default";
        Optional<Home> homeOpt = homeManager.getHome(player.getUniqueId().toString(), name);
        if (homeOpt.isPresent()) {
            Home home = homeOpt.get();
            player.teleport(new Location(player.getWorld(), home.getX(), home.getY(), home.getZ()));
            player.sendMessage("§aTeleported to home '" + name + "'.");
        } else {
            player.sendMessage("§cHome not found.");
        }
    }

    @Command({"sethome", "cu sethome"})
    public void setHome(Player player, String name) {
        if (name == null) name = "default";
        List<Home> homes = homeManager.getAllHomes(player.getUniqueId().toString());
        int homeLimit = getHomeLimit(player);

        String finalName = name;
        if (homes.size() >= homeLimit && homes.stream().noneMatch(h -> h.getHomeName().equalsIgnoreCase(finalName))) {
            player.sendMessage("§cYou have reached your home limit of " + homeLimit + " homes.");
            return;
        }

        String finalName1 = name;
        if (requireConfirmation && homes.stream().anyMatch(h -> h.getHomeName().equalsIgnoreCase(finalName1))) {
            player.sendMessage("§eHome already exists. Run /sethome " + name + " again to confirm overwrite.");
            return;
        }

        Location loc = player.getLocation();
        homeManager.createHome(player.getUniqueId().toString(), name, loc.getX(), loc.getY(), loc.getZ());
        player.sendMessage("§aHome '" + name + "' set!");
    }

    @Command({"delhome", "cu delhome"})
    public void delHome(Player player, String name) {
        if (homeManager.getHome(player.getUniqueId().toString(), name).isPresent()) {
            homeManager.deleteHome(player.getUniqueId().toString(), name);
            player.sendMessage("§aHome '" + name + "' deleted.");
        } else {
            player.sendMessage("§cHome not found.");
        }
    }

    @Command({"homes", "cu homes"})
    public void listHomes(Player player) {
        List<Home> homes = homeManager.getAllHomes(player.getUniqueId().toString());
        if (homes.isEmpty()) {
            player.sendMessage("§cYou have no homes set.");
        } else {
            String homeList = homes.stream().map(Home::getHomeName).collect(Collectors.joining(", "));
            player.sendMessage("§aYour homes: " + homeList);
        }
    }

    private int getHomeLimit(Player player) {
        if (player.hasPermission("craftutils.homes.unlimited")) return Integer.MAX_VALUE;
        for (int i = 10; i > 0; i--) {
            if (player.hasPermission("craftutils.homes." + i)) return i;
        }
        return defaultHomeLimit;
    }
}