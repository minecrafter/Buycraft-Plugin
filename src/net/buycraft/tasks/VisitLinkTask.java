package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class VisitLinkTask extends ApiTask {
    private String player;
    private String URL;

    public static void call(Player player, String URL) {
        Plugin.getInstance().addTask(new VisitLinkTask(player.getName(), URL));
    }

    private VisitLinkTask(String player, String URL) {
        try {
            this.player = player;
            this.URL = "http://is.gd/create.php?format=json&url=" + URLEncoder.encode(URL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String httpResponse = getApi().HttpRequest(URL);

            if (httpResponse != null) {
                JSONObject jsonResponse = new JSONObject(httpResponse);

                if (jsonResponse.has("shorturl")) {
                    sendMessage(player, Chat.header());
                    sendMessage(player, Chat.seperator());
                    sendMessage(player, Chat.seperator() + ChatColor.GREEN + getLanguage().getString("pleaseVisit") + ":");
                    sendMessage(player, Chat.seperator());
                    sendMessage(player, Chat.seperator() + jsonResponse.getString("shorturl"));
                    sendMessage(player, Chat.seperator());
                    sendMessage(player, Chat.seperator() + ChatColor.RED + getLanguage().getString("turnChatBackOn"));
                    sendMessage(player, Chat.seperator());
                    sendMessage(player, Chat.footer());

                    disableChat(player);

                    getLogger().info("Generated short URL " + jsonResponse.getString("shorturl") + ".");

                    return;
                } else {
                    getLogger().severe(jsonResponse.getString("errormessage"));
                }
            } else {
                getLogger().severe("HTTP request error during url shortening.");
            }
        } catch (JSONException e) {
            getLogger().severe("JSON parsing error.");
        }

        sendMessage(player, Chat.header());
        sendMessage(player, Chat.seperator());
        sendMessage(player, Chat.seperator() + ChatColor.RED + getLanguage().getString("urlError"));
        sendMessage(player, Chat.seperator());
        sendMessage(player, Chat.footer());
    }
}