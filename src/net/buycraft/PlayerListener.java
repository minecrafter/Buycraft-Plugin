package net.buycraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private Plugin plugin;

    public PlayerListener() {
        this.plugin = Plugin.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getChatManager().enableChat(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getChatManager().isDisabled(event.getPlayer())) {
            event.setCancelled(true);
        } else {
            plugin.getChatManager().clearPlayerSet(event.getRecipients());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase("Buycraft")) {
            event.disallow(Result.KICK_OTHER, "This user has been disabled due to security reasons.");
        }
    }
}
