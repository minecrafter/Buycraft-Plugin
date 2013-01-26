package net.buycraft.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

public class VisitLinkTask extends Thread
{
	private Plugin plugin;
	
	private Player player;
	private String URL;
	
	public VisitLinkTask(Player player, String URL)
	{
		this.plugin = Plugin.getInstance();

		try 
		{
			this.player = player;
			this.URL = "http://is.gd/create.php?format=json&url=" + URLEncoder.encode(URL, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		if(player.isOnline())
		{
			try 
			{
				String httpResponse = plugin.getApi().HttpRequest(URL);
			
				if(httpResponse != null)
				{
					JSONObject jsonResponse = new JSONObject(httpResponse);
					
					if(jsonResponse.has("shorturl"))
					{
						player.sendMessage(Chat.header());
						player.sendMessage(Chat.seperator());
						player.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("pleaseVisit") + ":");
						player.sendMessage(Chat.seperator());
						player.sendMessage(Chat.seperator() + jsonResponse.getString("shorturl"));
						player.sendMessage(Chat.seperator());
						player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("turnChatBackOn"));
						player.sendMessage(Chat.seperator());
						player.sendMessage(Chat.footer());
						
						plugin.getChatManager().disableChat(player);
			
						plugin.getLogger().info("Generated short URL " + jsonResponse.getString("shorturl") + ".");
						
						return;
					}
					else
					{
						plugin.getLogger().severe(jsonResponse.getString("errormessage"));
					}
				}
				else
				{
					plugin.getLogger().severe("HTTP request error during url shortening.");
				}	
			} 
			catch (JSONException e) 
			{
				plugin.getLogger().severe("JSON parsing error.");
			}
			
			player.sendMessage(Chat.header());
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("urlError"));
			player.sendMessage(Chat.seperator());
			player.sendMessage(Chat.footer());
		}
	}
}
