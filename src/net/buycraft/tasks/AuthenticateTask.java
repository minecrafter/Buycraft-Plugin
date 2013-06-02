package net.buycraft.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import net.buycraft.Plugin;
import net.buycraft.util.Updater;

public class AuthenticateTask extends Thread
{
	private Plugin plugin;
	
	public AuthenticateTask()
	{
		this.plugin = Plugin.getInstance();
	}
	
	@Override
	public void run()
	{
		try 
		{
			JSONObject apiResponse = plugin.getApi().authenticateAction();
			
			plugin.setAuthenticated(false);
			
			if(apiResponse != null)
			{
				plugin.setAuthenticatedCode(apiResponse.getInt("code"));
				
				if(apiResponse.getInt("code") == 0)
				{
					JSONObject payload = apiResponse.getJSONObject("payload");
					
					plugin.setServerID(payload.getInt("serverId"));
					plugin.setServerCurrency(payload.getString("serverCurrency"));
					plugin.setServerStore(payload.getString("serverStore"));
					plugin.setAuthenticated(true);
					
					if(payload.has("buyCommand"))
					{
						plugin.setBuyCommand(payload.getString("buyCommand"));
					}
					
					if(payload.getDouble("latestVersion") > Double.valueOf(plugin.getVersion()))
					{
						String downloadUrl = payload.getString("latestDownload");

						if(plugin.getSettings().getBoolean("autoUpdate"))
						{
							Updater updater = new Updater();
							updater.download(downloadUrl);
						}
						else
						{
							plugin.getLogger().info("Ignoring update due to auto update disabled.");
						}
					}
					
					plugin.getLogger().info("Authenticated with the specified Secret key.");
					plugin.getLogger().info("Plugin is now ready to be used.");
					
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new ReloadPackagesTask());
				}
				else if(apiResponse.getInt("code") == 101)
				{
					plugin.getLogger().severe("The specified Secret key could not be found.");
					plugin.getLogger().severe("Type /buycraft for further advice & help.");
				}
			}
		} 
		catch(JSONException e)
		{
			plugin.getLogger().severe("JSON parsing error.");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
