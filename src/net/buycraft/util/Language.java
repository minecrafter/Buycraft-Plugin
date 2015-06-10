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
        defaultProperties.put("turnChatBackOn", "Type /{{ENABLE_CHAT_COMMAND}} to turn your chat back on.");
        defaultProperties.put("packageNotFound", "Package not found.");
        defaultProperties.put("noPackagesForSale", "We currently do not have any packages for sale.");
        defaultProperties.put("toPurchase", "To purchase a package, please type");
        defaultProperties.put("howToNavigate", "Browse through our packages by using");
        defaultProperties.put("packageId", "ID");
        defaultProperties.put("packageName", "Name");
        defaultProperties.put("packagePrice", "Price");
        defaultProperties.put("pageNotFound", "Page not found.");
        defaultProperties.put("categoryMenu", "Category Menu");

        defaultProperties.put("inventoryMenuPrefix", "Buycraft");
        defaultProperties.put("chooseACategory", "Choose a category");
        defaultProperties.put("category", "Category");
        defaultProperties.put("inventoryMenuNotReady", "The inventory menu has not been loaded yet. Please wait.");
        defaultProperties.put("nextPage", "Next Page");
        defaultProperties.put("previousPage", "Previous Page");
        defaultProperties.put("homePage", "Back to categories");
        
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
