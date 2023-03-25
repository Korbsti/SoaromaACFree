package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class StopCommands implements Listener {
    Main plugin;

    public StopCommands(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent e) {

        PlayerInstance playerInstance = plugin.playerInstances.get(e.getPlayer().getName());

        if (playerInstance.playerFrozen == null) {
            playerInstance.playerFrozen = false;
        }
        if (playerInstance.playerFrozen) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.configMessage.returnString("frozenPlayerCommandBlocked"));
        }
    }
}
