package net.buycraft.tasks;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

public class CommandDeleteTask extends ApiTask {

    public static void call(String json) {
        Plugin.getInstance().addTask(new CommandDeleteTask(json));
    }

    private String json;

    private CommandDeleteTask(String json) {
        this.json = json;
    }

    public void run() {
       getApi().commandsDeleteAction(json);
    }

}
