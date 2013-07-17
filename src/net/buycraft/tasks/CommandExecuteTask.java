package net.buycraft.tasks;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.buycraft.api.ApiTask;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CommandExecuteTask extends ApiTask {
	private static final Pattern REPLACE_NAME = Pattern.compile("[{\\(<\\[](name|player|username)[}\\)>\\]]", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Queues commands to be run
	 * <p>
	 * Note: 'Probably' not required, but safer to use it than not.
	 */
	private final ConcurrentLinkedQueue<String> commandQueue;
	private final AtomicBoolean isScheduled;
	private BukkitTask task;

    public CommandExecuteTask() {
		commandQueue = new ConcurrentLinkedQueue<String>();
		isScheduled = new AtomicBoolean(false);
	}
    
    /**
     * Parses the command and queues it to be executed in the main thread
     */
    public void queueCommand(String command, String username) {
        try {
        	command = REPLACE_NAME.matcher(command).replaceAll(username);

            Bukkit.getLogger().info("Executing command '" + command + "' on behalf of user '" + username + "'.");

            if (command.startsWith("{mcmyadmin}")) {
                String newCommand = command.replace("{mcmyadmin}", "");

                Logger.getLogger("McMyAdmin").info("Buycraft tried command: " + newCommand);
            } else {
                commandQueue.add(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCommands() {
        commandQueue.clear();
    }

    /**
     * Schedules the command executor to run
     * <p>
     * Should be run after PackageChecker finishes
     */
    public void scheduleExecutor() {
    	if (commandQueue.isEmpty()) {
    		// What?
    		return;
    	}
    	// Make sure the task is not already scheduled
    	// NOTE: This will only happen if commands take 6000 ticks to execute (Lets play it safe)
    	if (isScheduled.compareAndSet(false, true)) {
    	    task = syncTimer(this, 1L, 1L);
            // Make sure the task was actually scheduled
    	    if (task == null) {
    	        isScheduled.set(false);
    	    }
    	}
    }

	public void run() {
		long start = System.nanoTime();
		// Cap execution time at 500us
		while (System.nanoTime() - start < 500000 && !commandQueue.isEmpty()) {

			try {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandQueue.poll());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		if (commandQueue.isEmpty()) {
			BukkitTask task = this.task;
			// Null the task now so we can't overwrite a new one
			this.task = null;
			// Allow a new task to be scheduled
			isScheduled.set(false);
			// Cancel the current task
			task.cancel();
		}
    }
}
