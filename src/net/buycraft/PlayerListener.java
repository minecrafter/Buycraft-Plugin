package net.buycraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class PlayerListener implements Listener 
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Plugin.getInstance().getChatManager().enableChat(event.getPlayer());
	}	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if(Plugin.getInstance().getChatManager().isDisabled(event.getPlayer()))
		{
			event.setCancelled(true);
		}
		else
		{
			for(String playerName: Plugin.getInstance().getChatManager().getDisabledChatList())
			{
				Player player = Plugin.getInstance().getServer().getPlayer(playerName);
				
				event.getRecipients().remove(player);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		if(event.getPlayer().getName().equalsIgnoreCase("Buycraft"))
		{
			event.disallow(Result.KICK_OTHER, "This user has been disabled due to security reasons.");
		}
	}
}
