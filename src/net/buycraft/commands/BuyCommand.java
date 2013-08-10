package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.packages.PackageModal;
import net.buycraft.tasks.VisitLinkTask;
import net.buycraft.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class BuyCommand {

    private BuyCommand() {}

    public static boolean process(Player commandSender, String[] args) {
        Plugin plugin = Plugin.getInstance();
        if (!plugin.getSettings().getBoolean("disableBuyCommand")) {
            if (plugin.isAuthenticated(commandSender)) {
                String pageToView = "1";

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("page") && args.length == 3) {
                        pageToView = args[2];
                    } else {
                        if (args.length == 2 && isNumber(args[1])) {
                            int packageID = Integer.valueOf(args[1]);

                            boolean packageExists = false;
                            PackageModal packageModel = null;

                            for (PackageModal row : plugin.getPackageManager().getPackagesForSale()) {
                                if (row.getOrder() == packageID) {
                                    packageExists = true;
                                    packageModel = row;

                                    break;
                                }
                            }

                            if (packageExists == true) {
                                String buyNowLink = plugin.getServerStore() + "/checkout/packages?action=add&package=" + packageModel.getId() + "&ign=" + commandSender.getName();

                                if (plugin.getSettings().getBoolean("directPay")) {
                                    String directGateway = plugin.getSettings().getString("directPayGateway");

                                    buyNowLink = plugin.getServerStore() + "/checkout/pay?direct=true&package=" + packageModel.getId() + "&agreement=true&gateway=" + directGateway + "&ign=" + commandSender.getName();
                                }

                                VisitLinkTask.call((Player) commandSender, buyNowLink);
                            } else {
                                commandSender.sendMessage(Chat.header());
                                commandSender.sendMessage(Chat.seperator());
                                commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("packageNotFound"));
                                commandSender.sendMessage(Chat.seperator());
                                commandSender.sendMessage(Chat.footer());
                            }

                            return true;
                        } else {
                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("invalidBuyCommand"));
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        }
                    }
                }

                if (isNumber(pageToView) && pageToView.length() < 5) {
                    Integer pageNumber = Integer.parseInt(pageToView);

                    List<PackageModal> packages = plugin.getPackageManager().getPackagesForSale();

                    if (packages.size() == 0) {
                        commandSender.sendMessage(Chat.header());
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("noPackagesForSale"));
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.footer());
                    } else {
                        int pageCount = (int) Math.ceil(packages.size() / 3.0);

                        int startingPoint = -3 + (3 * pageNumber);
                        int finishPoint = 0 + (3 * pageNumber);

                        if (finishPoint > packages.size() || finishPoint < 3) finishPoint = packages.size();
                        if (startingPoint > packages.size() || startingPoint < 0) startingPoint = packages.size();

                        List<PackageModal> packagesToDisplay = packages.subList(startingPoint, finishPoint);

                        if (packagesToDisplay.size() > 0) {
                            plugin.getChatManager().disableChat((Player) commandSender);

                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("toPurchase") + " " + ChatColor.LIGHT_PURPLE + "/" + plugin.getBuyCommand() + " <ID>");

                            if (pageCount > 1) {
                                commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + plugin.getLanguage().getString("howToNavigate") + " " + ChatColor.LIGHT_PURPLE + "/" + plugin.getBuyCommand() + " page <1-" + pageCount + ">");
                            }

                            commandSender.sendMessage(Chat.seperator());

                            for (PackageModal row : packagesToDisplay) {
                                commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + row.getOrder());
                                commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packageName") + ": " + ChatColor.LIGHT_PURPLE + row.getName());
                                commandSender.sendMessage(Chat.seperator() + ChatColor.YELLOW + plugin.getLanguage().getString("packagePrice") + ": " + ChatColor.LIGHT_PURPLE + row.getPrice() + ' ' + plugin.getServerCurrency());
                                commandSender.sendMessage(Chat.seperator());
                            }

                            commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("turnChatBackOn"));
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        } else {
                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("pageNotFound"));
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        }
                    }

                    return true;
                }

                commandSender.sendMessage(Chat.header());
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("invalidBuyCommand"));
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.footer());
            }
        } else {
            VisitLinkTask.call((Player) commandSender, plugin.getServerStore());
        }

        return true;
    }

    private static boolean isNumber(String string) {
        char[] c = string.toCharArray();

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(c[i])) {
                return false;
            }
        }

        return true;
    }
}
