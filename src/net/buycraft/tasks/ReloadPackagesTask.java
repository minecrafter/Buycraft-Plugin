package net.buycraft.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.buycraft.Plugin;

public class ReloadPackagesTask extends Thread
{
	private Plugin plugin;
	
	public ReloadPackagesTask()
	{
		this.plugin = Plugin.getInstance();
	}
	
	@Override
	public void run()
	{
		plugin.getPackageManager().getPackagesForSale().clear();
		
		try
		{
			JSONObject apiResponse = plugin.getApi().packagesAction();
			
			if(apiResponse != null && apiResponse.getInt("code") == 0)
			{
				JSONArray packages = apiResponse.getJSONArray("payload");
				
				for (int i = 0; i < packages.length(); i++) 
				{
					if(packages.isNull(i) == false)
					{
						JSONObject row = packages.getJSONObject(i);
				   			
						plugin.getPackageManager().add(row.getInt("id"), row.get("name").toString(), row.get("price").toString(), row.getInt("order"));
					}
				}
				
				plugin.getLogger().info("Loaded " + packages.length() + " package(s) into the cache.");
			}
			else
			{
				plugin.getLogger().severe("No response/invalid key during package reload.");
			}
		}
		catch(JSONException e)
		{
			plugin.getLogger().severe("Failed to load packages due to JSON parse error.");
		}
	}
}
