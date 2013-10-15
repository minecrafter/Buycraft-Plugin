package net.buycraft.tasks;

import java.util.logging.Level;

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
        plugin.getPackageManager().reset();

        try {
        	JSONObject categoriesResponse = plugin.getApi().categoriesAction();
            JSONObject packagesResponse = plugin.getApi().packagesAction();

            if (categoriesResponse == null || categoriesResponse.getInt("code") != 0 || packagesResponse == null || packagesResponse.getInt("code") != 0) {
            	plugin.getLogger().severe("No response/invalid key during package reload.");
            	return;
            }
            
            JSONArray categories = categoriesResponse.getJSONArray("payload");
            JSONArray packages = packagesResponse.getJSONArray("payload");

            for (int i = 0; i < categories.length(); ++i) {
                JSONObject row = categories.getJSONObject(i);
                plugin.getPackageManager().addCategory(row.isNull("id") ? 0 : row.getInt("id"), row.getString("name"), row.getString("shortDescription"), row.getInt("guiItemId"));
            }

            for (int i = 0; i < packages.length(); i++) {
                if (packages.isNull(i)) {
                    continue;
                }

                JSONObject row = packages.getJSONObject(i);
                int categoryId = row.isNull("category") ? 0 : row.getInt("category");
                plugin.getPackageManager().add(categoryId, row.getInt("id"), row.getInt("guiItemId"), row.get("name").toString(), row.getString("shortDescription"), row.get("price").toString());
            }

            plugin.getPackageManager().cleanCategories();
            plugin.getBuyUi().packagesReset();
            plugin.getLogger().info("Loaded " + packages.length() + " package(s) into the cache.");

        } catch (JSONException e) {
            plugin.getLogger().severe("Failed to load packages due to JSON parse error.");
            ReportTask.setLastException(e);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error while resetting packages", e);
            ReportTask.setLastException(e);
        }
    }
}
