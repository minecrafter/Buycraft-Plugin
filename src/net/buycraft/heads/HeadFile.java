package net.buycraft.heads;

import net.buycraft.Plugin;
import net.buycraft.util.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HeadFile {

    // config values
    boolean enabled = false;
    boolean currency = false;
    int update = 5;

    Plugin plugin;
    private YamlConfiguration config = null;
    private File file = null;

    private List<HeadSign> signs = new ArrayList<HeadSign>();
    HeadThread thread = null;
    HeadListener listener = null;

    public HeadFile(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "heads.yml");
        this.config = new YamlConfiguration();

        checkConfig();
        registerEvents();
        
        try {
            if(enabled) {
                onEnable();
                loadSigns();
                
                thread = new HeadThread(this);
                
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, thread, 20*60*update, 20*60*update);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        plugin.getCommand("buysign").setExecutor(listener);
    }

    private void registerEvents() {
        listener = new HeadListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private void checkConfig() {
        Settings settings = plugin.getSettings();
        enabled = settings.getString("headsEnabled").equals("true");
        currency = settings.getString("headsCurrency").equals("true");
        update = 30;
    }

    private void onEnable() throws Exception {
        if(file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if(!file.exists()) {
            file.createNewFile();
        }
        config = new YamlConfiguration();
        config.load(file);
    }

    public List<HeadSign> getSigns() {
        return signs;
    }

    public void addSign(HeadSign h) {
        List<String> signs = config.getStringList("signs");
        if(signs == null) {
            signs = new ArrayList<String>();
        }
        signs.add(h.serialize());
        config.set("signs", signs);
        try {
            config.save(file);
            // and cleanly load the signs again
            loadSigns();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSigns() throws JSONException {
        // clear current signs if not empty, so we can re-use this method safely
        if(!this.signs.isEmpty()) {
            this.signs.clear();
        }
        List<String> signs = config.getStringList("signs");
        if(signs != null && signs.size() > 0) {
            for(String sign : signs) {
                // deserialize and add to the list
                HeadSign h = HeadSign.deserialize(sign);
                this.signs.add(h);
            }
        }
    }

}
