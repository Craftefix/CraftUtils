package dev.craftefix.craftUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftUtilsCommand {

    @Command({"cu"})
    @CommandPermission("craftutils.main")
    public void craftUtils(Player actor) {
        actor.sendMessage(Component.text()
                .append(Component.text(" ~~~ CraftUtils ~~~ ", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("A utils plugin that has everything you need.", NamedTextColor.BLUE).decorate(TextDecoration.ITALIC))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Version: ", NamedTextColor.BLUE))
                .append(Component.text(getPlugin().getDescription().getVersion(), NamedTextColor.YELLOW))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("GitHub: Craftefix/CraftUtils", NamedTextColor.BLACK))
                        .clickEvent(ClickEvent.openUrl("https://github.com/Craftefix/CraftUtils"))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("CraftUtils on Modrinth", NamedTextColor.YELLOW))
                        .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/craftutils"))
                .append(Component.text("Discord", NamedTextColor.BLUE))
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/GbKQUqp6yG"))
                .build());

    }

    private JavaPlugin getPlugin() {
        return JavaPlugin.getProvidingPlugin(CraftUtilsCommand.class);
    }
}