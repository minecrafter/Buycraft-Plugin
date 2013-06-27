package net.buycraft.heads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        Location[] loc = new Location[arr.length()-1];
        for(int i=1; i<arr.length(); i++) {
            loc[i-1] = getLocation(arr.getString(i));
        }
        return new HeadSign(loc, filter);
    }

    public static String getLocation(Location loc) {
        return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
    }

    public static Location getLocation(String loc) {
        String spl[] = loc.split(",");
        return new Location(Bukkit.getWorld(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]), Integer.parseInt(spl[3]));
    }

}
