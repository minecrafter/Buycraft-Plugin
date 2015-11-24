package net.buycraft.util;

import com.google.common.base.Objects;
import net.buycraft.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by william on 01/06/15.
 */
public class SignSelector implements Listener {

    private Plugin plugin;

    public Map<SavedBlockLocation, Integer> signs = new HashMap<SavedBlockLocation, Integer>();

    public SignSelector(){
        this.plugin = Plugin.getInstance();

        loadSignsFromFile();

        for (World world : Bukkit.getWorlds()) {
            deleteSignsThatDontExistAnymore(world);
        }
    }

    public void deleteSignsThatDontExistAnymore(World world){

        int discardedCount = 0;

        for (Iterator<SavedBlockLocation> it = signs.keySet().iterator(); it.hasNext(); ) {

            SavedBlockLocation location = it.next();

            if (Objects.equal(world, location.getBukkitWorld())) {

                Material type = location.getBukkitLocation().getBlock().getType();

                if (type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.SIGN) {
                    continue;
                }

                discardedCount++;
                it.remove();

            }

        }

        if(discardedCount > 0) {
            plugin.getLogger().info("Discarded " + discardedCount + " signs as they no longer exist in the world");
            saveDataToFile();
        }

    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        deleteSignsThatDontExistAnymore(event.getWorld());
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

                JSONArray data = new JSONArray(new String(Files.readAllBytes(file.toPath())));

                for (int  i = 0 ; i < data.length(); i++){
                    JSONObject row = data.getJSONObject(i);
                    SavedBlockLocation location = SavedBlockLocation.deserialize(row);

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
        signs.put(SavedBlockLocation.fromLocation(location, true), packageId);

        plugin.getLogger().info("Saved new buycraft buy sign");

        saveDataToFile();

    }

    public void deleteSign(Location location){
        signs.remove(SavedBlockLocation.fromLocation(location, true));

        plugin.getLogger().info("Deleted buycraft buy sign");

        saveDataToFile();

    }

    public void saveDataToFile(){
        JSONArray data = new JSONArray();

        for(Map.Entry<SavedBlockLocation, Integer> entry : signs.entrySet()) {
            JSONObject obj = new JSONObject();

            SavedBlockLocation loc = entry.getKey();

            try {
                obj.put("world", entry.getKey().getWorld());
                obj.put("location", new int[]{loc.getX(), loc.getY(), loc.getZ()});
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
