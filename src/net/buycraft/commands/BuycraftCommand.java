package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.tasks.PackageCheckerTask;
import net.buycraft.tasks.ReloadPackagesTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuycraftCommand
{
	private Plugin plugin;
	
	public BuycraftCommand()
	{
		this.plugin = Plugin.getInstance();
	}
	
	public Boolean process(CommandSender commandSender, String[] args)
	{
		if(args.length > 0)
		{
			if(commandSender instanceof Player == false || commandSender.hasPermission("buycraft.admin") || commandSender.isOp())
			{
				if(args[0].equalsIgnoreCase("secret"))
				{
					if(args.length == 2)
					{
						plugin.getSettings().setString("secret", args[1]);
						
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("secretKeySet"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
						
						plugin.getServer().reload();
						
						return true;
					}
					else
					{
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("enterValidSecret"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
						
						return true;
					}
				}
				
				if(plugin.requireStarted(commandSender))
				{
					if(args[0].equalsIgnoreCase("reload"))
					{
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new ReloadPackagesTask());
						
						if(commandSender instanceof Player)
						{
							commandSender.sendMessage(Chat.header());
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("packageCacheReloaded"));
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.footer());
						}
				
						return true;
					}
					
					if(args[0].equalsIgnoreCase("forcecheck"))
					{
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new PackageCheckerTask());
						
						if(commandSender instanceof Player)
						{
							commandSender.sendMessage(Chat.header());
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("forceCheckPerformed"));
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.footer());
						}
				
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
			if(plugin.requireStarted(commandSender))
			{
				commandSender.sendMessage(Chat.header());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + plugin.getLanguage().getString("mainCommand") + ChatColor.GREEN + " " + plugin.getLanguage().getString("viewAvailablePackagesHelp"));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + plugin.getLanguage().getString("mainCommand") + " page <ID>:" + ChatColor.GREEN + " " + plugin.getLanguage().getString("navigateThroughPackagesHelp"));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + plugin.getLanguage().getString("mainCommand") + " <ID>: " + ChatColor.GREEN + " " + plugin.getLanguage().getString("purchaseSpecificPackageHelp"));
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Server ID: " + ChatColor.GREEN + String.valueOf(plugin.getServerID()));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Server URL: " + ChatColor.GREEN + String.valueOf(plugin.getServerStore()));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Version: " + ChatColor.GREEN + String.valueOf(plugin.getVersion()));
				commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Website: " + ChatColor.GREEN + "http://buycraft.net");
				commandSender.sendMessage(Chat.footer());
			}
			
			return true;
		}
		
		return false;
	}
}
