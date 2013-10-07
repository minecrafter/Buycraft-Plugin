package net.buycraft.ui;

import net.buycraft.Plugin;
import net.buycraft.packages.PackageModal;
import net.buycraft.tasks.VisitLinkTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class AbstractBuyUI implements BuyInterface {

    public boolean showPackage(Player player, int packageId) {
        boolean packageExists = false;
        PackageModal packageModel = null;

        for (PackageModal row : Plugin.getInstance().getPackageManager().getPackagesForSale()) {
            if (row.getOrder() == packageId) {
                packageExists = true;
                packageModel = row;
                break;
            }
        }

        if (packageExists == true) {
            String buyNowLink = Plugin.getInstance().getServerStore() + "/checkout/packages?action=add&package=" + packageModel.getId() + "&ign=" + player.getName();

            if (Plugin.getInstance().getSettings().getBoolean("directPay")) {
                buyNowLink = Plugin.getInstance().getServerStore() + "/checkout/packages?popup=true&action=add&direct=true&package=" + packageModel.getId() + "&ign=" + player.getName();
            }

            VisitLinkTask.call(player, buyNowLink);
            return true;
        } else {
            player.sendMessage(Chat.header());
            player.sendMessage(Chat.seperator());
            player.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("packageNotFound"));
            player.sendMessage(Chat.seperator());
            player.sendMessage(Chat.footer());
            return false;
        }
    }

}
