package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuycraftCommand
{
	public static Boolean process(CommandSender commandSender, String[] args)
	{
		if(args.length > 0)
		{
			if(commandSender instanceof Player == false || commandSender.hasPermission("buycraft.admin") || commandSender.isOp())
			{
				if(args[0].equalsIgnoreCase("secret"))
				{
					if(args.length == 2)
					{
						Plugin.getInstance().getSettings().setString("secret", args[1]);
						
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("secretKeySet"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
						
						Plugin.getInstance().getServer().reload();
						
						return true;
					}
					else
					{
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("enterValidSecret"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
						
						return true;
					}
				}
				
				if(Plugin.getInstance().requireStarted(commandSender))
				{
					if(args[0].equalsIgnoreCase("reload"))
					{
						Plugin.getInstance().getPackageManager().loadPackages();
						
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("packageCacheReloaded"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
				
						return true;
					}
					
					if(args[0].equalsIgnoreCase("forcecheck"))
					{
						Plugin.getInstance().getPackageChecker().process();
						
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("forceCheckPerformed"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
				
						return true;
					}
				}
				else
				{
					return true;
				}
			}
			else
			{
				commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute that command.");
				
				return true;
			}
		}
		else
		{
			if(Plugin.getInstance().requireStarted(commandSender))
			{
				commandSender.sendMessage(Chat.header());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("mainCommand") + ChatColor.GREEN + " " + Plugin.getInstance().getLanguage().getString("viewAvailablePackagesHelp"));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("mainCommand") + " page <ID>:" + ChatColor.GREEN + " " + Plugin.getInstance().getLanguage().getString("navigateThroughPackagesHelp"));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("mainCommand") + " <ID>: " + ChatColor.GREEN + " " + Plugin.getInstance().getLanguage().getString("purchaseSpecificPackageHelp"));
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Server ID: " + ChatColor.GREEN + String.valueOf(Plugin.getInstance().getServerID()));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Plugin version: " + ChatColor.GREEN + String.valueOf(Plugin.getInstance().getVersion()));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Website: " + ChatColor.GREEN + "https://buycraft.net");
				commandSender.sendMessage(Chat.footer());
			}
			
			return true;
		}
		
		return false;
	}
}
