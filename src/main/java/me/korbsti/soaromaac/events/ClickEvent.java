package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.ArrayList;

public class ClickEvent implements Listener {
    Main plugin;

    public ClickEvent(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void click(PlayerInteractEvent e) {
        Player player;
        player = e.getPlayer();
        String playerName;
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
        playerName = e.getPlayer().getName();
        if (playerInstance.playerChecking == null) {
            playerInstance.playerChecking= 0.0;
        }
        if (playerInstance.playerRunning == null) {
            playerInstance.playerChecking= 1.0;
        }
        if (!plugin.enableAntiCheat || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore) || player.hasPermission("sac.bypass")) {
            return;
        }
        if (playerInstance.slowTimerPacketB != null && playerInstance.slowTimerInstantB != null) {
            playerInstance.slowTimerPacketB++;
        }

        if (playerInstance.alivePacketEnabler != null) {
            playerInstance.alivePacketEnabler--;
        }

        if (plugin.checkGhostHand) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    if (e.getClickedBlock() != null) {
                        double clickX = e.getClickedBlock().getX();
                        double clickY = e.getClickedBlock().getY();
                        double clickZ = e.getClickedBlock().getZ();

                        double playerX = e.getPlayer().getEyeLocation().getBlockX();
                        double playerY = e.getPlayer().getEyeLocation().getBlockY();
                        double playerZ = e.getPlayer().getEyeLocation().getBlockZ();
                        double dd = Math.sqrt(Math.pow(clickX - playerX, 2.0) + Math.pow(clickY - playerY, 2.0) + Math.pow(clickZ - playerZ, 2.0)) - 1;

                        Vector dir = e.getPlayer().getEyeLocation().getDirection();
                        ArrayList<Material> mat = new ArrayList<>();
                        for (double dis = 0; dis < dd; dis += 0.5) {
                            mat.add(e.getPlayer().getEyeLocation().toVector().clone().add(dir.clone().multiply(dis)).toLocation(e.getPlayer().getWorld()).add(0, -0.5, 0).getBlock().getType());
                        }
                        for (int x = 0; x != mat.size(); x++) {
                            if (mat.get(x).isSolid() && !mat.get(x).toString().contains("SLAB")) {
                                plugin.notify.notify(player, plugin.message.type(31), plugin.message.cheat(1), plugin.highString);
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(31), plugin.message.cheat(1));
                                if (plugin.cancelEventIfHacking) {
                                    e.setCancelled(true);
                                }
                                if (plugin.debugMode) {
                                    Bukkit.broadcastMessage("No internal config, checkGhostHand was set to true");
                                }

                            }
                        }
                    }

                }

            });

        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().toString().contains("SHULKER")) {
                for (Entity entity : e.getClickedBlock().getLocation().getWorld().getNearbyEntities(e.getClickedBlock().getLocation(), 1.5, 1.5, 1.5)) {
                    if (entity instanceof Player) {
                        playerInstance.nearbyShulkerBox =  15;
                    }
                }
            }
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (plugin.checkElytraFlight) {
                if (player.getInventory().getItemInMainHand().toString().contains("FIREWORK_ROCKET")) {
                    playerInstance.elytraFlightNum= 0;
                    playerInstance.elytraFlightB= 0;
                    playerInstance.elytraFlightNum= 0;
                    playerInstance.elytraMaxYCounter= 0;
                    playerInstance.elytraSecondCounter= 0;
                }
                if (player.getInventory().getItemInOffHand().toString().contains("FIREWORK_ROCKET")) {
                    playerInstance.elytraFlightNum= 0;
                    playerInstance.elytraFlightB= 0;
                    playerInstance.elytraFlightNum= 0;
                    playerInstance.elytraMaxYCounter= 0;
                    playerInstance.elytraSecondCounter=0;
                }
            }
        }

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (playerInstance.playerEnableAntiCheat == null) {
                playerInstance.playerEnableAntiCheat= true;
                return;
            }
            if (!playerInstance.playerEnableAntiCheat) {
                return;
            }
            if (plugin.checkAutoClickerA) {
                if (playerInstance.playerClicks == null) {
                    playerInstance.playerClicks= 0.0;
                }
                playerInstance.playerClicks++;

                if (playerInstance.playerChecking == null) {
                    playerInstance.playerChecking= 0.0;
                }
                if (playerInstance.playerRunning == null) {
                    playerInstance.playerRunning= 0.0;
                }

                if (playerInstance.playerChecking == 0.0 && playerInstance.playerRunning == 1.0) {
                    playerInstance.playerChecking= 1.0;
                }
                if (playerInstance.playerChecking == 1.0) {
                    playerInstance.playerChecking= 3.0;
                    playerInstance.playerRunning= 2.0;
                    BukkitScheduler scheduler = plugin.getServer().getScheduler();
                    scheduler.runTaskLaterAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (playerInstance.playerClicks == null) {
                                playerInstance.playerClicks= 0.0;
                            }
                            if (playerInstance.playerClicks > plugin.CPSUntilHacking) {
                                plugin.notify.notify(player, plugin.message.type(33), plugin.message.cheat(1), plugin.notify.level(plugin.CPSUntilHacking + plugin.roundedThresholdLow, plugin.CPSUntilHacking + plugin.roundedThresholdMedium, plugin.CPSUntilHacking + plugin.roundedThresholdHigh, playerInstance.autoClickerBFlag));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(33), plugin.message.cheat(1));
                                if (plugin.cancelEventIfHacking) {
                                    e.setCancelled(true);
                                }
                                if (plugin.debugMode) {
                                    Bukkit.broadcastMessage("Exceeded CPSUntilHacking");
                                }
                            }

                            playerInstance.playerClicks= 0.0;
                            playerInstance.playerChecking= 0.0;
                            playerInstance.playerRunning= 1.0;
                        }
                    }, 20);
                }
            }
            if (plugin.checkAutoClickerB) {
                if (playerInstance.autoClickerB == null) {
                    playerInstance.autoClickerB= Instant.now();
                }

                if (playerInstance.autoClickerBTemp == null)
                    playerInstance.autoClickerBTemp= 0L;

                if (playerInstance.autoClickerBTemp >= 2) {
                    Long time = Instant.now().toEpochMilli() - playerInstance.autoClickerB.toEpochMilli();
                    if (playerInstance.autoClickBInner != null) {

                        boolean containsPickaxeOrAxe = false;
                        ItemStack mainHandStack = player.getInventory().getItemInMainHand();
                        ItemStack offHandStack = player.getInventory().getItemInOffHand();

                        if (mainHandStack != null) {
                            if (mainHandStack.getType().toString().contains("AXE"))
                                containsPickaxeOrAxe = true;
                        }
                        if (offHandStack != null) {
                            if (offHandStack.getType().toString().contains("AXE"))
                                containsPickaxeOrAxe = true;
                        }

                        if (Math.abs(playerInstance.autoClickBInner - time) < plugin.autoClickerBms) {
                            if (playerInstance.autoClickerBFlag > plugin.autoClickerBLimit && containsPickaxeOrAxe) {
                                plugin.notify.notify(player, plugin.message.type(33), plugin.message.cheat(2), plugin.notify.level(plugin.autoClickerBLimit + plugin.roundedThresholdLow, plugin.autoClickerBLimit + plugin.roundedThresholdMedium, plugin.autoClickerBLimit + plugin.roundedThresholdHigh, playerInstance.autoClickerBFlag));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(33), plugin.message.cheat(2));
                                if (plugin.cancelEventIfHacking) {
                                    e.setCancelled(true);
                                }
                                if (plugin.debugMode) {
                                    Bukkit.broadcastMessage("Exceeded autoClickerBLimit");
                                }

                            }
                            playerInstance.autoClickerBFlag++;
                        } else {
                            playerInstance.autoClickerBFlag= (long) 0;
                        }
                    }

                    playerInstance.autoClickBInner= time;
                    playerInstance.autoClickerB= null;
                    playerInstance.autoClickerBTemp= (long) 0;
                }

                playerInstance.autoClickerBTemp++;

            }

        }
    }
}