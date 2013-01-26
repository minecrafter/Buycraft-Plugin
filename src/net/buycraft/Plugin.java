package net.buycraft;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;

import net.buycraft.api.Api;
import net.buycraft.commands.BuyCommand;
import net.buycraft.commands.BuycraftCommand;
import net.buycraft.commands.EnableChatCommand;
import net.buycraft.packages.PackageManager;
import net.buycraft.tasks.AuthenticateTask;
import net.buycraft.tasks.PackageCheckerTask;
import net.buycraft.util.Chat;
import net.buycraft.util.Language;
import net.buycraft.util.Settings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{
	private static Plugin instance;
	
	private String version;
	
	private Settings settings;
	private Language language;
	
	private Api api;
	
	private Integer serverID;
	private String serverCurrency;
	private String serverStore;
	
	private PackageManager packageManager;
	private ChatManager chatManager;

	private Boolean authenticated = false;
	private Integer authenticatedCode = 1;
	
	private String folderPath;
	
	public Plugin()
	{
		instance = this;
	}
	
	public void onEnable()
	{
		folderPath = getDataFolder().getAbsolutePath();
		
		checkDirectory();
		
		moveFileFromJar("README.md", getFolderPath() + "/README.txt", true);
		
		version = getDescription().getVersion();
		
		settings = new Settings();
		language = new Language();
		
		api = new Api();
		
		packageManager = new PackageManager();
		chatManager = new ChatManager();

		getServer().getScheduler().runTaskAsynchronously(this, new AuthenticateTask());
		getServer().getScheduler().runTaskTimerAsynchronously(this, new PackageCheckerTask(false), 6000L, 6000L);
		
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	public void onDisable()
	{
		getLogger().info("Plugin has been disabled.");
	}
	
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
	{	
		HashMap<String, Integer> commandList = new HashMap<String, Integer>();
		
		commandList.put("buy", 0);
		commandList.put("donate", 1);
		commandList.put("purchase", 2);
		commandList.put("store", 3);
		commandList.put("shop", 4);
		
		commandList.put("ec", 5);
		commandList.put("buycraft", 6);
		
		Boolean status = false;
		
		switch(commandList.get(command.getLabel().toLowerCase()))
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				status = new BuyCommand().process(commandSender, args);
			break;
			
			case 5:
				status = new EnableChatCommand().process(commandSender, args);
			break;
			
			case 6:
				status = new BuycraftCommand().process(commandSender, args);
			break;
		}
		
		return status;
	}
	
	private void checkDirectory()
	{
		File directory = new File(getFolderPath());
		
		if(!directory.exists())
		{
			directory.mkdir();
		}
	}
	
	public void moveFileFromJar(String jarFileName, String targetLocation, Boolean overwrite)
	{
		try
		{
		    File targetFile = new File(targetLocation);
	
		    if(overwrite || targetFile.exists() == false || targetFile.length() == 0)
		    {
			    InputStream inFile = getClass().getClassLoader().getResourceAsStream(jarFileName);
			    FileWriter outFile = new FileWriter(targetFile);

			    int c;
	
			    while ((c = inFile.read()) != -1)
			    {
			    	outFile.write(c);
			    }
			    
			    inFile.close();
				outFile.close();
		    }
		}
		catch(NullPointerException e)
		{
			getLogger().info("Failed to create " + jarFileName + ".");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Boolean isAuthenticated(CommandSender commandSender)
	{
		if(!authenticated)
		{
			if(commandSender != null)
			{
				commandSender.sendMessage(Chat.header());
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "Buycraft has failed to startup.");
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "This is normally to do with an invalid Secret key,");
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "please enter the Secret key into the settings.conf");
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "file, and reload your server.");
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "If it did not resolve the issue, restart your server");
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "a couple of times.");
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "If the previous advice failed, please contact");
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "customer support via support@buycraft.net and");
				commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "reference the code " + authenticatedCode + ".");
				commandSender.sendMessage(Chat.seperator());
				commandSender.sendMessage(Chat.footer());
			}
			
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void setAuthenticated(Boolean value)
	{
		authenticated = value;
	}
	
	public void setAuthenticatedCode(Integer value)
	{
		authenticatedCode = value;
	}
	
	public Integer getAuthenticatedCode()
	{
		return authenticatedCode;
	}
	
	public static Plugin getInstance()
	{
		return instance;
	}
	
	public Api getApi()
	{
		return api;
	}
	
	public void setServerID(Integer value)
	{
		serverID = value;
	}
	
	public void setServerCurrency(String value)
	{
		serverCurrency = value;
	}
	
	public void setServerStore(String value)
	{
		serverStore = value;
	}
	
	public Integer getServerID()
	{
		return serverID;
	}
	
	public String getServerStore()
	{
		return serverStore;
	}
	
	public PackageManager getPackageManager()
	{
		return packageManager;
	}
	
	public String getServerCurrency()
	{
		return serverCurrency;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public Language getLanguage()
	{
		return language;
	}
	
	public File getJarFile()
	{
		return getFile();
	}
	
	public ChatManager getChatManager()
	{
		return chatManager;
	}
	
	public String getFolderPath()
	{
		return folderPath;
	}
}
