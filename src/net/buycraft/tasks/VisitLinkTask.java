package net.buycraft.tasks;

import com.sun.scenario.Settings;
import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class VisitLinkTask extends ApiTask {
    private String playerName;
    private String URL;
    private Plugin plugin;

    public static void call(Player player, String URL) {
        Plugin.getInstance().addTask(new VisitLinkTask(player.getName(), URL));
    }

    private VisitLinkTask(String playerName, String URL) {
        try {
            this.playerName = playerName;
            this.URL = URLEncoder.encode(URL, "UTF-8");
            this.plugin = Plugin.getInstance();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
    }

    public void run() {
        // Player is online so its fine to use this
        @SuppressWarnings("deprecation")
        Player player = Bukkit.getPlayerExact(playerName);

        Boolean shouldDisableChat = plugin.getSettings().getBoolean("disableChatOnBuyCommand");

        try {
            JSONObject jsonResponse = getApi().urlAction(URL).getJSONObject("payload");
       
            if (jsonResponse != null) {
                if (jsonResponse.has("url") && !jsonResponse.isNull("url")) {

                    if (player != null) {

                        String message = "";

                        message += Chat.header() + "\n";
                        message += Chat.seperator() + "\n";
                        message += Chat.seperator() + ChatColor.GREEN + getLanguage().getString("pleaseVisit") + ":" + "\n";
                        message += Chat.seperator() + "\n";
                        message += Chat.seperator() + "\n";
                        message += Chat.seperator() + jsonResponse.getString("url") + "\n";

                        if(shouldDisableChat) {
                            message += Chat.seperator() + "\n";
                            message += Chat.seperator() + ChatColor.RED + getLanguage().getString("turnChatBackOn").replace("{{ENABLE_CHAT_COMMAND}}", plugin.getSettings().getString("re-enableChatCommand")) + "\n";
                        }
                        
                        message += Chat.seperator() + "\n";
                        message += Chat.footer();

                        player.sendMessage(message);
                    }


                    if(shouldDisableChat){
                        disableChat(playerName);
                    }

                    getLogger().info("Generated short URL " + jsonResponse.getString("url") + ".");

                    return;
                } else {
                    getLogger().severe(jsonResponse.getString("errormessage"));
                }
            } else {
                getLogger().severe("HTTP request error during url shortening.");
            }
        } catch (JSONException e) {
            getLogger().severe("JSON parsing error.");
            ReportTask.setLastException(e);
        }

        if (player != null) {
            player.sendMessage(new String[] {Chat.header(), Chat.seperator(),
                    Chat.seperator() + ChatColor.RED + getLanguage().getString("urlError"),
                    Chat.seperator(), Chat.footer()});
        }
    }
}