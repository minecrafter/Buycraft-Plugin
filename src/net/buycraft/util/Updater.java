package net.buycraft.util;

import net.buycraft.Plugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Updater {
    private Plugin plugin;

    public Updater() {
        this.plugin = Plugin.getInstance();
    }

    public void download(String latestDownloadUrl) {
        plugin.getLogger().info("Please wait, downloading latest version...");

        loadAllClasses();

        try {
            File local = new File("plugins/Buycraft.jar");

            OutputStream outputStream = new FileOutputStream(local);

            URL url = new URL(latestDownloadUrl);
            URLConnection connection = url.openConnection();

            InputStream inputStream = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int read = 0;

            while ((read = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.close();
            inputStream.close();

            plugin.getLogger().info("Installed latest version, please restart to apply changes.");
        } catch (IOException e) {
            plugin.getLogger().info("Failed to download new version. " + e.getLocalizedMessage());
        }
    }

    private void loadAllClasses() {
        try {
            JarFile jar = new JarFile(plugin.getJarFile());

            Enumeration<JarEntry> enumeration = jar.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();

                if (name.endsWith(".class")) {
                    String path = name.replaceAll("/", ".");
                    path = path.substring(0, path.length() - ".class".length());

                    plugin.getClass().getClassLoader().loadClass(path);
                }
            }

            jar.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
