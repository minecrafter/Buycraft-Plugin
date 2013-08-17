package net.buycraft.heads;

import java.util.ArrayList;

import net.buycraft.Plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONException;

public class HeadSign {

    private final Location[] loc;
    private final String filter;

    public HeadSign(Location[] loc, String filter) {
        this.loc = loc;
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public Location getLocation(int i) {
        return loc[i];
    }

    public Location[] getLocation() {
        return loc;
    }

    public String serialize() {
        JSONArray arr = new JSONArray();
        // put "null" as the String into the serialization
        if(filter == null) {
            arr.put("null");
        } else {
            arr.put(filter);
        }
        for(Location loc : this.loc) {
            arr.put(getLocation(loc));
        }
        return arr.toString();
    }

    public static HeadSign deserialize(String json) throws JSONException {
        JSONArray arr = new JSONArray(json);
        String filter = arr.getString(0);
        if(filter.equalsIgnoreCase("null")) {
            filter = null;
        }
        ArrayList<Location> loc = new ArrayList<Location>(arr.length()-1);
        for(int i=1; i<arr.length(); i++) {
            Location l = getLocation(arr.getString(i));
            if (l != null) {
                loc.add(l);
            }
        }

        return new HeadSign(loc.toArray(new Location[loc.size()]), filter);
    }

    public static String getLocation(Location loc) {
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    }

    public static Location getLocation(String loc) {
        String spl[] = loc.split(",");

        if (spl.length != 4) {
            Plugin.getInstance().getLogger().warning("Invalid HeadSign location found: " + loc);
            Plugin.getInstance().getLogger().warning("Fix it or remove it from your heads.yml");
            return null;
        }

        World world = Bukkit.getWorld(spl[0]);

        if (world == null) {
            Plugin.getInstance().getLogger().warning("Missing world for HeadSign location: " + loc);
            return null;
        }

        return new Location(world, Integer.parseInt(spl[1]), Integer.parseInt(spl[2]), Integer.parseInt(spl[3]));
    }

}
