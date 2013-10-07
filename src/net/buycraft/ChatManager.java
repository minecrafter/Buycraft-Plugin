package net.buycraft;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;

public class ChatManager {
    private HashSet<String> disabledChatSet;

    public ChatManager() {
        disabledChatSet = new HashSet<String>();
    }

    public synchronized Boolean isDisabled(Player player) {
        return disabledChatSet.contains(player.getName());
    }

    public synchronized void enableChat(Player player) {
        disabledChatSet.remove(player.getName());
    }

    public synchronized void disableChat(Player player) {
        disabledChatSet.add(player.getName());
    }

    /**
     * Clears all players from the provided set who have chat disabled
     */
    public synchronized void clearPlayerSet(Set<Player> players) {
        Iterator<Player> it = players.iterator();

        while (it.hasNext()) {
            if (disabledChatSet.contains(it.next().getName())) {
                it.remove();
            }
        }
    }
}
