package net.buycraft.heads;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class HeadListener implements Listener, CommandExecutor {

    public static final BlockFace[] FACES = {BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    private final HeadFile headFile;

    private Map<String, List<Location>> cache = new HashMap<String, List<Location>>();
    private Map<String, String> filter = new HashMap<String, String>();

    public Set<String> protections = new HashSet<String>();

    public HeadListener(HeadFile headFile) {
        this.headFile = headFile;
    }

    public boolean isProtected(Block block) {
        if(headFile.getSigns().isEmpty()) {
            return false;
        }
        if(protections.size() != 0) {
            return protections.contains(HeadSign.getLocation(block.getLocation()));
        }
        // iterate through heads
        for(HeadSign head : headFile.getSigns()) {
            // and faces
            Location[] locations = head.getLocation();
            for(BlockFace face : FACES) {
                Location l = block.getRelative(face).getLocation();
                for(Location loc : locations) {
                    if(loc.getWorld().getName().equals(l.getWorld().getName()) &&
                            loc.getBlockX() == l.getBlockX() &&
                            loc.getBlockY() == l.getBlockY() &&
                            loc.getBlockZ() == l.getBlockZ()) {
                        protections.add(HeadSign.getLocation(block.getLocation()));
                    }
                }
                l = block.getRelative(face).getRelative(BlockFace.UP).getLocation();
                for(Location loc : locations) {
                    if(loc.getWorld().getName().equals(l.getWorld().getName()) &&
                            loc.getBlockX() == l.getBlockX() &&
                            loc.getBlockY() == l.getBlockY() &&
                            loc.getBlockZ() == l.getBlockZ()) {
                        protections.add(HeadSign.getLocation(block.getLocation()));
                    }
                }
            }
        }
        return protections.contains(HeadSign.getLocation(block.getLocation()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // do nothing where nothing happens
        if(!(sender.hasPermission("buycraft.admin") || sender.hasPermission("buycraft.signs"))) {
            sender.sendMessage("You don't have permission to do that.");
            return true;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage("You aren't a player, go away!");
            return true;
        }
        Player player = (Player) sender;
        if(args.length < 1 || args.length > 2) {
            return false;
        }
        // filtering
        if(args.length == 2 && args[0].equalsIgnoreCase("filter")) {
            filter.put(player.getName(), args[1]);
            player.sendMessage("Filtering signs based on '"+args[1]+"'");
            return true;
        } else if(args.length == 2) {
            return false;
        }
        // the rest of the commands
        if(args[0].equalsIgnoreCase("begin")) {
            cache.put(player.getName(), new ArrayList<Location>());
            player.sendMessage("Sign detection begun, punch the signs to add them to the list");
            return true;
        } else if(args[0].equalsIgnoreCase("end")) {
            if(cache.containsKey(player.getName())) {
                protections.clear(); // clear protections so things are recached
                // now process adding to the thing and update all the heads, hurrah!
                List<Location> blocks = this.cache.remove(player.getName());
                String filter = this.filter.remove(player.getName());
                // put to file and update
                Location[] l = new Location[blocks.size()];
                l = blocks.toArray(l);
                this.headFile.addSign(new HeadSign(l, filter));
                player.sendMessage("Sign detection ended, updating signs...");
                headFile.thread.updateHeads();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // filter out non-clicked block events
        if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        if(!(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        if(event.getPlayer().hasPermission("buycraft.admin") || event.getPlayer().hasPermission("buycraft.signs")) {
            if(this.cache.containsKey(event.getPlayer().getName())) {
                // cancel event
                event.setCancelled(true);
                // that way they can't break the block
                List<Location> locations = cache.get(event.getPlayer().getName());
                Location l = event.getClickedBlock().getLocation();
                boolean add = true;
                // locations.contains(l);
                for(Location loc : locations) {
                    if(loc.getWorld().getName().equals(l.getWorld().getName()) &&
                            loc.getBlockX() == l.getBlockX() &&
                            loc.getBlockY() == l.getBlockY() &&
                            loc.getBlockZ() == l.getBlockZ()) {
                        add = false;
                    }
                }
                if(add) {
                    locations.add(l);
                    event.getPlayer().sendMessage("Sign added to list!");
                } else {
                    event.getPlayer().sendMessage("Sign already in the list!");
                }
            }
        } else if(isProtected(event.getClickedBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED+"That block is protected!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission("buycraft.admin") || event.getPlayer().hasPermission("buycraft.signs")) {
            // do nothing
        } else if(isProtected(event.getBlockPlaced())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED+"That block is protected!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission("buycraft.admin") || event.getPlayer().hasPermission("buycraft.signs")) {
            // do nothing
        } else if(isProtected(event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED+"That block is protected!");
        }
    }
}
