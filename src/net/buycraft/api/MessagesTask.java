package net.buycraft.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessagesTask implements Runnable {

    public static class Message {

        private final String player;
        private final String message;

        public Message(String player, String message) {
            this.player = player;
            this.message = message;
        }

        public String getPlayer() {
            return player;
        }

        public String getMessage() {
            return message;
        }

    }

    private List<Message[]> messages = new ArrayList<Message[]>();

    public void run() {
        if(messages.size() == 0) {
            return;
        }
        List<Message[]> messages = new ArrayList<Message[]>(getMessages());
        getMessages().clear();

        for(Message[] message : messages) {
            for(int i=0; i<message.length; i++) {
                Player player = Bukkit.getPlayer(message[i].getPlayer());
                if(player != null) {
                    player.sendMessage(message[i].getMessage());
                }
            }
        }
    }

    private synchronized List<Message[]> getMessages() {
        return messages;
    }

    private void add(Message[] messages) {
        getMessages().add(messages);
    }

    private void add(Message message) {
        getMessages().add(new Message[]{message});
    }

    public void sendMessage(String player, String message) {
        add(new Message(player, message));
    }

    public void sendMessage(String player, String[] messages) {
        Message[] m = new Message[messages.length];
        for(int i=0; i<m.length; i++) {
            m[i] = new Message(player, messages[i]);
        }
        add(m);
    }


}
