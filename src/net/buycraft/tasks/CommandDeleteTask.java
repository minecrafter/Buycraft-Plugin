package net.buycraft.tasks;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONArray;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;

public class CommandDeleteTask extends ApiTask {

    private final AtomicBoolean scheduled = new AtomicBoolean(false);
    private final HashSet<Integer> commandsToDelete = new HashSet<Integer>();
    private BukkitTask currentTask;

    public synchronized void deleteCommand(int cid) {
        commandsToDelete.add(cid);

        // Delay the task for 10 seconds to allow for more deletions to occur at once
        if (scheduled.compareAndSet(false, true)) {
            currentTask = Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), new Runnable() {
                public void run() {
                    currentTask = null;
                    Plugin.getInstance().addTask(CommandDeleteTask.this);
                }
            }, 600L);
        }
    }

    public synchronized boolean queuedForDeletion(int cid) {
        return commandsToDelete.contains(cid);
    }

    /**
     * Forces the delete task to run.
     * Should only be used on plugin disable.
     */
    public synchronized void runNow() {
        if (currentTask != null) {
            currentTask.cancel();
        }

        if (!commandsToDelete.isEmpty())
            Plugin.getInstance().addTask(this);
    }

    public void run() {
        try
        {
            scheduled.set(false);
            Integer[] commandIds = fetchCommands();

            if (commandIds.length == 0)
                // What are we doing here??
                return;

            getApi().commandsDeleteAction(new JSONArray(commandIds).toString());

            removeCommands(commandIds);
        }
        catch (Exception e)
        {
            Plugin.getInstance().getLogger().log(Level.SEVERE, "Error occured when deleting commands from the API", e);
            ReportTask.setLastException(e);
        }
    }

    private synchronized void removeCommands(Integer[] commandIds) {
        for (Integer id : commandIds) {
            commandsToDelete.remove(id);
        }
    }

    private synchronized Integer[] fetchCommands() {
        Integer[] commandIds = commandsToDelete.toArray(new Integer[commandsToDelete.size()]);
        return commandIds;
    }

}
