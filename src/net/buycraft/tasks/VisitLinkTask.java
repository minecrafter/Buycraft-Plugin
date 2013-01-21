package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VisitLinkTask extends Thread
{
	private Plugin plugin;
	
	private Player player;
	private String URL;
	
	public VisitLinkTask(Player player, String URL)
	{
		this.plugin = Plugin.getInstance();
		
		this.player = player;
		this.URL = "http://is.gd/create.php?format=simple&url=" + URL;
	}
	
	@Override
	public void run()
	{
		String shortenedUrl = plugin.getApi().HttpRequest(URL);
		
		if(shortenedUrl != null && player.isOnline())
		{
			player.sendMessage(Chat.header());
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("pleaseVisit") + ":");
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.seperator() + shortenedUrl);
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("turnChatBackOn"));
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.footer());
			
			plugin.getChatManager().disableChat(player);

			plugin.getLogger().info("Generated short URL " + shortenedUrl + ".");
		}
		else
		{
			 plugin.getLogger().severe("Could not generate a shortened URL.");
		}
	}
}
