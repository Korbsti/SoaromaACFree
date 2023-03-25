package me.korbsti.soaromaac.violations;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.api.SoaromaFlagEvent;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map.Entry;

public class Notify {
    Main plugin;

    public Notify(Main instance) {
        this.plugin = instance;
    }

    public void not(Player player, String hack, String type, String violation) {
        String name = player.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(name);
        if (!playerInstance.playerEnableAntiCheat || !player.isOnline() || player.isBanned()) {
            return;
        }
        if (playerInstance.playerPing == null) {
            playerInstance.playerPing = 0.0;
        }
        if (playerInstance.isFloodGatePlayer) {
            if (plugin.ignorePlayers.contains("bedrock")) {
                return;
            }
        } else {
            if (plugin.ignorePlayers.contains("java")) {
                return;
            }
        }

        if (playerInstance.playerNumOfViolations == null) {
            playerInstance.playerNumOfViolations = -1.0;
        }
        if (plugin.worlds.contains(playerInstance.playerWorld) || !player.isOnline()) {
            return;
        }
        if (plugin.ignoreZeroPing) {
            if (playerInstance.playerPing == 0) {
                return;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String replaced = plugin.yamlConfig.getString("messages.violationMessage").replace("{player}", name);
                String replaced1 = replaced.replace("{hack}", hack);
                String replaced2 = replaced1.replace("{num}", String.valueOf(playerInstance.num));
                String replaced3 = replaced2.replace("{ping}", playerInstance.playerPing.toString()).replace("{type}", type);
                String r = "please reset your config, something went wrong";
                r = replaced3.replace("{vioType}", violation);
                if (plugin.enablePlayerViolationLog) {
                    plugin.violationLogger.writeViolation(player, r);
                }
                if (plugin.spacedViolationMessages) {
                    playerInstance.playerNumOfViolations++;
                    if (playerInstance.playerNumOfViolations >= plugin.number) {
                        playerInstance.playerNumOfViolations = -1.0;
                        if (plugin.warnFlaggedPlayer) {
                            player.sendMessage(plugin.configMessage.returnString("warnFlaggedPlayerMessage"));
                        }
                        if (plugin.enableApi) {
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    SoaromaFlagEvent d = new SoaromaFlagEvent(plugin, player, hack);
                                    Bukkit.getPluginManager().callEvent(d);
                                }
                            });
                        }

                        Bukkit.getConsoleSender().sendMessage(plugin.hex.translateHexColorCodes("#", "/", r));
                        String str = plugin.hex.translateHexColorCodes("#", "/", r);

                        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                            if (allPlayers.hasPermission("sac.notify") && playerInstance.enableNotifications) {
                                allPlayers.sendMessage(str);
                            }
                        }

                    }
                } else {
                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaFlagEvent d = new SoaromaFlagEvent(plugin, player, hack);
                                Bukkit.getPluginManager().callEvent(d);
                            }
                        });
                    }

                    Bukkit.getConsoleSender().sendMessage(plugin.hex.translateHexColorCodes("#", "/", r));
                    for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                        if (allPlayers.hasPermission("sac.notify")) {
                            String str = allPlayers.getName();
                            if (playerInstance.enableNotifications) {
                                playerInstance.enableNotifications =  true;
                            }
                            if (playerInstance.enableNotifications) {
                                allPlayers.sendMessage(plugin.hex.translateHexColorCodes("#", "/", r));
                            }
                        }
                    }
                }

                if (plugin.enableDiscord) {
                    playerInstance.discordNum++;
                    if (playerInstance.cheatsFlagged.get(hack) == null)
                        playerInstance.cheatsFlagged.put(hack, 0);
                    playerInstance.cheatsFlagged.put(hack, playerInstance.cheatsFlagged.get(hack) + 1);
                    if (playerInstance.discordNum > plugin.discordThreshold) {

                        Location loc = playerInstance.currentLocation;

                        ArrayList<String> top = new ArrayList<String>();
                        Location location3 = loc.clone().add(0.5, 1, 0.5);
                        Material location3A = location3.getBlock().getType();
                        Location location4 = loc.clone().add(0, 1, 0.5);
                        Material location44 = location4.getBlock().getType();
                        Location location5 = loc.clone().add(-0.5, 1, 0);
                        Material location55 = location5.getBlock().getType();
                        Location location6 = loc.clone().add(0, 1, -0.5);
                        Material location66 = location6.getBlock().getType();
                        Location location7 = loc.clone().add(0.5, 1, 0); // changed from 0.5, 1, 0.5
                        Material location77 = location7.getBlock().getType();
                        Location location8 = loc.clone().add(-0.5, 1, 0.5);
                        Material location88 = location8.getBlock().getType();
                        Location location9 = loc.clone().add(0.5, 1, -0.5);
                        Material location99 = location9.getBlock().getType();
                        Location location10 = loc.clone().add(-0.5, 1, -0.5);
                        Material location100 = location10.getBlock().getType();
                        Location playerBlock1 = loc.clone().add(0, 0, 0);
                        Material playerBlock11 = playerBlock1.getBlock().getType();
                        Location playerBlock2 = loc.clone().add(0, 1, 0);
                        Material playerBlock22 = playerBlock2.getBlock().getType();
                        Location playerBlock3 = loc.clone().add(0.5, 0, 0);
                        Material playerBlock33 = playerBlock3.getBlock().getType();
                        Location playerBlock4 = loc.clone().add(-0.5, 0, 0);
                        Material playerBlock44 = playerBlock4.getBlock().getType();
                        Location playerBlock5 = loc.clone().add(0, 0, 0.5);
                        Material playerBlock55 = playerBlock5.getBlock().getType();
                        Location playerBlock6 = loc.clone().add(0, 0, -0.5);
                        Material playerBlock66 = playerBlock6.getBlock().getType();
                        Location playerBlock7 = loc.clone().add(0.5, 0, 0.5);
                        Material playerBlock77 = playerBlock7.getBlock().getType();
                        Location playerBlock8 = loc.clone().add(-0.5, 0, 0.5);
                        Material playerBlock88 = playerBlock8.getBlock().getType();
                        Location playerBlock9 = loc.clone().add(0.5, 0, -0.5);
                        Material playerBlock99 = playerBlock9.getBlock().getType();
                        Location playerBlock10 = loc.clone().add(0.5, 0, 0.5);
                        Material playerBlock100 = playerBlock10.getBlock().getType();
                        Location playerBlock110 = loc.clone().add(-0.5, 0, -0.5);
                        playerBlock110.getBlock().getType();
                        Location playerBlock2D = loc.clone().add(0, 1.5, 0);
                        Material playerBlock22D = playerBlock2D.getBlock().getType();
                        Location playerBlock3D = loc.clone().add(0.5, 1.5, 0);
                        Material playerBlock33D = playerBlock3D.getBlock().getType();
                        Location playerBlock4D = loc.clone().add(-0.5, 1.5, 0);
                        Material playerBlock44D = playerBlock4D.getBlock().getType();
                        Location playerBlock5D = loc.clone().add(0, 1.5, 0.5);
                        Material playerBlock55D = playerBlock5D.getBlock().getType();
                        Location playerBlock6D = loc.clone().add(0, 1.5, -0.5);
                        Material playerBlock66D = playerBlock6D.getBlock().getType();
                        Location playerBlock7D = loc.clone().add(0.5, 1.5, 0.5);
                        Material playerBlock77D = playerBlock7D.getBlock().getType();
                        Location playerBlock8D = loc.clone().add(-0.5, 1.5, 0.5);
                        Material playerBlock88D = playerBlock8D.getBlock().getType();
                        Location playerBlock9D = loc.clone().add(0.5, 1.5, -0.5);
                        Material playerBlock99D = playerBlock9D.getBlock().getType();
                        Location playerBlock10D = loc.clone().add(0.5, 1.5, 0.5);
                        Material playerBlock100D = playerBlock10D.getBlock().getType();
                        Location playerYHeadTop = loc.clone().add(0, 2.1, 0);
                        playerYHeadTop.getBlock().getType();
                        Location Locationplayer = loc.clone().add(0, -1, 0);
                        Material Locationplayerr = Locationplayer.getBlock().getType();
                        Location Locationplayer1 = loc.clone().add(0.5, -1, 0);
                        Material Locationplayer11 = Locationplayer1.getBlock().getType();
                        Location Locationplayer2 = loc.clone().add(-0.5, -1, 0);
                        Material Locationplayer22 = Locationplayer2.getBlock().getType();
                        Location Locationplayer3 = loc.clone().add(0, -1, 0.5);
                        Material Locationplayer33 = Locationplayer3.getBlock().getType();
                        Location Locationplayer4 = loc.clone().add(0, -1, -0.5);
                        Material Locationplayer44 = Locationplayer4.getBlock().getType();
                        Location Locationplayer5 = loc.clone().add(0.5, -1, 0.5);
                        Material Locationplayer55 = Locationplayer5.getBlock().getType();
                        Location Locationplayer6 = loc.clone().add(-0.5, -1, 0.5);
                        Material Locationplayer66 = Locationplayer6.getBlock().getType();
                        Location Locationplayer7 = loc.clone().add(0.5, -1, -0.5);
                        Material Locationplayer77 = Locationplayer7.getBlock().getType();
                        Location Locationplayer9 = loc.clone().add(-0.5, -1.0, -0.5);
                        Material Locationplayer99 = Locationplayer9.getBlock().getType();
                        Location Locationplayer1d = loc.clone().add(0.5, -2, 0);
                        Locationplayer1d.getBlock().getType();
                        Location Locationplayer2d = loc.clone().add(-0.5, -2, 0);
                        Locationplayer2d.getBlock().getType();
                        Location Locationplayer3d = loc.clone().add(0, -2, 0.5);
                        Locationplayer3d.getBlock().getType();
                        Location Locationplayer4d = loc.clone().add(0, -2, -0.5);
                        Locationplayer4d.getBlock().getType();
                        Location Locationplayer5d = loc.clone().add(0.5, -2, 0.5);
                        Locationplayer5d.getBlock().getType();
                        Location Locationplayer6d = loc.clone().add(-0.5, -2, 0.5);
                        Locationplayer6d.getBlock().getType();
                        Location Locationplayer7d = loc.clone().add(0.5, -2, -0.5);
                        Locationplayer7d.getBlock().getType();
                        Location Locationplayer8d = loc.clone().add(0.5, -2, 0.5);
                        Locationplayer8d.getBlock().getType();
                        Location Locationplayer9d = loc.clone().add(-0.5, -2, -0.5);
                        Locationplayer9d.getBlock().getType();
                        top.add(playerBlock22D + "");
                        top.add(playerBlock33D + "");
                        top.add(playerBlock44D + "");
                        top.add(playerBlock55D + "");
                        top.add(playerBlock66D + "");
                        top.add(playerBlock77D + "");
                        top.add(playerBlock88D + "");
                        top.add(playerBlock99D + "");
                        top.add(playerBlock100D + "");
                        ArrayList<String> mid = new ArrayList<String>();
                        mid.add(location3A + "");
                        mid.add(location44 + "");
                        mid.add(location55 + "");
                        mid.add(location66 + "");
                        mid.add(location77 + "");
                        mid.add(location88 + "");
                        mid.add(location99 + "");
                        mid.add(location100 + "");
                        mid.add(playerBlock22 + "");

                        ArrayList<String> mid2 = new ArrayList<String>();
                        mid2.add(playerBlock11 + "");
                        mid2.add(playerBlock33 + "");
                        mid2.add(playerBlock44 + "");
                        mid2.add(playerBlock55 + "");
                        mid2.add(playerBlock66 + "");
                        mid2.add(playerBlock77 + "");
                        mid2.add(playerBlock88 + "");
                        mid2.add(playerBlock99 + "");
                        mid2.add(playerBlock100 + "");

                        ArrayList<String> below = new ArrayList<String>();
                        below.add(Locationplayerr + "");
                        below.add(Locationplayer11 + "");
                        below.add(Locationplayer22 + "");
                        below.add(Locationplayer33 + "");
                        below.add(Locationplayer44 + "");
                        below.add(Locationplayer55 + "");
                        below.add(Locationplayer66 + "");
                        below.add(Locationplayer77 + "");
                        below.add(Locationplayer99 + "");

                        ArrayList<String> cheatsFlagged = new ArrayList<String>();
                        for (Entry<String, Integer> set : playerInstance.cheatsFlagged.entrySet()) {
                            cheatsFlagged.add(set.getKey() + " | " + set.getValue());
                        }
                        String tpss = "";
                        if (plugin.tps > 21) {
                            tpss = "N/A";
                        } else {
                            tpss = plugin.tps + "";
                        }
                        StringBuilder b = new StringBuilder();
                        for (Object j : plugin.discordYaml.getList("sendMessage")) {
                            String d = j.toString().replace("{player}", player.getName())
                                    .replace("{total-violations}", "" + plugin.violationFileYaml.getInt(player
                                            .getUniqueId() + ".violations") + "")
                                    .replace("{temp-violations}", "" + playerInstance.num)
                                    .replace("{cheats-flagged}", cheatsFlagged + "")
                                    .replace("{insideVeh}", player.isInsideVehicle() + "")
                                    .replace("{veh-name}", player.getVehicle() + "")
                                    .replace("{elytra}", player.isGliding() + "")
                                    .replace("{riptide}", player.isRiptiding() + "")
                                    .replace("{world}", player.getWorld().getName() + "")
                                    .replace("{location}", player.getLocation() + "")
                                    .replace("{top}", top + "")
                                    .replace("{mid}", mid + "")
                                    .replace("{mid2}", mid2 + "")
                                    .replace("{below}", below + "")
                                    .replace("{ping}", playerInstance.playerPing + "")
                                    .replace("{tps}", tpss)
                                    .replace("{allow-flight}", (player.isFlying() && player.getAllowFlight()) + "");

                            b.append(d + System.lineSeparator());

                        }
                        plugin.discord.sendDiscord(b.toString(), name);
                    }
                }

            }

        });

    }

    public String level(double lowThreshold, double mediumThreshold, double highThreshold, double userValue) {
        if (userValue <= lowThreshold) {
            return plugin.lowString;
        } else if (userValue > lowThreshold && userValue < highThreshold) {
            return plugin.mediumString;
        } else {
            return plugin.highString;
        }
    }

    public void notify(Player player, String hack, String type, String violation) {
        if (player.hasPermission("sac.bypass") || !player.isOnline() || player.hasPermission(("sac.bypass." + hack + type).toLowerCase().replace(" ", ""))) {
            return;
        }
        if (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore) {
            return;
        }
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
        if ((plugin.checkPingSpoofing && playerInstance.playerPing >= plugin.pingUntilPingSpoofing)
                || playerInstance.playerPing <= plugin.checkPlayerLagNum && plugin.checkPlayerLag) {
            not(player, hack, type, violation);
        } else if (!plugin.checkPlayerLag) {
            not(player, hack, type, violation);
        }
    }
}