package net.buycraft.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import net.buycraft.tasks.ReportTask;

public class Language {
    private final String LOCATION = "plugins/Buycraft/language.conf";
    private File file;

    private HashMap<String, String> defaultProperties;
    private Properties properties;

    public Language() {
        this.file = new File(LOCATION);

        this.defaultProperties = new HashMap<String, String>();
        this.properties = new Properties();

        load();
        assignDefault();
    }

    private void load() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            properties.load(new FileInputStream(LOCATION));
        } catch (IOException e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
    }

    private void assignDefault() {
        Boolean toSave = false;

        defaultProperties.put("invalidBuyCommand", "Please enter the correct command parameters.");
        defaultProperties.put("urlError", "Failed to generate the shortened URL.");
        defaultProperties.put("chatEnabled", "Your chat is now enabled.");
        defaultProperties.put("chatAlreadyEnabled", "Your chat is already enabled.");
        defaultProperties.put("commandsExecuted", "Your purchased packages have been credited.");
        defaultProperties.put("pleaseVisit", "Please click the link below to continue");
        defaultProperties.put("turnChatBackOn", "Type /ec to turn your chat back on.");
        defaultProperties.put("packageNotFound", "Package not found.");
        defaultProperties.put("noPackagesForSale", "We currently do not have any packages for sale.");
        defaultProperties.put("toPurchase", "To purchase a package, please type");
        defaultProperties.put("howToNavigate", "Browse through our packages by using");
        defaultProperties.put("packageId", "ID");
        defaultProperties.put("packageName", "Name");
        defaultProperties.put("packagePrice", "Price");
        defaultProperties.put("pageNotFound", "Page not found.");
        
        defaultProperties.put("playerVerifyBegin", "Code is being verified. Please wait for code confirmation.");
        defaultProperties.put("playerVerifyNoCode", "Must provide a verification code.");
        defaultProperties.put("playerVerifySuccess", "Code verified. You will receive your package shortly.");
        defaultProperties.put("playerVerifyFailure", "Code verification failed. Check you typed the code correctly");
        defaultProperties.put("playerVerifyApiFailure", "Code verification failed. Connection to Buycraft failed.");
        
        defaultProperties.put("playerCheckExpiringBegin", "Fetcing your current packages. Please wait a moment.");
        defaultProperties.put("playerCheckExpiringApiFailure", "Couldn't check your packages. Connection to Buycraft API failed.");
        defaultProperties.put("playerCheckExpiringHeader", "Packages:");
        defaultProperties.put("playerCheckExpiringNone", "You don't have any packages");

        defaultProperties.put("commandExecuteNotEnoughFreeInventory", "%d free inventory slot(s) are required.");
        defaultProperties.put("commandExecuteNotEnoughFreeInventory2", "Please empty your inventory to receive these items.");

        for (Entry<String, String> entry : defaultProperties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!properties.containsKey(key)) {
                properties.setProperty(key, value);

                toSave = true;
            }
        }

        if (toSave) {
            try {
                properties.store(new FileOutputStream(LOCATION), "Buycraft Plugin (English language file)");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                ReportTask.setLastException(e);
            } catch (IOException e) {
                e.printStackTrace();
                ReportTask.setLastException(e);
            }
        }
    }

    public String getString(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            throw new RuntimeException("Language key '" + key + "' not found in the language.conf file.");
        }
    }
}
