package dev.craftefix.craftUtils;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public class AdminCommands {

    private final SpiGUI spiGUI;

    public AdminCommands(JavaPlugin plugin) {
        this.spiGUI = new SpiGUI(plugin);
    }

    @Command({"admingui", "cc admingui"})
    @Description("Opens the admin GUI")
    @revxrsal.commands.bukkit.annotation.CommandPermission("CraftNet.craftUtils.admingui")
    public void openAdminGUI(Player player) {
        SGMenu menu = spiGUI.create("Admin GUI", 3);


        ItemStack gamemodeItem = new ItemStack(Material.DIAMOND_SWORD);
        menu.addButton(new SGButton(gamemodeItem).withListener(event -> {
            player.sendMessage("Gamemode category clicked!");
        }));


        ItemStack PlayerManagementItem = new ItemStack(Material.PLAYER_HEAD);
        menu.addButton(new SGButton(PlayerManagementItem).withListener(event -> {
            player.sendMessage("Player management category clicked!");
        }));

        ItemStack utilsItem = new ItemStack(Material.REDSTONE);
        menu.addButton(new SGButton(utilsItem).withListener(event -> {
            player.sendMessage("Utils category clicked!");
        }));

        player.openInventory(menu.getInventory());
    }
}