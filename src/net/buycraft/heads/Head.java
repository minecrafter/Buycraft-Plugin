package net.buycraft.heads;

import net.buycraft.Plugin;

import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

import java.text.NumberFormat;

public class Head {
    // [ign]
    private final String name;
    // [price]
    private final double price;
    private final String currency;
    // packages filter
    private final String[] packages;

    public Head(String name, double price, String currency, String[] packages) {
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.packages = packages;
    }

    // used for the filter(String package) method
    public boolean hasPackage(String p) {
        for(String pa : packages) {
            if(pa.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private Skull getSkull(Sign sign) {
        Block b = sign.getBlock().getRelative(BlockFace.UP);
        for(BlockFace face : HeadListener.FACES) {
            Block s = b.getRelative(face);
            if(s.getState() instanceof Skull) {
                return (Skull) s.getState();
            }
        }
        return null;
    }

    // format
    public void format(Sign sign, boolean currency) {
        Skull skull = getSkull(sign);
        // if the skull is not null, set the skull to the ign
        if(skull != null) {
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(name);
            skull.update();
        }
        sign.setLine(1, name);
        if(currency) {
            if (price <= 0.0 && Plugin.getInstance().getSettings().getBoolean("buysignsRemoveFreePrice")) {
                sign.setLine(2, null);
            } else {
                sign.setLine(2, NumberFormat.getCurrencyInstance().format(price).substring(1) + " " + this.currency);
            }
        }
        sign.update();
    }

}
