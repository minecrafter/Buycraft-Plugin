package net.buycraft.commands;

import net.buycraft.Plugin;
import net.buycraft.tasks.AuthenticateTask;
import net.buycraft.tasks.RecentPaymentsTask;
import net.buycraft.tasks.ReloadPackagesTask;
import net.buycraft.tasks.ReportTask;
import net.buycraft.util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuycraftCommand {

    private BuycraftCommand() {}

    public static boolean process(CommandSender commandSender, String[] args) {
        Plugin plugin = Plugin.getInstance();
        if (args.length > 0) {
            if (commandSender instanceof Player == false || commandSender.hasPermission("buycraft.admin") || commandSender.isOp()) {
                
                if(args[0].equalsIgnoreCase("payments")) {
                    String playerLookup = "";
                    
                    if(args.length == 2) {
                        playerLookup = args[1];
                    }
                        
                    RecentPaymentsTask.call(commandSender, playerLookup);

                    return true;
                }
                
                if (args[0].equalsIgnoreCase("report")) {
                    // Call the report task, if it fails we don't send the following messages to the player
                    if (ReportTask.call(commandSender) && commandSender instanceof Player) {
                        commandSender.sendMessage(Chat.header());
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + "Beginning generation of report");
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.footer());
                    }
                    return true;
                }
                
                if (args[0].equalsIgnoreCase("secret")) {
                    if (args.length == 2) {
                        String secretKey = args[1];

                        if (commandSender instanceof Player) {
                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + "Server authenticated. Type /buycraft for confirmation.");
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        }

                        plugin.getSettings().setString("secret", secretKey);
                        plugin.getApi().setApiKey(secretKey);

                        AuthenticateTask.call();

                        return true;
                    } else {
                        commandSender.sendMessage(Chat.header());
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "Please enter a valid secret key.");
                        commandSender.sendMessage(Chat.seperator());
                        commandSender.sendMessage(Chat.footer());

                        return true;
                    }
                }

                if (plugin.isAuthenticated(commandSender)) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        ReloadPackagesTask.call();

                        if (commandSender instanceof Player) {
                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + "Package cache successfully reloaded.");
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        }

                        return true;
                    }

                    if (args[0].equalsIgnoreCase("forcecheck")) {
                        plugin.getPendingPlayerCheckerTask().call(true);

                        if(plugin.getHeadFile().enabled)
                        {
                            plugin.getHeadFile().getHeadThread().update();
                        }

                        if (commandSender instanceof Player) {
                            commandSender.sendMessage(Chat.header());
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.seperator() + ChatColor.GREEN + "Force check successfully executed.");
                            commandSender.sendMessage(Chat.seperator());
                            commandSender.sendMessage(Chat.footer());
                        } else {
                        	commandSender.sendMessage("Force check successfully executed.");
                        }
                        else
                        {
                        	commandSender.sendMessage("Force check successfully executed.");
                        }

                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute that command.");

                return true;
            }
        } else {
            if (plugin.isAuthenticated(commandSender)) {
                commandSender.sendMessage(Chat.header());
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/" + plugin.getBuyCommand() + ":" + ChatColor.GREEN + " View available packages for sale");
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/" + plugin.getBuyCommand() + " page <ID>:" + ChatColor.GREEN + " Navigate through package pages");
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/" + plugin.getBuyCommand() + " <ID>: " + ChatColor.GREEN + " Purchase a specific package");
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator());
                
                if (commandSender instanceof Player == false || commandSender.hasPermission("buycraft.admin") || commandSender.isOp()) {
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buycraft reload:" + ChatColor.GREEN + " Reload the package cache");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buycraft forcecheck:" + ChatColor.GREEN + " Check for pending commands");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buycraft secret <key>:" + ChatColor.GREEN + " Set the Secret key");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buycraft payments <ign>:" + ChatColor.GREEN + " Get recent payments of a user");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buycraft report:" + ChatColor.GREEN + " Generate an error report");
                }
                   
                if (commandSender instanceof Player == false || commandSender.hasPermission("buycraft.admin") || commandSender.hasPermission("buycraft.signs") || commandSender.isOp()) {
                    commandSender.sendMessage(Chat.seperator());
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buysign begin/filter <Package>:" + ChatColor.GREEN + " Set payment signs");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "/buysign end:" + ChatColor.GREEN + " End payment signs");
                }

                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Server ID: " + ChatColor.GREEN + String.valueOf(plugin.getServerID()));
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Server URL: " + ChatColor.GREEN + String.valueOf(plugin.getServerStore()));
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Version: " + ChatColor.GREEN + String.valueOf(plugin.getVersion()));
                commandSender.sendMessage(Chat.seperator() + ChatColor.LIGHT_PURPLE + "Website: " + ChatColor.GREEN + "http://buycraft.net");
                commandSender.sendMessage(Chat.footer());
            }

            return true;
        }

        return false;
    }
}
