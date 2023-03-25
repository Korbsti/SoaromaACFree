package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener {
    Main plugin;

    public PlayerTeleport(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        try {
            if (e.getPlayer() instanceof Player && !e.getCause().toString().equals("UNKNOWN")) {
                
                String playerName = e.getPlayer().getName();
                PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
                
                if (playerInstance.playerEnableAntiCheat == null) {
                    playerInstance.playerEnableAntiCheat= true;
                }
                playerInstance.noFall= -1.0;
                playerInstance.hit= false;
                playerInstance.lowSpeed= 0;
                playerInstance.longJumpNum= 0.0;
                playerInstance.beforePlayerX= playerInstance.playerX;
                playerInstance.beforePlayerY= playerInstance.playerY;
                playerInstance.beforePlayerZ= playerInstance.playerZ;
                playerInstance.irrMovementSetterB= 0;
                playerInstance.beforePlayerYaw= playerInstance.playerYaw;
                playerInstance.beforePlayerPitch= playerInstance.playerPitch;
                playerInstance.irrMovementSetter= 0;
                playerInstance.stepNum= 2.0;
                playerInstance.timeBUntilFlag= 0;
                playerInstance.medianSpeedCounter.clear();
                playerInstance.medianYPos.clear();
                if (playerInstance.playerFrozen == null) {
                    playerInstance.playerFrozen= false;
                }
                if (!playerInstance.playerFrozen) {
                    playerInstance.noFall= -1.0;
                    playerInstance.longJumpNum= 0.0;
                    if (!playerInstance.teleported) {
                        plugin.death.disablerACPlayer(e.getPlayer(), plugin.disableAntiCheatXTime);
                    } else {
                        plugin.death.disablerACPlayer(e.getPlayer(), plugin.teleportSensitivity);
                        playerInstance.teleported= false;
                    }
                }
            }
        } catch (Exception eeee) {
        }
    }
}
