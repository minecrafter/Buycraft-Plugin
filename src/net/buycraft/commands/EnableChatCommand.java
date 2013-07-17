package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableChatCommand {

    private EnableChatCommand() {}

    public static boolean process(CommandSender commandSender, String[] args) {
        Plugin plugin = Plugin.getInstance();
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
