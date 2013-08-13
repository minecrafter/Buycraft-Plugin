package net.buycraft;

import net.buycraft.api.Api;
import net.buycraft.api.ApiTask;
import net.buycraft.commands.BuyCommand;
import net.buycraft.commands.BuycraftCommand;
import net.buycraft.commands.EnableChatCommand;
import net.buycraft.heads.HeadFile;
import net.buycraft.packages.PackageManager;
import net.buycraft.tasks.AuthenticateTask;
import net.buycraft.tasks.CommandDeleteTask;
import net.buycraft.tasks.CommandExecuteTask;
import net.buycraft.tasks.PackageCheckerTask;
import net.buycraft.tasks.PlayerCheckExpireTask;
import net.buycraft.tasks.PlayerVerifyTask;
import net.buycraft.tasks.ReportTask;
import net.buycraft.util.Chat;
import net.buycraft.util.Language;
import net.buycraft.util.Settings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Plugin extends JavaPlugin implements Listener {
    private static Plugin instance;

    private String version;

    private Settings settings;
    private Language language;

    private Api api;

    private int serverID = 0;
    private String serverCurrency = "";
    private String serverStore = "";

    private PackageManager packageManager;
    private ChatManager chatManager;
    private CommandExecuteTask commandExecutor;
    private PlayerVerifyTask playerVerifyTask;
    private PlayerCheckExpireTask playerCheckExpireTask;
    private CommandDeleteTask commandDeleteTask;

    private boolean authenticated = false;
    private int authenticatedCode = 1;

    private String folderPath;

    private String buyCommand = "buy";
    private String buyCommandSearchString = "/buy";

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

        api = new Api();

        packageManager = new PackageManager();
        chatManager = new ChatManager();
        commandExecutor = new CommandExecuteTask();
        playerVerifyTask = new PlayerVerifyTask();
        playerCheckExpireTask = new PlayerCheckExpireTask();
        commandDeleteTask = new CommandDeleteTask();

        setBuyCommand(getSettings().getString("buyCommand"));

        headFile = new HeadFile(this);

        AuthenticateTask.call();
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            public void run() {
                PackageCheckerTask.call(false);
            }
        }, 6000L, 6000L);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void onDisable() {
        // Make sure any commands which have been run are deleted
        commandDeleteTask.runNow();

        executors.shutdown();
        while (!executors.isTerminated()) {
        }
        getLogger().info("Plugin has been disabled.");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("ec")) {
            return EnableChatCommand.process(commandSender, args);
        }

        if (label.equalsIgnoreCase("buycraft")) {
            return BuycraftCommand.process(commandSender, args);
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void preCommandListener(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        int cmdLength = buyCommandSearchString.length();
        if (message.startsWith(buyCommandSearchString) && (message.length() == cmdLength || message.charAt(cmdLength) == ' ')) {
            BuyCommand.process(event.getPlayer(), message.split(" "));
            event.setCancelled(true);
        }
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
        if (!authenticated) {
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

    public void setServerStore(String value) {
        serverStore = value;
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

    public PlayerVerifyTask getPlayerVerifyTask() {
        return playerVerifyTask;
    }

    public PlayerCheckExpireTask getPlayerCheckExipreTask() {
        return playerCheckExpireTask;
    }

    public CommandDeleteTask getCommandDeleteTask() {
        return commandDeleteTask;
    }

    public String getServerCurrency() {
        return serverCurrency;
    }

    public String getVersion() {
        return version;
    }

    public Settings getSettings() {
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

    public String getBuyCommand() {
        return buyCommand;
    }
    
    public HeadFile getHeadFile()
    {
        return headFile;
    }
}
