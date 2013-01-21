package net.buycraft.commands;

import java.util.List;

import net.buycraft.Plugin;
import net.buycraft.packages.PackageModal;
import net.buycraft.tasks.VisitLinkTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand
{
	private Plugin plugin;
	
	public BuyCommand()
	{
		this.plugin = Plugin.getInstance();
	}
	
	public Boolean process(CommandSender commandSender, String[] args)
	{
		if(commandSender instanceof Player == false)
		{
			plugin.getLogger().info("You cannot execute this command from inside the console.");
			
			return true;
		}
		
		if(plugin.getSettings().getBoolean("disableBuyCommand"))
		{
			commandSender.sendMessage(ChatColor.RED + "Command has been disabled, please use our webstore instead.");
			
			return true;
		}
		
		if(!plugin.requireStarted(commandSender)) return true;
		
		String pageToView = "1";
		
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("page") && args.length == 2)
			{
				pageToView = args[1];
			}
			else
			{
				if(args.length == 1 && isNumber(args[0]))
				{
					int packageID = Integer.valueOf(args[0]);
					
					boolean packageExists = false;
					PackageModal packageModel = null;
					
					for(PackageModal row : plugin.getPackageManager().getPackagesForSale())
					{
						if(row.getOrder() == packageID)
						{
							packageExists = true;
							packageModel = row;
							
							break;
						}
					}
				
					if(packageExists == true)
					{
						String buyNowLink = plugin.getServerStore() + "/checkout/packages?action=add%26package=" + packageModel.getId() + "%26ign=" + commandSender.getName();
						
						if(plugin.getSettings().getBoolean("directPay"))
						{
							String directGateway = plugin.getSettings().getString("directPayGateway");
							
							buyNowLink = plugin.getServerStore() + "/checkout/pay?direct=true%26package=" + packageModel.getId() + "%26agreement=true%26gateway=" + directGateway + "%26ign=" + commandSender.getName();
						}
						
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new VisitLinkTask((Player) commandSender, buyNowLink));
					}
					else
					{
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("packageNotFound"));
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.footer());
					}	
					
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		
		if(isNumber(pageToView) && pageToView.length() < 5)
		{
			Integer pageNumber = Integer.parseInt(pageToView);
			
			List<PackageModal> packages = plugin.getPackageManager().getPackagesForSale();
			
			if(packages.size() == 0)
			{
				commandSender.sendMessage(Chat.header());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("noPackagesForSale"));
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.footer());
			}
			else
			{
				int pageCount = (int) Math.ceil(packages.size() / 3.0);
				
				int startingPoint = -3 + (3 * pageNumber);
				int finishPoint = 0 + (3 * pageNumber);
				
				if(finishPoint > packages.size() || finishPoint < 3) finishPoint = packages.size();
				if(startingPoint > packages.size() || startingPoint < 0) startingPoint = packages.size();
				
				List<PackageModal> packagesToDisplay = packages.subList(startingPoint, finishPoint);
				
				if(packagesToDisplay.size() > 0)
				{
					plugin.getChatManager().disableChat((Player) commandSender);
					
					commandSender.sendMessage(Chat.header());
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("toPurchase") + " " + ChatColor.LIGHT_PURPLE + plugin.getLanguage().getString("mainCommand") + " <ID>");
					
					if(pageCount > 1) 
					{
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("howToNavigate") + " " + ChatColor.LIGHT_PURPLE + plugin.getLanguage().getString("mainCommand") + " page <1-" + pageCount  + ">");
					}
					
					commandSender.sendMessage(Chat.seperator());
					
					for(PackageModal row : packagesToDisplay)
					{
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + row.getOrder());
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packageName") + ": " + ChatColor.LIGHT_PURPLE + row.getName());
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packagePrice") + ": " + ChatColor.LIGHT_PURPLE + row.getPrice() + ' ' + plugin.getServerCurrency());
						commandSender.sendMessage(Chat.seperator());
					}
					
					commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("turnChatBackOn"));
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.footer());
				}
				else
				{
					commandSender.sendMessage(Chat.header());
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("pageNotFound"));
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.footer());
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private static boolean isNumber(String string)
	{
	      char[] c = string.toCharArray();
	      
	      for(int i=0; i < string.length(); i++)
	      {
	          if(!Character.isDigit(c[i]))
	          {
	             return false;
	          }
	     }
	      
	     return true;
	}
}
