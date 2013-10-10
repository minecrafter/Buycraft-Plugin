package net.buycraft.util;

import java.util.List;
import java.util.Map;

import net.buycraft.Plugin;
import net.buycraft.packages.ItemParser;
import net.buycraft.packages.PackageCategory;
import net.buycraft.packages.PackageModal;
import static net.buycraft.util.BuycraftInventoryCreator.BuycraftInventoryType.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BuycraftInventoryCreator {

    private BuycraftInventoryCreator() {}

    public static void createMainMenu(InventoryHolder holder, Map<String, Inventory> inventories, List<PackageCategory> categories) {
        int pageId = 1;
        int itemsToBeMade = categories.size() + 1;
        boolean globalMade = false;
        String invTitle = Plugin.getInstance().getLanguage().getString("chooseACategory");
        for (int categoryI = 0; categoryI < categories.size(); ++categoryI) {
            // Fetch the number of slots we need
            int size = getSize(itemsToBeMade + (globalMade?0:1));

            // Create the inventory
            Inventory inv = createInventory(holder, size, pageId > 1 ? invTitle + " " + pageId : invTitle);

            // If we are adding any other buttons we need a free line of slots
            if (itemsToBeMade + (globalMade?0:1) > size) {
                size -= 9;
                // Add random items to make sure we don't put packages on the first line
                for (int i = 0; i < 9; ++i) {
                    inv.setItem(i, new ItemStack(Material.STONE));
                }
            }

            // Fill empty slots of the inventory
            for (; categoryI < categories.size(); ++categoryI) {
                // When this returns a un-empty map the current view is full
                if (!inv.addItem(ItemParser.getCategoryItem(categories.get(categoryI))).isEmpty()) {
                    break;
                }
            }

            // If we are adding any buttons we need to clear the top line of junk items
            if (itemsToBeMade + (globalMade?0:1) > size) {
                for (int i = 0; i < 9; ++i) {
                    inv.clear(i);
                }
                // Then add buttons
                placePrevAndNextPage(inv, pageId > 1, itemsToBeMade > size, false);
            }
            itemsToBeMade -= size;
            inventories.put(MAIN_MENU.toString() + pageId++, inv);
        }
    }

    public static void createPackagePages(InventoryHolder holder, Map<String, Inventory> inventories, PackageCategory category, boolean addHome) {
        int pageId = 1;
        List<PackageModal> packages = category.getPackages();
        int itemsToBeMade = packages.size();
        for (int itemI = 0; itemI < packages.size(); ++itemI) {
            // Fetch the number of slots we need
            int size = getSize(itemsToBeMade + 9);

            // Create the inventory
            Inventory inv = createInventory(holder, size, pageId > 1 ? category.getName() + " " + pageId : category.getName());

            // If we are adding any other buttons we need a free line of slots
            if (addHome || itemsToBeMade > size) {
                size -= 9;
                // Add random items to make sure we don't put packages on the first line
                for (int i = 0; i < 9; ++i) {
                    inv.setItem(i, new ItemStack(Material.STONE));
                }
            }

            // Fill empty slots of the inventory
            for (; itemI < packages.size(); ++itemI) {
                inv.addItem(ItemParser.getPackageItem(packages.get(itemI)));
            }

            // If we are adding any buttons we need to clear the top line of junk items
            if (addHome || itemsToBeMade > size) {
                for (int i = 0; i < 9; ++i) {
                    inv.clear(i);
                }
                // Then add buttons
                placePrevAndNextPage(inv, pageId > 1, itemsToBeMade > size, addHome);
            }
            itemsToBeMade -= size;
            inventories.put(CATEGORY_MENU.toString() + (addHome ? category.getNiceId() : 0) + "-" + pageId++, inv);
        }
    }

    private static void placePrevAndNextPage(Inventory inventory, boolean prevPage, boolean nextPage, boolean homePage) {
        if (prevPage) {
            inventory.setItem(nextPage ? 7 : 8, ItemParser.getPreviousPage());
        }
        if (nextPage) {
            inventory.setItem(8, ItemParser.getNextPage());
        }
        if (homePage) {
            inventory.setItem(0, ItemParser.getHomePage());
        }
    }

    private static Inventory createInventory(InventoryHolder holder, int size, String title) {
        // Fetch the inventory title prefix
        String prefix = Plugin.getInstance().getLanguage().getString("inventoryMenuPrefix") + " - ";

        if (prefix.length() + title.length() > 30) {
            if (title.length() > 30) {
                title = title.substring(0, 28) + "..";
            }
        } else {
            title = prefix + title;
        }

        return Bukkit.createInventory(holder, size, ChatColor.DARK_RED + title);
    }

    private static int getSize(int requiredSize) {
        requiredSize = 9 * (requiredSize / 9 + (requiredSize % 9 > 0 ? 1 : 0));
        if (requiredSize > 6 * 9) {
            requiredSize = 6 * 9;
        }
        return requiredSize;
    }

    public enum BuycraftInventoryType {
        MAIN_MENU,
        CATEGORY_MENU;

        public String stripType(String key) {
            return key.substring(toString().length());
        }

        public static BuycraftInventoryType getType(String key) {
            for (BuycraftInventoryType t : values()) {
                if (key.startsWith(t.toString()))
                    return t;
            }
            return null;
        }
    }
}
