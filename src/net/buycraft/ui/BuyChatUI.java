package net.buycraft.ui;

import java.util.List;

import net.buycraft.Plugin;
import net.buycraft.packages.PackageModal;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BuyChatUI extends AbstractBuyUI {

    public void showCategoryPage(Player player, int pageNumber) {
        pageNumber++;
    }

    public void showPage(Player player, int categoryId, int pageNumber) {
        // TODO implement categories
        if (pageNumber == 0)
            pageNumber = 1;

        List<PackageModal> packages = Plugin.getInstance().getPackageManager().getPackagesForSale();

        if (packages.size() == 0) {
            player.sendMessage(Chat.header());
            player.sendMessage(Chat.seperator());
            player.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("noPackagesForSale"));
            player.sendMessage(Chat.seperator());
            player.sendMessage(Chat.footer());
        } else {
            int pageCount = (int) Math.ceil(packages.size() / 3.0);

            int startingPoint = -3 + (3 * pageNumber);
            int finishPoint = 0 + (3 * pageNumber);

            if (finishPoint > packages.size() || finishPoint < 3) finishPoint = packages.size();
            if (startingPoint > packages.size() || startingPoint < 0) startingPoint = packages.size();

            List<PackageModal> packagesToDisplay = packages.subList(startingPoint, finishPoint);

            if (packagesToDisplay.size() > 0) {
                Plugin.getInstance().getChatManager().disableChat((Player) player);

                player.sendMessage(Chat.header());
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("toPurchase") + " " + ChatColor.LIGHT_PURPLE + "/" + Plugin.getInstance().getBuyCommand() + " <ID>");

                if (pageCount > 1) {
                    player.sendMessage(Chat.seperator() + ChatColor.GREEN + Plugin.getInstance().getLanguage().getString("howToNavigate") + " " + ChatColor.LIGHT_PURPLE + "/" + Plugin.getInstance().getBuyCommand() + " page <1-" + pageCount + ">");
                }

                player.sendMessage(Chat.seperator());

                for (PackageModal row : packagesToDisplay) {
                    player.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + row.getOrder());
                    player.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageName") + ": " + ChatColor.LIGHT_PURPLE + row.getName());
                    player.sendMessage(Chat.seperator() + ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packagePrice") + ": " + ChatColor.LIGHT_PURPLE + row.getPrice() + ' ' + Plugin.getInstance().getServerCurrency());
                    player.sendMessage(Chat.seperator());
                }

                player.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("turnChatBackOn"));
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.footer());
            } else {
                player.sendMessage(Chat.header());
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.seperator() + ChatColor.RED + Plugin.getInstance().getLanguage().getString("pageNotFound"));
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.footer());
            }
        }
    }

    public void packagesReset() {
    }

    public void pluginReloaded() {
    }

}
