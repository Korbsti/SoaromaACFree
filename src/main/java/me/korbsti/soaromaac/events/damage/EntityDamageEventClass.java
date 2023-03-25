package me.korbsti.soaromaac.events.damage;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class EntityDamageEventClass implements Listener {
    Main plugin;

    private final HashMap<String, Integer> schID = new HashMap<>();

    public EntityDamageEventClass(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            String playerName = e.getEntity().getName();
            PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
            if (playerInstance.lastFallDamage == null) {
                playerInstance.lastFallDamage = true;
            }
            if ("FALL".equals(e.getCause().toString())) {
                playerInstance.lastFallDamage = true;
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                if (schID.get(playerName) != null) {
                    scheduler.cancelTask(schID.get(playerName));
                }
                int id = scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.lastFallDamage = false;
                        schID.put(playerName, null);
                    }
                }, 30);
                schID.put(playerName, id);
            }
        }
    }
}
