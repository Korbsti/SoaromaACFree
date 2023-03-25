package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerWorldEvent {
    Main plugin;

    public PlayerWorldEvent(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void world(PlayerChangedWorldEvent e) {

        PlayerInstance playerInstance = plugin.playerInstances.get(e.getPlayer().getName());
        playerInstance.playerWorld = e.getPlayer().getWorld().getName();
    }
}
