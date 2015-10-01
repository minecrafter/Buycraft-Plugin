package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.tasks.VisitLinkTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand extends AbstractCommand {

    public BuyCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Plugin plugin = Plugin.getInstance();

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(Chat.header());
            commandSender.sendMessage(Chat.seperator());
            commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "Only players may run this command");
            commandSender.sendMessage(Chat.seperator());
            commandSender.sendMessage(Chat.footer());
            return true;
        }

        Player player = (Player) commandSender;

        if (!plugin.getSettings().getBoolean("disableBuyCommand")) {
            if (plugin.isAuthenticated(player)) {
                String pageToView = "0";
                String categoryToView = "0";
                
                if (!plugin.getPackageManager().hasPackages()) {
                    player.sendMessage(Chat.header());
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("noPackagesForSale"));
                    player.sendMessage(Chat.seperator());
                    player.sendMessage(Chat.footer());
                    return true;
                }

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("page") && (args.length == 3 || args.length == 4)) {
                        if (args.length == 4) {
                            pageToView = args[3];
                            categoryToView = args[2];
                        }
                        else if (args.length == 3) {
                            pageToView = args[2];
                        }
                    } else {
                        if (args.length == 2 && isNumber(args[1])) {
                            int packageID = Integer.valueOf(args[1]);

                            Plugin.getInstance().getBuyUi().showPackage(player, packageID);
                            return true;
                        } else {
                            player.sendMessage(Chat.header());
                            player.sendMessage(Chat.seperator());
                            player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("invalidBuyCommand"));
                            player.sendMessage(Chat.seperator());
                            player.sendMessage(Chat.footer());
                        }
                    }
                }

                if (isNumber(pageToView) && isNumber(categoryToView) && pageToView.length() < 5) {
                    // Fetch page number and category id
                    int pageNumber = Integer.parseInt(pageToView);
                    int categoryId = Integer.parseInt(categoryToView);
                    Plugin.getInstance().getBuyUi().showPage(player, categoryId, pageNumber);
                    return true;
                }

                player.sendMessage(Chat.header());
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("invalidBuyCommand"));
                player.sendMessage(Chat.seperator());
                player.sendMessage(Chat.footer());
            }
        } else {
            VisitLinkTask.call((Player) player, plugin.getServerStore());
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
