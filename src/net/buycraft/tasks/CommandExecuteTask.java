package net.buycraft.tasks;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.PackageCommand;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CommandExecuteTask extends ApiTask {
	private static final Pattern REPLACE_NAME = Pattern.compile("[{\\(<\\[](name|player|username)[}\\)>\\]]", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Queues commands to be run
	 * <p>
	 * Note: 'Probably' not required, but safer to use it than not.
	 */
	private final PriorityBlockingQueue<PackageCommand> commandQueue;
	private final AtomicBoolean isScheduled;
	private BukkitTask task;

	private String lastLongRunningCommand = "None";

    public CommandExecuteTask() {
		commandQueue = new PriorityBlockingQueue<PackageCommand>();
		isScheduled = new AtomicBoolean(false);
	}
    
    public String getLastLongRunningCommand()
    {
        return lastLongRunningCommand;
    }
    
    /**
     * Parses the command and queues it to be executed in the main thread
     * @param delay The time in seconds for the task to be delayed
     */
    public void queueCommand(String command, String username, int delay) {
        // Convert delay from seconds to ticks
        delay *= 20;
        try {
            username = Bukkit.getServer().getOfflinePlayer(username).getName();
        	command = REPLACE_NAME.matcher(command).replaceAll(username);

            if (command.startsWith("{mcmyadmin}")) {
                Plugin.getInstance().getLogger().info("Executing command '" + command + "' on behalf of user '" + username + "'.");
                String newCommand = command.replace("{mcmyadmin}", "");                
                Logger.getLogger("McMyAdmin").info("Buycraft tried command: " + newCommand);
            } else {
                commandQueue.add(new PackageCommand(username, command, delay));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
		while (!commandQueue.isEmpty() && commandQueue.peek().runtime <= System.currentTimeMillis() && System.nanoTime() - start < 500000) {
			try {
                PackageCommand pkgcmd = commandQueue.poll();
                Plugin.getInstance().getLogger().info("Executing command '" + pkgcmd.command + "' on behalf of user '" + pkgcmd.username + "'.");
			    long cmdStart = System.currentTimeMillis();

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pkgcmd.command);
				// Check if the command lasted longer than our threshold
				long cmdDiff = System.currentTimeMillis() - cmdStart;
				if (cmdDiff >= 10) {
				    // Save the command and time it took to run
				    lastLongRunningCommand = "Time=" + cmdDiff + "ms - CMD=" + pkgcmd.command;
				}
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
