package net.buycraft.tasks;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class CommandExecuteTask extends Thread
{
	private String command;
	private String username;
	
	public CommandExecuteTask(String command, String username)
	{
		this.command = command;
		this.username = username;
	}
	
	@Override
	public void run()
	{
		try 
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
			
			Bukkit.getLogger().info("Executing command '" + command + "' on behalf of user '" + username + "'.");
			
			if(command.startsWith("{mcmyadmin}"))
			{
				String newCommand = command.replace("{mcmyadmin}", "");
				
				Logger.getLogger("McMyAdmin").info("Buycraft tried command: " + newCommand);
			}
			else
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
