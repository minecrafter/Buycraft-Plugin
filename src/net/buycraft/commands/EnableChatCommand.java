package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableChatCommand extends AbstractCommand {

    public EnableChatCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Plugin plugin = Plugin.getInstance();
        if(commandSender instanceof Player)
        {
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
        }
        else
        {
            commandSender.sendMessage("You cannot execute this command from the console.");
        }

        return true;
    }
}
