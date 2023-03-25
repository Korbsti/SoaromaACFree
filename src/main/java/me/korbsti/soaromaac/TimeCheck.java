package me.korbsti.soaromaac;

import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;

public class TimeCheck {
    Main plugin;

    public TimeCheck(Main main) {
        plugin = main;
    }

    public void irregularMoveEventTimerB(Player p, String playerName) {

        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);

        if (!playerInstance.playerEnableAntiCheat) {
            return;
        }
        if (playerInstance.playerEventNumber == null || playerInstance.playerFrozen == null) {
            playerInstance.playerFrozen=false;
            return;
        }
        if (playerInstance.playerFrozen) {
            return;
        }

        if (playerInstance.slowTimerPacketB == null) {
            playerInstance.slowTimerPacketB=0;
        }

        if (playerInstance.slowTimerPacketB >= plugin.irregularNumSampleNumB) {
            long time = Instant.now().toEpochMilli() - playerInstance.slowTimerInstantB.toEpochMilli();
            if (time >= plugin.irregularTimeCountB && !playerInstance.isFloodGatePlayer) {

                Location currentLocation = p.getLocation();
                Location previousLocation = playerInstance.timerBLocation;
                double dis = Math.sqrt(Math.pow(currentLocation.getX() - previousLocation.getX(), 2) + Math.pow(currentLocation.getY() - previousLocation.getY(), 2) + Math.pow(currentLocation.getZ() - previousLocation.getZ(), 2));
                if (playerInstance.timeBUntilFlag >= 3) {
                    plugin.notify.notify(p, plugin.message.type(2), plugin.message.cheat(2), plugin.notify.level(
                            plugin.roundedThresholdLow, plugin.roundedThresholdMedium, plugin.roundedThresholdHigh,
                            time));
                    plugin.violationChecker.violationChecker(p, playerInstance.num++, false, plugin.message.type(2), plugin.message.cheat(2));
                    playerInstance.timeBUntilFlag=0;
                } else if (dis > plugin.irregularNumSampleNumB / 6.0) {
                    playerInstance.timeBUntilFlag++;
                } else {
                    playerInstance.timeBUntilFlag=0;
                }
            } else {
                playerInstance.timeBUntilFlag=0;
            }

            playerInstance.slowTimerPacketB=0;
        }

        if (playerInstance.slowTimerPacketB <= 0) {
            playerInstance.slowTimerInstantB=Instant.now();
            playerInstance.timerBLocation=p.getLocation();
        }

        playerInstance.slowTimerPacketB++;

    }

    public void irregularMoveEvent(String playerName, int x) {
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if (!playerInstance.playerEnableAntiCheat) {
            return;
        }
        if (playerInstance.playerEventNumber == null || playerInstance.playerFrozen == null) {
            playerInstance.playerFrozen=false;
            return;
        }
        if (playerInstance.playerFrozen) {
            return;
        }

        if (playerInstance.alivePacketEnabler == null) {
            playerInstance.alivePacketEnabler=-1.0;
        }
        if (playerInstance.alivePacketEnabler >= plugin.irregularNumSampleNum) {
            playerInstance.currentAlivePacket=Instant.now();
            long time = playerInstance.currentAlivePacket.toEpochMilli() - playerInstance.beforeAlivePacket.toEpochMilli();
            if (playerInstance.isFloodGatePlayer) {
                if (Bukkit.getPlayer(playerName).getVehicle() != null) {
                    time += 3000;
                }
            }

            if (playerInstance.playerEventNumber >= plugin.irregularCheckNumUntilHacking && time <= plugin.irregularTimeCount) {
                playerInstance.userPlaceholder=x;
                playerInstance.timerFlag=true;
                playerInstance.playerTime=time;
            }
            playerInstance.playerEventNumber=0.0;
            playerInstance.alivePacketEnabler=0.0;
        }
        if (playerInstance.alivePacketEnabler <= 1.0) {
            playerInstance.beforeAlivePacket=Instant.now();
        }
        playerInstance.alivePacketEnabler++;
        playerInstance.playerEventNumber++;

    }

    public boolean irregularPlaceEvent(String playerName) {
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if (!playerInstance.playerEnableAntiCheat) {
            return false;
        }
        if (playerInstance.playerFrozen == null) {
            playerInstance.playerFrozen=false;
            return false;
        }
        if (playerInstance.placedBlockCounter == null) {
            playerInstance.placedBlockCounter=0.0;
        }
        if (playerInstance.playerFrozen) {
            return false;
        }
        if (playerInstance.beforeBlock == null) {
            playerInstance.beforeBlock=Instant.now();
        }
        if (playerInstance.placedBlockCounter >= plugin.fastPlaceSampleNum) {
            playerInstance.currentBlock=Instant.now();
            long time = playerInstance.currentBlock.toEpochMilli() - playerInstance.beforeBlock.toEpochMilli();
            if (time <= plugin.fastPlaceFlagNum) {
                return true;
            }
            playerInstance.placedBlockCounter=0.0;
            playerInstance.currentBlock=null;
            playerInstance.beforeBlock=null;
        }
        playerInstance.placedBlockCounter++;
        return false;
    }
}
