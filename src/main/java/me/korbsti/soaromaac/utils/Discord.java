package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;

public class Discord {
    Main plugin;

    public Discord(Main instance) {
        plugin = instance;
    }

    public void sendDiscord(String str, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    PlayerInstance playerInstance = plugin.playerInstances.get(name);
                    playerInstance.discordNum = 0;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("content", str);
                    HttpsURLConnection connection = (HttpsURLConnection) plugin.url.openConnection();
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("User-Agent", "Java-DiscordWebhook");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");

                    OutputStream stream = connection.getOutputStream();
                    stream.write(jsonObject.toJSONString().getBytes());
                    stream.flush();
                    stream.close();

                    connection.getInputStream().close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }
}
