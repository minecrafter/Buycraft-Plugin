package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableChatCommand {
    private Plugin plugin;

    public EnableChatCommand() {
        this.plugin = Plugin.getInstance();
    }

    public Boolean process(CommandSender commandSender, String[] args) {
        if (plugin.isAuthenticated(commandSender)) {
            if (plugin.getChatManager().isDisabled((Player) commandSender)) {
                commandSender.sendMessage(Chat.header());
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("chatEnabled"));
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.footer());
                plugin.getChatManager().enableChat((Player) commandSender);
            } else {
                commandSender.sendMessage(Chat.header());
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("chatAlreadyEnabled"));
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.footer());
            }
        }

        return true;
    }
}
