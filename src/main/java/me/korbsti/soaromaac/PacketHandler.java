package me.korbsti.soaromaac;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;

public class PacketHandler extends ChannelDuplexHandler {
    Main plugin;
    private final Player p;

    public PacketHandler(final Player p, Main instance) {
        this.p = p;
        this.plugin = instance;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) {
        try {
            super.write(ctx, packet, promise);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) {
        String playerName = p.getName();
        String packetName = packet.getClass().getSimpleName();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        String lastPacket = playerInstance.lastPacket;

        try {
            if (packetName.equalsIgnoreCase("PacketPlayInCustomPayload")) {
                if (playerInstance.key == null) {
                    playerInstance.key= new ArrayList<String>();
                    playerInstance.namespace= new ArrayList<String>();
                    playerInstance.data= new ArrayList<String>();
                    playerInstance.customPayload= new ArrayList<Object>();
                    // Bukkit.broadcastMessage("set new array");
                }
                playerInstance.customPayload.add(packet);
                Object minecraftKey = Reflection.getFieldValue(packet, "a");
                playerInstance.key.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "c")));
                playerInstance.namespace.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "a")));
                playerInstance.data.add(String.valueOf(Reflection.getFieldValue(packet, "d")));
            }
            if (packetName.equals("PacketPlayInAbilities")) {

                if (playerInstance.isFloodGatePlayer != null) {

                    if (!playerInstance.isFloodGatePlayer) {

                        if (!p.getAllowFlight() && plugin.checkFly && plugin.checkFlightE) {

                            plugin.notify.notify(p, plugin.message.type(3), plugin.message.cheat(5), plugin.highString);
                            plugin.violationChecker.violationChecker(p, playerInstance.num++, true, plugin.message.type(3), plugin.message.cheat(5));

                            if (plugin.debugMode) {
                                plugin.message.Messages(42, 0, 0);
                            }

                        }
                    }
                }
            }

            if (packetName.equalsIgnoreCase("PacketPlayInBlockDig")) {
                String str;
                str = "" + Reflection.getFieldValue(packet, "c");
                if (str.equals("START_DESTROY_BLOCK")) {
                    if (playerInstance.nukerSample == null) {
                        playerInstance.nukerSample= 0;
                    }
                    if (playerInstance.nukerSample == 1) {
                        playerInstance.nukerFirst= Instant.now();
                    }

                    if (playerInstance.nukerSample >= plugin.sample) {

                        long num = Instant.now().toEpochMilli() - playerInstance.nukerFirst.toEpochMilli();

                        if (num <= plugin.maxMili) {

                            plugin.notify.notify(p, plugin.message.type(22), plugin.message.cheat(1), plugin.highString);
                            plugin.violationChecker.violationChecker(p, playerInstance.num++, false, plugin.message.type(22), plugin.message.cheat(1));

                            if (plugin.debugMode) {
                                plugin.message.Messages(41, num, 4);
                            }

                        }
                        playerInstance.nukerSample= 0;
                    }
                    playerInstance.nukerSample++;
                }
            }
        } catch (Exception ewwae) {
            ewwae.printStackTrace();
        }

        /*
         * if(packetName.equalsIgnoreCase("PacketLoginInStart")) { Object
         * gameProfile = Reflection.getFieldValue(packet, "a");
         * Bukkit.broadcastMessage("Name: " +
         * Reflection.getFieldValue(gameProfile, "name"));
         * Bukkit.broadcastMessage("UUID: " +
         * Reflection.getFieldValue(gameProfile, "id")); Object properties =
         * Reflection.getFieldValue(gameProfile, "properties");
         * Bukkit.broadcastMessage("" + Reflection.getFieldValue(properties,
         * "properties"));
         *
         * } if(packetName.equalsIgnoreCase("PacketPlayInSettings")) {
         * Bukkit.broadcastMessage("locale: " + Reflection.getFieldValue(packet,
         * "locale")); Bukkit.broadcastMessage("e: " +
         * Reflection.getFieldValue(packet, "e")); }
         */
        if ("PacketPlayInFlying".equalsIgnoreCase(packetName)) {
            if (playerInstance.playerEnableAntiCheat == null) {
                playerInstance.playerEnableAntiCheat= true;
            }
            if (plugin.checkIrregularEvent && playerInstance.playerEnableAntiCheat) {
                if (playerInstance.playerEventNumber == null) {
                    playerInstance.playerEventNumber = 0.0;
                }
                plugin.timeCheck.irregularMoveEvent(playerName, 1);
            }

        } else if ("PacketPlayInVehicleMove".equalsIgnoreCase(packetName)) {
            Class<?> superClass = packet.getClass();
            Double x = 0.0;
            Double y = 0.0;
            Double z = 0.0;
            float yaw = 0;
            float pitch = 0;

            try {
                x = (Double) Reflection.getFieldValue(packet, "a");
                y = (Double) Reflection.getFieldValue(packet, "b");
                z = (Double) Reflection.getFieldValue(packet, "c");
                yaw = (float) Reflection.getFieldValue(packet, "d");
                pitch = (float) Reflection.getFieldValue(packet, "e");

                if (playerInstance.playerWorld == null) {
                    playerInstance.playerWorld= p.getWorld().toString();
                }
                plugin.settingValues.setting(p, x, y, z, yaw, pitch);
                if (playerInstance.playerEnableAntiCheat == null) {
                    playerInstance.playerEnableAntiCheat= true;
                }
                if (plugin.checkIrregularEvent && playerInstance.playerEnableAntiCheat) {
                    if (playerInstance.playerEventNumber == null) {
                        playerInstance.playerEventNumber= 0.0;
                    }
                    plugin.timeCheck.irregularMoveEvent(playerName, 2);
                    plugin.timeCheck.irregularMoveEventTimerB(p, playerName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (("PacketPlayInPositionLook".equals(packetName) || "PacketPlayInPosition".equals(packetName))) {
            Class<?> superClass = packet.getClass().getSuperclass();
            Double x = 0.0;
            Double y = 0.0;
            Double z = 0.0;
            float yaw = 0;
            float pitch = 0;
            try {
                x = Reflection.getFieldValueClass(superClass, packet, "a");
                y = Reflection.getFieldValueClass(superClass, packet, "b");
                z = Reflection.getFieldValueClass(superClass, packet, "c");
                yaw = Reflection.getFieldValueClassFloat(superClass, packet, "d");

                pitch = Reflection.getFieldValueClassFloat(superClass, packet, "e");

                playerInstance.playerOnGround= Reflection.getFieldBooleanSuperClass(superClass, packet, "f");

                if (playerInstance.playerWorld == null) {
                    playerInstance.playerWorld= p.getWorld().toString();
                }

                plugin.settingValues.setting(p, x, y, z, yaw, pitch);
                if (playerInstance.playerEnableAntiCheat == null) {
                    playerInstance.playerEnableAntiCheat= true;
                }
                if (lastPacket != null) {
                    if (plugin.checkIrregularEvent && playerInstance.playerEnableAntiCheat && !lastPacket.equals("PacketPlayInUseItem") && !lastPacket.equals("PacketPlayInBlockDig")) {
                        if (playerInstance.playerEventNumber == null) {
                            playerInstance.playerEventNumber= 0.0;
                        }
                        plugin.timeCheck.irregularMoveEvent(playerName, 2);
                        plugin.timeCheck.irregularMoveEventTimerB(p, playerName);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerInstance.lastPacket= packetName;

        try {
            super.channelRead(channelHandlerContext, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}