package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockEvents implements Listener {
    protected static final int String = 0;
    Main plugin;

    public BlockEvents(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (!plugin.enableAntiCheat || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("sac.bypass")) {
            return;
        }
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
        if (playerInstance.alivePacketEnabler != null) {
            playerInstance.alivePacketEnabler--;
        }

        String playerName = player.getName();
        if (plugin.checkAutoClickerA) {
            if (playerInstance.playerClicks != null) playerInstance.playerClicks--;
        }
        if (plugin.checkAutoClickerB) {
            if (playerInstance.autoClickerBFlag != null) playerInstance.autoClickerBFlag--;
        }
        if (plugin.checkXray) {
            if (player.getGameMode() != GameMode.SURVIVAL) {
                return;
            }

            boolean added = true;
            boolean wasBlock = true;
            for (String str : plugin.xrayMaterials) {
                if (event.getBlock().getType() == Material.getMaterial(str.split(" ")[0])) {
                    wasBlock = false;
                    if (event.getBlock().getLocation().getY() < Integer.valueOf(str.split(" ")[3])) {
                        playerInstance.blocksMined.add(event.getBlock().getType());
                        added = false;
                        break;
                    }
                }

            }
            if (added && wasBlock) {
                playerInstance.blocksMined.add(event.getBlock().getType());
            }
            if (playerInstance.startAsyncTaskXRAY) {

                playerInstance.startAsyncTaskXRAY = false;
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.startAsyncTaskXRAY = true;
                        HashMap<String, Integer> blockRatio = new HashMap<>();
                        HashMap<String, Integer> blockMineLimit = new HashMap<>();
                        HashMap<String, Integer> blockDisregardLimit = new HashMap<>();
                        for (String str : plugin.xrayMaterials) {
                            String bl = str.split(" ")[0];
                            blockRatio.put(bl, 0);
                            blockMineLimit.put(bl, Integer.valueOf(str.split(" ")[1]));
                            blockDisregardLimit.put(bl, Integer.valueOf(str.split(" ")[2]));
                        }
                        int y = 0;

                        for (Material mat : playerInstance.blocksMined) {
                            for (String str : plugin.xrayMaterials) {
                                String block = str.split(" ")[0];
                                if (mat == Material.getMaterial(block)) {
                                    blockRatio.put(block, blockRatio.get(block) + 1);

                                } else {
                                    y++;
                                }
                            }
                        }

                        for (Map.Entry<String, Integer> set : blockRatio.entrySet()) {
                            if (blockRatio.get(set.getKey()) > blockMineLimit.get(set.getKey()) && y > blockDisregardLimit.get(set.getKey())) {
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.xrayYaml.getString("cheat"), "A");
                                plugin.notify.notify(player, plugin.xrayYaml.getString("cheat"), "A", plugin.notify.level(plugin.roundedThresholdLow + blockMineLimit.get(set.getKey()), plugin.roundedThresholdMedium + blockMineLimit.get(set.getKey()), plugin.roundedThresholdHigh + blockMineLimit.get(set.getKey()), blockRatio.get(set.getKey())));
                                if (plugin.debugMode) {
                                    Bukkit.broadcastMessage("Exceeded first row and second row, first row: " + blockRatio.get(set.getKey()) + " | second row: " + y + " | Lower than third Y axis | block was " + set.getKey());
                                }
                            }
                        }

                        playerInstance.blocksMined = new ArrayList<Material>();

                    }

                }, plugin.xrayTimer);

            }
        }

        if (plugin.checkReachBlockBreak && event.getPlayer() instanceof Player) {
            Location locationDamager = player.getLocation();
            double LocationDamagerX = locationDamager.getX();
            double LocationDamagerY = locationDamager.getY();
            double LocationDamagerZ = locationDamager.getZ();
            double locationBlockX = event.getBlock().getX();
            double locationBlockY = event.getBlock().getY();
            double locationBlockZ = event.getBlock().getZ();
            double range;
            range = Math.sqrt(Math.pow(LocationDamagerX - locationBlockX, 2) + Math.pow(LocationDamagerY - locationBlockY, 2) + Math.pow(LocationDamagerZ - locationBlockZ, 2));

            if (range > plugin.reachBlockBreakNum && event.getBlock().getType() != Material.SCAFFOLDING) {
                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(26), plugin.message.cheat(1));
                plugin.notify.notify(player, plugin.cheatNames.get(26), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.reachBlockBreakNum, plugin.roundedThresholdMedium + plugin.reachBlockBreakNum, plugin.roundedThresholdHigh + plugin.reachBlockBreakNum, range));
                if (plugin.cancelEventIfHacking) {
                    event.setCancelled(true);
                }
                if (plugin.debugMode) {
                    Bukkit.broadcastMessage("Exceeded reachBlockBreakNum");
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (!plugin.enableAntiCheat || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore)) {
            return;
        }
        if (event.getPlayer() instanceof Player) {
            Player player = event.getPlayer();
            PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
            if (player.hasPermission("sac.bypass")) {
                return;
            }
            if (plugin.checkAutoClickerB) {
                playerInstance.autoClickerBFlag = (long) 0;
            }
            if (plugin.checkFastPlace) {
                Boolean bool = plugin.timeCheck.irregularPlaceEvent(player.getName());
                if (bool) {
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(27), plugin.message.cheat(1));
                    plugin.notify.notify(player, plugin.cheatNames.get(27), plugin.message.cheat(1), plugin.highString);
                    if (plugin.cancelEventIfHacking) {
                        event.setCancelled(true);
                    }
                    if (plugin.debugMode) {
                        Bukkit.broadcastMessage("Exceeded fastPlaceSampleNum and was lower than or equal too fastPlaceFlagNum");
                    }
                }
            }
            if (plugin.irrPlacement) {
                if (event.getBlockPlaced().getLocation().add(1.1, 0, 0).getBlock().getType() == Material.AIR && event.getBlockPlaced().getLocation().add(0.0, 0, 1.1).getBlock().getType() == Material.AIR && event.getBlockPlaced().getLocation().add(-0.5, 0, 0).getBlock().getType() == Material.AIR && event.getBlockPlaced().getLocation().add(0, 0, -0.5).getBlock().getType() == Material.AIR && event.getBlockPlaced().getLocation().add(0, 1.1, 0).getBlock().getType() == Material.AIR && event.getBlockPlaced().getLocation().add(0, -0.5, 0).getBlock().getType() == Material.AIR && event.getBlockPlaced().getType() != Material.POWDER_SNOW) {

                    plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(18), plugin.message.cheat(1));
                    plugin.notify.notify(player, plugin.cheatNames.get(18), plugin.message.cheat(1), plugin.highString);
                    if (plugin.cancelEventIfHacking) {
                        event.setCancelled(true);
                    }
                    if (plugin.debugMode) {
                        Bukkit.broadcastMessage("Exceeded irrPlacement, no internal config for this cheat other than checking for this cheat");
                    }
                }
            }
            if (plugin.checkReachBlockPlace) {
                Location locationDamager = player.getLocation();
                double LocationDamagerX = locationDamager.getX();
                double LocationDamagerY = locationDamager.getY();
                double LocationDamagerZ = locationDamager.getZ();
                double locationBlockX = event.getBlock().getX();
                double locationBlockY = event.getBlock().getY();
                double locationBlockZ = event.getBlock().getZ();
                double range;
                range = Math.sqrt(Math.pow(LocationDamagerX - locationBlockX, 2) + Math.pow(LocationDamagerY - locationBlockY, 2) + Math.pow(LocationDamagerZ - locationBlockZ, 2));
                if (range >= plugin.reachBlockPlaceNum && event.getBlock().getType() != Material.SCAFFOLDING) {
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(25), plugin.message.cheat(1));
                    plugin.notify.notify(player, plugin.cheatNames.get(25), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.reachBlockPlaceNum, plugin.reachBlockPlaceNum + plugin.roundedThresholdMedium, plugin.reachBlockPlaceNum + plugin.roundedThresholdHigh, range));
                    if (plugin.cancelEventIfHacking) {
                        event.setCancelled(true);
                    }
                    if (plugin.debugMode) {
                        Bukkit.broadcastMessage("Exceeded reachBlockPlaceNum");
                    }
                }
            }
        }
    }
}