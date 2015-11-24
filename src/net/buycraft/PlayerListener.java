package net.buycraft;

import net.buycraft.packages.PackageModal;
import net.buycraft.util.Chat;
import net.buycraft.util.SavedBlockLocation;
import net.buycraft.util.SignSelector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerListener implements Listener {
    private Plugin plugin;

    public PlayerListener() {
        this.plugin = Plugin.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getChatManager().enableChat(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getChatManager().isDisabled(event.getPlayer())) {
            event.setCancelled(true);

            String message = "";
            message += Chat.header() + "\n";
            message += Chat.seperator() + "\n";
            message += Chat.seperator() + ChatColor.RED + plugin.getLanguage().getString("turnChatBackOn") + "\n";
            message += Chat.seperator() + "\n";
            message += Chat.footer();

            event.getPlayer().sendMessage(message);

        } else {
            plugin.getChatManager().clearPlayerSet(event.getRecipients());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getPlayer().getName().equalsIgnoreCase("Buycraft")) {
            event.disallow(Result.KICK_OTHER, "This user has been disabled due to security reasons.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignClick(PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block != null && (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
            Sign sign = (Sign) block.getState();

            if(ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Buycraft]")){

                SavedBlockLocation savedBlockLocation = SavedBlockLocation.fromLocation(block.getLocation(), true);

                if(!plugin.signSelector.signs.containsKey(savedBlockLocation)){
                    event.getPlayer().sendMessage(Chat.header() + "\n" + Chat.seperator() + ChatColor.RED + "Unknown buycraft sign. Delete this sign and add a new one." + "\n" + Chat.footer());
                    return;
                }

                int packageId = plugin.signSelector.signs.get(savedBlockLocation);

                plugin.getBuyUi().showPackage(event.getPlayer(), packageId);

            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {

        Player player = event.getPlayer();

        if(event.getLine(0).equalsIgnoreCase("[buycraft]")){
            event.setLine(0, ChatColor.BLUE + "[Buycraft]");
        }else{
            return;
        }

        Boolean isValidFormat = true;

        int packageId = -1;

        if(event.getLine(1).length() == 0){
            isValidFormat = false;
        }{
            try{

                packageId = Integer.parseInt(event.getLine(1));

            }catch(NumberFormatException ex){
                isValidFormat = false;
            }
        }

        if(!isValidFormat){
            event.getPlayer().sendMessage("Invalid sign layout! Try this:");
            event.getPlayer().sendMessage("-----------------------------");
            event.getPlayer().sendMessage("-              [Buycraft]");
            event.getPlayer().sendMessage("-             {Package Id}");
            event.getPlayer().sendMessage("-        {Optional Message}");
            event.getPlayer().sendMessage("-----------------------------");
            return;
        }

        List<PackageModal> packages = Plugin.getInstance().getPackageManager().getPackagesForSale();

        for (PackageModal row : packages) {

            if(row.getOrder() == packageId){

                event.setLine(1, row.getName());

                plugin.signSelector.saveSign(event.getBlock().getLocation(), packageId);

                player.sendMessage(Chat.header() + "\n" + Chat.seperator() + ChatColor.GREEN + "Saved new Buycraft sign" + "\n" + Chat.footer());

                return;
            }
        }

        player.sendMessage("Could not find package!");

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignDestroy(BlockBreakEvent event) {

        Block block = event.getBlock();

        if (block != null && (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {

            SavedBlockLocation savedBlockLocation = SavedBlockLocation.fromLocation(block.getLocation(), true);

            if(plugin.signSelector.signs.get(savedBlockLocation) != null){
                plugin.signSelector.deleteSign(block.getLocation());

                event.getPlayer().sendMessage(Chat.header() + "\n" + Chat.seperator() + ChatColor.RED + "Removed Buycraft sign" + "\n" + Chat.footer());

            }

        }
    }
}
