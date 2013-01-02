package net.buycraft.commands;

import java.util.List;

import net.buycraft.Plugin;
import net.buycraft.packages.PackageModel;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand
{
	public static Boolean process(CommandSender commandSender, String[] args)
	{
		if(commandSender instanceof Player == false)
		{
			Plugin.getInstance().getLogger().info("You cannot execute this command from inside the console.");
			
			return true;
		}
		
		if(Plugin.getInstance().getSettings().getBoolean("disableBuyCommand"))
		{
			commandSender.sendMessage(ChatColor.RED + "Command has been disabled, please use our webstore instead.");
			
			return true;
		}
		
		if(!Plugin.getInstance().requireStarted(commandSender)) return true;
		
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
					Integer packageID = Integer.valueOf(args[0]);
					
					boolean packageExists = false;
					PackageModel packageModel = null;
					
					for(PackageModel row : Plugin.getInstance().getPackageManager().getPackagesForSale())
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
						String shortUrlLink = "http://is.gd/create.php?format=simple&url=" + Plugin.getInstance().getServerStore() + "/checkout/packages?action=add%26package=" + packageModel.getId() + "%26ign=" + commandSender.getName();
						
						if(Plugin.getInstance().getSettings().getBoolean("directPay"))
						{
							String directGateway = Plugin.getInstance().getSettings().getString("directPayGateway");
							
							shortUrlLink = "http://is.gd/create.php?format=simple&url=" + Plugin.getInstance().getServerStore() + "/checkout/pay?direct=true%26package=" + packageModel.getId() + "%26agreement=true%26gateway=" + directGateway + "%26ign=" + commandSender.getName();
						}

						String shortenedUrl =  Plugin.getInstance().getApi().HttpRequest(shortUrlLink);
						
						if(shortenedUrl != null)
						{
							commandSender.sendMessage(Chat.header());
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("pleaseVisit") + ":");
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.seperator() + shortenedUrl);
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("turnChatBackOn"));
							commandSender.sendMessage(Chat.seperator());
							commandSender.sendMessage(Chat.footer());
							
							Plugin.getInstance().getChatManager().disableChat((Player) commandSender);
						}
						else
						{
							 Plugin.getInstance().getLogger().severe("Could not generate a shortened URL.");
						}
					}
					else
					{
						commandSender.sendMessage(Chat.header());
						commandSender.sendMessage(Chat.seperator());
						commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("packageNotFound"));
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
			
			List<PackageModel> packages = Plugin.getInstance().getPackageManager().getPackagesForSale();
			
			if(packages.size() == 0)
			{
				commandSender.sendMessage(Chat.header());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("noPackagesForSale"));
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
				
				List<PackageModel> packagesToDisplay = packages.subList(startingPoint, finishPoint);
				
				if(packagesToDisplay.size() > 0)
				{
					Plugin.getInstance().getChatManager().disableChat((Player) commandSender);
					
					commandSender.sendMessage(Chat.header());
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("toPurchase") + " " + ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("mainCommand") + " <ID>");
					
					if(pageCount > 1) 
					{
						commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("howToNavigate") + " " + ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("mainCommand") + " page <1-" + pageCount  + ">");
					}
					
					commandSender.sendMessage(Chat.seperator());
					
					for(PackageModel row : packagesToDisplay)
					{
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + row.getOrder());
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageName") + ": " + ChatColor.LIGHT_PURPLE + row.getName());
						commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packagePrice") + ": " + ChatColor.LIGHT_PURPLE + row.getPrice() + ' ' + Plugin.getInstance().getServerCurrency());
						commandSender.sendMessage(Chat.seperator());
					}
					
					commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("turnChatBackOn"));
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.footer());
				}
				else
				{
					commandSender.sendMessage(Chat.header());
					commandSender.sendMessage(Chat.seperator());
					commandSender.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("pageNotFound"));
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
