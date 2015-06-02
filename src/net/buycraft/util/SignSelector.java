package net.buycraft.util;

import net.buycraft.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by william on 01/06/15.
 */
public class SignSelector {

    private Plugin plugin;

    public Map<Location, Integer> signs = new HashMap<Location, Integer>();

    public SignSelector(){
        this.plugin = Plugin.getInstance();

        loadSignsFromFile();

        deleteSignsThatDontExistAnymore();
    }

    public void deleteSignsThatDontExistAnymore(){

        int discardedCount = 0;

        Map<Location, Integer> signsReplacement = new HashMap<Location, Integer>();

        for(Map.Entry<Location, Integer> entry : signs.entrySet()) {

            Material type = entry.getKey().getBlock().getType();

            if(type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.SIGN){
                signsReplacement.put(entry.getKey(), entry.getValue());
            }else{
                discardedCount++;
            }

        }

        if(discardedCount > 0){
            plugin.getLogger().info("Discarded " + discardedCount + " signs as they no longer exists in the world");
        }

        signs = signsReplacement;

        saveDataToFile();

    }

    public void loadSignsFromFile(){
        File file = new File(plugin.getDataFolder(), "signs.json");

        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if(!file.exists()) {
            try {
                file.createNewFile();

                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(plugin.getDataFolder(), "signs.json")), "utf-8"));
                writer.write("[]");
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(file.canRead()){

            try {

                JSONArray data = null;

                data = new JSONArray(new String(Files.readAllBytes(file.toPath())));

                for (int  i = 0 ; i < data.length(); i++){
                    JSONObject row = data.getJSONObject(i);

                    JSONArray jsonarray = ((JSONArray) row.get("location"));

                    int[] loc = {jsonarray.getInt(0), jsonarray.getInt(1), jsonarray.getInt(2)};

                    Location location = new Location(Bukkit.getWorld(UUID.fromString(row.getString("world"))), loc[0], loc[1], loc[2]);

                    signs.put(location, row.getInt("package_id"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            try {
                throw new Exception("Can not read signs file!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded all signs from disk");

    }

    public void saveSign(Location location, int packageId){
        signs.put(location, packageId);

        plugin.getLogger().info("Saved new buycraft buy sign");

        saveDataToFile();

    }

    public void deleteSign(Location location){
        signs.remove(location);

        plugin.getLogger().info("Deleted buycraft buy sign");

        saveDataToFile();

    }

    public void saveDataToFile(){
        JSONArray data = new JSONArray();

        for(Map.Entry<Location, Integer> entry : signs.entrySet()) {
            JSONObject obj = new JSONObject();

            Location loc = entry.getKey();

            try {
                obj.put("world", entry.getKey().getWorld().getUID().toString());
                obj.put("location", new int[]{loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()});
                obj.put("package_id", entry.getValue());

                data.put(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {

            FileWriter file = new FileWriter(new File(plugin.getDataFolder(), "signs.json").getPath());
            file.write(data.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
