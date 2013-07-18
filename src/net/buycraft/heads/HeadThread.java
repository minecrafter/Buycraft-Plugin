package net.buycraft.heads;

import net.buycraft.Plugin;
import net.buycraft.tasks.ReportTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HeadThread implements Runnable {

    private List<Head> headList = new ArrayList<Head>();

    private HeadFile headFile = null;

    public HeadThread(HeadFile headFile) {
        this.headFile = headFile;
        Bukkit.getScheduler().runTaskAsynchronously(headFile.plugin, this);
    }

    public void run() {
        update();
    }

    public void update() {
        List<Head> headList = new ArrayList<Head>();
        try {
            // repopulate headList
        	JSONObject apiResponse = Plugin.getInstance().getApi().paymentsAction(100, false, "");

            if (apiResponse != null && apiResponse.getInt("code") == 0) {
                JSONArray entries = apiResponse.getJSONArray("payload");
                // now iterate through the payload, if there is any
                if(entries != null && entries.length() > 0) {
                    for(int i=0; i<entries.length(); i++) {
                        JSONObject entry = entries.getJSONObject(i);
                        String ign = entry.getString("ign");
                        double price = Double.parseDouble(entry.getString("price"));
                        String currency = entry.getString("currency");
                        JSONArray p = entry.getJSONArray("packages");
                        String[] packages = new String[0];
                        if(p != null && p.length() > 0) {
                            packages = new String[p.length()];
                            for(int j=0; j<p.length(); j++) {
                                packages[j] = p.get(j).toString();
                            }
                        }
                        // load the head and add it to memory
                        Head head = new Head(ign, price, currency, packages);
                        headList.add(head);
                    }
                }
            }
            this.headList = headList;
        } catch(Exception e) {
            e.printStackTrace();
            ReportTask.setLastException(e);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
            public void run() {
                updateHeads();
            }
        });
    }

    public void updateHeads() {
        List<HeadSign> signs = headFile.getSigns();
        List<Head> heads = getHeads();
        for(HeadSign sign : signs) {
            // use this to set the HeadSign
            List<Head> head = new ArrayList<Head>();
            if(sign.getFilter() == null) {
                head.addAll(heads);
            } else {
                for(int i=0; i<heads.size(); i++) {
                    Head h = heads.get(i);
                    if(h.hasPackage(sign.getFilter())) {
                        head.add(h);
                    }
                }
            }
            // now do the Location stuff
            Location[] locs = sign.getLocation();
            for(int i=0; i<locs.length && i<head.size(); i++) {
                Location l = locs[i];
                Head h = head.get(i);
                Block b = l.getBlock();
                if(b.getState() instanceof Sign) {
                    Sign s = (Sign) b.getState();
                    h.format(s, headFile.currency);
                }
            }
        }
    }

    public List<Head> getHeads() {
        return new ArrayList<Head>(headList);
    }

}
