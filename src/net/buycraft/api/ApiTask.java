package net.buycraft.api;

import net.buycraft.Plugin;
import net.buycraft.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public abstract class ApiTask implements Runnable {

    public void sync(Runnable r) {
        Bukkit.getScheduler().runTask(getPlugin(), r);
    }

    public void addTask(ApiTask task) {
        Plugin.getInstance().addTask(task);
    }

    public Plugin getPlugin() {
        return Plugin.getInstance();
    }

    public Language getLanguage() {
        return Plugin.getInstance().getLanguage();
    }

    public Api getApi() {
        return Plugin.getInstance().getApi();
    }

    public Logger getLogger() {
        return Plugin.getInstance().getLogger();
    }

    public void disableChat(final String name) {
        final Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                public void run() {
                    Plugin.getInstance().getChatManager().disableChat(player);
                }
            }, 1);
        }
    }

}
