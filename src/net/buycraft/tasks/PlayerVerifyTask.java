package net.buycraft.tasks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerVerifyTask extends ApiTask {

    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private final ArrayList<JSONObject> queuedChecks = new ArrayList<JSONObject>();

    public synchronized void verifyPlayerCode(Player player, String code) {
        try {
            queuedChecks.add(serializePlayerCode(player, code));

            // Delay the task for 2 seconds to allow for more verifications to occur at once
            if (scheduled.compareAndSet(false, true)) {
                Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                    public void run() {
                        Plugin.getInstance().addTask(PlayerVerifyTask.this);
                    }
                }, 40L);
            }
        } catch (Exception e) {
            Plugin.getInstance().getLogger().log(Level.SEVERE, "Failed to serialize player code object", e);
        }
    }

    public void run() {
        try {
            if (!Plugin.getInstance().isAuthenticated(null)) {
                return;
            }

            // Fetch an array of player codes to verify
            JSONObject[] playerCodes = clearQueue();
            // Allow another task to be queued
            PlayerVerifyTask.this.scheduled.set(false);

            // Make the call to the api
            JSONObject result = Plugin.getInstance().getApi().playerVerifyAction(new JSONArray(playerCodes));

            if (result == null || result.getInt("code") != 0) {
                String apiFailureMessage = ChatColor.RED + Plugin.getInstance().getLanguage().getString("playerVerifyApiFailure");

                // Tell each player that the verification of their code failed
                for (JSONObject obj : playerCodes) {
                    Player player = Bukkit.getPlayerExact(obj.getString("player"));
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
                boolean verified = playerVerifyResult.getBoolean("verified");

                String resultMessage = (verified ? ChatColor.GREEN : ChatColor.RED) + Plugin.getInstance().getLanguage().getString(verified ? "playerVerifySuccess" : "playerVerifyFailure");
    
                if (player != null) {
                    player.sendMessage(Chat.header());
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.seperator() + resultMessage);
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.footer());
                }
            }
        } catch (Exception e) {
            Plugin.getInstance().getLogger().log(Level.SEVERE, "Error occured when attempting to verify player codes", e);
            ReportTask.setLastException(e);
        }
    }

    private JSONObject serializePlayerCode(Player player, String code) throws JSONException {
            String playerName = player.getName();
            String address = player.getAddress().getAddress().getHostAddress();

            JSONObject obj = new JSONObject();
            obj.put("player", playerName);
            obj.put("address", address);
            obj.put("code", code);
            return obj;
    }

    /** Clears the queue and returns the old contents */
    private synchronized JSONObject[] clearQueue() {
        JSONObject[] currentQueue = queuedChecks.toArray(new JSONObject[queuedChecks.size()]);

        queuedChecks.clear();
        return currentQueue;
    }
}
