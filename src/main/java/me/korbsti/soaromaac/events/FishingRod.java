package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingRod implements Listener {

    Main plugin;

    public FishingRod(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void fish(PlayerFishEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                String str = event.getPlayer().getName();
                PlayerInstance playerInstance = plugin.playerInstances.get(str);
                if (playerInstance.alivePacketEnabler != null && !event.getPlayer().hasPermission("sac.bypass") && (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE)) {

                    playerInstance.alivePacketEnabler--;
                    playerInstance.playerEventNumber--;

                }

                if (event.getCaught() instanceof Player caught) {
                    if (caught.hasPermission("sac.bypass")) {
                        return;
                    }
                    plugin.death.disablerACPlayer(caught, 30);
                }

            }


        });
    }
}
