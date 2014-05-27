package net.buycraft.util;

import java.util.UUID;

public class UuidUtil {
    
    private UuidUtil() {
    }
    
    public static String addDashesToUUID(String s) {
        StringBuilder b = new StringBuilder();
        return b.append(s, 0, 8).append('-').append(s, 8, 12).append('-').append(s, 12, 16).append('-').append(s, 16, 20).append('-').append(s, 20, 32).toString();
    }
    
    public static String uuidToString(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

}
