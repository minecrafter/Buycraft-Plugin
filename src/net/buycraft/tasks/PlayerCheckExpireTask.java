package net.buycraft.tasks;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlayerCheckExpireTask extends ApiTask {

    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private final HashSet<String> queuedChecks = new HashSet<String>();

    public synchronized void checkExpired(Player player) {
        queuedChecks.add(player.getName());

        // Delay the task for 2 seconds to allow for more verifications to occur at once
        if (scheduled.compareAndSet(false, true)) {
            Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                public void run() {
                    Plugin.getInstance().addTask(PlayerCheckExpireTask.this);
                }
            }, 40L);
        }
    }

    public void run() {
        try {
            if (!Plugin.getInstance().isAuthenticated(null)) {
                return;
            }

            // Fetch an array of player to check for expiring packages
            String[] players = clearQueue();
            // Allow another task to be queued
            PlayerCheckExpireTask.this.scheduled.set(false);

            // Make the call to the api
            JSONObject result = Plugin.getInstance().getApi().playerCheckPackagesAction(new JSONArray(players));

            if (result == null || result.getInt("code") != 0) {
                String apiFailureMessage = ChatColor.RED + Plugin.getInstance().getLanguage().getString("playerCheckExpiringApiFailure");

                // Tell each player that the verification of their code failed
                for (String playerName : players) {
                    Player player = Bukkit.getPlayerExact(playerName);
                    if (player != null) {
                        player.sendMessage(Chat.header());
                        player.sendMessage(Chat.seperator());
                        player.sendMessage(Chat.seperator() + apiFailureMessage);
                        player.sendMessage(Chat.seperator());
                        player.sendMessage(Chat.footer());
                    }
                }
                return;
            }

            JSONArray verificationResultArray = result.getJSONArray("payload");

            for (int i = 0; i < verificationResultArray.length(); ++i) {
                JSONObject playerVerifyResult = verificationResultArray.getJSONObject(i);

                Player player = Bukkit.getPlayerExact(playerVerifyResult.getString("player"));
                JSONArray expiringPackages = playerVerifyResult.getJSONArray("expiring");

                if (player != null) {
                    player.sendMessage(Chat.header());
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.seperator() + Plugin.getInstance().getLanguage().getString("playerCheckExpiringHeader"));
                    if (expiringPackages.length() == 0) {
                        player.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("playerCheckExpiringNone"));
                    } else {
                        for (int j = 0; j < expiringPackages.length(); ++j) {
                            player.sendMessage(Chat.seperator() + ChatColor.RED + expiringPackages.getString(i));
                        }
                    }
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.footer());
                }
            }
        } catch (Exception e) {
            Plugin.getInstance().getLogger().log(Level.SEVERE, "Error occured when attempting to verify player codes", e);
            ReportTask.setLastException(e);
        }
    }

    /** Clears the queue and returns the old contents as a JSONArray*/
    private synchronized String[] clearQueue() {
        String[] currentQueue = queuedChecks.toArray(new String[queuedChecks.size()]);

        queuedChecks.clear();
        return currentQueue;
    }
}
