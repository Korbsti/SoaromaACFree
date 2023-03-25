package me.korbsti.soaromaac;

import me.korbsti.soaromaac.utils.MovementPacket;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;

public class AsyncNearbyBoat {

    Main plugin;
    private long sleeping = 10;
    public BukkitTask task;

    public BukkitTask mainTask;
    boolean cancelled = false;

    public AsyncNearbyBoat(Main instance) {
        this.plugin = instance;
    }

    public void runAsyncThread() {
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                while (!cancelled) {
                    try {
                        Thread.sleep(sleeping);
                        if (!plugin.playerNearbyEntities.isEmpty()) {
                            if (plugin.playerNearbyEntities.get(0) == null) {
                                plugin.playerNearbyEntities.remove(0);
                                continue;
                            }
                            plugin.playerNearbyEntities.get(0).getName();
                            Player player = plugin.playerNearbyEntities.get(0);
                            String playerName = plugin.playerNearbyEntities.get(0).getName();
                            PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
                            if (!playerInstance.elytraUse) {
                                //org.spigotmc.AsyncCatcher.enabled = false;
                                // find and get the field AsyncCatcher in the class org.spigotmc
                                // set the field to false


                                Location loc = player.getLocation();
                                for (Entity e : loc.getChunk().getEntities()) {

                                    if (e != null) {
                                        if (e instanceof Boat) {
                                            if (e.getLocation().distance(loc) < 1.3) {
                                                if (EntityType.BOAT.equals(e.getType())) {
                                                    playerInstance.fluidWalkNum= 0.0;
                                                    playerInstance.jumpsOnWaterTillHacking= 0.0;
                                                    playerInstance.fluidWalkLavaNum= 0.0;
                                                    playerInstance.nearBoat= false;
                                                    playerInstance.fluidWalkNCP= 0.0;
                                                    if (!player.isInsideVehicle()) {
                                                        playerInstance.fluidWalkUntilHackingAlt= 0.0;
                                                        playerInstance.glidePlayer= 0.0;
                                                        playerInstance.badPacketsANum= 0;
                                                        playerInstance.standingOnBoat= 1;

                                                        playerInstance.alivePacketEnabler= 0.0;

                                                    } else {
                                                        playerInstance.standingOnBoat= 0;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //org.spigotmc.AsyncCatcher.enabled = true;
                            }
                            plugin.playerNearbyEntities.remove(0);

                            int size = plugin.playerNearbyEntities.size();
                            if (size > 5) {
                                sleeping = 1;
                            } else {
                                sleeping = 10;
                            }
                        }
                    } catch (Exception e) {
                        if (plugin.playerNearbyEntities.get(0) != null) {
                            plugin.playerNearbyEntities.remove(0);
                        }
                    }
                }
                //org.spigotmc.AsyncCatcher.enabled = true;
            }

        });

    }

    public void runMainAsyncThread() {
        mainTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {


                while (!cancelled) {
                    try {
                        Thread.sleep(50);

                        if (!plugin.checkMovementPackets.isEmpty()) {
                            synchronized (plugin.checkMovementPackets) {
                                Iterator<MovementPacket> iterator = plugin.checkMovementPackets.iterator();

                                while (iterator.hasNext()) {
                                    MovementPacket packet = iterator.next();
                                    plugin.movement.movementCheck(packet.getPlayer(), packet.getFromX(), packet.getFromY(), packet.getFromZ(), packet.getToX(), packet.getToY(), packet.getFromZ(), packet.getPitch(), packet.getYaw());
                                    iterator.remove();
                                }
                            }


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            }
        });


    }


}
