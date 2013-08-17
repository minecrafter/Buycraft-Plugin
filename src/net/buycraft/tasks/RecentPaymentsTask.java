package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecentPaymentsTask extends ApiTask {
    
    private CommandSender receiver;
    private String playerLookup;
    
    public static void call(CommandSender receiver, String player) {
        Plugin.getInstance().addTask(new RecentPaymentsTask(receiver, player));
    }

    private RecentPaymentsTask(CommandSender receiver, String playerLookup) {
        this.receiver = receiver;
        this.playerLookup = playerLookup;
    }

    public void run() {
        try {
            
            JSONObject apiResponse = getApi().paymentsAction(10, playerLookup.length() > 0, playerLookup);
            
            if (apiResponse != null) {
                
                JSONArray entries = apiResponse.getJSONArray("payload");
                
                if(entries != null && entries.length() > 0) {
                    receiver.sendMessage(Chat.header());
                    receiver.sendMessage(Chat.seperator());
                    
                    if(playerLookup.isEmpty())
                    {
                        receiver.sendMessage(Chat.seperator() + "Displaying recent payments over all users: ");
                    }
                    else
                    {
                        receiver.sendMessage(Chat.seperator() + "Displaying recent payments from the user " + playerLookup + ":");
                    }

                    receiver.sendMessage(Chat.seperator());
                    
                    for(int i=0; i<entries.length(); i++) {
                        
                        JSONObject entry = entries.getJSONObject(i);
                        
                        receiver.sendMessage(Chat.seperator() + "[" + entry.getString("humanTime") + "] " + ChatColor.LIGHT_PURPLE + entry.getString("ign") + ChatColor.GREEN + " (" + entry.getString("price") + " " + entry.getString("currency") + ")");
                    }
                    
                    receiver.sendMessage(Chat.seperator());
                    receiver.sendMessage(Chat.footer());
                }
                else
                {
                    receiver.sendMessage(Chat.header());
                    receiver.sendMessage(Chat.seperator());
                    receiver.sendMessage(Chat.seperator() + ChatColor.RED + "No recent payments to display.");
                    receiver.sendMessage(Chat.seperator());
                    receiver.sendMessage(Chat.footer());
                }
            } 
        } catch (JSONException e) {
            getLogger().severe("JSON parsing error.");
            ReportTask.setLastException(e);
        }
    }
}