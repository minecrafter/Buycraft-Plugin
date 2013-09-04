package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;


public class PackageCheckerTask extends ApiTask {
    private Plugin plugin;

    private Boolean manualExecution;

    private static long lastExecution;

    public static long getLastExecution() {
        return lastExecution;
    }

    public static void call(Boolean manualExecution) {
        Plugin.getInstance().addTask(new PackageCheckerTask(manualExecution));
    }

    private PackageCheckerTask(Boolean manualExecution) {
        this.plugin = Plugin.getInstance();
        this.manualExecution = manualExecution;
    }

    public void run() {
        try {
            lastExecution = System.currentTimeMillis();
            if (plugin.isAuthenticated(null)) {
                if (plugin.getSettings().getBoolean("commandChecker") || manualExecution) {
                    // Fetch online player list
                    Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
                    if (onlinePlayers.length > 0 || manualExecution) {
                        
                        JSONObject apiResponse = plugin.getApi().commandsGetAction();
                        
                            if (apiResponse != null && apiResponse.getInt("code") == 0) {
                                JSONObject apiPayload = apiResponse.getJSONObject("payload");
                                JSONArray commandsPayload = apiPayload.getJSONArray("commands");
                                int executedCount = 0;
                                
                                for (int i = 0; i < commandsPayload.length(); i++) {
                                    JSONObject row = commandsPayload.getJSONObject(i);

                                    int commandId = row.getInt("id");
                                    String username = row.getString("ign");
                                    boolean requireOnline = row.getBoolean("requireOnline");
                                    String command = row.getJSONArray("commands").getString(0);
                                    int delay = row.getInt("delay");
                                    int requiredInventorySlots = row.getInt("requireInventorySlot");
                                    
                                    if (requireOnline == false || getPlayer(onlinePlayers, username) != null) {
                                    	if(executedCount < plugin.getSettings().getInt("commandThrottleCount")) {
                                    		String c = command;
                                        	String u = username;
                                        
                                        	Plugin.getInstance().getCommandExecutor().queueCommand(commandId, c, u, delay, requiredInventorySlots);
                                        
                                        	executedCount++;
                                    	}
                                    	else
                                    	{
                                    		plugin.getLogger().info("Skipping " + (commandsPayload.length() - executedCount) + " command(s) to prevent server load. To stop this from happening please change 'commandThrottleCount' to a higher value in settings.conf.");
                                    		
                                    		break;
                                    	}
                                    }
                                }
                                
                                // If the plugin is disabled here our commands won't get executed so we return
                                if (!Plugin.getInstance().isEnabled()) {
                                    return;
                                }
                                
                                Plugin.getInstance().getCommandExecutor().scheduleExecutor();

                                plugin.getLogger().info("Package checker successfully executed.");
                            } else {
                                plugin.getLogger().severe("No response/invalid key during package check.");
                            }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
    }

    private Player getPlayer(Player[] players, String name) {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }
}
