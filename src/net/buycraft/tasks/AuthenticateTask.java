package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

import org.bukkit.Bukkit;
import org.json.JSONObject;

public class AuthenticateTask extends ApiTask {
    private Plugin plugin;

    public static void call() {
        Plugin.getInstance().addTask(new AuthenticateTask());
    }

    private AuthenticateTask() {
        this.plugin = Plugin.getInstance();
    }

    public void run() {
        try {
            final JSONObject apiResponse = plugin.getApi().authenticateAction();
            plugin.setAuthenticated(false);
            // call sync
            if (apiResponse != null) {
                Runnable r = new Runnable() {
                    public void run() {
                        try {
                            plugin.setAuthenticatedCode(apiResponse.getInt("code"));

                            if (apiResponse.getInt("code") == 0) {
                                JSONObject payload = apiResponse.getJSONObject("payload");

                                plugin.setServerID(payload.getInt("serverId"));
                                plugin.setServerCurrency(payload.getString("serverCurrency"));
                                plugin.setServerStore(payload.getString("serverStore"));
                                plugin.setPendingPlayerCheckerInterval(payload.getInt("updateUsernameInterval"));
                                plugin.setAuthenticated(true);

                                if (payload.has("buyCommand")) {
                                    plugin.setBuyCommand(payload.getString("buyCommand"));
                                }

                                plugin.getLogger().info("Authenticated with the specified Secret key.");
                                
                                boolean requiresOnlineMode = payload.getBoolean("onlineMode");
                                plugin.setOnlineMode(requiresOnlineMode);

                                if(!plugin.getSettings().getBoolean("isBungeecord")){
                                    if(requiresOnlineMode && !plugin.getSettings().isOnlineMode()){
                                        plugin.getLogger().warning("This server is in offline mode, you will need to create an offline webstore to work with this server. " +
                                                "If this is a BungeeCord server, you can find a tutorial here: https://www.buycraft.net/community/article/39/using-buycraft-with-bungeecord-lilypad");
                                        return;
                                    }

                                    if(!requiresOnlineMode && plugin.getSettings().isOnlineMode()){
                                        plugin.getLogger().warning("This server is in online mode, you will need to create an online webstore to work with this server.");
                                        return;
                                    }
                                }

                                plugin.getLogger().info("Plugin is now ready to be used.");

                                ReloadPackagesTask.call();
                            } else if (apiResponse.getInt("code") == 101) {
                                plugin.getLogger().severe("The specified Secret key could not be found.");
                                plugin.getLogger().severe("Type /buycraft for further advice & help.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ReportTask.setLastException(e);
                        }
                    }
                };
                sync(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
    }
}
