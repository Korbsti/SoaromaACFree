package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GetPing {
    Main plugin;

    public GetPing(Main instance) {
        plugin = instance;
    }

    public void getPlayerPing(Player player) {
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());

        try {
            Class<?> cp = Class.forName("org.bukkit.craftbukkit." + getServerVersion(plugin) + ".entity.CraftPlayer");
            Object cv = cp.cast(player);

            Object obj = cv.getClass().getMethod("getPing").invoke(cv);

            playerInstance.playerPing =  Double.valueOf(obj.toString());
        } catch (Exception e) {
            playerInstance.playerPing = 0.0;
        }
    }

    public String getServerVersion(Main instance) {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        return pkg.substring(pkg.lastIndexOf('.') + 1);
    }
}
