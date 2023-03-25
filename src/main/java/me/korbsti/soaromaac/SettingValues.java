package me.korbsti.soaromaac;

import me.korbsti.soaromaac.utils.MovementPacket;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SettingValues {
    Main plugin;

    public SettingValues(Main instance) {
        this.plugin = instance;
    }

    /**
     * Public float yawType(float yaw) { if (yaw > 360) { return yaw - (360 * (int)
     * (yaw / 360)); } else if (yaw < 0) { if (yaw < -360) { return -yaw - (360 *
     * (int) (-yaw / 360)); } return -yaw; } return yaw; }.
     */

    public void setting(Player player, Double x, Double y, Double z, Float yaw, Float pitch) {
        try {
            String playerName = player.getName();
            PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
            if (playerInstance.playerSetter == null) {
                playerInstance.playerSetter= -1.0;
                playerInstance.playerX= x;
                playerInstance.playerY= y;
                playerInstance.playerZ= z;
                playerInstance.playerYaw= yaw;
                playerInstance.playerPitch= pitch;
                playerInstance.beforePlayerX= x;
                playerInstance.beforePlayerY= y;
                playerInstance.beforePlayerZ= z;
                playerInstance.beforePlayerYaw= yaw;
                playerInstance.beforePlayerPitch= pitch;
            }
            if (playerInstance.playerSetter >= 1.0) {
                playerInstance.beforePlayerX= x;
                playerInstance.beforePlayerY= y;
                playerInstance.beforePlayerZ= z;
                playerInstance.beforePlayerYaw= yaw;
                playerInstance.beforePlayerPitch= pitch;
                playerInstance.playerSetter= 0.0;

                double toX = playerInstance.playerX;
                double toY = playerInstance.playerY;
                double toZ = playerInstance.playerZ;
                double fromX = playerInstance.beforePlayerX;
                double fromY = playerInstance.beforePlayerY;
                double fromZ = playerInstance.beforePlayerZ;

                if (toY != fromY || toX != fromX || toZ != fromZ) {
                    synchronized (plugin.checkMovementPackets) {
                        plugin.checkMovementPackets.add(new MovementPacket(player, toX, toY, toZ, fromX, fromY, fromZ, pitch, yaw));
                    }
                    playerInstance.playerSpeed= Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2) + Math.pow(toZ - fromZ, 2)) * 10;
                    playerInstance.currentLocation= new Location(player.getWorld(), fromX, fromY, fromZ);
                }
            }

            playerInstance.playerX= x;
            playerInstance.playerY= y;
            playerInstance.playerZ= z;
            if (yaw != 0.0) {
                playerInstance.playerYaw= yaw;
            }
            playerInstance.playerPitch= pitch;
            playerInstance.playerSetter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}