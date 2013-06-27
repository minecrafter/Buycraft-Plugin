package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Updater;
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
                                plugin.setAuthenticated(true);

                                if (payload.has("buyCommand")) {
                                    plugin.setBuyCommand(payload.getString("buyCommand"));
                                }

                                if (payload.getDouble("latestVersion") > Double.valueOf(plugin.getVersion())) {
                                    String downloadUrl = payload.getString("latestDownload");

                                    if (plugin.getSettings().getBoolean("autoUpdate")) {
                                        Updater updater = new Updater();
                                        updater.download(downloadUrl);
                                    } else {
                                        plugin.getLogger().info("Ignoring update due to auto update disabled.");
                                    }
                                }

                                plugin.getLogger().info("Authenticated with the specified Secret key.");
                                plugin.getLogger().info("Plugin is now ready to be used.");

                                ReloadPackagesTask.call();
                            } else if (apiResponse.getInt("code") == 101) {
                                plugin.getLogger().severe("The specified Secret key could not be found.");
                                plugin.getLogger().severe("Type /buycraft for further advice & help.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                sync(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
