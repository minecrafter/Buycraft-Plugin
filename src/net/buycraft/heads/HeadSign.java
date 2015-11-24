package net.buycraft.heads;

import java.util.ArrayList;

import net.buycraft.Plugin;

import net.buycraft.util.SavedBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONException;

public class HeadSign {

    private final SavedBlockLocation[] loc;
    private final String filter;

    public HeadSign(SavedBlockLocation[] loc, String filter) {
        this.loc = loc;
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public SavedBlockLocation getLocation(int i) {
        return loc[i];
    }

    public SavedBlockLocation[] getLocation() {
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
        for(SavedBlockLocation loc : this.loc) {
            arr.put(loc.serializeAsCsv());
        }
        return arr.toString();
    }

    public static HeadSign deserialize(String json) throws JSONException {
        JSONArray arr = new JSONArray(json);
        String filter = arr.getString(0);
        if(filter.equalsIgnoreCase("null")) {
            filter = null;
        }
        ArrayList<SavedBlockLocation> loc = new ArrayList<SavedBlockLocation>(arr.length()-1);
        for(int i=1; i<arr.length(); i++) {
            SavedBlockLocation l;
            try {
                l = SavedBlockLocation.deserialize(arr.getString(i));
            } catch (IllegalArgumentException e) {
                Plugin.getInstance().getLogger().severe("Unable to deserialize the sign location " + arr.getString(i) + ". Ignoring it.");
                continue;
            }
            loc.add(l);
        }

        return new HeadSign(loc.toArray(new SavedBlockLocation[loc.size()]), filter);
    }

    public static String getLocation(Location loc) {
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    }

}
