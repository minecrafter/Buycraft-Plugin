package net.buycraft.util;

import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public final class SavedBlockLocation {
    private final String world;
    private final boolean worldIsUuid;
    private final int x;
    private final int y;
    private final int z;

    public SavedBlockLocation(String world, boolean worldIsUuid, int x, int y, int z) {
        this.world = world;
        this.worldIsUuid = worldIsUuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final String getWorld() {
        return world;
    }

    public final boolean isWorldIsUuid() {
        return worldIsUuid;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getZ() {
        return z;
    }

    public final World getBukkitWorld() {
        if (worldIsUuid) {
            return Bukkit.getWorld(UUID.fromString(world));
        } else {
            return Bukkit.getWorld(world);
        }
    }

    public final Location getBukkitLocation() {
        return new Location(getBukkitWorld(), x, y, z);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedBlockLocation that = (SavedBlockLocation) o;

        if (worldIsUuid != that.worldIsUuid) return false;
        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        if (world != null ? !world.equals(that.world) : that.world != null) return false;

        return true;
    }

    @Override
    public final int hashCode() {
        int result = world != null ? world.hashCode() : 0;
        result = 31 * result + (worldIsUuid ? 1 : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public final String serializeAsCsv() {
        return Joiner.on(',').join(world, x, y, z);
    }

    public static SavedBlockLocation fromLocation(Location location, boolean useUuid) {
        return new SavedBlockLocation(useUuid ? location.getWorld().getUID().toString() : location.getWorld().getName(),
                useUuid, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static SavedBlockLocation deserialize(JSONObject object) throws JSONException {
        JSONArray array = object.getJSONArray("location");
        return new SavedBlockLocation(object.getString("world"), true, array.getInt(0), array.getInt(1), array.getInt(2));
    }

    public static SavedBlockLocation deserialize(String loc) {
        String spl[] = loc.split(",");

        if (spl.length != 4) {
            throw new IllegalArgumentException("Sign location is not valid.");
        }

        return new SavedBlockLocation(spl[0], false, Integer.parseInt(spl[1]), Integer.parseInt(spl[2]), Integer.parseInt(spl[3]));
    }
}
