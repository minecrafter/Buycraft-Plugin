package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

import org.bukkit.Bukkit;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CommandExecuteTask extends ApiTask {
	private static final Pattern REPLACE_NAME = Pattern.compile("[{\\(<\\[](name|player|username)[}\\)>\\]]", Pattern.CASE_INSENSITIVE);
	
    private String command;
    private String username;

    public static void call(String command, String username) {
        Plugin.getInstance().addTask(new CommandExecuteTask(command, username));
    }

    private CommandExecuteTask(String command, String username) {
        this.command = command;
        this.username = username;
    }

    public void run() {
        try {
        	command = REPLACE_NAME.matcher(command).replaceAll(username);

            Bukkit.getLogger().info("Executing command '" + command + "' on behalf of user '" + username + "'.");

            if (command.startsWith("{mcmyadmin}")) {
                String newCommand = command.replace("{mcmyadmin}", "");

                Logger.getLogger("McMyAdmin").info("Buycraft tried command: " + newCommand);
            } else {
                Runnable r = new Runnable() {
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                    }
                };
                sync(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
