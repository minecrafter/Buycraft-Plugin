package net.buycraft.tasks;

import java.util.ArrayList;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;


public class CommandFetchTask extends ApiTask {

    private static long lastExecution;

    public static long getLastExecution() {
        return lastExecution;
    }

    public static void call(boolean offlineCommands, Player ...players) {
        Plugin.getInstance().addTask(new CommandFetchTask(offlineCommands, players));
    }

    private final Plugin plugin;
    private final boolean offlineCommands;
    private final Player[] players;

    private CommandFetchTask(boolean offlineCommands, Player[] players) {
        this.plugin = Plugin.getInstance();
        this.offlineCommands = offlineCommands;
        this.players = players;
    }

    public void run() {
        try {
            lastExecution = System.currentTimeMillis();
            if (!plugin.isAuthenticated(null)) {
                return;
            }

            // Create an array of player names
            String[] playerNames;
            if (players.length > 0){
                ArrayList<String> tmpPlayerNames = new ArrayList<String>(players.length);
                for (Player player : players) {
                    tmpPlayerNames.add(player.getName());
                }
                playerNames = tmpPlayerNames.toArray(new String[tmpPlayerNames.size()]);
            } else {
                playerNames = new String[0];
            }

            JSONObject apiResponse = plugin.getApi().fetchPlayerCommands(new JSONArray(playerNames), offlineCommands);

            if (apiResponse == null || apiResponse.getInt("code") != 0) {
                plugin.getLogger().severe("No response/invalid key during package check.");
                return;
            }

            JSONObject apiPayload = apiResponse.getJSONObject("payload");
            JSONArray commandsPayload = apiPayload.getJSONArray("commands");

            for (int i = 0; i < commandsPayload.length(); i++) {
                JSONObject row = commandsPayload.getJSONObject(i);

                String uuid = null;
                int commandId = row.getInt("id");
                String username = row.getString("ign");
                
                if(!row.isNull("uuid"))
                {
                	uuid = row.getString("uuid");
                }
                
                boolean requireOnline = row.getBoolean("requireOnline");
                String command = row.getString("command");
                int delay = row.getInt("delay");
                int requiredInventorySlots = row.getInt("requireInventorySlot");

                Player player = requireOnline ? getPlayer(players, username) : null;

                if (requireOnline == false || player != null) {
                    String c = command;
                    String u = username;

                    Plugin.getInstance().getCommandExecutor().queueCommand(commandId, c, u, uuid, delay, requiredInventorySlots);
                }
            }

            // If the plugin is disabled here our commands won't get executed so we return
            if (!Plugin.getInstance().isEnabled()) {
                return;
            }

            Plugin.getInstance().getCommandExecutor().scheduleExecutor();

            plugin.getLogger().info("Package checker successfully executed.");
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
