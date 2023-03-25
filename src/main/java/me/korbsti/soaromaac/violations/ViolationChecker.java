package me.korbsti.soaromaac.violations;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.api.SoaromaAutoKick;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Date;

public class ViolationChecker {
    Main plugin;

    public ViolationChecker(Main instance) {
        this.plugin = instance;
    }

    public void check(Player player, Integer number, boolean teleportPlayer) {
        String playerName = player.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if (!player.isOnline() || player.isBanned()) {

            if (playerInstance.num != null) {
                playerInstance.num--;
            }
            return;
        }

        if (playerInstance.isFloodGatePlayer) {
            if (plugin.ignorePlayers.contains("bedrock")) {
                playerInstance.num--;
                return;
            }
        } else {
            if (plugin.ignorePlayers.contains("java")) {
                playerInstance.num--;

                return;
            }
        }

        if (plugin.worlds.contains(playerInstance.playerWorld)) {
            playerInstance.num--;
            return;
        }
        if (plugin.ignoreZeroPing) {
            if (playerInstance.playerPing == 0) {
                playerInstance.num--;
                return;
            }
        }
        plugin.totalViolations += 1;
        if (plugin.cancelEventIfHacking) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (teleportPlayer && playerInstance.cancelX != null) {
                        double fromX = playerInstance.cancelX;
                        double fromY = playerInstance.cancelY;
                        double fromZ = playerInstance.cancelZ;
                        Location loc = new Location(player.getWorld(), fromX, fromY, fromZ);
                        if (fromY > 1 && loc.getBlock().isPassable() && loc.add(0, 1, 0).getBlock().isPassable()) {
                            playerInstance.teleported=true;
                            player.teleport(new Location(player.getWorld(), fromX, fromY, fromZ));
                            playerInstance.elytraFlightNum=plugin.elytraFlightUntilHacking - 50;
                        }
                        playerInstance.setCancelled=false;
                    }
                }
            });
        }
        if (plugin.enableViolationLogger) {
            if (playerInstance.violationLoggerHash >= plugin.violationLoggerNum) {
                try {
                    int newNum = plugin.violationFileYaml.getInt(player.getUniqueId() + ".violations") + playerInstance.violationLoggerHash;
                    plugin.violationFileYaml.set(player.getUniqueId() + ".violations", newNum);
                    plugin.violationFileYaml.set(player.getUniqueId() + ".ip", String.valueOf(player.getAddress()));
                    plugin.violationFileYaml.set(player.getUniqueId() + ".name", player.getName());
                    plugin.violationFileYaml.save(plugin.violationFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerInstance.violationLoggerHash=0;
            }
            playerInstance.violationLoggerHash++;
        }

        if (playerInstance.num >= plugin.violationKickNumUntilKick && plugin.autoViolationKick) {
            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                if (allPlayers.hasPermission("sac.notifykick")) {
                    String kickFormattedInGame = String.format(plugin.yamlConfig.getString("messages.kickMessageInGame"), playerName);
                    allPlayers.sendMessage(plugin.hex.translateHexColorCodes("#", "/", kickFormattedInGame));
                }
            }
            if (playerInstance.playerNumKicked == null) {
                playerInstance.playerNumKicked=0.0;
            }
            playerInstance.playerNumKicked++;
            if (plugin.enableAutoBan && !plugin.useUsageForAutoBan && playerInstance.playerNumKicked >= plugin.numPlayerKickUntilBan) {
                String kickFormattedInGame = String.format(plugin.yamlConfig.getString("messages.banMessageInGame"), playerName);
                for (Player allPlayers2 : Bukkit.getOnlinePlayers()) {
                    if (allPlayers2.hasPermission("sac.notifykick")) {
                        allPlayers2.sendMessage(plugin.hex.translateHexColorCodes("#", "/", kickFormattedInGame));
                    }
                }
                Bukkit.getLogger().info(plugin.hex.translateHexColorCodes("#", "/", kickFormattedInGame));
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        BanList banList = Bukkit.getBanList(Type.NAME);
                        Date number = new Date((long) (System.currentTimeMillis() + 60 * 60 * 1000 * plugin.yamlConfig.getDouble("flag-system.enableAutoBan.autoBanTime")));
                        banList.addBan(playerName, plugin.yamlConfig.getString("messages.banMessage"), number, null);
                        playerInstance.playerNumKicked=0.0;
                    }
                });

            } else if (plugin.enableAutoBan && plugin.useUsageForAutoBan && playerInstance.playerNumKicked >= plugin.numPlayerKickUntilBan) {
                String kickFormattedInGame = String.format(plugin.yamlConfig.getString("messages.banMessageInGame"), playerName);
                for (Player allPlayers2 : Bukkit.getOnlinePlayers()) {
                    if (allPlayers2.hasPermission("sac.notifykick")) {
                        allPlayers2.sendMessage(plugin.configMessage.returnString(kickFormattedInGame));
                    }
                }
                Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', kickFormattedInGame));
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString("flag-system.enableAutoBan.usage").replace("{player}", playerName));
                        playerInstance.playerNumKicked=0.0;
                    }
                });

            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (plugin.enableKickLogger) {
                        plugin.violationLogger.kickWriter(player);
                    }
                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaAutoKick d = new SoaromaAutoKick(plugin, player);
                                Bukkit.getPluginManager().callEvent(d);
                                if (!d.getCancelled()) {
                                    if (!plugin.yamlConfig.getBoolean("flag-system.useUsageForAutoKick")) {
                                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.yamlConfig.getString("messages.kickMessage")));
                                    } else {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString("flag-system.usageKick").replace("{player}", playerName));
                                    }
                                    playerInstance.num=0;
                                    plugin.totalKicks += 1;
                                }

                            }
                        });
                    } else {
                        if (!plugin.yamlConfig.getBoolean("flag-system.useUsageForAutoKick")) {
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.yamlConfig.getString("messages.kickMessage")));
                        } else {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString("flag-system.usageKick").replace("{player}", playerName));
                        }
                        playerInstance.num=0;
                        plugin.totalKicks += 1;
                    }

                }
            }, 0);
        }
    }

    public void violationChecker(Player player, Integer number, boolean teleportPlayer, String hack, String type) {
        if (!player.isOnline()) {
            return;
        }
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
        if (player.hasPermission(("sac.bypass." + hack + type).toLowerCase().replace(" ", "")) || player.hasPermission("sac.bypass")) {
            String playerName = player.getName();
            playerInstance.num--;
            return;
        }

        if (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore) {
            return;
        }
        if ((plugin.checkPingSpoofing && playerInstance.playerPing >= plugin.pingUntilPingSpoofing) || playerInstance.playerPing <= plugin.checkPlayerLagNum && plugin.checkPlayerLag) {
            try {
                check(player, number, teleportPlayer);
            } catch (Exception ee) {
            }
        } else if (!plugin.checkPlayerLag) {
            try {
                check(player, number, teleportPlayer);
            } catch (Exception ee) {
            }
        }
    }
}
