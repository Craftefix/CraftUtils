 package dev.craftefix.craftUtils.commands;

import dev.craftefix.craftUtils.language.LanguageManager;
import dev.craftefix.craftUtils.gui.LanguageGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftUtilsCommand {

    private LanguageManager languageManager;
    
    public void setLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @Command({"cu"})
    @CommandPermission("CraftUtils.main")
    public void craftUtils(Player actor) {
        actor.sendMessage(Component.text()
                .append(Component.text(" ~~~ CraftUtils ~~~ ", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("\"The utils plugin that has everything you need.\"", NamedTextColor.BLUE).decorate(TextDecoration.ITALIC))
                .append(Component.text("\n» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Version: ", NamedTextColor.BLUE))
                .append(Component.text(getPlugin().getDescription().getVersion(), NamedTextColor.YELLOW)));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("CraftUtils on Github", NamedTextColor.DARK_BLUE))
                        .clickEvent(ClickEvent.openUrl("https://github.com/Craftefix/CraftUtils")));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("CraftUtils on Modrinth", NamedTextColor.YELLOW))
                        .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/craftutils")));
        actor.sendMessage(Component.text()
                .append(Component.text("» ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .append(Component.text("Our Discord", NamedTextColor.BLUE))
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/GbKQUqp6yG")));

    }
    
    @Command({"cu language"})
    @CommandPermission("CraftUtils.language")
    public void language(Player player, @Optional String lang) {
        if (languageManager == null) {
            player.sendMessage(Component.text("Language system not available!", NamedTextColor.RED));
            return;
        }
        
        if (lang == null) {
            // Open language GUI
            LanguageGUI.openLanguageGUI(player, getPlugin(), languageManager);
        } else {
            // Set language directly
            if (languageManager.isLanguageAvailable(lang)) {
                languageManager.setPlayerLanguage(player, lang);
                player.sendMessage(languageManager.getMessage(player, "general.language-changed"));
            } else {
                player.sendMessage(languageManager.getMessage(player, "general.invalid-language"));
            }
        }
    }
    
    @Command({"cu reload"})
    @CommandPermission("CraftUtils.admin")
    public void reload(CommandSender sender) {
        if (languageManager != null) {
            languageManager.loadLanguages();
        }
        
        // Reload plugin config
        getPlugin().reloadConfig();
        
        Component message = languageManager != null && sender instanceof Player ? 
            languageManager.getMessage((Player) sender, "general.reload-success") :
            Component.text("Configuration reloaded successfully!", NamedTextColor.GREEN);
            
        sender.sendMessage(message);
    }

    private JavaPlugin getPlugin() {
        return JavaPlugin.getProvidingPlugin(CraftUtilsCommand.class);
    }
}
