package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class CommandExecuteTask extends ApiTask {
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
            command = command.replace("{name}", username);
            command = command.replace("(name)", username);
            command = command.replace("{player}", username);
            command = command.replace("(player)", username);
            command = command.replace("{username}", username);
            command = command.replace("(username)", username);
            command = command.replace("<name>", username);
            command = command.replace("<name>", username);
            command = command.replace("<player>", username);
            command = command.replace("<player>", username);
            command = command.replace("<username>", username);
            command = command.replace("<username>", username);
            command = command.replace("[name]", username);
            command = command.replace("[name]", username);
            command = command.replace("[player]", username);
            command = command.replace("[player]", username);
            command = command.replace("[username]", username);
            command = command.replace("[username]", username);

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
