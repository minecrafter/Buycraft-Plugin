package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReloadPackagesTask extends ApiTask {
    Plugin plugin;

    public static void call() {
        Plugin.getInstance().addTask(new ReloadPackagesTask());
    }

    private ReloadPackagesTask() {
        this.plugin = Plugin.getInstance();
    }

    public void run() {
        plugin.getPackageManager().getPackagesForSale().clear();

        try {
            JSONObject apiResponse = plugin.getApi().packagesAction();

            if (apiResponse != null && apiResponse.getInt("code") == 0) {
                JSONArray packages = apiResponse.getJSONArray("payload");

                for (int i = 0; i < packages.length(); i++) {
                    if (packages.isNull(i) == false) {
                        JSONObject row = packages.getJSONObject(i);

                        plugin.getPackageManager().add(row.getInt("id"), row.get("name").toString(), row.get("price").toString(), row.getInt("order"));
                    }
                }

                plugin.getLogger().info("Loaded " + packages.length() + " package(s) into the cache.");
            } else {
                plugin.getLogger().severe("No response/invalid key during package reload.");
            }
        } catch (JSONException e) {
            plugin.getLogger().severe("Failed to load packages due to JSON parse error.");
            ReportTask.setLastException(e);
        }
    }
}
