package net.buycraft.packages;

import java.util.ArrayList;

import net.buycraft.Plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemParser {

    private static final ItemStack nextPage;
    private static final ItemStack previousPage;
    private static final ItemStack homePage;

    static
    {
        nextPage = new ItemStack(Material.MAP);
        previousPage = new ItemStack(Material.MAP);
        homePage = new ItemStack(Material.MAP);

        setDisplayName(nextPage, ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("nextPage"));
        setDisplayName(previousPage, ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("previousPage"));
        setDisplayName(homePage, ChatColor.LIGHT_PURPLE + Plugin.getInstance().getLanguage().getString("homePage"));
    }

    private ItemParser() {}

    public static PackageCategory getCategory(ItemStack item) {
        int index = Plugin.getInstance().getLanguage().getString("packageId").length() + 6;
        String idStr = item.getItemMeta().getLore().get(0).substring(index);
        return Plugin.getInstance().getPackageManager().getPackageCategoryByNiceId(Integer.valueOf(idStr));
    }

    public static ItemStack getCategoryItem(PackageCategory c) {
       ItemStack item = new ItemStack(c.getGuiItem() != null ? c.getGuiItem() : Material.BOOK);

       setDisplayName(item, ChatColor.LIGHT_PURPLE + c.getName());
       setLore(item, c.getDescription(), new String[] {
           ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + c.getNiceId(),
           null, null, null
           });

       return item;
    }

    public static int getPackage(ItemStack item) {
        int index = Plugin.getInstance().getLanguage().getString("packageId").length() + 6;
        String idStr = item.getItemMeta().getLore().get(0).substring(index);
        return Integer.valueOf(idStr);
    }

    public static ItemStack getPackageItem(PackageModal p) {
       ItemStack item = new ItemStack(p.getMaterial() != null ? p.getMaterial() : Material.PAPER);

       setDisplayName(item, ChatColor.LIGHT_PURPLE + p.getName());
       setLore(item, p.getDescription(), new String[] {
               ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packageId") + ": " + ChatColor.LIGHT_PURPLE + p.getOrder(),
               ChatColor.YELLOW + Plugin.getInstance().getLanguage().getString("packagePrice") + ": " + ChatColor.LIGHT_PURPLE + p.getPrice() + " " + Plugin.getInstance().getServerCurrency(),
               null, null
               });

       return item;
    }

    public static ItemStack getNextPage() {
        return nextPage.clone();
    }

    public static ItemStack getPreviousPage() {
        return previousPage.clone();
    }

    public static ItemStack getHomePage() {
        return homePage.clone();
    }

    private static void setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    private static void fillLore(String description, String[] lore) {
        int loreI;
        for (loreI = 0; loreI < 4 && lore[loreI] != null; ++loreI);

        while (loreI < 4) {
            // Find the first space
            int index = description.indexOf(' ');

            // If there are no more spaces we just set the string
            if (index == -1 || description.length() < 20) {
                lore[loreI] = description;
                return;
            }
            // Find the next part of the string we can cut off
            while ((index = description.indexOf(' ', index)) < 20);
            index = description.lastIndexOf(' ', index);

            lore[loreI++] = description.substring(0, index);
            description.substring(index + 1);
        }
    }

    private static void setLore(ItemStack item, String description, String[] lines) {
        ItemMeta meta = item.getItemMeta();
        if (description != null) {
            fillLore(description, lines);
        }
        ArrayList<String> lore = new ArrayList<String>(lines.length);
        for (String str : lines) {
            if (str != null) {
                lore.add(str);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public enum ItemType {
        NEXT,
        PREV,
        HOME,
        OTHER;

        public static ItemType checkType(ItemStack item) {
            String name = item.getItemMeta().getDisplayName();

            if (name.equals(nextPage.getItemMeta().getDisplayName()))
                return NEXT;
            if (name.equals(previousPage.getItemMeta().getDisplayName()))
                return PREV;
            if (name.equals(homePage.getItemMeta().getDisplayName()))
                return HOME;
            return OTHER;
        }
    }

}
