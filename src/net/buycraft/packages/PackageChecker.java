package net.buycraft.packages;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.buycraft.Plugin;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PackageChecker extends Thread
{
	public void run()
	{
		if(Plugin.getInstance().getServer().getOnlinePlayers().length > 0)
		{
			process();
		}
	}
	
	private void executeCommand(String command, String username)
	{
		command = command.replace("{name}", username);
		command = command.replace("(name)", username);
		command = command.replace("{player}", username);
		command = command.replace("(player)", username);
		command = command.replace("{username}", username);
		command = command.replace("(username)", username);
		command = command.replace("<name>", username);
		command = command.replace("<name>", username);
		command = command.replace("<player>", username);
		command = command.replace("<player>", username);
		command = command.replace("<username>", username);
		command = command.replace("<username>", username);
		command = command.replace("[name]", username);
		command = command.replace("[name]", username);
		command = command.replace("[player]", username);
		command = command.replace("[player]", username);
		command = command.replace("[username]", username);
		command = command.replace("[username]", username);
		
		if(command.startsWith("{mcmyadmin}"))
		{
			String newCommand = command.replace("{mcmyadmin}", "");
			
			Logger.getLogger("McMyAdmin").info("Buycraft tried command: " + newCommand);
		}
		else
		{
			Plugin.getInstance().getServer().dispatchCommand(Plugin.getInstance().getServer().getConsoleSender(), command);
		}
	}
	
	/**
	 * Performs the actual checker, we put it in its own method to allow external access
	 */
	public void process()
	{
		try
		{
			JSONObject apiResponse = Plugin.getInstance().getApi().commandsGetAction();
            
			if(apiResponse != null)
			{
				JSONObject apiPayload = apiResponse.getJSONObject("payload");
				JSONArray commandsPayload = apiPayload.getJSONArray("commands");
				
				ArrayList<String> executedCommands = new ArrayList<String>();
				    
			    if(commandsPayload.length() > 0)
				{
					for (int i = 0; i < commandsPayload.length(); i++) 
					{
						JSONObject row = commandsPayload.getJSONObject(i);
						
						String username = row.getString("ign");
						Boolean requireOnline = row.getBoolean("requireOnline");
						JSONArray commands = row.getJSONArray("commands");
						
						Player currentPlayer = Plugin.getInstance().getServer().getPlayer(username);

						if(currentPlayer != null || requireOnline == false)
						{
							Plugin.getInstance().getLogger().info("Executing command(s) on behalf of user '" + username + "'.");
							
							for (int c = 0; c < commands.length(); ++c) 
							{
							    executeCommand(commands.getString(c), username);
							}
							
							if(executedCommands.contains(username) == false)
							{
								executedCommands.add(username);
							}
							
							if(currentPlayer != null)
							{
								currentPlayer.sendMessage(Chat.header());
								currentPlayer.sendMessage(Chat.seperator());
								currentPlayer.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("commandsExecuted"));
								currentPlayer.sendMessage(Chat.seperator());
								currentPlayer.sendMessage(Chat.footer());
							}
						}
					}
				}
			    
			    if(executedCommands.size() > 0)
			    {
			    	Plugin.getInstance().getApi().commandsDeleteAction(new JSONArray(executedCommands.toArray()).toString());
			    }
			}
		}
		catch(JSONException e)
		{
			Plugin.getInstance().getLogger().severe("JSON Parsing error.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
