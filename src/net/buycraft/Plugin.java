package net.buycraft;

import net.buycraft.api.Api;
import net.buycraft.api.ApiTask;
import net.buycraft.commands.AbstractCommand;
import net.buycraft.commands.BuyCommand;
import net.buycraft.commands.BuycraftCommand;
import net.buycraft.commands.EnableChatCommand;
import net.buycraft.heads.HeadFile;
import net.buycraft.packages.PackageManager;
import net.buycraft.tasks.AuthenticateTask;
import net.buycraft.tasks.CommandDeleteTask;
import net.buycraft.tasks.CommandExecuteTask;
import net.buycraft.tasks.PendingPlayerCheckerTask;
import net.buycraft.tasks.ReportTask;
import net.buycraft.ui.BuyChatUI;
import net.buycraft.ui.BuyInterface;
import net.buycraft.ui.BuyInventoryUI;
import net.buycraft.util.*;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Plugin extends JavaPlugin implements Listener {
    private static Plugin instance;

    // The version of the plugin
    private String version;

    private static Settings settings;
    private Language language;

    private Updater updater = null;
    
    private Api api;

    private int serverID = 0;
    private String serverCurrency = "";
    private String serverStore = "";

    private PackageManager packageManager;
    private ChatManager chatManager;
    private CommandExecuteTask commandExecutor;
    private CommandDeleteTask commandDeleteTask;
    private PendingPlayerCheckerTask pendingPlayerCheckerTask;

    public SignSelector signSelector;

    private BukkitTask pendingPlayerCheckerTaskExecutor;

    private boolean authenticated = false;
    private int authenticatedCode = 1;
    private boolean onlineMode;

    private String folderPath;

    private BuyInterface buyUi;
    private String buyCommand = "buy";
    private String buyCommandSearchString = "/buy";

    private String ecCommand = "ec";
    private String ecCommandSearchString = "/ec";

    private ExecutorService executors = null;

    private HeadFile headFile = null;
    
    public Plugin() {
        instance = this;
    }

    public void addTask(ApiTask task) {
        executors.submit(task);
    }

    public void onEnable() {
        // thread pool model
        executors = Executors.newFixedThreadPool(5);
        folderPath = getDataFolder().getAbsolutePath();
        
        checkDirectory();

        moveFileFromJar("README.md", getFolderPath() + "/README.txt", true);

        version = getDescription().getVersion();

        settings = new Settings();
        language = new Language();

        signSelector = new SignSelector();
        
        if (settings.getBoolean("autoUpdate")) {
    		this.updater = new Updater(this, 31571, this.getFile(), Updater.UpdateType.DEFAULT, true);
    	} else {
    		getLogger().info("Ignoring update due to auto update disabled.");
    	}
        
        api = new Api();

        packageManager = new PackageManager();
        chatManager = new ChatManager();
        commandExecutor = new CommandExecuteTask();
        commandDeleteTask = new CommandDeleteTask();
        pendingPlayerCheckerTask = new PendingPlayerCheckerTask();

        AbstractCommand buyCommand = new BuyCommand(getSettings().getString("buyCommand"));
        buyCommand.register();

        AbstractCommand ecCommand = new EnableChatCommand(getSettings().getString("re-enableChatCommand"));
        ecCommand.register();

        buyUi = getSettings().getBoolean("useBuyGUI") ? new BuyInventoryUI() : new BuyChatUI();

        headFile = new HeadFile(this);

        AuthenticateTask.call();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(pendingPlayerCheckerTask, this);
        getServer().getPluginManager().registerEvents(signSelector, this);
    }

    public void onDisable() {
        // Make sure any commands which have been run are deleted
        commandDeleteTask.runNow();

        // Run BuyUI cleanup
        if (buyUi != null) {
            buyUi.pluginReloaded();
        }

        executors.shutdown();
        while (!executors.isTerminated()) {
        }
        getLogger().info("Plugin has been disabled.");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("buycraft")) {
            return BuycraftCommand.process(commandSender, args);
        }

        return false;
    }

    private void checkDirectory() {
        File directory = new File(getFolderPath());

        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public void moveFileFromJar(String jarFileName, String targetLocation, Boolean overwrite) {
        try {
            File targetFile = new File(targetLocation);

            if (overwrite || targetFile.exists() == false || targetFile.length() == 0) {
                InputStream inFile = getResource(jarFileName);
                FileWriter outFile = new FileWriter(targetFile);

                int c;

                while ((c = inFile.read()) != -1) {
                    outFile.write(c);
                }

                inFile.close();
                outFile.close();
            }
        } catch (NullPointerException e) {
            getLogger().info("Failed to create " + jarFileName + ".");
            ReportTask.setLastException(e);
        } catch (Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
    }

    public Boolean isAuthenticated(CommandSender commandSender) {
        boolean invalidOnlineMode = onlineMode && !Plugin.getSettings().isOnlineMode();
        if (!authenticated || invalidOnlineMode) {
            if (commandSender != null) {
                commandSender.sendMessage(Chat.header());
                commandSender.sendMessage(Chat.seperator());
                commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "Buycraft has failed to startup.");
                commandSender.sendMessage(Chat.seperator());
                if(authenticatedCode == 101)  {
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "This is because of an invalid secret key,");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "please enter the Secret key into the settings.conf");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "file, and reload your server.");
                    commandSender.sendMessage(Chat.seperator());
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "You can find your secret key from the control panel.");
                    commandSender.sendMessage(Chat.seperator());
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "If it did not resolve the issue, restart your server");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "a couple of times.");
                    commandSender.sendMessage(Chat.seperator());
                } else if (invalidOnlineMode) {
                    commandSender.sendMessage(Chat.seperator());
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "This server is in offline mode. Buycraft requires this");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "server to be set to online mode. Either set this");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "server to online mode or you can create a new webstore.");
                    commandSender.sendMessage(Chat.seperator());
                } else {
                    commandSender.sendMessage(Chat.seperator());
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "Please execute the '/buycraft report' command and");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "then send the generated report.txt file to");
                    commandSender.sendMessage(Chat.seperator() + ChatColor.RED + "support@buycraft.net. We will be happy to help.");
                    commandSender.sendMessage(Chat.seperator());
                }
                commandSender.sendMessage(Chat.footer());
            }

            return false;
        } else {
            return true;
        }
    }

    public void setAuthenticated(Boolean value) {
        authenticated = value;
    }

    public void setAuthenticatedCode(Integer value) {
        authenticatedCode = value;
    }

    public Integer getAuthenticatedCode() {
        return authenticatedCode;
    }
    
    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public static Plugin getInstance() {
        return instance;
    }

    public Api getApi() {
        return api;
    }

    public void setServerID(Integer value) {
        serverID = value;
    }

    public void setServerCurrency(String value) {
        serverCurrency = value;
    }

    public void setBuyCommand(String value) {
        buyCommand = value;
        buyCommandSearchString = new StringBuilder().append('/').append(buyCommand).toString().toLowerCase();
    }

    public void setEcCommand(String value) {
        ecCommand = value;
        ecCommandSearchString = new StringBuilder().append('/').append(ecCommand).toString().toLowerCase();
    }

    public void setServerStore(String value) {
        serverStore = value;
    }

    public void setPendingPlayerCheckerInterval(int interval) {
        if (pendingPlayerCheckerTaskExecutor != null) {
            pendingPlayerCheckerTaskExecutor.cancel();
            pendingPlayerCheckerTaskExecutor = null;
        }

        // Convert seconds to ticks
        interval *= 20;

        if (getSettings().getBoolean("commandChecker")) {
            pendingPlayerCheckerTaskExecutor = getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                public void run() {
                    pendingPlayerCheckerTask.call(false);
                }
            }, 20, interval);
        }

    }

    public Integer getServerID() {
        return serverID;
    }

    public String getServerStore() {
        return serverStore;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }
    
    public CommandExecuteTask getCommandExecutor() {
        return commandExecutor;
    }

    public CommandDeleteTask getCommandDeleteTask() {
        return commandDeleteTask;
    }

    public PendingPlayerCheckerTask getPendingPlayerCheckerTask() {
        return pendingPlayerCheckerTask;
    }

    public String getServerCurrency() {
        return serverCurrency;
    }

    public String getVersion() {
        return version;
    }

    public static Settings getSettings() {
        return settings;
    }

    public Language getLanguage() {
        return language;
    }

    public File getJarFile() {
        return getFile();
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public BuyInterface getBuyUi() {
        return buyUi;
    }
    public String getBuyCommand() {
        return buyCommand;
    }
    
    public HeadFile getHeadFile()
    {
        return headFile;
    }
    
    public Updater getUpdater()
    {
    	return updater;
    }
}
