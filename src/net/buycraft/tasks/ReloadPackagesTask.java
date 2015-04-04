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
                
                // Get item data
                String guiItemData = row.getString("itemId");
                int guiItemId = this.getGuiItemId(guiItemData);
                short guiItemDamage = this.getGuidItemDamage(guiItemData);
                
                // Create category
                plugin.getPackageManager().addCategory(row.isNull("id") ? 0 : row.getInt("id"), row.getString("name"), row.getString("shortDescription"), guiItemId, guiItemDamage);
            }

            for (int i = 0; i < packages.length(); i++) {
                if (packages.isNull(i)) {
                    continue;
                }

                JSONObject row = packages.getJSONObject(i);
                int categoryId = row.isNull("category") ? 0 : row.getInt("category");
                
                // Get item data
                String guiItemData = row.getString("itemId");
                int guiItemId = this.getGuiItemId(guiItemData);
                short guiItemDamage = this.getGuidItemDamage(guiItemData);
                
                plugin.getPackageManager().add(categoryId, row.getInt("id"), guiItemId, guiItemDamage, row.get("name").toString(), row.getString("shortDescription"), row.get("price").toString());
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
    
    private int getGuiItemId(String guiItemData) {
        // Find colon index
        int splitIndex = guiItemData.indexOf(':');

        return splitIndex == -1 ? Integer.parseInt(guiItemData) : Integer.parseInt(guiItemData.substring(0, splitIndex));
    }
    
    private short getGuidItemDamage(String guiItemData) {
        // Find colon index
        int splitIndex = guiItemData.indexOf(':');
        
        int l = guiItemData.length();
        return splitIndex != -1 && l > splitIndex + 1 ? Short.parseShort(guiItemData.substring(splitIndex + 1, l)) : 0;
    }
}
