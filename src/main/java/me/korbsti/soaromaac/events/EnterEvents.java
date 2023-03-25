package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class EnterEvents implements Listener {

    Main plugin;

    public EnterEvents(Main instance) {
        plugin = instance;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        plugin.death.disablerACPlayer(event.getPlayer(), 20);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        plugin.death.disablerACPlayer(event.getPlayer(), 20);
    }

    @EventHandler
    public void onVehLeave(VehicleExitEvent e) {

        if (e.getExited() instanceof Player) {
            plugin.death.disablerACPlayer((Player) e.getExited(), 20);
        }
    }

    @EventHandler
    public void onVehEnter(VehicleEnterEvent e) {
        if (e.getEntered() instanceof Player) {
            plugin.death.disablerACPlayer((Player) e.getEntered(), 20);
        }
    }
}
