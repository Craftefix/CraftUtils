package dev.craftefix.craftUtils;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class AdminGUICommand {

    private final AdminGUI adminGUI;

    public AdminGUICommand(AdminGUI adminGUI) {
        this.adminGUI = adminGUI;
    }

    @Command({"admingui", "cc admingui"})
    @CommandPermission("CraftUtils.admingui")
    public void openAdminGUI(Player player) {
        adminGUI.openAdminGUI(player);
    }
}