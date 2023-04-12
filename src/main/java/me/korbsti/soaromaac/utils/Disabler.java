package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class Disabler implements Listener {
    Main plugin;

    public Disabler(Main instance) {
        this.plugin = instance;
    }

    public void disablerACPlayer(Player player, int x) {
        if (!plugin.enableAntiCheat || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore)) {
            return;
        }
        String playerName = player.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if(playerInstance == null) return;
        playerInstance.medianSpeedCounter= new ArrayList<Double>();
        playerInstance.medianYPos= new ArrayList<Integer>();
        playerInstance.hit= false;
        playerInstance.lowSpeed= 0;
        playerInstance.noFall= -1.0;
        playerInstance.hit= false;
        playerInstance.lowSpeed= 0;
        playerInstance.longJumpNum= 0.0;
        playerInstance.beforePlayerX= playerInstance.playerX;
        playerInstance.beforePlayerY= playerInstance.playerY;
        playerInstance.beforePlayerZ= playerInstance.playerZ;
        playerInstance.beforePlayerYaw= playerInstance.playerYaw;
        playerInstance.beforePlayerPitch= playerInstance.playerPitch;
        playerInstance.irrMovementSetter= 0;
        playerInstance.noFall= -1.0;
        playerInstance.hit= false;
        playerInstance.stepNum= 2.0;
        playerInstance.badPacketsANum= 0;
        playerInstance.lastSpeed= plugin.speedMaxA;
        playerInstance.slowTimerPacketB= -1;
        playerInstance.samePitchAndYaw= 0;
        playerInstance.predictedYDown= 0;
        playerInstance.semiPredBNum= 0.0;
        playerInstance.semiPredANum= 0.0;
        playerInstance.semiPredID= 200;
        playerInstance.predictedYUp= 0;

        playerInstance.playerEnableAntiCheat= false;
        playerInstance.disableAntiCheatID= x;

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        if (playerInstance.disableAntiCheatID != null) {
            scheduler.cancelTask(playerInstance.disableAntiCheatID);
        }
        playerInstance.playerEnableAntiCheat= false;
        int id = scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                playerInstance.playerEnableAntiCheat= true;
                playerInstance.disableAntiCheatID= null;
            }
        }, x);
        playerInstance.disableAntiCheatID= id;
    }

    public void countdownDisablers(Player p) {
        String playerName = p.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if (playerInstance.disableAntiCheatID == null)
            return;

        int id = playerInstance.disableAntiCheatID;

        if (id < 0) {
            if (!playerInstance.playerEnableAntiCheat)
                playerInstance.speedMboat= 10;

            playerInstance.playerEnableAntiCheat= true;
        }
        playerInstance.medianSpeedCounter= new ArrayList<Double>();
        playerInstance.medianYPos= new ArrayList<Integer>();
        playerInstance.hit= false;
        playerInstance.lowSpeed= 0;
        playerInstance.noFall= -1.0;
        playerInstance.hit= false;
        playerInstance.lowSpeed= 0;
        playerInstance.longJumpNum= 0.0;
        playerInstance.beforePlayerX= playerInstance.playerX;
        playerInstance.beforePlayerY= playerInstance.playerY;
        playerInstance.beforePlayerZ= playerInstance.playerZ;
        playerInstance.beforePlayerYaw= playerInstance.playerYaw;
        playerInstance.beforePlayerPitch= playerInstance.playerPitch;
        playerInstance.irrMovementSetter= 0;
        playerInstance.noFall= -1.0;
        playerInstance.hit= false;
        playerInstance.stepNum= 2.0;
        playerInstance.badPacketsANum= 0;
        playerInstance.slowTimerPacketB= -1;
        playerInstance.samePitchAndYaw= 0;
        playerInstance.predictedYDown= 0;
        playerInstance.semiPredBNum= 0.0;
        playerInstance.semiPredANum= 0.0;
        playerInstance.semiPredID= 200;
        playerInstance.predictedYUp= 0;
        playerInstance.irrStartupLimiter= -5;

        if (id > 0) {
            playerInstance.disableAntiCheatID= id - 1;
        }

    }

    @EventHandler
    public void playerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
			disablerACPlayer(player, plugin.disableAntiCheatXTime);
        }
    }

    @EventHandler
    public void playerDeath(PlayerRespawnEvent event) {
        disablerACPlayer(event.getPlayer(), plugin.disableAntiCheatXTime);
    }
}