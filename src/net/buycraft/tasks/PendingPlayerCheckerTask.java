package net.buycraft.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.UuidUtil;

/**
 * Fetches an array of players which are waiting for commands to be run.
 * 
 * If any players in the list are online the command fetch task is called
 * If a player which is in the pending players set joins the server the command fetch task is called
 *
 */
public class PendingPlayerCheckerTask extends ApiTask implements Listener {

    private final Plugin plugin;
    private final AtomicBoolean running = new AtomicBoolean(false);
    /** Stores players with pending commands in lower case */
    private HashSet<String> pendingPlayers = new HashSet<String>();
    private boolean manualExecution;
    private long lastPlayerLogin;

    public PendingPlayerCheckerTask() {
        plugin = Plugin.getInstance();
        lastPlayerLogin = System.currentTimeMillis();
    }

    public void call(boolean manualExecution) {
        if (running.compareAndSet(false, true)) {
            this.manualExecution = manualExecution;
            addTask(this);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public synchronized void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        // If the player has pending commands we call the package checker
        String playerKey = Plugin.getSettings().isOnlineMode() ? UuidUtil.uuidToString(p.getUniqueId()) : p.getName().toLowerCase();
        if (pendingPlayers.remove(playerKey)) {
            CommandFetchTask.call(false, event.getPlayer());
        }
        lastPlayerLogin = System.currentTimeMillis();
    }

    public void run() {
        try {
            // Don't attempt to run if we are not authenticated
            if (!plugin.isAuthenticated(null)) {
                return;
            }

            // If the command checker is disabled and this was not a manual execution we do nothing
            if (!plugin.getSettings().getBoolean("commandChecker") && !manualExecution) {
                return;
            }

            // Fetch online player list
            Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();

            // If nobody has logged in for over 3 hours do not execute the package checker (Manual execution is an exception)
            if (!manualExecution && lastPlayerLogin < (System.currentTimeMillis() - 1080000)) {
                return;
            } else if (onlinePlayers.length > 0) {
                lastPlayerLogin = System.currentTimeMillis();
            }

            // Fetch pending players
            JSONObject apiResponse = plugin.getApi().fetchPendingPlayers(Plugin.getSettings().isOnlineMode());

            if (apiResponse == null || apiResponse.getInt("code") != 0) {
                plugin.getLogger().severe("No response/invalid key during pending players check.");
                return;
            }

            JSONObject apiPayload = apiResponse.getJSONObject("payload");

            JSONArray pendingPlayers = apiPayload.getJSONArray("pendingPlayers");
            boolean offlineCommands = apiPayload.getBoolean("offlineCommands");

            // Clear current pending players (Just in case some don't have pending commands anymore)
            resetPendingPlayers();

            ArrayList<Player> onlinePendingPlayers = null;
            // No point in this if there are no pending players
            if (pendingPlayers.length() > 0) {
                onlinePendingPlayers = new ArrayList<Player>();

                // Iterate through each pending player
                for (int i = 0; i < pendingPlayers.length(); ++i) {
                    String playerKey = pendingPlayers.getString(i);
                    if (!Plugin.getSettings().isOnlineMode()) {
                        // Player names should be in lowercase
                        playerKey = playerKey.toLowerCase();
                    }
                    Player player = null;
                    if (Plugin.getSettings().isOnlineMode()) {
                        UUID uuid = UUID.fromString(UuidUtil.addDashesToUUID(playerKey));
                        player = getPlayer(onlinePlayers, uuid);
                    } else {
                        player = getPlayer(onlinePlayers, playerKey);
                    }

                    // Check if the player is offline
                    if (player == null) {
                        // Add them to the pending players set
                        addPendingPlayer(playerKey);
                    } else {
                        // Add the player to this online pending players list
                        onlinePendingPlayers.add(player);
                    }
                }
            }

            // Check if we need to run the command checker
            if (offlineCommands || (onlinePendingPlayers != null && !onlinePendingPlayers.isEmpty())) {
                // Create the array of players which will need commands to be fetched now
                Player[] players = onlinePendingPlayers != null ? onlinePendingPlayers.toArray(new Player[onlinePendingPlayers.size()]) : new Player[] {};

                // Call the command fetch task
                CommandFetchTask.call(offlineCommands, players);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        } finally {
            running.set(false);
        }
    }

    private synchronized void resetPendingPlayers() {
        pendingPlayers.clear();
    }

    private synchronized void addPendingPlayer(String playerKey) {
        pendingPlayers.add(playerKey);
    }

    private Player getPlayer(Player[] players, String name) {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }
    
    private Player getPlayer(Player[] players, UUID uuid) {
        for (Player player : players) {
            if (player.getUniqueId().equals(uuid)) {
                return player;
            }
        }
        return null;
    }
}
