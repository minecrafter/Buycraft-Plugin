package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PackageCheckerTask extends ApiTask {
    private Plugin plugin;

    private Boolean manualExecution;

    public static void call(Boolean manualExecution) {
        Plugin.getInstance().addTask(new PackageCheckerTask(manualExecution));
    }

    private PackageCheckerTask(Boolean manualExecution) {
        this.plugin = Plugin.getInstance();
        this.manualExecution = manualExecution;
    }

    public void run() {
        try {
            if (plugin.isAuthenticated(null)) {
                if (plugin.getSettings().getBoolean("commandChecker") || manualExecution) {
                    if (plugin.getServer().getOnlinePlayers().length > 0 || manualExecution) {
                        final JSONObject apiResponse = plugin.getApi().commandsGetAction();
                        Runnable r = new Runnable () {
                            public void run() {
                                try {
                                    if (apiResponse != null && apiResponse.getInt("code") == 0) {
                                        JSONObject apiPayload = apiResponse.getJSONObject("payload");
                                        JSONArray commandsPayload = apiPayload.getJSONArray("commands");

                                        ArrayList<String> executedCommands = new ArrayList<String>();

                                        if (commandsPayload.length() > 0) {
                                            for (int i = 0; i < commandsPayload.length(); i++) {
                                                JSONObject row = commandsPayload.getJSONObject(i);

                                                String username = row.getString("ign");
                                                Boolean requireOnline = row.getBoolean("requireOnline");
                                                String command = row.getJSONArray("commands").getString(0);

                                                Player currentPlayer = plugin.getServer().getPlayer(username);

                                                if (currentPlayer != null || requireOnline == false) {
                                                    if (executedCommands.contains(username) == false) {
                                                        executedCommands.add(username);
                                                    }
                                                    final String c = command;
                                                    final String u = username;
                                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                        public void run() {
                                                            CommandExecuteTask.call(c, u);
                                                        }
                                                    }, 60L);
                                                }
                                            }
                                        }

                                        if (executedCommands.size() > 0) {
                                            for (String username : executedCommands) {
                                                Player player = plugin.getServer().getPlayer(username);

                                                if (player != null) {
                                                    player.sendMessage(Chat.header());
                                                    player.sendMessage(Chat.seperator());
                                                    player.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("commandsExecuted"));
                                                    player.sendMessage(Chat.seperator());
                                                    player.sendMessage(Chat.footer());
                                                }
                                            }

                                            CommandDeleteTask.call(new JSONArray(executedCommands.toArray()).toString());
                                            // plugin.getApi().commandsDeleteAction(new JSONArray(executedCommands.toArray()).toString());
                                        }

                                        plugin.getLogger().info("Package checker successfully executed.");
                                    } else {
                                        plugin.getLogger().severe("No response/invalid key during package check.");
                                    }
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        sync(r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
