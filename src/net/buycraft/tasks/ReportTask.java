package net.buycraft.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import net.buycraft.Plugin;
import net.buycraft.api.ApiTask;
import net.buycraft.util.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportTask extends ApiTask {

    private static final String googleAddress = "www.google.com";
    private static final String apiAddress = "api.buycraft.net";
    private static final String isGdAddress = "www.is.gd";
    private static final String apiAccessPath = "http://" + apiAddress + "/ok.php";

    private static boolean running = false;
    private static Exception lastException = null;

    private final CommandSender sender;

    public static void setLastException(Exception e) {
        lastException = e;
    }

    public static boolean call(CommandSender sender) {
        if (running) {
            if (sender instanceof Player) {
                sender.sendMessage(Chat.header());
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.seperator() + ChatColor.RED + "A report is already being generated. Please wait.");
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.footer());
            } else {
                Plugin.getInstance().getLogger().warning("A report is already being generated. Please wait.");
            }

            return false;
        }
        
        Plugin.getInstance().addTask(new ReportTask(sender));
        return running = true;
    }
    
    private ReportTask(CommandSender sender) {
        this.sender = sender;
    }

    public void run() {
        try {
            Plugin.getInstance().getLogger().info("Starting generation of a report");

            String date = new Date().toString();;

            String os = System.getProperty("os.name") + " | " + System.getProperty("os.version") + " | " + System.getProperty("os.arch");
            String javaVersion = System.getProperty("java.version") + " | " + System.getProperty("java.vendor");
            String serverVersion = Bukkit.getBukkitVersion();
            String serverName = Bukkit.getServer().getName();
            String serverIP = Bukkit.getIp();
            int serverPort = Bukkit.getPort();
            String buycraftVersion = Plugin.getInstance().getVersion();

            boolean isAuthenticated = Plugin.getInstance().isAuthenticated(null);
            double lastPackageCheckerExecutionTime = (System.currentTimeMillis() - CommandFetchTask.getLastExecution()) / 1000.0 / 60.0;
            String lastPackageCheckerExecution = CommandFetchTask.getLastExecution() != 0 ? lastPackageCheckerExecutionTime + " minutes ago": "Never";


            String pingGoogle = pingCheck(googleAddress);
            String pingApi = pingCheck(apiAddress);
            
            String serviceCheck = checkOkay();

            writeReport(parseData(
                    "#### System Information ####", '\n',
                    "Date: ", date, '\n',
                    "Operating system: ", os, '\n',
                    "Java Version: ", javaVersion, '\n',
                    "Server Version: ", serverVersion, '\n',
                    "Server Name: ", serverName, "\n",
                    "Server IP: ", serverIP, ":", serverPort, "\n",
                    "Buycraft Version: ", buycraftVersion, '\n',
                    '\n',
                    "#### Buycraft Info ####", '\n',
                    "Store URL: " , Plugin.getInstance().getServerStore(), '\n',
                    "Store ID: " , Plugin.getInstance().getServerID(), '\n',
                    "Buy Command: ", Plugin.getInstance().getBuyCommand(), '\n',
                    "Authenticated: ", isAuthenticated, '\n',
                    "Error code: ", Plugin.getInstance().getAuthenticatedCode(), '\n',
                    "Last command execution: ", lastPackageCheckerExecution, '\n',
                    '\n',
                    "#### Connection ####", '\n',
                    "Google Ping Result: ", pingGoogle, '\n',
                    "Buycraft API Ping Result: ", pingApi, '\n',
                    "Buycraft API Status Result: ", serviceCheck, '\n',
                    '\n',
                    "#### Performance ####", '\n',
                    "Long Running Command: ", Plugin.getInstance().getCommandExecutor().getLastLongRunningCommand(), '\n',
                    '\n',
                    "#### Last Exception ####", '\n',
                    lastException != null ? lastException : "No errors since startup"
                    ));
        } catch (Throwable e) {
            if (sender instanceof Player) {
                sender.sendMessage(Chat.header());
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.seperator() + ChatColor.RED + "Error occured when generating the report");
                sender.sendMessage(Chat.seperator() + ChatColor.RED + "See console for more information");
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.footer());
            }

            Plugin.getInstance().getLogger().warning("Error occured when generating the report");

            e.printStackTrace();
        } finally {
            running = false;
        }
    }

    /**
     * Parses data we want to put into the report
     * <p>
     * If an object is null a new line is added.</br>
     * If an exception is found its error and stack trace are added</br>
     */
    private String[] parseData(Object ...data) {
        ArrayList<String> lines = new ArrayList<String>(data.length);

        for (Object o : data) {
            if (o == null) {
                lines.add("\n");
            } else if (o instanceof Exception) {
                Exception e = (Exception)  o;

                lines.add(e.getClass().toString());

                if (e.getMessage() != null)
                    lines.add('\n' + e.getMessage());
                lines.add("\nStackTrace:\n");
                
                for (StackTraceElement stack : e.getStackTrace()) {
                    lines.add(stack.toString() + '\n');
                }
            } else {
                lines.add(o.toString());
            }
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Writes each string to the report file and notifies the sender of its success/failure
     * @throws IOException 
     */
    private void writeReport(String ...data) throws IOException {
        File report = new File(Plugin.getInstance().getDataFolder(), "report.txt");

            FileWriter fw = new FileWriter(report);
            BufferedWriter bw = new BufferedWriter(fw);

            try {
                for (String str : data) {
                    bw.write(str);
                }
            } finally {
                bw.close();
            }

            if (sender instanceof Player) {
                sender.sendMessage(Chat.header());
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.seperator() + ChatColor.GREEN + "Report written to " + report.getPath());
                sender.sendMessage(Chat.seperator());
                sender.sendMessage(Chat.footer());
            }
            Plugin.getInstance().getLogger().info("Report written to " + report.getPath());
    }

    private String pingCheck(String url) {
        Socket socket = null;

        try {
            socket = new Socket(url, 80);
            return "Connected to " + url + " successfully.";
        } catch (UnknownHostException e) {
            return "Could not resolve host " + url;
        } catch (IOException e) {
            return "Could not create socket " + url;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }

    private String checkOkay() {
        Scanner s = null;

        try {
            URL url = new URL(apiAccessPath);
            s = new Scanner(url.openStream());

            return s.hasNextLine() ? "Response from API - " + s.nextLine() : "No response from API";
        } catch (IOException e) {
            return "Failed to connect to the API - " + e.getClass().toString() + " | " + e.getMessage();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
