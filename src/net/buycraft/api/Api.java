package net.buycraft.api;

import net.buycraft.Plugin;
import net.buycraft.tasks.ReportTask;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Api {
    private Plugin plugin;

    private String apiUrl;
    private String apiKey;

    public Api() {
        this.plugin = Plugin.getInstance();
        this.apiKey = plugin.getSettings().getString("secret");

        if (plugin.getSettings().getBoolean("https")) {
            this.apiUrl = "https://api.buycraft.net/v3";
        } else {
            this.apiUrl = "http://api.buycraft.net/v3";
        }
    }

    public JSONObject playerVerifyAction(JSONArray playerCodes) {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "verify");
        apiCallParams.put("verify", playerCodes.toString());

        return call(apiCallParams);
    }

    public JSONObject playerCheckPackagesAction(JSONArray players) {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "getExpiring");
        apiCallParams.put("players", players.toString());

        return call(apiCallParams);
    }

    public JSONObject authenticateAction() {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "info");

        return call(apiCallParams);
    }

    public JSONObject packagesAction() {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "packages");

        return call(apiCallParams);
    }
    
    public JSONObject paymentsAction(int limit, boolean usernameSpecific, String username) {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "payments");
        apiCallParams.put("limit", String.valueOf(limit));
        
        if(usernameSpecific) {
        	apiCallParams.put("ign", username);
        }

        return call(apiCallParams);
    }

    public JSONObject commandsGetAction() {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "commands");
        apiCallParams.put("do", "lookup");

        return call(apiCallParams);
    }

    public void commandsDeleteAction(String commandsToDelete) {
        HashMap<String, String> apiCallParams = new HashMap<String, String>();

        apiCallParams.put("action", "commands");
        apiCallParams.put("do", "removeId");

        apiCallParams.put("commands", commandsToDelete);

        call(apiCallParams);
    }

    private JSONObject call(HashMap<String, String> apiCallParams) {
        if (apiKey.length() == 0) {
            apiKey = "unspecified";
        }

        apiCallParams.put("secret", apiKey);
        apiCallParams.put("version", plugin.getVersion());
        apiCallParams.put("players_count", String.valueOf(Bukkit.getOnlinePlayers().length));
        apiCallParams.put("players_max", String.valueOf(Bukkit.getMaxPlayers()));
        apiCallParams.put("server_port", String.valueOf(Bukkit.getPort()));

        String url = apiUrl + generateUrlQueryString(apiCallParams);

        if (url != null) {
            String HTTPResponse = HttpRequest(url);

            try {
                if (HTTPResponse != null) {
                    return new JSONObject(HTTPResponse);
                } else {
                    return null;
                }
            } catch (JSONException e) {
                plugin.getLogger().severe("JSON parsing error.");
                ReportTask.setLastException(e);
            }
        }

        return null;
    }

    public static String HttpRequest(String url) {
        try {
            String content = "";

            URL conn = new URL(url);
            URLConnection yc = conn.openConnection();

            yc.setConnectTimeout(10000);
            yc.setReadTimeout(10000);

            BufferedReader in;

            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content = content + inputLine;
            }

            in.close();

            return content;
        } catch (ConnectException e) {
            Plugin.getInstance().getLogger().severe("HTTP request failed due to connection error.");
            ReportTask.setLastException(e);
        } catch (SocketTimeoutException e) {
            Plugin.getInstance().getLogger().severe("HTTP request failed due to timeout error.");
            ReportTask.setLastException(e);
        } catch (FileNotFoundException e) {
            Plugin.getInstance().getLogger().severe("HTTP request failed due to file not found.");
            ReportTask.setLastException(e);
        } catch (UnknownHostException e) {
            Plugin.getInstance().getLogger().severe("HTTP request failed due to unknown host.");
            ReportTask.setLastException(e);
        } catch (Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }

        return null;
    }

    private static String generateUrlQueryString(HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();

        sb.append("?");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 1) {
                sb.append("&");
            }

            sb.append(String.format("%s=%s",
                    entry.getKey().toString(),
                    entry.getValue().toString()
            ));
        }

        return sb.toString();
    }

    public void setApiKey(String value) {
        apiKey = value;
    }
}
