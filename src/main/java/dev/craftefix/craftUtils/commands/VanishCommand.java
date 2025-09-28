package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.Main;
import dev.craftefix.craftUtils.database.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class VanishCommand {
    private final Main plugin;
    private final VanishManager vanishManager;
    
    public VanishCommand(Main plugin) {
        this.plugin = plugin;
        this.vanishManager = VanishManager.getInstance(plugin);
    }
    
    @Command("vanish")
    @CommandPermission("CraftUtils.vanish")
    public void vanishCommand(CommandSender sender, @Optional String targetPlayer) {
        if (targetPlayer == null) {
            // Toggle own vanish
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use vanish without specifying a target.");
                return;
            }
            
            Player player = (Player) sender;
            vanishManager.toggleVanish(player);
        } else {
            // Toggle another player's vanish (requires admin permission)
            if (!sender.hasPermission("CraftUtils.vanish.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to vanish other players.");
                return;
            }
            
            Player target = Bukkit.getPlayer(targetPlayer);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Player '" + targetPlayer + "' is not online.");
                return;
            }
            
            vanishManager.toggleVanish(target);
            
            String status = vanishManager.isVanished(target) ? "vanished" : "unvanished";
            sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " is now " + status + ".");
        }
    }
    
    @Command("vanish list")
    @CommandPermission("CraftUtils.vanish.list")
    public void vanishListCommand(CommandSender sender) {
        if (vanishManager.getVanishedCount() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No players are currently vanished.");
            return;
        }
        
        sender.sendMessage(ChatColor.GREEN + "Vanished players (" + vanishManager.getVanishedCount() + "):");
        for (java.util.UUID uuid : vanishManager.getVanishedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                sender.sendMessage(ChatColor.GRAY + "- " + player.getName());
            }
        }
    }
}