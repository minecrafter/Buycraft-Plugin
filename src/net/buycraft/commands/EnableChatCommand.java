package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableChatCommand
{
	public static Boolean process(CommandSender commandSender, String[] args)
	{
		if(!Plugin.getInstance().requireStarted(commandSender)) return true;
		
		if(Plugin.getInstance().getChatManager().isDisabled((Player) commandSender))
		{
			commandSender.sendMessage(Chat.header());
			commandSender.sendMessage(Chat.seperator());
			commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("chatEnabled"));
			commandSender.sendMessage(Chat.seperator());
			commandSender.sendMessage(Chat.footer());
			
			Plugin.getInstance().getChatManager().enableChat((Player) commandSender);
		}
		else
		{
			commandSender.sendMessage(Chat.header());
			commandSender.sendMessage(Chat.seperator());
			commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("chatAlreadyEnabled"));
			commandSender.sendMessage(Chat.seperator());
			commandSender.sendMessage(Chat.footer());
		}
		
		return true;
	}
}
