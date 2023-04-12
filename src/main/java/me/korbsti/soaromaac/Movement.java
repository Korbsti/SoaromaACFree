package me.korbsti.soaromaac;

import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class Movement {
    Main plugin;

    public Movement(Main instance) {
        this.plugin = instance;
    }


    public void irrEvent(Player player, boolean usingElytra) {
        PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
        String playerName = player.getName();
        String typer = "";
        if (usingElytra) {
            if (playerInstance.playerTime > plugin.maxElytraMili) {
                return;
            }
        }

        if (playerInstance.playerTime > plugin.irregularEventCountLow) {
            typer = plugin.lowString;
        } else if (playerInstance.playerTime > plugin.irregularEventCountLow && playerInstance.playerTime < plugin.irregularEventCountHigh) {
            typer = plugin.mediumString;
        } else {
            typer = plugin.highString;
        }
        switch (playerInstance.userPlaceholder) {
            case 1:
                plugin.notify.notify(player, plugin.message.type(1), plugin.message.cheat(1), typer);
                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(1), plugin.message.cheat(1));
                if (plugin.debugMode) {
                    plugin.message.Messages(1, 2, 3);
                }
                playerInstance.timerFlag= false;
                break;
            case 2:
                plugin.notify.notify(player, plugin.message.type(2), plugin.message.cheat(1), typer);
                plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(2), plugin.message.cheat(1));
                if (plugin.debugMode) {
                    plugin.message.Messages(1, 2, 3);
                }
                playerInstance.timerFlag= false;
                break;
        }
    }

    public void movementCheck(Player player, double fromX, double fromY, double fromZ, double toX, double toY, double toZ, float pitch, float yaw) {
        try {
            PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
            if(playerInstance == null) return;
            String playerName = player.getName();
            if (playerInstance.playerPing == null)
                playerInstance.playerPing= 0.0;

            plugin.getPing.getPlayerPing(player);
            GameMode gamemode = player.getGameMode();
            plugin.death.countdownDisablers(player);
            if (!playerInstance.playerEnableAntiCheat) {
                playerInstance.noFall= -1.0;
                playerInstance.beforePlayerX= toX;
                playerInstance.beforePlayerY= toY;
                playerInstance.beforePlayerZ= toZ;
                playerInstance.irrMovementSetterB= 0;
                playerInstance.irrMovementSetter= 0;
                playerInstance.longJumpFromZ= toZ;
                playerInstance.longJumpNum= 0.0;
                playerInstance.stepNum= 2.0;
                playerInstance.badPacketsANum= 0;
                return;
            }

            if (playerInstance.playerFrozen) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.setCancelled= true;
                        player.teleport(new Location(player.getWorld(), playerInstance.cancelX, playerInstance.cancelY, playerInstance.cancelZ));
                    }
                }, 0);
                return;
            }

            if (gamemode == GameMode.CREATIVE || gamemode == GameMode.SPECTATOR || !player.isOnline()) {
                playerInstance.beforeGamemodeCreative= 1;
                return;
            }
            if (playerInstance.beforeGamemodeCreative == 1 && gamemode == GameMode.SURVIVAL || playerInstance.beforeGamemodeCreative == 1 && gamemode == GameMode.ADVENTURE) {
                playerInstance.beforeGamemodeCreative= 0;
                plugin.death.disablerACPlayer(player, 30);
                return;

            }
            if (player.getPlayer().hasPermission("sac.bypass")) {
                return;
            }
            if (!plugin.enableAntiCheat || !(player.getPlayer() instanceof Player)) {
                return;
            }
            if (player.hasPotionEffect(PotionEffectType.LEVITATION) && !player.hasPermission("sac.bypass") && plugin.checkFly && !playerInstance.elytraUse) {
                playerInstance.numFly= 0.0;
                playerInstance.numFlyCheck= 0.0;
                if (toY < fromY) {
                    if (playerInstance.numFlyCheckLev == null) {
                        playerInstance.numFlyCheckLev= 0.0;
                    }
                    playerInstance.numFlyCheckLev= playerInstance.numFlyCheckLev + 1.0;
                    if (playerInstance.numFlyCheckLev >= plugin.levitationDownUntilHacking) {
                        plugin.notify.notify(player, plugin.message.type(3), plugin.message.cheat(3),
                                plugin.notify.level(plugin.levitationDownUntilHacking
                                        + plugin.roundedThresholdLow,
                                        plugin.levitationDownUntilHacking
                                                + plugin.roundedThresholdMedium,
                                        plugin.levitationDownUntilHacking
                                                + plugin.roundedThresholdHigh,
                                        playerInstance.numFlyCheckLev));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(3), plugin.message.cheat(3));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(23, 1, 1);
                        }
                    }
                }
                if (toY > fromY) {
                    playerInstance.numFlyCheckLev= 0.0;
                }
            }
            Location loc = playerInstance.currentLocation;

            Location Locationplayerd = loc.clone().add(0, -2, 0);
            Material Locationplayerrd = Locationplayerd.getBlock().getType();
            boolean isElytra = player.isGliding();

            try {
                if (isElytra || player.isRiptiding() || (player.isFlying() && player.getAllowFlight())) {
                    playerInstance.elytraUseScheduler= 80;
                    playerInstance.elytraUse= true;
                    if (isElytra) {
                        playerInstance.isUsingElytra= true;
                    }

                } else if (playerInstance.elytraUseScheduler != 0) {
                    playerInstance.elytraUseScheduler--;

                } else {
                    playerInstance.elytraUse= false;
                    playerInstance.isUsingElytra= false;
                }
            } catch (Exception e) {
                playerInstance.elytraUseScheduler= 0;
            }
            boolean elytra = playerInstance.isUsingElytra;
            if (playerInstance.elytraUse) {
                playerInstance.usedElytra= 40;
                elytra = true;
            } else if (playerInstance.usedElytra != 0) {
                playerInstance.usedElytra--;
                elytra = true;
            }
            if (playerInstance.timerFlag && playerInstance.notInsideBoat <= 0 && playerInstance.standingOnBoat == 0) {
                irrEvent(player, elytra);
            }

            if (player.hasPotionEffect(PotionEffectType.LEVITATION) && player.getPotionEffect(PotionEffectType.LEVITATION).getAmplifier() >= 10) {
                plugin.death.disablerACPlayer(player, 50);
            }

            ArrayList<Location> locations = new ArrayList<Location>();
            Location location3 = loc.clone().add(0.5, 1, 0.5);
            Material location3A = location3.getBlock().getType();
            Location location4 = loc.clone().add(0, 1, 0.5);
            Material location44 = location4.getBlock().getType();
            Location location5 = loc.clone().add(-0.5, 1, 0);
            Material location55 = location5.getBlock().getType();
            Location location6 = loc.clone().add(0, 1, -0.5);
            Material location66 = location6.getBlock().getType();
            Location location7 = loc.clone().add(0.5, 1, 0); // changed from
            // 0.5, 1,
            // 0.5
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
            Material playerBlock111 = playerBlock110.getBlock().getType();
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
            Location playerBlock110D = loc.clone().add(-0.5, 1.5, -0.5);
            Material playerBlock111D = playerBlock110D.getBlock().getType();
            Location locationPlayerbHop = loc.clone().add(0, -0.1, 0);
            Material locationPlayerbHopMaterial = locationPlayerbHop.getBlock().getType();
            Location Locationplayerfluid = loc.clone().add(0, -0.4, 0);
            Material Locationplayerrfluid = Locationplayerfluid.getBlock().getType();
            Location Locationplayerfluid1 = loc.clone().add(0.5, -0.4, 0.5);
            Material Locationplayerrfluid1 = Locationplayerfluid1.getBlock().getType();
            Location Locationplayerfluid2 = loc.clone().add(0, -0.4, 0.5);
            Material Locationplayerrfluid2 = Locationplayerfluid2.getBlock().getType();
            Location Locationplayerfluid3 = loc.clone().add(-0.5, -0.4, 0);
            Material Locationplayerrfluid3 = Locationplayerfluid3.getBlock().getType();
            Location Locationplayerfluid4 = loc.clone().add(0, -0.4, -0.5);
            Material Locationplayerrfluid4 = Locationplayerfluid4.getBlock().getType();
            Location Locationplayerfluid5 = loc.clone().add(0.5, -0.4, 0.5);
            Material Locationplayerrfluid5 = Locationplayerfluid5.getBlock().getType();
            Location Locationplayerfluid6 = loc.clone().add(-0.5, -0.4, 0.5);
            Material Locationplayerrfluid6 = Locationplayerfluid6.getBlock().getType();
            Location Locationplayerfluid7 = loc.clone().add(0.5, -0.4, -0.5);
            Material Locationplayerrfluid7 = Locationplayerfluid7.getBlock().getType();
            Location Locationplayerfluid8 = loc.clone().add(-0.5, -0.4, -0.5);
            Material Locationplayerrfluid8 = Locationplayerfluid8.getBlock().getType();
            Location playerYHeadTop = loc.clone().add(0, 2.1, 0);
            Material LocationplayerYTop = playerYHeadTop.getBlock().getType();
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
            Material Locationplayer11d = Locationplayer1d.getBlock().getType();
            Location Locationplayer2d = loc.clone().add(-0.5, -2, 0);
            Material Locationplayer22d = Locationplayer2d.getBlock().getType();
            Location Locationplayer3d = loc.clone().add(0, -2, 0.5);
            Material Locationplayer33d = Locationplayer3d.getBlock().getType();
            Location Locationplayer4d = loc.clone().add(0, -2, -0.5);
            Material Locationplayer44d = Locationplayer4d.getBlock().getType();
            Location Locationplayer5d = loc.clone().add(0.5, -2, 0.5);
            Material Locationplayer55d = Locationplayer5d.getBlock().getType();
            Location Locationplayer6d = loc.clone().add(-0.5, -2, 0.5);
            Material Locationplayer66d = Locationplayer6d.getBlock().getType();
            Location Locationplayer7d = loc.clone().add(0.5, -2, -0.5);
            Material Locationplayer77d = Locationplayer7d.getBlock().getType();
            Location Locationplayer8d = loc.clone().add(0.5, -2, 0.5);
            Material Locationplayer88d = Locationplayer8d.getBlock().getType();
            Location Locationplayer9d = loc.clone().add(-0.5, -2, -0.5);
            Material Locationplayer99d = Locationplayer9d.getBlock().getType();
            Material locationYTop1 = loc.clone().add(0.5, 2.1, 0.5).getBlock().getType();
            Material locationYTop2 = loc.clone().add(0, 2.1, 0.5).getBlock().getType();
            Material locationYTop3 = loc.clone().add(-0.5, 2.1, 0).getBlock().getType();
            Material locationYTop4 = loc.clone().add(0, 2.1, -0.5).getBlock().getType();
            Material locationYTop5 = loc.clone().add(0.5, 2.1, 0.5).getBlock().getType();
            Material locationYTop6 = loc.clone().add(-0.5, 2.1, 0.5).getBlock().getType();
            Material locationYTop7 = loc.clone().add(0.5, 2.1, 0.5).getBlock().getType();
            Material locationYTop8 = loc.clone().add(0.5, 2.1, -0.5).getBlock().getType();
            Material locationYTop9 = loc.clone().add(-0.5, 2.1, -0.5).getBlock().getType();

            Material corner1 = loc.clone().add(0.5, 0, 0.5).getBlock().getType();
            Material corner2 = loc.clone().add(0.5, 0, -0.5).getBlock().getType();
            Material corner3 = loc.clone().add(-0.5, 0, 0.5).getBlock().getType();
            Material corner4 = loc.clone().add(-0.5, 0, -0.5).getBlock().getType();

            if (plugin.enableMovementReplay) {
                locations.add(location3);
                locations.add(location4);
                locations.add(location5);
                locations.add(location6);
                locations.add(location7);
                locations.add(location8);
                locations.add(location9);
                locations.add(location10);
                locations.add(playerBlock1);
                locations.add(playerBlock2);
                locations.add(playerBlock3);
                locations.add(playerBlock4);
                locations.add(playerBlock5);
                locations.add(playerBlock6);
                locations.add(playerBlock7);
                locations.add(playerBlock8);
                locations.add(playerBlock9);
                locations.add(playerBlock10);
                locations.add(playerBlock110);
                locations.add(playerBlock2D);
                locations.add(playerBlock3D);
                locations.add(playerBlock4D);
                locations.add(playerBlock5D);
                locations.add(playerBlock6D);
                locations.add(playerBlock7D);
                locations.add(playerBlock8D);
                locations.add(playerBlock9D);
                locations.add(playerBlock110);
                locations.add(locationPlayerbHop);
                locations.add(Locationplayerfluid);
                locations.add(Locationplayerfluid1);
                locations.add(Locationplayerfluid2);
                locations.add(playerBlock4D);
                locations.add(playerBlock5D);
                locations.add(playerBlock6D);
                locations.add(playerBlock7D);
                locations.add(playerBlock8D);
                locations.add(playerBlock9D);
                locations.add(locationPlayerbHop);
                locations.add(Locationplayerfluid);
                locations.add(Locationplayerfluid1);
                locations.add(Locationplayerfluid2);
                locations.add(Locationplayerfluid3);
                locations.add(Locationplayerfluid4);
                locations.add(Locationplayerfluid3);
                locations.add(Locationplayerfluid4);
                locations.add(Locationplayerfluid5);
                locations.add(Locationplayerfluid6);
                locations.add(Locationplayerfluid7);
                locations.add(Locationplayerfluid8);
                locations.add(playerYHeadTop);
                locations.add(Locationplayer);
                locations.add(Locationplayer1);
                locations.add(Locationplayer2);
                locations.add(Locationplayer3);
                locations.add(Locationplayer4);
                locations.add(Locationplayer5);
                locations.add(Locationplayer6);
                locations.add(Locationplayer7);
                locations.add(Locationplayer9);
                locations.add(Locationplayer1d);
                locations.add(Locationplayer2d);
                locations.add(Locationplayer3d);
                locations.add(Locationplayer4d);
                locations.add(Locationplayer5d);
                locations.add(Locationplayer6d);
                locations.add(Locationplayer7d);
                locations.add(Locationplayer8d);
                locations.add(Locationplayer9d);
                locations.add(loc.clone().add(0.5, 2.1, 0.5));
                locations.add(loc.clone().add(0, 2.1, 0.5));
                locations.add(loc.clone().add(-0.5, 2.1, 0));
                locations.add(loc.clone().add(0, 2.1, -0.5));
                locations.add(loc.clone().add(0.5, 2.1, 0.5));
                locations.add(loc.clone().add(-0.5, 2.1, 0.5));
                locations.add(loc.clone().add(0.5, 2.1, 0.5));
                locations.add(loc.clone().add(0.5, 2.1, -0.5));
                locations.add(loc.clone().add(-0.5, 2.1, -0.5));

                locations.add(loc.clone().add(0.5, 0, 0.5));
                locations.add(loc.clone().add(0.5, 0, -0.5));
                locations.add(loc.clone().add(-0.5, 0, 0.5));
                locations.add(loc.clone().add(-0.5, 0, -0.5));

                for (Location locactionThing : locations) {
                    if (locactionThing.getBlock().getType().isSolid()) {
                        plugin.fileManager.writePlayerBlockData(player, locactionThing.getBlockX(), locactionThing.getBlockY(), locactionThing.getBlockZ());
                    }
                }
                plugin.fileManager.writePlayerMovementData(player, toX, toY, toZ);
            }
            boolean notNearLiquid = true;
            boolean shulker = true;
            boolean nearSlowableBlock = true;
            boolean notAllowedFlight = true;
            boolean isntRiptiding = false;

            boolean isntLevitating = true;
            boolean topWeb = true;
            boolean waterColm = locationYTop1 == Material.BUBBLE_COLUMN || locationYTop2 == Material.BUBBLE_COLUMN || locationYTop3 == Material.BUBBLE_COLUMN || locationYTop4 == Material.BUBBLE_COLUMN || locationYTop5 == Material.BUBBLE_COLUMN || locationYTop6 == Material.BUBBLE_COLUMN || locationYTop7 == Material.BUBBLE_COLUMN || locationYTop8 == Material.BUBBLE_COLUMN || locationYTop9 == Material.BUBBLE_COLUMN || corner1 == Material.BUBBLE_COLUMN || corner2 == Material.BUBBLE_COLUMN || (corner3 == Material.BUBBLE_COLUMN) || (corner4 == Material.BUBBLE_COLUMN);
            boolean topNotLiquid;
            topNotLiquid = locationYTop1 == Material.WATER || locationYTop2 == Material.WATER || locationYTop3 == Material.WATER || locationYTop4 == Material.WATER || locationYTop5 == Material.WATER || locationYTop6 == Material.WATER || locationYTop7 == Material.WATER || locationYTop8 == Material.WATER || locationYTop9 == Material.WATER || locationYTop1 == Material.LAVA || locationYTop2 == Material.LAVA || locationYTop3 == Material.LAVA || locationYTop4 == Material.LAVA || locationYTop5 == Material.LAVA || locationYTop6 == Material.LAVA || locationYTop7 == Material.LAVA || locationYTop8 == Material.LAVA || locationYTop9 == Material.LAVA;
            boolean isNearSolidBlock = Locationplayerr != Material.getMaterial("AIR") || Locationplayer11 != Material.getMaterial("AIR") || Locationplayer22 != Material.AIR || Locationplayer33 != Material.AIR || Locationplayer44 != Material.AIR || Locationplayer55 != Material.AIR || Locationplayer66 != Material.AIR || Locationplayer77 != Material.AIR || Locationplayer99 != Material.AIR || playerBlock11 != Material.AIR || playerBlock22 != Material.AIR || playerBlock33 != Material.AIR || playerBlock44 != Material.AIR || playerBlock55 != Material.AIR || playerBlock66 != Material.AIR || playerBlock77 != Material.AIR || playerBlock88 != Material.AIR || playerBlock99 != Material.AIR || playerBlock100 != Material.AIR || playerBlock111 != Material.AIR || Locationplayerr == Material.getMaterial("LADDER") || Locationplayerr == Material.getMaterial("VINE") || playerBlock11 == Material.getMaterial("LADDER") || playerBlock22 == Material.getMaterial("VINE");
            if (playerBlock11 == Material.WATER || playerBlock22 == Material.WATER || playerBlock33 == Material.WATER || playerBlock44 == Material.WATER || playerBlock55 == Material.WATER || playerBlock66 == Material.WATER || playerBlock77 == Material.WATER || playerBlock88 == Material.WATER || playerBlock99 == Material.WATER || playerBlock100 == Material.WATER || playerBlock111 == Material.WATER || location3A == Material.WATER || location44 == Material.WATER || location55 == Material.WATER || location66 == Material.WATER || location77 == Material.WATER || location88 == Material.WATER || location99 == Material.WATER || location100 == Material.WATER || playerBlock11 == Material.LAVA || playerBlock22 == Material.LAVA || playerBlock33 == Material.LAVA || playerBlock44 == Material.LAVA || playerBlock55 == Material.LAVA || playerBlock66 == Material.LAVA || playerBlock77 == Material.LAVA || playerBlock88 == Material.LAVA || playerBlock99 == Material.LAVA || playerBlock100 == Material.LAVA || playerBlock111 == Material.LAVA || location3A == Material.LAVA || location44 == Material.LAVA || location55 == Material.LAVA || location66 == Material.LAVA || location77 == Material.LAVA || location88 == Material.LAVA || (location99 == Material.LAVA && location100 == Material.LAVA)) {
                notNearLiquid = false;
            }

            double speedIceIncrease = 0.0;

            double speed = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2) + Math.pow(toZ - fromZ, 2));
            double speedXY = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2)) * 10;
            double speedXZ = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toZ - fromZ, 2)) * 10;
            double speedY = (fromY - toY) * 10;
            double speed_change = speed * 10;
            double increaseTrapdoor = 0;
            if (playerBlock22.toString().contains("TRAPDOOR")) {
                increaseTrapdoor = 3.3;
            }
            Boolean insideVeh = player.isInsideVehicle();
            if (!player.isRiptiding())
                isntRiptiding = true;
            double bedrockIncrease = 0;

            if (insideVeh) {
                if (playerInstance.isFloodGatePlayer && player.getVehicle().getType().toString().contains("HORSE"))
                    bedrockIncrease += 10;
            }

            boolean semiPred = playerBlock11 != Material.BUBBLE_COLUMN && playerBlock11 != Material.SCAFFOLDING && playerBlock11 != Material.WEEPING_VINES && playerBlock11 != Material.GLOW_BERRIES && playerBlock11 != Material.VINE && playerBlock11 != Material.LADDER && playerBlock11 != Material.TWISTING_VINES && playerBlock22 != Material.BUBBLE_COLUMN && playerBlock22 != Material.SCAFFOLDING && playerBlock22 != Material.WEEPING_VINES && playerBlock22 != Material.GLOW_BERRIES && playerBlock22 != Material.VINE && playerBlock22 != Material.LADDER && playerBlock22 != Material.TWISTING_VINES;

            if (plugin.checkSemiPredA) {
                if (toY > fromY && !playerInstance.speedSlime && !playerInstance.elytraUse && isntRiptiding && notNearLiquid && !playerInstance.allMovementChangeRunning && notAllowedFlight && !player.hasPotionEffect(PotionEffectType.LEVITATION) && semiPred) {
                    double f = playerInstance.semiPredANum;

                    if (f <= 0 || f - 1.2 < 0) {
                        f = 1.3;
                    }
                    double difference = (speed_change - (-Math.log(f - 1.2) + (4.10004f)));
                    double iceIncrease = 0;
                    double jumpBoost = 0;
                    if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                        jumpBoost += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                    }
                    if (playerInstance.underIce || insideVeh) {
                        iceIncrease += 1.5;
                    }
                    if (playerInstance.underStair) {
                        iceIncrease += 3.0;
                    }

                    if (difference > plugin.semiPredYLimiterA + iceIncrease + jumpBoost + bedrockIncrease) {
                        if (playerInstance.predictedYUp >= plugin.semiPredYUntilFlagA) {
                            plugin.notify.notify(player, plugin.message.type(32), plugin.message.cheat(1), plugin.notify.level(plugin.semiPredYLimiterA + plugin.roundedThresholdLow, plugin.semiPredYLimiterA + plugin.roundedThresholdMedium, plugin.semiPredYLimiterA + plugin.roundedThresholdHigh, difference));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(32), plugin.message.cheat(1));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            if (plugin.debugMode) {
                                plugin.message.Messages(50, difference, 4);
                            }
                        }
                        playerInstance.predictedYUp++;
                    }
                    playerInstance.semiPredANum++;
                } else {
                    playerInstance.semiPredANum= 0.0;
                    playerInstance.predictedYUp= 0;
                }
            }

            if (plugin.checkSemiPredB) {

                if (!notNearLiquid && playerInstance.semiPred) {
                    playerInstance.semiPred= false;
                    playerInstance.semiPredID= 200;
                } else if (playerInstance.semiPredID > 0) {
                    playerInstance.semiPredID--;
                } else {
                    playerInstance.semiPred= true;
                }
                if (toY < fromY && !playerInstance.speedSlime && !playerInstance.elytraUse && isntRiptiding && notNearLiquid && !playerInstance.allMovementChangeRunning && notAllowedFlight && semiPred && playerInstance.semiPred && Locationplayerr == Material.AIR) {

                    double f = playerInstance.semiPredBNum;

                    if (f <= 0) {
                        f = 1;// 0.055555;
                    }
                    double equation = ((f * 0.9 - Math.sqrt(f) * 0.05555 * f));
                    if (equation > 35 || speedY > 35) {
                        equation = speedY;
                    }
                    double difference = speedY - equation;
                    double iceIncrease = 0;
                    double jumpBoost = 0;

                    if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                        jumpBoost += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                    }
                    if (playerInstance.underIce) {
                        iceIncrease += 1.5;
                    }

                    if (difference > plugin.semiPredYLimiterB + iceIncrease + jumpBoost + bedrockIncrease) {
                        if (playerInstance.predictedYDown >= plugin.semiPredYUntilFlagB) {
                            plugin.notify.notify(player, plugin.message.type(32), plugin.message.cheat(2), plugin.notify.level(plugin.semiPredYLimiterB + plugin.roundedThresholdLow, plugin.semiPredYLimiterB + plugin.roundedThresholdMedium, plugin.semiPredYLimiterB + plugin.roundedThresholdHigh, difference));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(32), plugin.message.cheat(2));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }

                            if (plugin.debugMode) {
                                plugin.message.Messages(51, difference, 4);
                            }
                        }
                        playerInstance.predictedYDown++;
                    }

                    playerInstance.semiPredBNum++;

                } else {
                    playerInstance.predictedYDown= 0;
                    playerInstance.semiPredBNum= 0.0;

                }
            }

            if (playerInstance.elytraUse && isntRiptiding) {
                if (isElytra && plugin.checkSpeed && speed_change >= plugin.maxSpeedElytra) {
                    plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(15), plugin.notify.level(plugin.maxSpeedElytra + plugin.roundedThresholdLow, plugin.maxSpeedElytra + plugin.roundedThresholdMedium, plugin.maxSpeedElytra + plugin.roundedThresholdHigh, speed_change));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(15));
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                        playerInstance.setCancelled= true;
                    }

                    if (plugin.debugMode) {
                        plugin.message.Messages(40, speed_change, 4);
                    }
                }

                if (isElytra && plugin.checkElytraFlight) {
                    if (LocationplayerYTop == Material.AIR && locationYTop1 == Material.AIR && locationYTop2 == Material.AIR && locationYTop3 == Material.AIR && locationYTop4 == Material.AIR && locationYTop5 == Material.AIR && locationYTop6 == Material.AIR && locationYTop7 == Material.AIR && locationYTop8 == Material.AIR && locationYTop9 == Material.AIR && Locationplayerr == Material.getMaterial("AIR") && Locationplayer11 == Material.getMaterial("AIR") && Locationplayer22 == Material.getMaterial("AIR") && Locationplayer33 == Material.getMaterial("AIR") && Locationplayer44 == Material.getMaterial("AIR") && Locationplayer55 == Material.getMaterial("AIR") && Locationplayer66 == Material.getMaterial("AIR") && Locationplayer77 == Material.getMaterial("AIR") && Locationplayer99 == Material.getMaterial("AIR") && playerBlock11 == Material.getMaterial("AIR") && playerBlock22 == Material.getMaterial("AIR") && playerBlock33 == Material.getMaterial("AIR") && playerBlock44 == Material.getMaterial("AIR") && playerBlock55 == Material.getMaterial("AIR") && playerBlock66 == Material.getMaterial("AIR") && playerBlock77 == Material.getMaterial("AIR") && playerBlock88 == Material.getMaterial("AIR") && playerBlock99 == Material.getMaterial("AIR") && playerBlock100 == Material.getMaterial("AIR") && !player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                        if (toY == fromY && speed_change < plugin.elytraFlightSpeedMinB) {
                            playerInstance.elytraFlightB++;

                            if (playerInstance.elytraFlightB > plugin.elytraFlightLimitB) {
                                plugin.notify.notify(player, plugin.message.type(21), plugin.message.cheat(2), plugin.notify.level(plugin.elytraFlightLimitB + plugin.roundedThresholdLow, plugin.elytraFlightLimitB + plugin.roundedThresholdMedium, plugin.elytraFlightLimitB + plugin.roundedThresholdHigh, playerInstance.elytraFlightB));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(21), plugin.message.cheat(2));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(48, speed_change, 4);
                                }
                            }

                        }
                        if (!isntRiptiding) {
                            playerInstance.elytraFlightNum= 0;
                        }

                        if (toY >= fromY) {
                            if (playerInstance.elytraMaxYCounter != -1) {
                                playerInstance.elytraSecondCounter= -1;
                            }

                            if (playerInstance.elytraSecondCounter == 1) {
                                if (toY >= playerInstance.elytraMaxY) {
                                    playerInstance.elytraFlightNum++;
                                }
                                if (playerInstance.elytraFlightNum >= plugin.elytraFlightUntilHacking) {
                                    plugin.notify.notify(player, plugin.message.type(21), plugin.message.cheat(1), plugin.notify.level(plugin.elytraFlightUntilHacking + plugin.roundedThresholdLow, plugin.elytraFlightUntilHacking + plugin.roundedThresholdMedium, plugin.elytraFlightUntilHacking + plugin.roundedThresholdHigh, playerInstance.elytraFlightNum));
                                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(21), plugin.message.cheat(1));
                                    if (plugin.cancelEventIfHacking) {
                                        playerInstance.cancelX= fromX;
                                        playerInstance.cancelY= fromY;
                                        playerInstance.cancelZ= fromZ;
                                        playerInstance.setCancelled= true;
                                    }
                                    if (plugin.debugMode) {
                                        plugin.message.Messages(38, 3, 4);
                                    }
                                }

                            }

                        } else if (toY < fromY) {
                            if (playerInstance.elytraSecondCounter == -1) {
                                playerInstance.elytraMaxY= toY;
                                playerInstance.elytraSecondCounter= 1;
                                playerInstance.elytraMaxYCounter= -1;
                            }

                        }

                    } else {
                        playerInstance.elytraFlightNum= 0;
                        playerInstance.elytraFlightB= 0;
                        playerInstance.elytraFlightNum= 0;
                        playerInstance.elytraMaxYCounter= 0;
                        playerInstance.elytraSecondCounter= 0;
                    }
                }
                return;
            }

            try {
                if (plugin.checkMedianSpeed) {
                    playerInstance.medianSpeedCounter.add(speed_change);
                    playerInstance.medianYPos.add((int) toY);
                    double ad = 0.0;

                    if (increaseTrapdoor > 0) {
                        ad += 2;
                    }

                    if (playerInstance.underIce) {
                        ad = 5.0;
                    }
                    if (playerInstance.underStair) {
                        ad += 1.0;
                    }
                    if (LocationplayerYTop.isSolid()) {
                        ad += 0.5;
                    }

                    if (playerInstance.speedSlime || insideVeh || playerInstance.elytraUse) {
                        playerInstance.medianSpeedCounter.clear();
                        playerInstance.medianYPos.clear();
                    }
                    if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                        ad += player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() * 0.7;
                    }
                    if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                        ad += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() * 0.5;
                    }
                    if (playerInstance.allMovementChange == 1.0) {
                        ad = plugin.smartCombatMovementChangeNumber / 3.3;
                    }
                    if (player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
                        ad += player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE).getAmplifier() * 0.5;
                    }
                    if (player.getInventory().getBoots() != null) {
                        if (player.getInventory().getBoots().getEnchantments().containsKey(Enchantment.DEPTH_STRIDER)) {
                            ad += player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER) * 4.6;
                        }
                    }

                    if (playerInstance.medianSpeedCounter.size() >= plugin.sampleMedianSpeed) {
                        double y = 0;
                        for (double x : playerInstance.medianSpeedCounter) {
                            y += x;
                        }

                        if (Math.abs(playerInstance.medianYPos.get(0) - playerInstance.medianYPos.get(plugin.sampleMedianSpeed - 1)) < plugin.axisYIgnoreMedian && y / plugin.sampleMedianSpeed > plugin.maxMedianSpeed + ad + increaseTrapdoor) {
                            plugin.notify.notify(player, plugin.message.type(30), plugin.message.cheat(1), plugin.notify.level(plugin.maxMedianSpeed + ad + increaseTrapdoor + plugin.roundedThresholdLow, plugin.maxMedianSpeed + ad + increaseTrapdoor + plugin.roundedThresholdMedium, plugin.maxMedianSpeed + ad + increaseTrapdoor + plugin.roundedThresholdHigh, y / plugin.sampleMedianSpeed));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(30), plugin.message.cheat(1));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            if (plugin.debugMode) {
                                plugin.message.Messages(45, y / plugin.sampleMedianSpeed, 4);
                            }
                        }

                        playerInstance.medianSpeedCounter.clear();
                        playerInstance.medianYPos.clear();

                        if (playerInstance.allMovementChangeRunning) {
                            playerInstance.medianSpeedCounter.clear();
                            playerInstance.medianYPos.clear();
                        }

                    }
                }
            } catch (Exception e) {

            }

            // Bukkit.broadcastMessage("" + speed_change;
            if (plugin.checkStepB) {
                if (playerBlock11.isSolid() && toY > fromY && speed_change > plugin.stepMaxSpeedB && !Locationplayerr.toString().contains("BED") && !Locationplayerrd.toString().contains("BED")) {
                    plugin.notify.notify(player, plugin.message.type(6), plugin.message.cheat(2), plugin.notify.level(plugin.stepMaxSpeedB + plugin.roundedThresholdLow, plugin.stepMaxSpeedB + plugin.roundedThresholdMedium, plugin.stepMaxSpeedB + plugin.roundedThresholdHigh, speed_change));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(6), plugin.message.cheat(2));
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                        playerInstance.setCancelled= true;
                    }
                    if (plugin.debugMode) {
                        plugin.message.Messages(38, 3, 4);
                    }
                }
            }
            double x = 0;
            double y = 0;
            if (playerInstance.allMovementChange == 1.0) {
                y = plugin.smartCombatMovementChangeNumber;
            }

            if (locationYTop1 == Material.COBWEB || locationYTop2 == Material.COBWEB || locationYTop3 == Material.COBWEB || locationYTop4 == Material.COBWEB || locationYTop5 == Material.COBWEB || locationYTop7 == Material.COBWEB || locationYTop6 == Material.COBWEB || locationYTop8 == Material.COBWEB || locationYTop9 == Material.COBWEB) {
                topWeb = false;
            }

            if (player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                isntLevitating = false;
            }
            if (player.isFlying() && player.getAllowFlight()) {
                notAllowedFlight = false;
            }

            double genericSpeedMovementIncrease = 0.0;
            if (plugin.enableItemAttributeChecking) {
                for (ItemStack m : player.getInventory().getArmorContents()) {
                    if (m != null && m.getItemMeta().getAttributeModifiers() != null) {
                        Collection<AttributeModifier> metaA = m.getItemMeta().getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED);
                        if (metaA != null) {
                            for (AttributeModifier att : metaA) {
                                genericSpeedMovementIncrease += (att.getAmount() / 0.05) * plugin.speedIncrease;
                                playerInstance.counterForItemAttribute= plugin.attributeCounter;
                            }
                        }
                    }
                }
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR && player.getInventory().getItemInMainHand().getItemMeta().getAttributeModifiers() != null) {
                    Collection<AttributeModifier> metaA = player.getInventory().getItemInMainHand().getItemMeta().getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (metaA != null) {
                        for (AttributeModifier att : metaA) {
                            genericSpeedMovementIncrease += (att.getAmount() / 0.05) * plugin.speedIncrease;
                            playerInstance.counterForItemAttribute= plugin.attributeCounter;
                        }
                    }
                }
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR && player.getInventory().getItemInOffHand().getItemMeta().getAttributeModifiers() != null) {
                    Collection<AttributeModifier> metaA = player.getInventory().getItemInOffHand().getItemMeta().getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (metaA != null) {
                        for (AttributeModifier att : metaA) {
                            genericSpeedMovementIncrease += (att.getAmount() / 0.05) * plugin.speedIncrease;
                            playerInstance.counterForItemAttribute= plugin.attributeCounter;
                        }
                    }
                }
                playerInstance.increaseAmountForItemAttribute= genericSpeedMovementIncrease;
                if (playerInstance.counterForItemAttribute <= 0) {
                    playerInstance.increaseAmountForItemAttribute= 0.0;
                } else {
                    playerInstance.counterForItemAttribute--;
                }
                genericSpeedMovementIncrease = playerInstance.counterForItemAttribute;
            }

            playerInstance.nearBoat= true;

            playerInstance.playerNearbyEntities.add(player);

            // NearbyBoat nearbyBoat = new NearbyBoat(player, plugin,
            // playerName, toX,toY,
            // toZ, Locationplayerfluid);

            /*
             * Bukkit.getScheduler().runTaskAsynchronously(plugin, new
             * BukkitRunnable() {
             *
             * @Override public void run() {
             *
             * if (!playerInstance.elytraUse) { Entity e =
             * player.getWorld().rayTraceEntities(Locationplayerfluid, new
             * Vector(toX, toY, toZ), 0.1).getHitEntity(); if (e != null) {
             * if (EntityType.BOAT.equals(e.getType()) && e instanceof Boat)
             * { Bukkit.broadcastMessage("Boat");
             * playerInstance.fluidWalkNum= 0.0;
             * playerInstance.jumpsOnWaterTillHacking= 0.0;
             * playerInstance.fluidWalkLavaNum= 0.0;
             * playerInstance.nearBoat= false;
             * playerInstance.fluidWalkNCP= 0.0; if (!insideVeh) {
             * playerInstance.fluidWalkUntilHackingAlt= 0.0;
             * playerInstance.glidePlayer= 0.0;
             * playerInstance.untilGroundSpoof= 0.0;
             * playerInstance.standingOnBoat= 1);
             *
             * playerInstance.alivePacketEnabler= 0.0;
             *
             * } else { playerInstance.standingOnBoat= 0; } }
             *
             * } }
             *
             * }
             *
             * });
             */

            /*
             * Bukkit.getScheduler().runTaskAsynchronously(plugin, new
             * Runnable() {
             *
             * @Override public void run() { if (!playerInstance.elytraUse)
             * { for (Entity e : player.getNearbyEntities(1, 1, 1)) { if (e
             * != null) { if (EntityType.BOAT.equals(e.getType()) && e
             * instanceof Boat) { playerInstance.fluidWalkNum= 0.0;
             *
             * playerInstance.jumpsOnWaterTillHacking= 0.0;
             * playerInstance.fluidWalkLavaNum= 0.0;
             * playerInstance.nearBoat= false;
             * playerInstance.fluidWalkNCP= 0.0; if (!insideVeh) {
             * playerInstance.fluidWalkUntilHackingAlt= 0.0;
             * playerInstance.glidePlayer= 0.0;
             * playerInstance.untilGroundSpoof= 0.0;
             * playerInstance.standingOnBoat= 1);
             *
             * playerInstance.alivePacketEnabler= 0.0;
             *
             * } else { playerInstance.standingOnBoat= 0; } } } } }
             * } });
             */

            if (insideVeh) {
                playerInstance.speedMboat= 4;
            } else if (playerInstance.speedMboat != 0) {
                playerInstance.speedMboat--;
            }
            int ddaw = playerInstance.notInsideBoat;
            if (ddaw <= 0 && insideVeh && playerInstance.underIce) {
                playerInstance.notInsideBoat= 20;
            } else if (ddaw > 0 && !insideVeh) {
                playerInstance.notInsideBoat--;
            }
            if (LocationplayerYTop != Material.AIR || locationYTop2 != Material.AIR || locationYTop3 != Material.AIR || locationYTop4 != Material.AIR || locationYTop5 != Material.AIR || locationYTop6 != Material.AIR || locationYTop7 != Material.AIR || locationYTop8 != Material.AIR || locationYTop9 != Material.AIR || locationYTop1 != Material.AIR) {
                playerInstance.underBlock= true;
                playerInstance.underBlockSCHID= 60;
            } else if (playerInstance.underBlockSCHID != 0) {
                playerInstance.underBlockSCHID--;
            } else {
                playerInstance.underBlock= false;

            }


            if (Locationplayerr.toString().contains("STAIRS") || Locationplayerrfluid.toString().contains("STAIRS") || Locationplayer11.toString().contains("STAIRS") || Locationplayerrfluid1.toString().contains("STAIRS") || Locationplayer22.toString().contains("STAIRS") || Locationplayerrfluid2.toString().contains("STAIRS") || Locationplayer33.toString().contains("STAIRS") || Locationplayerrfluid3.toString().contains("STAIRS") || Locationplayer44.toString().contains("STAIRS") || Locationplayerrfluid4.toString().contains("STAIRS") || Locationplayer55.toString().contains("STAIRS") || Locationplayerrfluid5.toString().contains("STAIRS") || Locationplayer66.toString().contains("STAIRS") || Locationplayerrfluid6.toString().contains("STAIRS") || Locationplayer77.toString().contains("STAIRS") || Locationplayerrfluid7.toString().contains("STAIRS") || Locationplayer99.toString().contains("STAIRS") || Locationplayerrfluid8.toString().contains("STAIRS") || Locationplayerr.toString().contains("SLAB") || Locationplayerrfluid.toString().contains("SLAB") || Locationplayer11.toString().contains("SLAB") || Locationplayerrfluid1.toString().contains("SLAB") || Locationplayer22.toString().contains("SLAB") || Locationplayerrfluid2.toString().contains("SLAB") || Locationplayer33.toString().contains("SLAB") || Locationplayerrfluid3.toString().contains("SLAB") || Locationplayer44.toString().contains("SLAB") || Locationplayerrfluid4.toString().contains("SLAB") || Locationplayer55.toString().contains("SLAB") || Locationplayerrfluid5.toString().contains("SLAB") || Locationplayer66.toString().contains("SLAB") || Locationplayerrfluid6.toString().contains("SLAB") || Locationplayer77.toString().contains("SLAB") || Locationplayerrfluid7.toString().contains("SLAB") || Locationplayer99.toString().contains("SLAB") || Locationplayerrfluid8.toString().contains("SLAB")) {
                playerInstance.underStair= true;
                playerInstance.underStairID= 100;
            } else if (playerInstance.underStairID != 0) {
                playerInstance.underStairID--;
            } else {
                playerInstance.underStair= false;
            }
            if (playerBlock11 == Material.COBWEB || playerBlock22 == Material.COBWEB || playerBlock33 == Material.COBWEB || playerBlock44 == Material.COBWEB || playerBlock55 == Material.COBWEB || playerBlock66 == Material.COBWEB || playerBlock77 == Material.COBWEB || playerBlock88 == Material.COBWEB || playerBlock99 == Material.COBWEB || playerBlock100 == Material.COBWEB || playerBlock111 == Material.COBWEB || location3A == Material.COBWEB || location44 == Material.COBWEB || location55 == Material.COBWEB || location66 == Material.COBWEB || location77 == Material.COBWEB || location88 == Material.COBWEB || location99 == Material.COBWEB || location100 == Material.COBWEB || playerBlock11 == Material.SWEET_BERRY_BUSH || playerBlock22 == Material.SWEET_BERRY_BUSH || playerBlock33 == Material.SWEET_BERRY_BUSH || playerBlock44 == Material.SWEET_BERRY_BUSH || playerBlock55 == Material.SWEET_BERRY_BUSH || playerBlock66 == Material.SWEET_BERRY_BUSH || playerBlock77 == Material.SWEET_BERRY_BUSH || playerBlock88 == Material.SWEET_BERRY_BUSH || playerBlock99 == Material.SWEET_BERRY_BUSH || playerBlock100 == Material.SWEET_BERRY_BUSH || playerBlock111 == Material.SWEET_BERRY_BUSH || location3A == Material.SWEET_BERRY_BUSH || location44 == Material.SWEET_BERRY_BUSH || location55 == Material.SWEET_BERRY_BUSH || location66 == Material.SWEET_BERRY_BUSH || location77 == Material.SWEET_BERRY_BUSH || location88 == Material.SWEET_BERRY_BUSH || (location99 == Material.SWEET_BERRY_BUSH && location100 == Material.SWEET_BERRY_BUSH)) {
                nearSlowableBlock = false;
            }
            if (Locationplayer11.toString().contains("SHULKER_BOX") || Locationplayer22.toString().contains("SHULKER_BOX") || Locationplayer33.toString().contains("SHULKER_BOX") || Locationplayer44.toString().contains("SHULKER_BOX") || Locationplayer55.toString().contains("SHULKER_BOX") || Locationplayer66.toString().contains("SHULKER_BOX") || Locationplayer77.toString().contains("SHULKER_BOX") || Locationplayer55.toString().contains("SHULKER_BOX") || Locationplayer99.toString().contains("SHULKER_BOX") || playerInstance.nearbyShulkerBox != 0) {
                shulker = false;
            }
            if (playerInstance.nearbyShulkerBox != 0) {
                playerInstance.nearbyShulkerBox--;
            }
            boolean nearClimableBlock = !playerBlock11.isSolid() && !playerBlock22.isSolid() && playerBlock11 != Material.SCAFFOLDING && playerBlock22 != Material.SCAFFOLDING && Locationplayerr != Material.SCAFFOLDING && Locationplayerr != Material.WATER && Locationplayerr != Material.WATER && Locationplayerr != Material.LAVA && Locationplayerr != Material.LADDER && Locationplayerr != Material.VINE && Locationplayerr != Material.TWISTING_VINES && Locationplayerr != Material.WEEPING_VINES && location3A != Material.WEEPING_VINES && location44 != Material.WEEPING_VINES && location55 != Material.WEEPING_VINES && location66 != Material.WEEPING_VINES && playerBlock33 != Material.WEEPING_VINES && playerBlock55 != Material.WEEPING_VINES && playerBlock66 != Material.WEEPING_VINES && playerBlock66 != Material.WEEPING_VINES && location3A != Material.TWISTING_VINES && location44 != Material.TWISTING_VINES && location55 != Material.TWISTING_VINES && location66 != Material.TWISTING_VINES && playerBlock33 != Material.TWISTING_VINES && playerBlock55 != Material.TWISTING_VINES && playerBlock66 != Material.TWISTING_VINES && location44 != Material.GLOW_BERRIES && location55 != Material.GLOW_BERRIES && location66 != Material.GLOW_BERRIES && playerBlock33 != Material.GLOW_BERRIES && playerBlock55 != Material.GLOW_BERRIES && playerBlock66 != Material.GLOW_BERRIES;

            if (plugin.checkBaritone && Locationplayerrfluid != Material.SLIME_BLOCK && Locationplayerrfluid != Material.SOUL_SAND && Locationplayerrfluid != Material.HONEY_BLOCK) {
                try {
                    int cc = playerInstance.baritoneResetCounter;
                    if (toY == fromY) {
                        playerInstance.baritoneResetCounter++;
                        if (playerInstance.baritoneResetCounter >= plugin.baritoneReset) {
                            playerInstance.baritoneJumpSpeedsOne= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwo= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsOneB= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwoB= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOne= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwo= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOneB= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwoB= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsOneC= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwoC= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOneC= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwoC= new ArrayList<Double>();
                            playerInstance.locationA= new ArrayList<Location>();
                            playerInstance.locationB= new ArrayList<Location>();
                            playerInstance.locationC= new ArrayList<Location>();
                            playerInstance.baritoneResetCounter= 0;
                        }
                    }

                    if (toY > fromY && !playerInstance.hit && !playerInstance.elytraUse && !insideVeh && shulker && notNearLiquid && toX != fromX && toZ != fromZ && toY != fromY && !playerInstance.underStair && !insideVeh && !player.hasPotionEffect(PotionEffectType.LEVITATION) && cc < plugin.baritoneReset) {
                        // Bukkit.broadcastMessage("block: " +
                        // loc.clone().add(-0.6, 0.2,
                        // 0).getBlock().getType());
                        Location lp = loc.clone();
                        if (playerInstance.baritoneJumpSpeedsOneB.size() <= plugin.packetSamplesB) {
                            playerInstance.baritoneJumpSpeedsOneB.add(speed_change);
                            playerInstance.baritoneSpeedYCoordOneB.add(fromY);
                        } else {
                            playerInstance.locationB.add(lp);
                            playerInstance.baritoneJumpSpeedsTwoB.add(speed_change);
                            playerInstance.baritoneSpeedYCoordTwoB.add(fromY);
                        }
                        if (playerInstance.baritoneJumpSpeedsOne.size() <= plugin.packetSamples) {
                            playerInstance.baritoneJumpSpeedsOne.add(speed_change);
                            playerInstance.baritoneSpeedYCoordOne.add(fromY);
                        } else {
                            playerInstance.locationA.add(lp);
                            playerInstance.baritoneJumpSpeedsTwo.add(speed_change);
                            playerInstance.baritoneSpeedYCoordTwo.add(fromY);
                        }
                        if (playerInstance.baritoneJumpSpeedsOneC.size() <= plugin.packetSamplesC) {
                            playerInstance.baritoneJumpSpeedsOneC.add(speed_change);
                            playerInstance.baritoneSpeedYCoordOneC.add(fromY);
                        } else {
                            playerInstance.locationC.add(lp);
                            playerInstance.baritoneJumpSpeedsTwoC.add(speed_change);
                            playerInstance.baritoneSpeedYCoordTwoC.add(fromY);
                        }

                        int count = 0;
                        int blockCountNotAirSideOne = 0;
                        int blockCountNotAirSideTwo = 0;
                        int blockCountNotAirSideThree = 0;
                        int blockCountNotAirSideFour = 0;
                        if (playerInstance.baritoneJumpSpeedsTwo.size() >= plugin.packetSamples) {
                            count = 0;
                            // Bukkit.broadcastMessage("Checked A");
                            Location completeOriginalPosition = null;
                            for (int ll = 0; ll != plugin.packetSamples; ll++) {
                                double num = 0, num2 = 0;
                                if (!playerInstance.baritoneJumpSpeedsOne.isEmpty() && !playerInstance.baritoneSpeedYCoordOne.isEmpty()
                                        && !playerInstance.baritoneSpeedYCoordTwo.isEmpty() && !playerInstance.baritoneJumpSpeedsTwo.isEmpty()) {
                                    if (ll == 0) {
                                        completeOriginalPosition = playerInstance.locationA.get(ll);
                                    }
                                    if (playerInstance.baritoneJumpSpeedsOne.get(ll).toString().length() > plugin.substringNum - 1) {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsOne.get(ll).toString().substring(0, plugin.substringNum));
                                    } else {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsOne.get(ll).toString());
                                    }
                                    if (playerInstance.baritoneJumpSpeedsTwo.get(ll).toString().length() > plugin.substringNum - 1) {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwo.get(ll).toString().substring(0, plugin.substringNum));
                                    } else {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwo.get(ll).toString());
                                    }
                                    if (playerInstance.baritoneSpeedYCoordOne.get(ll).equals(playerInstance.baritoneSpeedYCoordTwo.get(ll))) {
                                        playerInstance.baritoneJumpSpeedsOne= new ArrayList<Double>();
                                        playerInstance.baritoneJumpSpeedsTwo= new ArrayList<Double>();
                                        playerInstance.baritoneSpeedYCoordOne= new ArrayList<Double>();
                                        playerInstance.baritoneSpeedYCoordTwo= new ArrayList<Double>();
                                        playerInstance.locationA= new ArrayList<Location>();
                                        count = 0;
                                        break;
                                    }
                                    boolean sameBlockOne = false;
                                    boolean sameBlockTwo = false;
                                    boolean sameBlockThree = false;
                                    boolean sameBlockFour = false;
                                    Location original = playerInstance.locationA.get(ll);
                                    Location one = original.add(plugin.doubleBlockNum, 0.2, 0);
                                    if ((int) toX == one.getBlockX() || ((int) toZ == one.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockOne = true;
                                    }

                                    if (!one.getBlock().isPassable() && sameBlockOne) {
                                        blockCountNotAirSideOne++;
                                        // Bukkit.broadcastMessage("Increased
                                        // one");
                                    }
                                    Location two = original.add(-plugin.doubleBlockNum * 2, 0, 0);
                                    if ((int) toX == two.getBlockX() || ((int) toZ == two.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockTwo = true;
                                    }
                                    if (!two.getBlock().isPassable() && sameBlockTwo) {
                                        blockCountNotAirSideTwo++;
                                        // Bukkit.broadcastMessage("Increased
                                        // two");
                                    }
                                    Location three = original.add(plugin.doubleBlockNum, 0, plugin.doubleBlockNum);
                                    if ((int) toX == three.getBlockX() || ((int) toZ == three.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockThree = true;
                                    }
                                    if (!three.getBlock().isPassable() && sameBlockThree) {
                                        blockCountNotAirSideThree++;
                                        // Bukkit.broadcastMessage("Increased
                                        // three");
                                    }
                                    Location four = original.add(0, 0, -plugin.doubleBlockNum * 2);
                                    if ((int) toX == four.getBlockX() || ((int) toZ == four.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockFour = true;
                                    }
                                    if (!four.getBlock().isPassable() && sameBlockFour) {
                                        blockCountNotAirSideFour++;
                                        // Bukkit.broadcastMessage("Increased
                                        // four");
                                    }
                                    if (!four.getBlock().isPassable() && sameBlockFour) {
                                        blockCountNotAirSideFour++;
                                    }
                                    if (num == num2) {
                                        count++;
                                        // Bukkit.broadcastMessage("Increased
                                        // A");
                                    }
                                }
                            }
                            if ((blockCountNotAirSideOne == plugin.allowedBlockTillFlagNum || blockCountNotAirSideTwo == plugin.allowedBlockTillFlagNum || blockCountNotAirSideThree == plugin.allowedBlockTillFlagNum || blockCountNotAirSideFour == plugin.allowedBlockTillFlagNum) && count >= plugin.baritoneLoopThreshold && count <= plugin.minFlagCount) {
                                playerInstance.counter++;
                                // Bukkit.broadcastMessage("Increased
                                // counterA");
                            }
                            if (plugin.allowedBlockTillFlagNum == 0) {
                                if (count >= plugin.baritoneLoopThreshold && count <= plugin.minFlagCount) {
                                    playerInstance.counter++;
                                    // Bukkit.broadcastMessage("increased
                                    // counter");
                                }
                            } else if (playerInstance.counter >= plugin.baritoneFlag) {
                                plugin.notify.notify(player, plugin.message.type(20), plugin.message.cheat(1), plugin.highString);
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(20), plugin.message.cheat(1));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(37, 3, 5);
                                }
                                playerInstance.counter= 0;
                            }
                            playerInstance.baritoneJumpSpeedsOne= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwo= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOne= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwo= new ArrayList<Double>();
                            playerInstance.locationA= new ArrayList<Location>();
                            /*
                             * for (int a = 0; a != playerInstance.packetSamples;
                             * a++) {
                             * baritoneJumpSpeeds.remove(0;
                             * }
                             */
                        }
                        if (playerInstance.baritoneJumpSpeedsTwoB.size() >= plugin.packetSamplesB) {
                            count = 0;
                            blockCountNotAirSideOne = 0;
                            blockCountNotAirSideTwo = 0;
                            blockCountNotAirSideThree = 0;
                            blockCountNotAirSideFour = 0;
                            // Bukkit.broadcastMessage("Checked B");
                            Location completeOriginalPosition = null;
                            for (int ll = 0; ll != plugin.packetSamplesB; ll++) {
                                double num = 0, num2 = 0;
                                if (!playerInstance.baritoneJumpSpeedsOneB.isEmpty()) {
                                    if (ll == 0) {
                                        completeOriginalPosition = playerInstance.locationB.get(0);
                                    }
                                    if (playerInstance.baritoneJumpSpeedsOneB.get(ll).toString().length() > plugin.substringNumB - 1) {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsOneB.get(ll).toString().substring(0, plugin.substringNumB));
                                    } else {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsOneB.get(ll).toString());
                                    }
                                    if (playerInstance.baritoneJumpSpeedsTwoB.get(ll).toString().length() > plugin.substringNumB - 1) {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwoB.get(ll).toString().substring(0, plugin.substringNumB));
                                    } else {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwoB.get(ll).toString());
                                    }
                                    if (playerInstance.baritoneSpeedYCoordOneB.get(ll).equals(playerInstance.baritoneSpeedYCoordTwoB.get(ll))) {
                                        playerInstance.baritoneJumpSpeedsOneB= new ArrayList<Double>();
                                        playerInstance.baritoneJumpSpeedsTwoB= new ArrayList<Double>();
                                        playerInstance.baritoneSpeedYCoordOneB= new ArrayList<Double>();
                                        playerInstance.baritoneSpeedYCoordTwoB= new ArrayList<Double>();
                                        playerInstance.locationB= new ArrayList<Location>();
                                        count = 0;
                                        break;
                                    }
                                    /*
                                     * Bukkit.
                                     * broadcastMessage("LocationB 1 Block: "
                                     * + locationB.get(ll)
                                     * .add(plugin.doubleBlockNum, 0.2,
                                     * 0).getBlock().getType());
                                     *
                                     * Bukkit.
                                     * broadcastMessage("LocationB 2: " +
                                     * locationB.get(ll)
                                     * .add(-plugin.doubleBlockNum, 0.2,
                                     * 0).getBlock().getType());
                                     *
                                     * Bukkit.
                                     * broadcastMessage("LocationB 3: " +
                                     * locationB.get(ll)
                                     * .add(-plugin.doubleBlockNum * 2, 0,
                                     * 0).getBlock().getType());
                                     *
                                     * Bukkit.
                                     * broadcastMessage("LocationB 4: " +
                                     * locationB.get(ll)
                                     * .add(0, 0, -plugin.doubleBlockNum *
                                     * 2).getBlock().getType());
                                     */
                                    boolean sameBlockOne = false;
                                    boolean sameBlockTwo = false;
                                    boolean sameBlockThree = false;
                                    boolean sameBlockFour = false;
                                    Location original = playerInstance.locationB.get(ll);
                                    // Location mainOriginal = original;
                                    // Location copy = mainOriginal;
                                    Location one = original.add(plugin.doubleBlockNum, 0.2, 0);
                                    // copy = copy.add(0, -1.0, 0.1);
                                    /*
                                     * if (ll == 0) { double distance =
                                     * Math.sqrt( Math.pow(toX -
                                     * original.getX(), 2) + Math.pow(toY -
                                     * original.getY(), 2) + Math.pow(toZ -
                                     * original.getZ(), 2)); if (distance >
                                     * playerInstance.minDistance) {
                                     * playerInstance.baritoneJumpSpeedsOneB.put(
                                     * playerName, new ArrayList<Double>();
                                     * baritoneJumpSpeedsTwoB.put(
                                     * playerName, new ArrayList<Double>();
                                     * baritoneSpeedYCoordOneB.put(
                                     * playerName, new ArrayList<Double>();
                                     * baritoneSpeedYCoordTwoB.put(
                                     * playerName, new ArrayList<Double>();
                                     * locationB= new
                                     * ArrayList<Location>());
                                     * Bukkit.broadcastMessage("reset");
                                     * break; } } for(double awd = 0.0; awd
                                     * < 2.0; awd += 0.1) {
                                     * Bukkit.broadcastMessage("awd: " +
                                     * awd); copy = copy.add(0.1, 0, 0;
                                     * if(!copy.getBlock().isPassable()) {
                                     *
                                     * distanceBlock.add(Math.sqrt(Math.pow(
                                     * mainOriginal.getX() - copy.getX(), 2)
                                     * + Math.pow(mainOriginal.getY() -
                                     * copy.getY(), 2) +
                                     * Math.pow(mainOriginal.getZ() -
                                     * copy.getZ(), 2)));
                                     * Bukkit.broadcastMessage("copy: " +
                                     * copy.getBlock().getType()); Bukkit.
                                     * broadcastMessage("Copy Location: " +
                                     * copy);
                                     * Bukkit.broadcastMessage("distance: "
                                     * + distanceBlock); break; }
                                     *
                                     * Bukkit.broadcastMessage("copy: " +
                                     * copy.getBlock().getType()); Bukkit.
                                     * broadcastMessage("Copy Location: " +
                                     * copy);
                                     *
                                     * }
                                     */
                                    if ((int) toX == one.getBlockX() || ((int) toZ == one.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockOne = true;
                                    }
                                    if (!one.getBlock().isPassable() && sameBlockOne) {
                                        blockCountNotAirSideOne++;
                                        // Bukkit.broadcastMessage("Increased
                                        // one");
                                    }
                                    // Bukkit.broadcastMessage("one: " +
                                    // one.getBlock().getType());
                                    Location two = original.add(-plugin.doubleBlockNum * 2, 0, 0);
                                    if ((int) toX == two.getBlockX() || ((int) toZ == two.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockTwo = true;
                                    }
                                    // Bukkit.broadcastMessage("Two: " +
                                    // two.getBlock().getType());
                                    if (!two.getBlock().isPassable() && sameBlockTwo) {
                                        blockCountNotAirSideTwo++;
                                        // Bukkit.broadcastMessage("Increased
                                        // two");
                                    }
                                    Location three = original.add(plugin.doubleBlockNum, 0, plugin.doubleBlockNum);
                                    if ((int) toX == three.getBlockX() || ((int) toZ == three.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockThree = true;
                                    }
                                    if (!three.getBlock().isPassable() && sameBlockThree) {
                                        blockCountNotAirSideThree++;
                                        // Bukkit.broadcastMessage("Increased
                                        // three");
                                    }
                                    // Bukkit.broadcastMessage("Three: " +
                                    // three.getBlock().getType());
                                    Location four = original.add(0, 0, -plugin.doubleBlockNum * 2);
                                    if ((int) toX == four.getBlockX() || ((int) toZ == four.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockFour = true;
                                    }
                                    if (!four.getBlock().isPassable() && sameBlockFour) {
                                        blockCountNotAirSideFour++;
                                        // Bukkit.broadcastMessage("Increased
                                        // four");
                                    }
                                    // Bukkit.broadcastMessage("four: " +
                                    // four.getBlock().getType());
                                    if (num == num2) {
                                        count++;
                                    }
                                }
                            }
                            if ((plugin.allowedBlockTillFlagNum == 0 || blockCountNotAirSideOne == plugin.allowedBlockTillFlagNum || blockCountNotAirSideTwo == plugin.allowedBlockTillFlagNum || blockCountNotAirSideThree == plugin.allowedBlockTillFlagNum || blockCountNotAirSideFour == plugin.allowedBlockTillFlagNum) && count >= plugin.baritoneLoopThresholdB && count <= plugin.minFlagCountB) {
                                playerInstance.counterB++;
                                // Bukkit.broadcastMessage("increased
                                // counter");
                            }
                            if (playerInstance.counterB >= plugin.baritoneFlagB) {
                                plugin.notify.notify(player, plugin.message.type(20), plugin.message.cheat(2), plugin.highString);
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(20), plugin.message.cheat(2));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(37, 3, 5);
                                }
                                playerInstance.counterB= 0;
                            }
                            playerInstance.baritoneJumpSpeedsOneB= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwoB= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOneB= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwoB= new ArrayList<Double>();
                            playerInstance.locationB= new ArrayList<Location>();
                            /*
                             * for (int a = 0; a != playerInstance.packetSamples;
                             * a++) {
                             * baritoneJumpSpeeds.remove(0;
                             * }
                             */
                        }
                        if (playerInstance.baritoneJumpSpeedsTwoC.size() >= plugin.packetSamplesC) {
                            // Bukkit.broadcastMessage("Checked C");
                            count = 0;
                            blockCountNotAirSideOne = 0;
                            blockCountNotAirSideTwo = 0;
                            blockCountNotAirSideThree = 0;
                            blockCountNotAirSideFour = 0;
                            Location completeOriginalPosition = null;
                            for (int ll = 0; ll != plugin.packetSamplesC; ll++) {
                                double num = 0, num2 = 0;
                                if (!playerInstance.baritoneJumpSpeedsTwoC.isEmpty() && !playerInstance.baritoneJumpSpeedsOneC.isEmpty() && !playerInstance.baritoneSpeedYCoordOneC.isEmpty() && !playerInstance.baritoneSpeedYCoordTwoC.isEmpty()) {
                                    if (ll == 0) {
                                        completeOriginalPosition = playerInstance.locationC.get(ll);
                                    }
                                    if (playerInstance.baritoneJumpSpeedsTwoC.get(ll).toString().length() > plugin.substringNumC - 1) {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwoC.get(ll).toString().substring(0, plugin.substringNumC));
                                    } else {
                                        num = Double.parseDouble(playerInstance.baritoneJumpSpeedsTwoC.get(ll).toString());
                                    }
                                    if (playerInstance.baritoneJumpSpeedsOneC.get(ll).toString().length() > plugin.substringNumC - 1) {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsOneC.get(ll).toString().substring(0, plugin.substringNumC));
                                    } else {
                                        num2 = Double.parseDouble(playerInstance.baritoneJumpSpeedsOneC.get(ll).toString());
                                    }
                                    boolean sameBlockOne = false;
                                    boolean sameBlockTwo = false;
                                    boolean sameBlockThree = false;
                                    boolean sameBlockFour = false;
                                    Location original = playerInstance.locationC.get(ll);
                                    Location one = original.add(plugin.doubleBlockNum, 0.2, 0);
                                    if ((int) toX == one.getBlockX() || ((int) toZ == one.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockOne = true;
                                    }
                                    if (!one.getBlock().isPassable() && sameBlockOne) {
                                        blockCountNotAirSideOne++;
                                        // Bukkit.broadcastMessage("Increased
                                        // one");
                                    }
                                    Location two = original.add(-plugin.doubleBlockNum * 2, 0, 0);
                                    if ((int) toX == two.getBlockX() || ((int) toZ == two.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockTwo = true;
                                    }
                                    if (!two.getBlock().isPassable() && sameBlockTwo) {
                                        blockCountNotAirSideTwo++;
                                        // Bukkit.broadcastMessage("Increased
                                        // two");
                                    }
                                    Location three = original.add(plugin.doubleBlockNum, 0, plugin.doubleBlockNum);
                                    if ((int) toX == three.getBlockX() || ((int) toZ == three.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockThree = true;
                                    }
                                    if (!three.getBlock().isPassable() && sameBlockThree) {
                                        blockCountNotAirSideThree++;
                                        // Bukkit.broadcastMessage("Increased
                                        // three");
                                    }
                                    Location four = original.add(0, 0, -plugin.doubleBlockNum * 2);
                                    if ((int) toX == four.getBlockX() || ((int) toZ == four.getBlockZ() && (int) toX != completeOriginalPosition.getBlockX()) || (int) toZ != completeOriginalPosition.getBlockZ()) {
                                        sameBlockFour = true;
                                    }
                                    if (!four.getBlock().isPassable() && sameBlockFour) {
                                        blockCountNotAirSideFour++;
                                        // Bukkit.broadcastMessage("Increased
                                        // four");
                                    }
                                    if (num == num2) {
                                        count++;
                                        // Bukkit.broadcastMessage("Increased
                                        // C");
                                    }
                                }
                            }
                            if ((plugin.allowedBlockTillFlagNum == 0 || blockCountNotAirSideOne == plugin.allowedBlockTillFlagNum || blockCountNotAirSideTwo == plugin.allowedBlockTillFlagNum || blockCountNotAirSideThree == plugin.allowedBlockTillFlagNum || blockCountNotAirSideFour == plugin.allowedBlockTillFlagNum) && count >= plugin.baritoneLoopThresholdC && count <= plugin.minFlagCountC) {
                                playerInstance.counterC++;
                                // Bukkit.broadcastMessage("increased
                                // counter");
                            }
                            if (playerInstance.counterC >= plugin.baritoneFlagC) {
                                plugin.notify.notify(player, plugin.message.type(20), plugin.message.cheat(3), plugin.highString);
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(20), plugin.message.cheat(3));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(37, 3, 5);
                                }
                                playerInstance.counterC= 0;
                            }
                            playerInstance.baritoneJumpSpeedsOneC= new ArrayList<Double>();
                            playerInstance.baritoneJumpSpeedsTwoC= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordOneC= new ArrayList<Double>();
                            playerInstance.baritoneSpeedYCoordTwoC= new ArrayList<Double>();
                            playerInstance.locationC= new ArrayList<Location>();
                            /*
                             * for (int a = 0; a != playerInstance.packetSamples;
                             * a++) {
                             * baritoneJumpSpeeds.remove(0;
                             * }
                             */
                        }
                    }
                } catch (Exception ee) {
                    // ee.printStackTrace();
                    playerInstance.baritoneJumpSpeedsOne= new ArrayList<Double>();
                    playerInstance.baritoneJumpSpeedsTwo= new ArrayList<Double>();
                    playerInstance.baritoneJumpSpeedsOneB= new ArrayList<Double>();
                    playerInstance.baritoneJumpSpeedsTwoB= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordOne= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordTwo= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordOneB= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordTwoB= new ArrayList<Double>();
                    playerInstance.baritoneJumpSpeedsOneC= new ArrayList<Double>();
                    playerInstance.baritoneJumpSpeedsTwoC= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordOneC= new ArrayList<Double>();
                    playerInstance.baritoneSpeedYCoordTwoC= new ArrayList<Double>();
                    playerInstance.locationA= new ArrayList<Location>();
                    playerInstance.locationB= new ArrayList<Location>();
                    playerInstance.locationC= new ArrayList<Location>();
                }
            }
            int upUntil = 0;
            int upJump = 0;
            final double speed_change_final = speed * 10;
            if (playerInstance.hit) {
                if (speed_change < plugin.velocitySpeedMin && !playerInstance.underBlock && playerBlock11 == Material.AIR && playerBlock22 == Material.AIR) {

                    if (player.getInventory().getItemInMainHand() != null) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
                            playerInstance.hit= false;
                            playerInstance.lowSpeed= 0;
                            return;
                        }
                    }

                    if (player.getInventory().getItemInOffHand() != null) {
                        if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                            playerInstance.hit= false;
                            playerInstance.lowSpeed= 0;
                            return;
                        }
                    }

                    playerInstance.lowSpeed++;
                    if (playerInstance.lowSpeed >= plugin.velocityFlagsUntilFlagging) {
                        plugin.notify.notify(player, plugin.message.type(17), plugin.message.cheat(1), plugin.notify.level(plugin.velocityFlagsUntilFlagging + plugin.roundedThresholdLow, plugin.velocityFlagsUntilFlagging + plugin.roundedThresholdMedium, plugin.velocityFlagsUntilFlagging + plugin.roundedThresholdHigh, playerInstance.lowSpeed));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(17), plugin.message.cheat(1));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(33, 3, 4);
                        }
                        playerInstance.hit= false;
                    }
                } else {
                    playerInstance.hit= false;
                    playerInstance.lowSpeed= 0;
                }
            }
            if (plugin.checkIrregularPitch && (pitch >= plugin.irregularPitchPositiveMax || pitch <= plugin.irregularPitchNegativeMax)) {
                plugin.notify.notify(player, plugin.message.type(4), plugin.message.cheat(1), "NANI");
                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(4), plugin.message.cheat(1));
                if (plugin.cancelEventIfHacking) {
                    playerInstance.cancelX= fromX;
                    playerInstance.cancelY= fromY;
                    playerInstance.cancelZ= fromZ;
                    playerInstance.setCancelled= true;
                }
                if (plugin.debugMode) {
                    plugin.message.Messages(2, 3, 4);
                }
            }
            if (Locationplayerr == Material.ICE || Locationplayerr == Material.FROSTED_ICE || Locationplayerr == Material.BLUE_ICE || Locationplayerrd == Material.ICE || Locationplayerrd == Material.FROSTED_ICE || Locationplayerrd == Material.BLUE_ICE || Locationplayerr == Material.PACKED_ICE || Locationplayerrd == Material.PACKED_ICE || playerBlock11 == Material.ICE || playerBlock11 == Material.FROSTED_ICE || playerBlock11 == Material.BLUE_ICE || playerBlock11 == Material.PACKED_ICE) {
                playerInstance.underIce= true;
                playerInstance.underIceID= 100;
            } else if (playerInstance.underIceID != 0) {
                playerInstance.underIceID--;
            } else {
                playerInstance.underIce= false;
            }
            if (playerInstance.underIce) {
                speedIceIncrease = plugin.iceIncrease;
                if (LocationplayerYTop.toString().contains("SLAB")) {
                    speedIceIncrease = 8.5;
                }
            }
            if (insideVeh) {
                if (player.getVehicle().toString().equals("CraftBoat")) {
                    upJump = 1;
                    upUntil = 2;
                } else {
                    upUntil = 17;
                    upJump = 3;
                }
            }

            if (plugin.checkIrrMovement) {
                if (notNearLiquid && notAllowedFlight && !insideVeh && !playerInstance.elytraUse && isntRiptiding && !playerInstance.underBlock && !playerInstance.underIce && isntLevitating) {
                    if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                        x += player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() * 1.5;
                    }

                    if (playerInstance.irrMovementSetterB >= plugin.irrMovementPacketSamplesB) {
                        Location temp = playerInstance.beforePacketIrrB;
                        double X = temp.getX();
                        double Y = temp.getY();
                        double Z = temp.getZ();
                        double x1 = Y + plugin.irrYAxisIgnoreB;
                        double x2 = Y - plugin.irrYAxisIgnoreB;
                        double distance = Math.sqrt(Math.pow(toX - X, 2) + Math.pow(toY - Y, 2) + Math.pow(toZ - Z, 2));
                        if (distance > plugin.irrMaxDistanceB + x && toY < x1 && toY > x2) {
                            plugin.notify.notify(player, plugin.message.type(18), plugin.message.cheat(2), plugin.notify.level(plugin.irrMaxDistance + plugin.roundedThresholdLow, plugin.irrMaxDistance + plugin.roundedThresholdMedium, plugin.irrMaxDistance + plugin.roundedThresholdHigh, distance));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(18), plugin.message.cheat(2));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            if (plugin.debugMode) {
                                plugin.message.Messages(19, speed_change, 5);
                            }
                            playerInstance.irrMovementSetterB= 0;
                        }
                        playerInstance.irrMovementSetterB= 0;
                    }

                    if (playerInstance.irrMovementSetterB == 1) {
                        playerInstance.beforePacketIrrB= loc.clone();
                    }
                    playerInstance.irrMovementSetterB++;
                    if (playerInstance.irrMovementSetter >= plugin.irrMovementPacketSamples) {
                        playerInstance.irrMovementSetter= 0;
                        Location temp = playerInstance.beforePacketIrr;
                        double X = temp.getX();
                        double Y = temp.getY();
                        double Z = temp.getZ();
                        double x1 = Y + plugin.irrYAxisIgnore;
                        double x2 = Y - plugin.irrYAxisIgnore;
                        double distance = Math.sqrt(Math.pow(toX - X, 2) + Math.pow(toY - Y, 2) + Math.pow(toZ - Z, 2));
                        if (distance > plugin.irrMaxDistance + x && toY < x1 && toY > x2) {
                            plugin.notify.notify(player, plugin.message.type(18), plugin.message.cheat(1), plugin.notify.level(plugin.irrMaxDistance + plugin.roundedThresholdLow, plugin.irrMaxDistance + plugin.roundedThresholdMedium, plugin.irrMaxDistance + plugin.roundedThresholdHigh, distance));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(18), plugin.message.cheat(1));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            if (plugin.debugMode) {
                                plugin.message.Messages(19, speed_change, 5);
                            }
                            playerInstance.irrMovementSetter= 0;
                        }
                    }
                    if (playerInstance.irrMovementSetter == 1) {
                        playerInstance.beforePacketIrr= loc.clone();
                    }
                    playerInstance.irrMovementSetter++;
                } else {
                    playerInstance.irrMovementSetter= 0;
                    playerInstance.irrMovementSetterB= 0;
                }
            }

            if (plugin.checkBadPacketsA) {
                boolean ridingStrider = insideVeh && "CraftStrider".equals(player.getVehicle().toString());
                if (toY < fromY && playerInstance.playerOnGround && shulker && !ridingStrider) {
                    if (playerInstance.speedMboat == 0) {
                        playerInstance.badPacketsANum++;
                        if (playerInstance.badPacketsANum >= plugin.badPacketsALimiter) {

                            plugin.notify.notify(player, plugin.message.type(16), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.badPacketsALimiter, plugin.roundedThresholdMedium + plugin.badPacketsALimiter, plugin.roundedThresholdHigh + plugin.badPacketsALimiter, playerInstance.badPacketsANum));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(16), plugin.message.cheat(1));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                        }
                    }
                } else {
                    playerInstance.badPacketsANum= 0;
                }
            }

            x = 0;
            if (plugin.checkBHop) {
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    x += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                }
                if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    x += player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() / 2.5;
                }
                if (speed_change >= plugin.speedCheckMidAirAlternative + x + y + speedIceIncrease && Locationplayerr != Material.AIR && Locationplayerr.isSolid() && !locationPlayerbHopMaterial.isSolid() && notAllowedFlight && !playerInstance.elytraUse && isntRiptiding && !playerInstance.elytraUse && locationPlayerbHopMaterial != Material.SLIME_BLOCK && toY >= fromY && playerBlock11 != Material.WATER && playerBlock11 != Material.SLIME_BLOCK && !player.hasPotionEffect(PotionEffectType.LEVITATION) && !player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE) && playerBlock22 != Material.WATER && !insideVeh) {
                    if (playerInstance.bHopAlternative == null) {
                        playerInstance.bHopAlternative= 0.0;
                    }
                    playerInstance.bHopAlternative= playerInstance.bHopAlternative + 1.0;
                    if (playerInstance.bHopAlternative > genericSpeedMovementIncrease + plugin.BhopUntilHackingAlternative + x + y && !playerInstance.elytraUse) {
                        plugin.notify.notify(player, plugin.message.type(5), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.BhopUntilHackingAlternative + x + y, plugin.roundedThresholdMedium + plugin.BhopUntilHackingAlternative + x + y, plugin.roundedThresholdHigh + plugin.BhopUntilHackingAlternative + x + y, playerInstance.bHopAlternative));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(5), plugin.message.cheat(2));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(19, speed_change, 5);
                        }
                    }
                } else {
                    playerInstance.bHopAlternative= 0.0;
                }
                if (speed_change >= plugin.speedCheckMidAir + x + y + speedIceIncrease && Locationplayerrd != Material.AIR && Locationplayerrd.isSolid() && !Locationplayerr.isSolid() && toY > fromY && notAllowedFlight && !player.isGliding() && isntRiptiding && !playerInstance.elytraUse && Locationplayerr != Material.SLIME_BLOCK && playerBlock11 != Material.SLIME_BLOCK && playerBlock11 != Material.WATER && playerBlock22 != Material.WATER && !player.hasPotionEffect(PotionEffectType.LEVITATION) && !insideVeh && !playerInstance.elytraUse) {
                    if (playerInstance.playerInAirHop == null) {
                        playerInstance.playerInAirHop= 0.0;
                    }
                    playerInstance.playerInAirHop= playerInstance.playerInAirHop + 1.0;
                    if (playerInstance.playerInAirHop > genericSpeedMovementIncrease + plugin.BhopUntilHacking + x + y && !playerInstance.elytraUse && !insideVeh) {
                        plugin.notify.notify(player, plugin.message.type(5), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.BhopUntilHacking + x + y, plugin.roundedThresholdMedium + plugin.BhopUntilHacking + x + y, plugin.roundedThresholdHigh + plugin.BhopUntilHacking + x + y, playerInstance.playerInAirHop));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(5), plugin.message.cheat(1));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(3, speed_change, 5);
                        }
                    }
                } else {
                    playerInstance.playerInAirHop= 0.0;
                }
            }
            if (playerInstance.stepNum == null) {
                playerInstance.stepNum= 0.0;
            }
            if (playerInstance.stepNum == 2.0) {
                BukkitScheduler scheduler3 = Bukkit.getServer().getScheduler();
                scheduler3.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.stepNum= 0.0;
                    }
                }, 70);
            }

            if (plugin.checkStep && isntRiptiding && !player.isGliding() && notAllowedFlight && !playerInstance.elytraUse && !playerInstance.speedSlime) {
                int tof = (int) toY;
                int fromf = (int) fromY;
                int distance = tof - fromf;
                if (playerInstance.stepNum == null) {
                    playerInstance.stepNum= 0.0;
                }
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    x = player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                }
                double dis = Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toZ - fromZ, 2));
                if (Locationplayerr == Material.getMaterial("SLIME_BLOCK")) {
                    playerInstance.stepNum= 1.0;
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            playerInstance.stepNum= 0.0;
                        }
                    }, 70);
                }
                if (Locationplayerr != Material.getMaterial("SLIME_BLOCK") && playerInstance.stepNum == 0.0 && distance >= plugin.stepBlockHeight + x + y + bedrockIncrease / 5 && dis <= 3.0 && speed_change <= 41.0) {
                    plugin.notify.notify(player, plugin.message.type(6), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.stepBlockHeight, plugin.roundedThresholdMedium + plugin.stepBlockHeight, plugin.roundedThresholdHigh + plugin.stepBlockHeight, distance));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(6), plugin.message.cheat(1));
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.setCancelled= true;
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                    }
                    if (plugin.debugMode) {
                        plugin.message.Messages(4, distance, 5);
                    }
                }
            }
            x = 0;
            /*
             * if (playerInstance.checkLongJump) { if
             * (playerInstance.longJumpNum == null ||
             * player.isGliding() || (!notAllowedFlight) ||
             * player.isRiptiding()) { playerInstance.longJumpNum=
             * 0.0; } if
             * (player.hasPotionEffect(PotionEffectType.LEVITATION) ||
             * player.hasPotionEffect(PotionEffectType.SLOW_FALLING) ||
             * playerInstance.elytraUse || Locationplayer11 ==
             * Material.WATER || Locationplayer11 == Material.LAVA ||
             * insideVeh) { playerInstance.longJumpNum= 0.0; }
             * boolean isSolid = false; Bukkit.broadcastMessage("" +
             * playerInstance.longJumpNum); if((Locationplayerrfluid
             * != Material.getMaterial("AIR") &&
             * Locationplayerrfluid.isSolid() && Locationplayerrfluid1 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid1.isSolid() && Locationplayerrfluid2 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid2.isSolid() && Locationplayerrfluid3 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid3.isSolid() && Locationplayerrfluid4 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid4.isSolid() && Locationplayerrfluid5 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid5.isSolid() && Locationplayerrfluid6 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid6.isSolid() && Locationplayerrfluid7 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid7.isSolid() && Locationplayerrfluid8 !=
             * Material.getMaterial("AIR") &&
             * Locationplayerrfluid8.isSolid())) { isSolid = true;
             *
             * } if (playerInstance.longJumpNum == 0 && isSolid &&
             * toY < fromY) { playerInstance.longJumpNum= 0.0; int
             * toYY1 = (int) (1 + toY + playerInstance.longJumpBlockYNumTillIgnore;
             * int toYY2 = (int) (toY - playerInstance.longJumpBlockYNumTillIgnore;
             * double distance = Math.sqrt(Math.pow(toX -
             * playerInstance.longJumpFromX, 2) + Math.pow(toY -
             * playerInstance.longJumpFromY, 2) + Math.pow(toZ -
             * playerInstance.longJumpFromZ, 2)); if
             * (player.hasPotionEffect(PotionEffectType.JUMP)) { x = 1.5 *
             * player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
             * } if (player.hasPotionEffect(PotionEffectType.SPEED)) { x +=
             * 0.5 *
             * player.getPotionEffect(PotionEffectType.SPEED).getAmplifier()
             * ; } if (distance >= playerInstance.longJumpDistanceTillHacking + x +
             * y && playerInstance.longJumpFromY <= toYY1 &&
             * playerInstance.longJumpFromY >= toYY2 &&
             * !playerInstance.elytraUse) { plugin.notify.notify(player,
             * plugin.message.type(7), plugin.message.cheat(1),
             * plugin.notify.level( plugin.roundedThresholdLow +
             * playerInstance.longJumpDistanceTillHacking + x + y,
             * plugin.roundedThresholdMedium +
             * playerInstance.longJumpDistanceTillHacking + x + y,
             * plugin.roundedThresholdHigh +
             * playerInstance.longJumpDistanceTillHacking + x + y, distance));
             * plugin.violationChecker.violationChecker(player,
             * playerInstance.num++);
             * if (plugin.cancelEventIfHacking) {
             * playerInstance.cancelX= fromX;
             * playerInstance.cancelY= fromY;
             * playerInstance.cancelZ= fromZ; } if
             * (plugin.debugMode) { plugin.message.Messages(5, distance, 7);
             * } } } if (!isSolid && playerInstance.longJumpNum >= 1
             * || playerInstance.underBlock) {
             * playerInstance.longJumpNum= 0.0;
             * playerInstance.longJumpFromX= fromX;
             * playerInstance.longJumpFromY= fromY;
             * playerInstance.longJumpFromZ= fromZ; } if (isSolid &&
             * playerInstance.longJumpNum == 0) {
             * playerInstance.longJumpNum=
             * playerInstance.longJumpNum + 1.0;
             * playerInstance.longJumpFromX= fromX;
             * playerInstance.longJumpFromY= fromY;
             * playerInstance.longJumpFromZ= fromZ; } }
             */
            x = 0;
            if (plugin.checkPingSpoofing) {
                if (playerInstance.playerPing == null) {
                    playerInstance.playerPing= 0.0;
                }
                if (plugin.pingUntilPingSpoofing < playerInstance.playerPing) {
                    plugin.notify.notify(player, plugin.message.type(8), plugin.message.cheat(1), plugin.mediumString);
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(8), plugin.message.cheat(1));
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.setCancelled= true;
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                    }
                    if (plugin.debugMode) {
                        plugin.message.Messages(6, 7, 8);
                    }
                }
            }

            if (plugin.checkGlide && playerBlock11 == Material.AIR && toY < fromY && playerBlock22 == Material.AIR && !player.hasPotionEffect(PotionEffectType.LEVITATION) && !player.hasPotionEffect(PotionEffectType.SLOW_FALLING) && isntRiptiding && notAllowedFlight && !playerInstance.elytraUse && !insideVeh && nearSlowableBlock && playerInstance.nearBoat && topWeb && notNearLiquid && shulker && !Locationplayerr.isSolid()) {
                if (!isNearSolidBlock && Locationplayerrd == Material.AIR && Locationplayer22d == Material.AIR && Locationplayer33d == Material.AIR && Locationplayer44d == Material.AIR && Locationplayer55d == Material.AIR && Locationplayer66d == Material.AIR && Locationplayer77d == Material.AIR && Locationplayer88d == Material.AIR && Locationplayer99d == Material.AIR && !playerInstance.speedSlime) {
                    if (playerInstance.glideCountB >= plugin.glideUntilHackingB && speedY < plugin.speedMinimumWhenDescendingB) {
                        plugin.notify.notify(player, plugin.message.type(9), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedMinimumWhenDescendingB, plugin.roundedThresholdMedium + plugin.speedMinimumWhenDescendingB, plugin.roundedThresholdHigh + plugin.speedMinimumWhenDescendingB, speedY));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(9), plugin.message.cheat(2));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(28, speedY, 8);
                        }
                    }
                    playerInstance.glideCountB++;
                } else {
                    playerInstance.glideCountB= 0;
                }
                if (speedY < plugin.speedMinimumWhenDescending && playerBlock11 != Material.WEEPING_VINES && playerBlock22 != Material.WEEPING_VINES && playerBlock33 != Material.WEEPING_VINES && playerBlock44 != Material.WEEPING_VINES && playerBlock55 != Material.WEEPING_VINES && playerBlock66 != Material.WEEPING_VINES && playerBlock77 != Material.WEEPING_VINES && playerBlock88 != Material.WEEPING_VINES && playerBlock99 != Material.WEEPING_VINES && playerBlock100 != Material.WEEPING_VINES && playerBlock111 != Material.WEEPING_VINES && location3A != Material.WEEPING_VINES && location44 != Material.WEEPING_VINES && location55 != Material.WEEPING_VINES && location66 != Material.WEEPING_VINES && location77 != Material.WEEPING_VINES && location88 != Material.WEEPING_VINES && location99 != Material.WEEPING_VINES && location100 != Material.WEEPING_VINES

                        && playerBlock11 != Material.GLOW_BERRIES && playerBlock22 != Material.GLOW_BERRIES && playerBlock33 != Material.GLOW_BERRIES && playerBlock44 != Material.GLOW_BERRIES && playerBlock55 != Material.GLOW_BERRIES && playerBlock66 != Material.GLOW_BERRIES && playerBlock77 != Material.GLOW_BERRIES && playerBlock88 != Material.GLOW_BERRIES && playerBlock99 != Material.GLOW_BERRIES && playerBlock100 != Material.GLOW_BERRIES && playerBlock111 != Material.GLOW_BERRIES && location3A != Material.GLOW_BERRIES && location44 != Material.GLOW_BERRIES && location55 != Material.GLOW_BERRIES && location66 != Material.GLOW_BERRIES && location77 != Material.GLOW_BERRIES && location88 != Material.GLOW_BERRIES && location99 != Material.GLOW_BERRIES && location100 != Material.GLOW_BERRIES

                        && playerBlock11 != Material.TWISTING_VINES && playerBlock22 != Material.TWISTING_VINES && playerBlock33 != Material.TWISTING_VINES && playerBlock44 != Material.TWISTING_VINES && playerBlock55 != Material.TWISTING_VINES && playerBlock66 != Material.TWISTING_VINES && playerBlock77 != Material.TWISTING_VINES && playerBlock88 != Material.TWISTING_VINES && playerBlock99 != Material.TWISTING_VINES && playerBlock100 != Material.TWISTING_VINES && playerBlock111 != Material.TWISTING_VINES && location3A != Material.TWISTING_VINES && location44 != Material.TWISTING_VINES && location55 != Material.TWISTING_VINES && location66 != Material.TWISTING_VINES && location77 != Material.TWISTING_VINES && location88 != Material.TWISTING_VINES && location99 != Material.TWISTING_VINES && location100 != Material.TWISTING_VINES && Locationplayerr != Material.SHULKER_BOX && Locationplayerrfluid != Material.VINE && Locationplayerrfluid != Material.LADDER && Locationplayerr != Material.LADDER && Locationplayerr != Material.VINE && Locationplayerr != Material.CAVE_VINES) {
                    playerInstance.glidePlayer= playerInstance.glidePlayer + 1.0;
                    if (playerInstance.glidePlayer > plugin.glideUntilHacking) {
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(9), plugin.message.cheat(1));
                        plugin.notify.notify(player, plugin.message.type(9), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.glideUntilHacking, plugin.roundedThresholdMedium + plugin.glideUntilHacking, plugin.roundedThresholdHigh + plugin.glideUntilHacking, playerInstance.glidePlayer));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(28, speedY, 8);
                        }
                    }
                }
            } else {
                playerInstance.glidePlayer= 0.0;
            }
            if (plugin.checkSpider) {
                if (playerInstance.spiderCountNum == null || playerInstance.spiderState == null || playerInstance.elytraUse) {
                    playerInstance.spiderCountNum= 0.0;
                }
                if (notAllowedFlight && !player.isGliding() && isntRiptiding) {
                    if (Locationplayerr == Material.getMaterial("SLIME_BLOCK")) {
                        playerInstance.spiderState= 0.0;
                        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                        scheduler.runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                playerInstance.spiderState= 1.0;
                            }
                        }, 80);
                    }
                    if (player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || player.isRiptiding()) {
                        playerInstance.spiderState= 0.0;
                        playerInstance.spiderCountNum= 0.0;
                        playerInstance.playerUntilSpiderHacking= -10.0;
                    } else {
                        playerInstance.spiderState= 1.0;
                    }
                    double wad = 0.0;
                    if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                        wad += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() * 3.5;
                    }

                    if (playerInstance.spiderState == 1.0) {
                        if (((playerBlock11 == Material.getMaterial("AIR") && playerBlock22 == Material.getMaterial("AIR") && Locationplayerr == Material.getMaterial("AIR") && location3A != Material.getMaterial("AIR") && toY > fromY) || (playerBlock11 == Material.getMaterial("AIR") && playerBlock22 == Material.getMaterial("AIR") && Locationplayerr == Material.getMaterial("AIR") && location44 != Material.getMaterial("AIR") && toY > fromY) || (playerBlock11 == Material.getMaterial("AIR") && Locationplayerr == Material.getMaterial("AIR") && location55 != Material.getMaterial("AIR") && toY > fromY) || (playerBlock11 == Material.getMaterial("AIR") && playerBlock22 == Material.getMaterial("AIR")

                                && Locationplayerr == Material.getMaterial("AIR") && location66 != Material.getMaterial("AIR") && toY > fromY)) && location3A != Material.WEEPING_VINES && location44 != Material.WEEPING_VINES && location55 != Material.WEEPING_VINES && location66 != Material.WEEPING_VINES && playerBlock33 != Material.WEEPING_VINES && playerBlock55 != Material.WEEPING_VINES && playerBlock66 != Material.WEEPING_VINES && playerBlock66 != Material.WEEPING_VINES && location3A != Material.GLOW_BERRIES && location44 != Material.GLOW_BERRIES && location55 != Material.GLOW_BERRIES && location66 != Material.GLOW_BERRIES && playerBlock33 != Material.GLOW_BERRIES && playerBlock55 != Material.GLOW_BERRIES && playerBlock66 != Material.GLOW_BERRIES && playerBlock66 != Material.GLOW_BERRIES

                                && location3A != Material.TWISTING_VINES && location44 != Material.TWISTING_VINES && location55 != Material.TWISTING_VINES && location66 != Material.TWISTING_VINES && playerBlock33 != Material.TWISTING_VINES && playerBlock55 != Material.TWISTING_VINES && playerBlock66 != Material.TWISTING_VINES && !topNotLiquid && notNearLiquid && !playerInstance.speedSlime && location3A != Material.SCAFFOLDING && location44 != Material.SCAFFOLDING && location55 != Material.SCAFFOLDING && location66 != Material.SCAFFOLDING && playerBlock33 != Material.SCAFFOLDING && playerBlock55 != Material.SCAFFOLDING && playerBlock66 != Material.SCAFFOLDING

                        ) {
                            playerInstance.spiderCountNum= playerInstance.spiderCountNum + 1.0;
                            if (playerInstance.spiderCountNum > plugin.spiderUpUntilHacking + wad) {
                                plugin.notify.notify(player, plugin.message.type(10), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.spiderUpUntilHacking, plugin.roundedThresholdMedium + plugin.spiderUpUntilHacking, plugin.roundedThresholdHigh + plugin.spiderUpUntilHacking, playerInstance.spiderCountNum));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(10), plugin.message.cheat(1));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.setCancelled= true;

                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(7, 8, 9);
                                }
                            }
                        } else {
                            playerInstance.spiderCountNum= 0.0;
                        }
                    }

                    String placeholder = String.valueOf(speed_change);
                    if (placeholder.length() >= 4) {
                        placeholder = String.valueOf(speed_change).substring(0, 3);
                    } else {
                        placeholder = String.valueOf(speed_change);
                    }
                    double placeholder_num = Double.parseDouble(placeholder);
                    if (toY > fromY && placeholder_num == playerInstance.lastUpSpeed && nearClimableBlock && playerBlock11 == Material.AIR && playerBlock22 == Material.AIR && notNearLiquid && !waterColm && !insideVeh && location3A != Material.SCAFFOLDING && location44 != Material.SCAFFOLDING && location55 != Material.SCAFFOLDING && location66 != Material.SCAFFOLDING && playerBlock33 != Material.SCAFFOLDING && playerBlock55 != Material.SCAFFOLDING && playerBlock66 != Material.SCAFFOLDING) {
                        playerInstance.playerUntilSpiderHacking= playerInstance.playerUntilSpiderHacking + 1.0;
                        if (playerInstance.playerUntilSpiderHacking > plugin.spiderUpUntilHackingAlternative) {
                            plugin.notify.notify(player, plugin.message.type(10), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.spiderUpUntilHackingAlternative, plugin.roundedThresholdMedium + plugin.spiderUpUntilHackingAlternative, plugin.roundedThresholdHigh + plugin.spiderUpUntilHackingAlternative, playerInstance.playerUntilSpiderHacking));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(10), plugin.message.cheat(2));
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            if (plugin.debugMode) {
                                plugin.message.Messages(8, 9, 10);
                            }
                        }
                    } else {
                        playerInstance.playerUntilSpiderHacking= 0.0;
                        playerInstance.lastUpSpeed= placeholder_num;
                    }
                }
            }
            x = 0;
            if (plugin.checkNoFall && playerInstance.stepNum != 2.0 && notAllowedFlight && isntRiptiding && !playerInstance.elytraUse) {
                if (player.isGliding() || player.isRiptiding() || insideVeh || !notAllowedFlight || Locationplayerr == Material.WATER || playerBlock11 == Material.WATER || Locationplayerr == Material.LAVA || playerBlock11 == Material.LAVA || playerBlock11.createBlockData() instanceof Waterlogged || speed_change_final >= 41.0) {
                    playerInstance.noFall= 1.0;
                }
                if (Locationplayerr == Material.getMaterial("SLIME_BLOCK") || Locationplayerr.createBlockData() instanceof Waterlogged) {
                    playerInstance.noFall= 1.0;
                }

                int tof = (int) toY;
                int fromf = (int) fromY;
                if (playerInstance.noFall >= plugin.noFallBlockHeight && fromf == tof) {
                    playerInstance.noFall= 0.0;
                    if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING) || Locationplayerr == Material.getMaterial("SLIME_BLOCK") || Locationplayerr == Material.getMaterial("WATER") || Locationplayerr == Material.getMaterial("LAVA") || !notNearLiquid || playerBlock11 == Material.WATER || playerBlock22 == Material.WATER || playerBlock11 == Material.LAVA || playerBlock22 == Material.LAVA || Locationplayerr.createBlockData() instanceof Waterlogged || playerBlock11.createBlockData() instanceof Waterlogged || !isntLevitating) {
                        playerInstance.noFall= 1.0;
                    }
                    if (playerInstance.noFall == 0.0) {
                        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                boolean check = false;
                                if (!playerInstance.playerEnableAntiCheat) {
                                    playerInstance.noFall= -1.0;
                                    return;
                                }
                                if ((toY % 1 == 0 && fromY % 1 != 0) || toY == fromY) {
                                    check = true;
                                }
                                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                                    int x = player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                                    if (x >= 5 && x <= 15) {
                                        return;
                                    }
                                }
                                playerInstance.healthWhenFallen= player.getHealth();
                                if (!playerInstance.lastFallDamage && speed_change_final <= 41 && !player.hasPotionEffect(PotionEffectType.SLOW_FALLING) && !player.getAllowFlight() && !player.isFlying() && Locationplayerr != Material.getMaterial("SLIME_BLOCK") && Locationplayerr != Material.getMaterial("WATER") && Locationplayerr != Material.getMaterial("LAVA") && playerBlock11 != Material.WATER && !(Locationplayerr.createBlockData() instanceof Waterlogged) && playerInstance.noFall != -1.0 && check) {
                                    if (plugin.cancelEventIfHacking) {
                                        playerInstance.cancelX= fromX;
                                        playerInstance.cancelY= fromY;
                                        playerInstance.cancelZ= fromZ;
                                        playerInstance.setCancelled= true;
                                    }
                                    plugin.notify.notify(player, plugin.message.type(11), plugin.message.cheat(1), plugin.mediumString);
                                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(11), plugin.message.cheat(1));
                                    if (plugin.debugMode) {
                                        plugin.message.Messages(9, 10, 11);
                                    }
                                }
                            }
                        }, plugin.noFallTimer);
                    }

                } else if (tof < fromf) {
                    playerInstance.noFall= playerInstance.noFall + 1.0;
                    playerInstance.healthWhenFalling= player.getHealth();
                } else {
                    playerInstance.noFall= 0.0;
                }
            }

            if (plugin.noSlowDownCheck && !playerInstance.elytraUse) {
                if (!notAllowedFlight) {
                    return;
                }
                if (speed_change > plugin.speedCheckWhenCrouching && player.isSneaking() && toY == fromY) {
                    playerInstance.noSlowShiftNum= 1.0 + playerInstance.noSlowShiftNum;
                } else if (!player.isSneaking()) {
                    playerInstance.noSlowShiftNum= 0.0;
                }
                if (plugin.checkNoSlowDownC && player.isSprinting() && player.getFoodLevel() <= 5) {
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                        playerInstance.setCancelled= true;
                    }
                    plugin.notify.notify(player, plugin.message.type(12), plugin.message.cheat(3), plugin.highString);
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(12), plugin.message.cheat(3));
                    if (plugin.debugMode) {
                        plugin.message.Messages(39, 10, 10);
                    }
                }

                double dda = 0;
                if (playerInstance.allMovementChange == 1.0) {
                    dda = plugin.smartCombatMovementChangeNumber;
                }
                if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    dda += player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() / 0.3;
                }
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    dda += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() / 0.3;
                }
                if (speed_change > plugin.noSlowDownDMaxSpeed + dda + speedIceIncrease && !insideVeh && player.getFallDistance() < 2 && (player.isBlocking() || player.isHandRaised())) {
                    playerInstance.noSlowDownD++;

                    if (playerInstance.noSlowDownD > plugin.noSlowDownDUntilCheat) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        plugin.notify.notify(player, plugin.message.type(12), plugin.message.cheat(4), plugin.notify.level(plugin.noSlowDownDMaxSpeed + plugin.roundedThresholdLow, plugin.noSlowDownDMaxSpeed + plugin.roundedThresholdMedium, plugin.noSlowDownDMaxSpeed + +plugin.roundedThresholdHigh, speed_change));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(12), plugin.message.cheat(4));
                        if (plugin.debugMode) {
                            plugin.message.Messages(49, speed_change, 10);
                        }

                    }
                } else {
                    playerInstance.noSlowDownD= 0;
                }

                if (speed_change > plugin.speedCheckWhenCrouching && player.isSneaking() && toY == fromY && playerInstance.noSlowShiftNum > plugin.shiftUntilCheckingNoSlow && !player.isSwimming()) {
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                        playerInstance.setCancelled= true;
                    }
                    plugin.notify.notify(player, plugin.message.type(12), plugin.message.cheat(1), plugin.notify.level(plugin.speedCheckWhenCrouching + plugin.roundedThresholdLow, plugin.speedCheckWhenCrouching + plugin.roundedThresholdMedium, plugin.speedCheckWhenCrouching + +plugin.roundedThresholdHigh, speed_change));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(12), plugin.message.cheat(1));
                    if (plugin.debugMode) {
                        plugin.message.Messages(10, 10, 10);
                    }
                }
                if ((playerBlock11 == Material.getMaterial("COBWEB") && speed_change > plugin.noSlowDownSpeedNum) || (playerBlock22 == Material.getMaterial("COBWEB") && speed_change > plugin.noSlowDownSpeedNum) || (playerBlock11 == Material.getMaterial("SWEET_BERRY_BUSH") && speed_change > plugin.noSlowDownSpeedNum && toY == fromY) || (playerBlock22 == Material.getMaterial("SWEET_BERRY_BUSH") && speed_change > plugin.noSlowDownSpeedNum && toY == fromY)) {
                    playerInstance.inslowableBlockUntilFlag++;
                    if (playerInstance.inslowableBlockUntilFlag >= plugin.inSlowableBlockUntilCheckB) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        plugin.notify.notify(player, plugin.message.type(12), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.inSlowableBlockUntilCheckB, plugin.roundedThresholdMedium + plugin.inSlowableBlockUntilCheckB, plugin.roundedThresholdHigh + plugin.inSlowableBlockUntilCheckB, playerInstance.inslowableBlockUntilFlag));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(12), plugin.message.cheat(2));
                        if (plugin.debugMode) {
                            plugin.message.Messages(10, 10, 10);
                        }
                    }
                } else {
                    playerInstance.inslowableBlockUntilFlag= 0;
                }
            }

            x = 0;
            double d = 0;
            double z = 0;
            double levi = 0;
            double speed_change_slime_check = speed_change;
            if (Locationplayerrfluid == Material.SLIME_BLOCK || Locationplayerrfluid1 == Material.SLIME_BLOCK || Locationplayerrfluid2 == Material.SLIME_BLOCK || Locationplayerrfluid3 == Material.SLIME_BLOCK || Locationplayerrfluid4 == Material.SLIME_BLOCK || Locationplayerrfluid5 == Material.SLIME_BLOCK || Locationplayerrfluid6 == Material.SLIME_BLOCK || Locationplayerrfluid7 == Material.SLIME_BLOCK || location44 == Material.SLIME_BLOCK || location55 == Material.SLIME_BLOCK || location66 == Material.SLIME_BLOCK || location77 == Material.SLIME_BLOCK || playerBlock33D == Material.SLIME_BLOCK || playerBlock44D == Material.SLIME_BLOCK || playerBlock55D == Material.SLIME_BLOCK || playerBlock66D == Material.SLIME_BLOCK || loc.clone().add(1, 0, 0).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(0, 0, 1).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(-1, 0, 0).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(0, 0, -1).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(1, 1, 0).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(0, 1, 1).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(-1, 1, 0).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(0, 1, -1).getBlock().getType() == Material.SLIME_BLOCK || Locationplayerrfluid8 == Material.SLIME_BLOCK || Locationplayer11 == Material.SLIME_BLOCK || Locationplayer22 == Material.SLIME_BLOCK || Locationplayer33 == Material.SLIME_BLOCK || Locationplayer44 == Material.SLIME_BLOCK || Locationplayer55 == Material.SLIME_BLOCK || Locationplayer66 == Material.SLIME_BLOCK || Locationplayer77 == Material.SLIME_BLOCK || Locationplayer99 == Material.SLIME_BLOCK || Locationplayerr == Material.SLIME_BLOCK || playerBlock11 == Material.SLIME_BLOCK || playerBlock22 == Material.SLIME_BLOCK || ((loc.clone().add(0, -3.0, 0).getBlock().getType() == Material.SLIME_BLOCK || loc.clone().add(0, -2.5, 0).getBlock().getType() == Material.SLIME_BLOCK) && toY < fromY)) {
                playerInstance.speedSlime= true;
                speed_change = 0.0;
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                if (playerInstance.slimeID != null) {
                    scheduler.cancelTask(playerInstance.slimeID);
                    playerInstance.semiPred= false;
                    playerInstance.semiPredID= 200;
                }
                int id = scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.speedSlime= false;
                        playerInstance.slimeID= null;
                    }
                }, 30);
                playerInstance.slimeID= id;
            }

            if (plugin.checkIrrStartup) {
                double ad = 0.0;
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    ad += player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                }
                if (playerInstance.underStair) {
                    ad += 1.0;
                }

                if (playerInstance.isFloodGatePlayer) {
                    ad += 1.5;
                }

                double abs = Math.abs(speed_change - playerInstance.lastSpeed);
                boolean aaa = !playerInstance.speedSlime && abs > plugin.speedMaxB + ad && !playerInstance.lastFallDamage && !insideVeh && playerInstance.speedMboat == 0 && !playerInstance.elytraUse && notNearLiquid;
                boolean da = !playerInstance.speedSlime && abs > plugin.speedMaxA + ad && !playerInstance.lastFallDamage && !insideVeh && playerInstance.speedMboat == 0 && !playerInstance.elytraUse && notNearLiquid && !(abs > 10 && Locationplayerr == Material.AIR && toY < fromY);
                boolean nearClimable = playerBlock11 != Material.VINE && playerBlock22 != Material.VINE && playerBlock11 != Material.GLOW_BERRIES && playerBlock22 != Material.GLOW_BERRIES && playerBlock11 != Material.WEEPING_VINES && playerBlock22 != Material.WEEPING_VINES && playerBlock11 != Material.TWISTING_VINES && playerBlock22 != Material.TWISTING_VINES && playerBlock11 != Material.LADDER && playerBlock22 != Material.LADDER && nearClimableBlock

                        && location77 != Material.VINE && location77 != Material.GLOW_BERRIES && location77 != Material.WEEPING_VINES && location77 != Material.TWISTING_VINES && location77 != Material.SCAFFOLDING

                        && location88 != Material.VINE && location88 != Material.GLOW_BERRIES && location88 != Material.WEEPING_VINES && location88 != Material.TWISTING_VINES && location88 != Material.SCAFFOLDING

                        && location99 != Material.VINE && location99 != Material.GLOW_BERRIES && location99 != Material.WEEPING_VINES && location99 != Material.TWISTING_VINES && location99 != Material.SCAFFOLDING

                        && location100 != Material.VINE && location100 != Material.GLOW_BERRIES && location100 != Material.WEEPING_VINES && location100 != Material.TWISTING_VINES && location100 != Material.SCAFFOLDING && nearSlowableBlock && shulker && playerBlock11 != Material.POWDER_SNOW;
                if (da && !playerInstance.allMovementChangeRunning && nearClimable) {

                    plugin.notify.notify(player, plugin.message.type(29), plugin.message.cheat(1), plugin.notify.level(plugin.speedMaxA + plugin.roundedThresholdLow, plugin.speedMaxA + plugin.roundedThresholdMedium, plugin.speedMaxA + plugin.roundedThresholdHigh, abs));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(29), plugin.message.cheat(1));
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                        playerInstance.setCancelled= true;
                    }
                    if (plugin.debugMode) {
                        plugin.message.Messages(46, abs, 4);
                    }
                }

                if (aaa && !playerInstance.allMovementChangeRunning && nearClimable) {
                    int a = playerInstance.irrStartupLimiter;
                    if (a >= plugin.limiterB) {

                        plugin.notify.notify(player, plugin.message.type(29), plugin.message.cheat(2), plugin.notify.level(plugin.speedMaxB + plugin.roundedThresholdLow, plugin.speedMaxB + plugin.roundedThresholdMedium, plugin.speedMaxB + plugin.roundedThresholdHigh, abs));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(29), plugin.message.cheat(2));
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        if (plugin.debugMode) {
                            plugin.message.Messages(44, abs, 4);
                        }

                    }

                    playerInstance.irrStartupLimiter= a + 1;

                } else {
                    playerInstance.irrStartupLimiter= 0;
                }

                playerInstance.lastSpeed= speed_change;
            }
            if (plugin.checkSpeed) {
                if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    x += plugin.speedAmplifierThreshold * player.getPotionEffect(PotionEffectType.SPEED).getAmplifier();
                    d = 1.5;
                }
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    z += plugin.jumpAmplifierThreshold * player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                }
                if (player.hasPotionEffect(PotionEffectType.LEVITATION))
                    levi += player.getPotionEffect(PotionEffectType.LEVITATION).getAmplifier();

                if (Locationplayerr == Material.getMaterial("SLIME_BLOCK") || Locationplayerr == Material.getMaterial("WATER") || Locationplayerr == Material.getMaterial("LAVA")) {
                    speed_change = 1.0;
                    if (toY == fromY) {
                        playerInstance.onSlime= playerInstance.onSlime + 1.0;
                    }
                    if (playerInstance.onSlime > plugin.onSlimeTillCheckSpeed) {
                        speed_change = speed_change_slime_check;
                    }
                } else {
                    playerInstance.onSlime= 0.0;
                }

                if (insideVeh) {
                    int add = 0;
                    if (playerInstance.isFloodGatePlayer)
                        add += 3;
                    if (speed_change > plugin.maxSpeedBoatQ + add && player.getVehicle().toString().contains("Boat") && !notNearLiquid && toY == fromY && !playerInstance.underIce) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                            playerInstance.setCancelled= true;
                        }
                        plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(17), plugin.notify.level(plugin.roundedThresholdLow + x + y + plugin.maxSpeedBoatQ, plugin.roundedThresholdMedium + x + y + plugin.maxSpeedBoatQ, plugin.roundedThresholdHigh + x + y + plugin.maxSpeedBoatQ, speed_change));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(17));
                        if (plugin.debugMode) {
                            plugin.message.Messages(47, speed_change, 10);
                        }
                    }
                    if (player.getVehicle().toString().contains("Boat") && playerInstance.underIce) {
                        if (speed_change > x + y + plugin.maxSpeedBoatP && toY == fromY) {
                            playerInstance.gotInBoatTillCheckingSpeed= playerInstance.gotInBoatTillCheckingSpeed + 1.0;
                            if (playerInstance.gotInBoatTillCheckingSpeed >= 2) {
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(16), plugin.notify.level(plugin.roundedThresholdLow + x + y + plugin.speedMaxInVehicle, plugin.roundedThresholdMedium + x + y + plugin.speedMaxInVehicle, plugin.roundedThresholdHigh + x + y + plugin.speedMaxInVehicle, speed_change));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(16));
                                if (plugin.debugMode) {
                                    plugin.message.Messages(47, speed_change, 10);
                                }
                            }
                        }

                    } else {

                        if (speed_change > x + y + plugin.speedMaxInVehicle && toY == fromY) {

                            playerInstance.gotInBoatTillCheckingSpeed= playerInstance.gotInBoatTillCheckingSpeed + 1.0;
                            if (playerInstance.gotInBoatTillCheckingSpeed >= 2) {
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(4), plugin.notify.level(plugin.roundedThresholdLow + x + y + plugin.speedMaxInVehicle, plugin.roundedThresholdMedium + x + y + plugin.speedMaxInVehicle, plugin.roundedThresholdHigh + x + y + plugin.speedMaxInVehicle, speed_change));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(4));
                                if (plugin.debugMode) {
                                    plugin.message.Messages(11, speed_change, 10);
                                }
                            }
                        }
                    }
                } else {
                    if (playerInstance.playerUsingElytra == null) {
                        playerInstance.playerUsingElytra= false;
                    }
                    double knockbackSmart = 0.0;
                    double punchSmart = 0.0;
                    if (playerInstance.smartMovementKnockback) {
                        knockbackSmart = plugin.smartCombatMovementChangeSpeedKnockbackLM;
                    }
                    if (playerInstance.smartMovementPunch) {
                        punchSmart = plugin.smartCombatMovementChangeSpeedL;
                    }

                    playerInstance.gotInBoatTillCheckingSpeed= 0.0;
                    if ((location3A != Material.SLIME_BLOCK || location44 != Material.SLIME_BLOCK || location55 != Material.SLIME_BLOCK || location66 != Material.SLIME_BLOCK || playerBlock33 != Material.SLIME_BLOCK || playerBlock55 != Material.SLIME_BLOCK || playerBlock44 != Material.SLIME_BLOCK || playerBlock66 != Material.SLIME_BLOCK || playerBlock11 != Material.SLIME_BLOCK || playerBlock22 != Material.SLIME_BLOCK || playerBlock77 != Material.SLIME_BLOCK || playerBlock88 != Material.SLIME_BLOCK) && isntRiptiding) {
                        if (((Locationplayerr != Material.getMaterial("AIR") && Locationplayerr == Material.ICE) || Locationplayerr == Material.PACKED_ICE || (Locationplayerr == Material.BLUE_ICE && !player.isGliding() && notAllowedFlight)) && speed_change > x + y + plugin.speedMaxOnIce && toY == fromY && !playerInstance.elytraUse) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(6), plugin.notify.level(plugin.roundedThresholdLow + x + y + plugin.speedMaxOnIce, plugin.roundedThresholdMedium + x + y + plugin.speedMaxOnIce, plugin.roundedThresholdHigh + x + y + plugin.speedMaxOnIce, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(6));
                            if (plugin.debugMode) {
                                plugin.message.Messages(13, speed_change, 10);
                            }
                        }
                        double dwa = 0;
                        if (playerInstance.underStair) {
                            dwa += 1.0;
                        }

                        double awd = 0.0;
                        if (player.getInventory().getBoots() != null) {
                            if (player.getInventory().getBoots().getEnchantments().containsKey(Enchantment.SOUL_SPEED)) {
                                awd += player.getInventory().getBoots().getEnchantmentLevel(Enchantment.SOUL_SPEED);
                            }
                        }

                        if (LocationplayerYTop == Material.AIR && playerInstance.speedMboat == 0 && Locationplayerr.isSolid() && playerInstance.notInsideBoat <= 0 && toY >= fromY && !playerInstance.speedSlime && !playerInstance.underBlock && speed_change > plugin.speedMaxOnBlock + genericSpeedMovementIncrease + dwa + d + x + y + knockbackSmart + speedIceIncrease + z + awd && !playerInstance.elytraUse && notNearLiquid && isntRiptiding) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                                playerInstance.setCancelled= true;
                            }
                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(13), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedMaxOnBlock + dwa + d + x + y + awd + knockbackSmart + speedIceIncrease + genericSpeedMovementIncrease + z, plugin.roundedThresholdMedium + plugin.speedMaxOnBlock + dwa + x + y + awd + knockbackSmart + speedIceIncrease + genericSpeedMovementIncrease + z, plugin.roundedThresholdHigh + plugin.speedMaxOnBlock + dwa + x + y + awd + knockbackSmart + speedIceIncrease + genericSpeedMovementIncrease + z, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(13));
                            if (plugin.debugMode) {
                                plugin.message.Messages(31, speed_change, 10);
                            }
                        }
                        Material bubble = loc.clone().add(0, -2, 0).getBlock().getType();
                        if (-speedY >= plugin.speedMaxYN + x + y + levi + z + speedIceIncrease && !playerInstance.elytraUse && notNearLiquid && isntRiptiding && !playerInstance.underStair && Locationplayerr != Material.WATER && Locationplayerr != Material.BUBBLE_COLUMN && bubble != Material.BUBBLE_COLUMN && bubble != Material.WATER && !playerInstance.speedSlime && !waterColm) {
                            playerInstance.speedMaxYNFlagHashMap++;
                            if (playerInstance.speedMaxYNFlagHashMap > plugin.speedMaxYNFlag) {
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                    playerInstance.setCancelled= true;
                                }
                                plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(14), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedMaxYN + x + y + levi + z + speedIceIncrease, plugin.roundedThresholdMedium + plugin.speedMaxYN + x + y + levi + z + speedIceIncrease, plugin.roundedThresholdHigh + plugin.speedMaxYN + x + y + levi + z + speedIceIncrease, -speedY));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(14));
                                if (plugin.debugMode) {
                                    plugin.message.Messages(35, speed_change, 10);
                                }
                            }
                        } else {
                            playerInstance.speedMaxYNFlagHashMap= 0;
                        }
                        double r = 0.0;
                        if (playerInstance.speedSlime) {
                            r = 3.1;
                        }
                        if (playerBlock11 == Material.SKELETON_SKULL) {
                            r += 1.0;
                        }

                        if (speedXZ >= plugin.speedMaxXZ + genericSpeedMovementIncrease + increaseTrapdoor + x + y + speedIceIncrease + punchSmart + knockbackSmart + awd + r && !playerInstance.elytraUse && LocationplayerYTop == Material.AIR && fromY != toY && notNearLiquid && !player.isRiptiding() && !playerInstance.underBlock && !playerInstance.underStair && playerInstance.notInsideBoat <= 0) {
                            playerInstance.speedMaxFlagXZL++;
                            if (playerInstance.speedMaxFlagXZL >= plugin.speedMaxXZMaxL) {
                                if (player.getInventory().getChestplate() != null) {
                                    if (player.getInventory().getChestplate().getType() != Material.ELYTRA && speedXZ > 0.5 + plugin.speedMaxXZMaxL + r + x + y + speedIceIncrease) {
                                        if (plugin.cancelEventIfHacking) {
                                            playerInstance.cancelX= fromX;
                                            playerInstance.cancelY= fromY;
                                            playerInstance.cancelZ= fromZ;
                                            playerInstance.setCancelled= true;
                                        }

                                        plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(12), plugin.notify.level(plugin.roundedThresholdLow + increaseTrapdoor + plugin.speedMaxXZMaxL, plugin.roundedThresholdMedium + increaseTrapdoor + plugin.speedMaxXZMaxL, plugin.roundedThresholdHigh + increaseTrapdoor + plugin.speedMaxXZMaxL, playerInstance.speedMaxFlagXZL));
                                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(12));
                                        if (plugin.debugMode) {
                                            plugin.message.Messages(30, speed_change, 10);
                                        }
                                    }
                                } else {
                                    if (plugin.cancelEventIfHacking) {
                                        playerInstance.cancelX= fromX;
                                        playerInstance.cancelY= fromY;
                                        playerInstance.cancelZ= fromZ;
                                        playerInstance.setCancelled= true;
                                    }
                                    plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(12), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedMaxXZMaxL, plugin.roundedThresholdMedium + plugin.speedMaxXZMaxL, plugin.roundedThresholdHigh + plugin.speedMaxXZMaxL, playerInstance.speedMaxFlagXZL));
                                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(12));
                                    if (plugin.debugMode) {
                                        plugin.message.Messages(30, speed_change, 10);
                                    }
                                }
                            }
                        } else {
                            playerInstance.speedMaxFlagXZL= 0;
                        }
                        if (r > 0) {
                            r -= 1.5;
                        }
                        double increaseIceSpeedMaxOnGround = 0.0;

                        if (playerInstance.underIce) {
                            increaseIceSpeedMaxOnGround = 3.0;
                        }

                        if ((playerBlock33D != Material.AIR || playerBlock44D != Material.AIR || playerBlock55D != Material.AIR || playerBlock66D != Material.AIR || playerBlock77D != Material.AIR || playerBlock88D != Material.AIR || playerBlock99D != Material.AIR || playerBlock100D != Material.AIR || playerBlock111D != Material.AIR) && playerBlock22D.isSolid() && !player.isRiptiding() && !playerInstance.elytraUse && speed_change >= x + genericSpeedMovementIncrease + y + plugin.speedMaxUnderBlock + increaseTrapdoor && toY == fromY) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + genericSpeedMovementIncrease + increaseTrapdoor + x + y + plugin.speedMaxUnderBlock, plugin.roundedThresholdMedium + genericSpeedMovementIncrease + increaseTrapdoor + x + y + plugin.speedMaxUnderBlock, plugin.roundedThresholdHigh + genericSpeedMovementIncrease + increaseTrapdoor + x + y + plugin.speedMaxUnderBlock, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(2));
                            if (plugin.debugMode) {
                                plugin.message.Messages(27, speed_change, 10);
                            }

                        } else if (Locationplayerr != Material.getMaterial("AIR") && Locationplayerr != Material.ICE && Locationplayerr != Material.PACKED_ICE && Locationplayerr != Material.BLUE_ICE && !playerBlock11.toString().contains("DOOR") && !player.isGliding() && notAllowedFlight && !playerInstance.elytraUse && speed_change > x + y + plugin.speedMaxOnGround + increaseIceSpeedMaxOnGround + genericSpeedMovementIncrease + r && toY == fromY && !playerInstance.speedSlime && !playerInstance.elytraUse && !player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE) && !playerInstance.underStair) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }

                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(5), plugin.notify.level(plugin.roundedThresholdLow + x + y + r + increaseIceSpeedMaxOnGround + genericSpeedMovementIncrease + plugin.speedMaxOnGround, plugin.roundedThresholdMedium + x + y + genericSpeedMovementIncrease + plugin.speedMaxOnGround + r + increaseIceSpeedMaxOnGround, plugin.roundedThresholdHigh + x + genericSpeedMovementIncrease + y + plugin.speedMaxOnGround + r + increaseIceSpeedMaxOnGround, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(5));
                            if (plugin.debugMode) {
                                plugin.message.Messages(12, speed_change, 10);
                            }
                        }

                        if (player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
                            x += player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE).getAmplifier();
                        }
                        if (playerBlock11 == Material.WATER && playerBlock22 == Material.AIR && speedXY > plugin.speedMaxInWater + x + y && !playerInstance.elytraUse && isntRiptiding && !player.isGliding()) {
                            playerInstance.speedWater= playerInstance.speedWater + 1.0;
                            if (playerInstance.speedWater > plugin.speedMaxInWaterNum) {
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.setCancelled= true;
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                }
                                plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(11), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedMaxInWaterNum, plugin.roundedThresholdMedium + plugin.speedMaxInWaterNum, plugin.roundedThresholdHigh + plugin.speedMaxInWaterNum, playerInstance.speedWater));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(11));
                                if (plugin.debugMode) {
                                    plugin.message.Messages(26, speedXY, 10);
                                }
                            }
                        } else {
                            playerInstance.speedWater= 0.0;
                        }

                        if (Locationplayerr == Material.getMaterial("AIR") && shulker && notAllowedFlight && !player.getAllowFlight() && !playerInstance.playerUsingElytra && shulker && speed_change > x + z + y + genericSpeedMovementIncrease + levi + plugin.speedMaxWhenAscending + knockbackSmart + speedIceIncrease + r && toY > fromY && !playerInstance.underBlock && !playerInstance.elytraUse && !playerInstance.speedSlime && !insideVeh) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(8), plugin.notify.level(plugin.roundedThresholdLow + x + knockbackSmart + y + levi + plugin.speedMaxWhenAscending + speedIceIncrease + genericSpeedMovementIncrease, plugin.roundedThresholdMedium + x + knockbackSmart + y + levi + plugin.speedMaxWhenAscending + speedIceIncrease + genericSpeedMovementIncrease, plugin.roundedThresholdHigh + x + y + knockbackSmart + levi + plugin.speedMaxWhenAscending + speedIceIncrease + genericSpeedMovementIncrease, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(8));
                            if (plugin.debugMode) {
                                plugin.message.Messages(14, speed_change, 10);
                            }
                        }
                        String placeholder = String.valueOf(toY);
                        try {
                            placeholder = String.valueOf(toY).substring(0, 3);
                        } catch (Exception e) {
                            placeholder = String.valueOf(toY);
                        }

                        double placeholder_num = Double.parseDouble(placeholder);
                        if (placeholder_num == playerInstance.playersYCoord && playerBlock11 == Material.AIR && Locationplayerr == Material.AIR && playerBlock22 == Material.AIR && speed_change > x + y + plugin.speedCheckWhenInAirAlternative && notAllowedFlight && isntRiptiding && !player.isGliding() && isntLevitating && !playerInstance.elytraUse) {
                            playerInstance.playersYCheck= playerInstance.playersYCheck + 1.0;
                            if (playerInstance.playersYCheck > plugin.speedCheckWhenInAirAlternativeNum) {
                                plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.speedCheckWhenInAirAlternativeNum, plugin.roundedThresholdMedium + plugin.speedCheckWhenInAirAlternativeNum, plugin.roundedThresholdHigh + plugin.speedCheckWhenInAirAlternativeNum, playerInstance.playersYCheck));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(1));
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.setCancelled= true;
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                }
                                if (plugin.debugMode) {
                                    plugin.message.Messages(15, speed_change, 10);
                                }
                            }
                        } else {
                            playerInstance.playersYCheck= 0.0;
                            playerInstance.playersYCoord= placeholder_num;
                        }
                        if (Locationplayerr == Material.getMaterial("AIR") && !player.isGliding() && notAllowedFlight && speed_change > x + y + levi + plugin.speedMaxWhenDescending && toY < fromY) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(9), plugin.notify.level(plugin.roundedThresholdLow + x + levi + y + plugin.speedMaxWhenDescending, plugin.roundedThresholdMedium + x + levi + y + plugin.speedMaxWhenDescending, plugin.roundedThresholdHigh + x + levi + y + plugin.speedMaxWhenDescending, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(9));
                            if (plugin.debugMode) {
                                plugin.message.Messages(16, speed_change, 1);
                            }
                        }
                        if (Locationplayerr == Material.getMaterial("AIR") && speed_change > x + y + plugin.speedMaxInAir) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }

                            plugin.notify.notify(player, plugin.message.type(13), plugin.message.cheat(3), plugin.notify.level(plugin.roundedThresholdLow + x + y + plugin.speedMaxInAir, plugin.roundedThresholdMedium + x + y + plugin.speedMaxInAir, plugin.roundedThresholdHigh + x + y + plugin.speedMaxInAir, speed_change));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(13), plugin.message.cheat(3));
                            if (plugin.debugMode) {
                                plugin.message.Messages(17, speed_change, 1);
                            }
                        }
                    }
                }
            }
            /*
            if (plugin.checkNeuralAnalysis) {
                double[] inputs = new double[2];
                /*
                inputs[0] = playerInstance.speedSlime ? 0.9 : 0.1;
                inputs[1] = playerInstance.speedMboat > 0 ? 0.9 : 0.1;
                if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    inputs[2] = 0.9;
                } else {
                    inputs[2] = 0.1;
                }=
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    inputs[3] = 0.9;
                } else {
                    inputs[3] = 0.1;
                }
                inputs[0] = (speed_change) / 20;

                inputs[1] = speedXZ / 20;




                if (plugin.trainNeuralNetwork && inputs[0] != 0 && inputs[1] != 0) {
                    if(plugin.neuralWrong) {
                        plugin.neuralNetwork.train(inputs, new double[]{0.01});
                        // write inputs to file and the expected output

                        try {
                            File file = new File(System.getProperty("user.dir") + File.separator + "neuralnetwork.txt");
                            if(!file.exists()) file.createNewFile();
                            BufferedWriter myWriter = new BufferedWriter(new FileWriter(file, true));
                            myWriter.write("{" + (Arrays.toString(inputs) + ", " + Arrays.toString(new double[]{0.01}) + "\n").replace("[", "").replace("]", "") + "}, ");
                            myWriter.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        plugin.neuralNetwork.train(inputs, new double[]{0.10});
                        // get current file directory

                        try {
                            File file = new File(System.getProperty("user.dir") + File.separator + "neuralnetwork.txt");
                            if(!file.exists()) file.createNewFile();
                            BufferedWriter myWriter = new BufferedWriter(new FileWriter(file, true));
                            myWriter.write("{" + (Arrays.toString(inputs) + ", " + Arrays.toString(new double[]{0.10}) + "\n").replace("[", "").replace("]", "") + "}, ");
                            myWriter.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(plugin.neuralWrong) {
                    } else {
                    }
                } else {
                    double[] output = plugin.neuralNetwork.feedForward(inputs);
                    // print outputs
                    System.out.println("Output: " + output[0]);


                }


            }*/


            x = 0;
            speed_change = speed_change_final;
            if ((plugin.checkWalkOnFluid && player.getGameMode() == GameMode.SURVIVAL && !insideVeh && notAllowedFlight && !playerInstance.elytraUse) || (plugin.checkWalkOnFluid && player.getGameMode() == GameMode.ADVENTURE && !player.isFlying() && !player.getAllowFlight() && !playerInstance.elytraUse)) {
                if (playerInstance.fluidWalkPlaceHolder >= 2.0) {
                    playerInstance.playersYBeforeCoordAlt= toY;
                    playerInstance.playersYBeforeCoord= fromY;
                    playerInstance.fluidWalkPlaceHolder= 0.0;
                }
                playerInstance.fluidWalkPlaceHolder= playerInstance.fluidWalkPlaceHolder + 1.0;
                if (!playerBlock11.toString().contains("TRAPDOOR") && playerBlock11 != Material.LILY_PAD && playerBlock11 != Material.POINTED_DRIPSTONE && playerBlock11 != Material.BIG_DRIPLEAF && !playerBlock11.toString().contains("STAIRS") && !Locationplayerr.toString().contains("STAIRS") && playerInstance.notInsideBoat <= 0 && ((toY == playerInstance.playersYBeforeCoordAlt && fromY == playerInstance.playersYBeforeCoord) || (fromY == playerInstance.playersYBeforeCoordAlt && toY == playerInstance.playersYBeforeCoord)) && fromY != toY && !playerInstance.elytraUse) {
                    playerInstance.fluidWalkUntilHackingAlt= playerInstance.fluidWalkUntilHackingAlt + 1.0;
                    if (playerInstance.fluidWalkUntilHackingAlt > plugin.fluidWalkUntilHackingAlternative) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.setCancelled= true;
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                        }
                        plugin.notify.notify(player, plugin.message.type(14), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.fluidWalkUntilHackingAlternative, plugin.roundedThresholdMedium + plugin.fluidWalkUntilHackingAlternative, plugin.roundedThresholdHigh + plugin.fluidWalkUntilHackingAlternative, playerInstance.fluidWalkUntilHackingAlt));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(14), plugin.message.cheat(1));
                        if (plugin.debugMode) {
                            plugin.message.Messages(18, 1, 1);
                        }
                    }

                } else {
                    playerInstance.fluidWalkUntilHackingAlt= 0.0;
                    boolean equalsWaterUnderneath = Locationplayerrfluid1 == Material.WATER && Locationplayerrfluid2 == Material.WATER && Locationplayerrfluid7 == Material.WATER && Locationplayerrfluid3 == Material.WATER && Locationplayerrfluid8 == Material.WATER && Locationplayerrfluid4 == Material.WATER && Locationplayerrfluid5 == Material.WATER && Locationplayerrfluid6 == Material.WATER && Locationplayerrfluid == Material.WATER;
                    if (equalsWaterUnderneath && toY == fromY && playerBlock22 == Material.AIR && !Locationplayerr.toString().contains("FENCE") && !player.isSwimming() && !insideVeh) {
                        playerInstance.fluidWalkNCP= playerInstance.fluidWalkNCP + 1.0;
                        if (playerInstance.fluidWalkNCP > plugin.fluidWalkUntilHacking && playerInstance.nearBoat) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(14), plugin.message.cheat(3), plugin.notify.level(plugin.roundedThresholdLow + plugin.fluidWalkUntilHacking, plugin.roundedThresholdMedium + plugin.fluidWalkUntilHacking, plugin.roundedThresholdHigh + plugin.fluidWalkUntilHacking, playerInstance.fluidWalkNCP));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(14), plugin.message.cheat(3));
                            if (plugin.debugMode) {
                                plugin.message.Messages(21, 1, 1);
                            }
                        }
                    } else {
                        playerInstance.fluidWalkNCP= 0.0;
                    }
                    if (Locationplayerr == Material.WATER && toY == fromY && playerBlock11 == Material.AIR && playerBlock33 == Material.AIR && playerBlock44 == Material.AIR && playerBlock55 == Material.AIR && playerBlock66 == Material.AIR && playerBlock77 == Material.AIR && playerBlock88 == Material.AIR && playerBlock99 == Material.AIR && playerBlock100 == Material.AIR && playerBlock111 == Material.AIR && !player.isSwimming() && !insideVeh) {
                        playerInstance.fluidWalkNum= playerInstance.fluidWalkNum + 1.0;
                    }
                    if (plugin.fluidWalkDCheck && playerInstance.lastPlayerYSpeed == speedY && !notNearLiquid && isntRiptiding && !playerInstance.elytraUse && equalsWaterUnderneath && !player.isSwimming() && !insideVeh && playerBlock22 == Material.AIR && toY > fromY && !playerInstance.underBlock) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.setCancelled= true;
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                        }
                        plugin.notify.notify(player, plugin.message.type(14), plugin.message.cheat(4), plugin.lowString);
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(14), plugin.message.cheat(4));
                        if (plugin.debugMode) {
                            plugin.message.Messages(36, 1, 1);
                        }
                    }
                    if (Locationplayerr != Material.getMaterial("WATER") || Locationplayer11 != Material.getMaterial("WATER") || Locationplayer22 != Material.getMaterial("WATER") || Locationplayer33 != Material.getMaterial("WATER") || Locationplayer44 != Material.getMaterial("WATER") || Locationplayer55 != Material.getMaterial("WATER") || Locationplayer66 != Material.getMaterial("WATER") || Locationplayer77 != Material.getMaterial("WATER") || Locationplayer99 != Material.getMaterial("WATER") || playerBlock22 != Material.getMaterial("AIR") || playerBlock11 != Material.getMaterial("AIR") || playerInstance.elytraUse) {
                        playerInstance.fluidWalkNum= 0.0;
                    }
                    boolean playerBlockNotNearAir = playerBlock11 != Material.AIR || playerBlock33 != Material.AIR || playerBlock44 != Material.AIR || playerBlock55 != Material.AIR || playerBlock66 != Material.AIR || playerBlock77 != Material.AIR || playerBlock88 != Material.AIR || playerBlock99 != Material.AIR || playerBlock100 != Material.AIR || playerBlock111 != Material.AIR;
                    if (playerBlockNotNearAir && (playerBlock11 != Material.WATER || playerBlock33 != Material.WATER || playerBlock44 != Material.WATER || playerBlock55 != Material.WATER || playerBlock66 != Material.WATER || playerBlock77 != Material.WATER || playerBlock88 != Material.WATER || playerBlock99 != Material.WATER || playerBlock100 != Material.WATER || playerBlock111 != Material.WATER || playerInstance.underBlock)) {
                        playerInstance.jumpsOnWaterTillHacking= 0.0;
                        playerInstance.fluidWalkNum= 0.0;
                        playerInstance.fluidWalkNCP= 0.0;
                    }
                    if (speed_change > plugin.fluidWalkIrregularSpeed && playerBlock22 == Material.AIR && playerBlock11 == Material.AIR && playerBlock33 == Material.AIR && playerBlock44 == Material.AIR && playerBlock55 == Material.AIR && playerBlock66 == Material.AIR && playerBlock77 == Material.AIR && playerBlock88 == Material.AIR && playerBlock99 == Material.AIR && playerBlock100 == Material.AIR && playerBlock111 == Material.AIR && Locationplayerrd == Material.WATER && Locationplayer11d == Material.WATER && Locationplayer22d == Material.WATER && Locationplayer33d == Material.WATER && Locationplayer44d == Material.WATER && Locationplayer55d == Material.WATER && (Locationplayer66d == Material.WATER) && Locationplayer77d == Material.WATER && Locationplayer88d == Material.WATER && Locationplayer99d == Material.WATER && Locationplayerr == Material.AIR && Locationplayer11 == Material.AIR && Locationplayer22 == Material.AIR && Locationplayer33 == Material.AIR && Locationplayer44 == Material.AIR && Locationplayer55 == Material.AIR && Locationplayer66 == Material.AIR && Locationplayer77 == Material.AIR && Locationplayer99 == Material.AIR && !playerInstance.elytraUse) {
                        playerInstance.jumpsOnWaterTillHacking= playerInstance.jumpsOnWaterTillHacking + 1.0;
                        if (playerInstance.jumpsOnWaterTillHacking > plugin.fluidJumpsOnWaterUntilHacking) {
                            plugin.notify.notify(player, plugin.message.type(14), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.fluidJumpsOnWaterUntilHacking, plugin.roundedThresholdMedium + plugin.fluidJumpsOnWaterUntilHacking, plugin.roundedThresholdHigh + plugin.fluidJumpsOnWaterUntilHacking, playerInstance.jumpsOnWaterTillHacking));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(14), plugin.message.cheat(2));
                            if (plugin.debugMode) {
                                plugin.message.Messages(20, 1, 1);
                            }
                        }
                    }
                    if (Locationplayerrd != Material.WATER && Locationplayerrd != Material.AIR && Locationplayerr != Material.AIR && Locationplayerr != Material.WATER && playerBlock11 != Material.WATER && fromY == toY

                    ) {
                        playerInstance.jumpsOnWaterTillHacking= 0.0;
                    }
                    if (!((Locationplayerrfluid == Material.WATER || Locationplayerrfluid == Material.AIR) && (Locationplayerrfluid1 == Material.WATER || Locationplayerrfluid1 == Material.AIR) && (Locationplayerrfluid2 == Material.WATER || Locationplayerrfluid2 == Material.AIR) && (Locationplayerrfluid3 == Material.WATER || Locationplayerrfluid3 == Material.AIR) && (Locationplayerrfluid4 == Material.WATER || Locationplayerrfluid4 == Material.AIR) && (Locationplayerrfluid5 == Material.WATER || Locationplayerrfluid5 == Material.AIR) && (Locationplayerrfluid6 == Material.WATER || Locationplayerrfluid6 == Material.AIR) && (Locationplayerrfluid7 == Material.WATER || Locationplayerrfluid7 == Material.AIR) && (Locationplayerrfluid8 == Material.WATER || Locationplayerrfluid8 == Material.AIR))) {
                        playerInstance.jumpsOnWaterTillHacking= 0.0;
                    }

                    if (Locationplayerr == Material.LAVA && toY == fromY) {
                        playerInstance.fluidWalkLavaNum++;
                    }
                    if (Locationplayerr != Material.getMaterial("LAVA") || Locationplayer11 != Material.getMaterial("LAVA") || Locationplayer22 != Material.getMaterial("LAVA") || Locationplayer33 != Material.getMaterial("LAVA") || Locationplayer44 != Material.getMaterial("LAVA") || Locationplayer55 != Material.getMaterial("LAVA") || Locationplayer66 != Material.getMaterial("LAVA") || Locationplayer77 != Material.getMaterial("LAVA") || insideVeh || player.isSwimming() || playerBlock2.getBlock().getType() == Material.getMaterial("LAVA") || playerBlockNotNearAir || playerInstance.elytraUse) {
                        playerInstance.fluidWalkLavaNum= 0.0;
                    }
                }

                if (insideVeh || player.isSwimming()) {
                    playerInstance.fluidWalkNum= 0.0;
                    playerInstance.fluidWalkLavaNum= 0.0;
                }
                if (playerInstance.fluidWalkNum > plugin.fluidWalkUntilHacking || (playerInstance.fluidWalkLavaNum > plugin.fluidWalkUntilHacking && !playerInstance.elytraUse)) {
                    if (plugin.cancelEventIfHacking) {
                        playerInstance.setCancelled= true;
                        playerInstance.cancelX= fromX;
                        playerInstance.cancelY= fromY;
                        playerInstance.cancelZ= fromZ;
                    }
                    plugin.notify.notify(player, plugin.message.type(14), plugin.message.cheat(3), plugin.notify.level(plugin.roundedThresholdLow + plugin.fluidWalkUntilHacking, plugin.roundedThresholdMedium + plugin.fluidWalkUntilHacking, plugin.roundedThresholdHigh + plugin.fluidWalkUntilHacking, playerInstance.fluidWalkNum));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(14), plugin.message.cheat(3));
                    if (plugin.debugMode) {
                        plugin.message.Messages(21, 1, 1);
                    }
                }
            }

            if (plugin.checkFastLadder && !player.isSwimming() && isntRiptiding && notAllowedFlight && !player.isGliding() && !player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE) && !playerInstance.elytraUse && (playerBlock11 == Material.getMaterial("AIR") || location3A != Material.getMaterial("BUBBLE_COLUMN")) && (playerBlock11 == Material.getMaterial("AIR") || location44 != Material.getMaterial("BUBBLE_COLUMN")) && (playerBlock11 == Material.getMaterial("AIR") || location55 != Material.getMaterial("BUBBLE_COLUMN")) && (playerBlock11 == Material.getMaterial("AIR") || location66 != Material.getMaterial("BUBBLE_COLUMN"))) {
                if ((speed_change > plugin.speedMaxClimbing && playerBlock11 == Material.LADDER && playerBlock22 == Material.LADDER && toY > fromY && Locationplayerr == Material.LADDER) || (speed_change > plugin.speedMaxClimbing && playerBlock11 == Material.WATER && playerBlock22 == Material.WATER && Locationplayerr == Material.WATER && toY > fromY) || (speed_change > plugin.speedMaxClimbing && playerBlock11 == Material.LAVA && playerBlock22 == Material.LAVA && Locationplayerr == Material.LAVA && toY > fromY) || (speed_change > plugin.speedMaxClimbing && playerBlock11 == Material.VINE && playerBlock22 == Material.VINE && Locationplayerr == Material.VINE && toY > fromY)) {
                    playerInstance.fastClimbNumCheck= playerInstance.fastClimbNumCheck + 1.0;
                    if (playerInstance.fastClimbNumCheck >= plugin.numUntilCheckingFastClimb) {
                        if (plugin.cancelEventIfHacking) {
                            playerInstance.setCancelled= true;
                            playerInstance.cancelX= fromX;
                            playerInstance.cancelY= fromY;
                            playerInstance.cancelZ= fromZ;
                        }
                        plugin.notify.notify(player, plugin.message.type(15), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.numUntilCheckingFastClimb, plugin.roundedThresholdMedium + plugin.numUntilCheckingFastClimb, plugin.roundedThresholdHigh + plugin.numUntilCheckingFastClimb, playerInstance.fastClimbNumCheck));
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(15), plugin.message.cheat(1));
                        if (plugin.debugMode) {
                            plugin.message.Messages(22, speed_change, 1);
                        }
                    }
                } else {
                    playerInstance.fastClimbNumCheck= 0.0;
                }
            }
            playerInstance.lastPlayerYSpeed= speedY;
            if (!plugin.checkFly || player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                return;
            }

            if (!notAllowedFlight || player.isRiptiding() || player.isGliding()) {
                playerInstance.numFly= 0.0;
                playerInstance.numFlyCheck= 0.0;
                playerInstance.numFlyCheckLev= 0.0;
                return;
            } else {
                if (Locationplayerr == Material.getMaterial("SLIME_BLOCK")) {
                    playerInstance.flyState= 1.0;
                }
                if (isNearSolidBlock) {
                    playerInstance.numFly= 0.0;
                    playerInstance.numFlyCheck= 0.0;
                    playerInstance.numFlyCheckLev= 0.0;
                    playerInstance.sameYCoord= 0;
                    return;
                }
                if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                    x = player.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
                }
                if (playerInstance.flyState == 1.0) {
                    playerInstance.flyCheck= 1.0;
                    playerInstance.numFly= 0.0;
                    playerInstance.numFlyCheck= 0.0;
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    scheduler.runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            playerInstance.numFlyCheck= 0.0;
                            playerInstance.numFly= 0.0;
                            playerInstance.flyState= 0.0;
                        }
                    }, 70);
                }
                if (playerInstance.flyCheck == 0.0 && !isNearSolidBlock) {
                    if (toY < fromY) {
                        playerInstance.numFly= 0.0;
                        playerInstance.GoingDown= true;
                    }
                    if (playerInstance.GoingDown && toY > fromY) {
                        playerInstance.numFlyCheck= playerInstance.numFlyCheck + 1.0;
                        playerInstance.flyState= 0.0;
                    }
                    if (toY > fromY) {
                        playerInstance.GoingDown= false;
                    }
                } else {
                    playerInstance.numFlyCheck= 0.0;
                }

                if (playerInstance.flyState == 0.0) {
                    playerInstance.flyCheck= 0.0;
                    if (player.isGliding() || player.isRiptiding()) {
                        playerInstance.numFly= 0.0;
                        playerInstance.numFlyCheck= 0.0;
                        playerInstance.numFlyCheckLev= 0.0;
                        playerInstance.sameYCoord= 0;
                        return;
                    }

                    boolean ridingStrider = insideVeh && "CraftStrider".equals(player.getVehicle().toString());
                    if (!isNearSolidBlock && isntLevitating && !ridingStrider) {
                        playerInstance.numFly++;
                        if (playerInstance.numFly >= plugin.inAirUpwardUntilHacking + x + y + upUntil && !playerInstance.speedSlime) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(3), plugin.message.cheat(2), plugin.notify.level(plugin.roundedThresholdLow + plugin.inAirUpwardUntilHacking + x + y + upUntil, plugin.roundedThresholdMedium + plugin.inAirUpwardUntilHacking + x + y + upUntil, plugin.roundedThresholdHigh + plugin.inAirUpwardUntilHacking + x + y + upUntil, playerInstance.numFly));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(3), plugin.message.cheat(2));
                            if (plugin.debugMode) {
                                plugin.message.Messages(24, 1, 1);
                            }
                        }

                        if (toY == fromY && !isNearSolidBlock && !insideVeh && isntLevitating) {
                            playerInstance.sameYCoord++;
                            if (playerInstance.sameYCoord >= plugin.inAirYCoodD) {
                                if (plugin.cancelEventIfHacking) {
                                    playerInstance.setCancelled= true;
                                    playerInstance.cancelX= fromX;
                                    playerInstance.cancelY= fromY;
                                    playerInstance.cancelZ= fromZ;
                                }
                                plugin.notify.notify(player, plugin.message.type(3), plugin.message.cheat(4), plugin.notify.level(plugin.roundedThresholdLow + plugin.inAirYCoodD, plugin.roundedThresholdMedium + plugin.inAirYCoodD, plugin.roundedThresholdHigh + plugin.inAirYCoodD, playerInstance.sameYCoord));
                                plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(3), plugin.message.cheat(4));
                                if (plugin.debugMode) {
                                    plugin.message.Messages(32, 1, 1);
                                }
                            }
                        } else {
                            playerInstance.sameYCoord= 0;
                        }
                        if (playerInstance.numFlyCheck >= plugin.inAirJumpUntilHacking + upJump + bedrockIncrease) {
                            if (plugin.cancelEventIfHacking) {
                                playerInstance.setCancelled= true;
                                playerInstance.cancelX= fromX;
                                playerInstance.cancelY= fromY;
                                playerInstance.cancelZ= fromZ;
                            }
                            plugin.notify.notify(player, plugin.message.type(3), plugin.message.cheat(1), plugin.notify.level(plugin.roundedThresholdLow + plugin.inAirJumpUntilHacking, plugin.roundedThresholdMedium + plugin.inAirJumpUntilHacking, plugin.roundedThresholdHigh + plugin.inAirJumpUntilHacking, playerInstance.numFlyCheck));
                            plugin.violationChecker.violationChecker(player, playerInstance.num++, true, plugin.message.type(3), plugin.message.cheat(1));
                            if (plugin.debugMode) {
                                plugin.message.Messages(25, 1, 1);
                            }
                        }
                    }
                }
            }
            String name = player.getName();
            if (!playerInstance.setCancelled || playerInstance.setCancelled == null) {
                playerInstance.setCancelled = false;
            }
        } catch (Exception eeeee) {
            eeeee.printStackTrace();
        }
    }

    // sigmoid function
    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

}
