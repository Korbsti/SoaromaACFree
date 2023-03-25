package me.korbsti.soaromaac;

import io.netty.channel.Channel;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketInjector {
    Main plugin;
    public String version = "";
    private Field EntityPlayer_playerConnection;
    private Class<?> PlayerConnection;
    private Field PlayerConnection_networkManager;

    private Class<?> NetworkManager;
    private Field k;

    /*
     * public PacketInjector() { try {// EntityPlayer //
     * EntityPlayer_playerConnection = //
     * Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"), //
     * "playerConnection"); // EntityPlayer
     *
     *
     * EntityPlayer_playerConnection =
     * Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"),
     * "playerConnection");
     *
     * PlayerConnection = Reflection.getClass("{nms}.PlayerConnection");
     * PlayerConnection_networkManager = Reflection.getField(PlayerConnection,
     * "networkManager");
     *
     * NetworkManager = Reflection.getClass("{nms}.NetworkManager"); k =
     * Reflection.getField(NetworkManager, "channel"); } catch (Throwable t) {
     * t.printStackTrace(); } }
     */

    public PacketInjector() {
        try {// EntityPlayer
            // EntityPlayer_playerConnection =
            // Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"),
            // "playerConnection");
            // EntityPlayer
            if (version.equals("")) {

                version = Bukkit.getServer().getBukkitVersion();
                if (version.contains("1.18.2")) {
                    version = "2";
                }else if (version.contains("1.19.4")){
                    version = "4";
                } else if (version.contains("1.19")) {
                    version = "3";
                } else {
                    version = "1";
                }

            }
            EntityPlayer_playerConnection = Reflection.getField(Reflection.getClass("net.minecraft.server.level.EntityPlayer"), "b");

            PlayerConnection = Reflection.getClass("net.minecraft.server.network.PlayerConnection");

            if (version.equals("3")) {
                PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "b");
            } else if (version.equals("4")){
                PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "h");

            }else {
                PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "a");
            }

            NetworkManager = Reflection.getClass("net.minecraft.network.NetworkManager");

            if (version.equals("2") || version.equals("3") || version.equals("4")) {
                k = Reflection.getField(NetworkManager, "m");
            } else {
                k = Reflection.getField(NetworkManager, "k");
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
    }

    public void addPlayer(Player p, Main instance) {
        try {
            this.plugin = instance;

            if(plugin.playerInstances.get(p.getName()) == null) plugin.playerInstances.put(p.getName(), new PlayerInstance(p, plugin));


            Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
            if (ch != null && ch.pipeline().get("PacketInjector") == null) {
                PacketHandler h = new PacketHandler(p, plugin);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
                // PacketInjector
            }
        } catch (Exception t) {

            t.printStackTrace();

            Bukkit.getLogger().info("An error has occurred trying to register the user inside the packet handler: " + p.getName());
            Bukkit.getLogger().info("This usually only happens mid-reload or when the server is starting up");
            Bukkit.getLogger().info("If this is happening consistently while the server is fully on, please contact the developer ASAP, the anti-cheat may require an update to a newer minecraft version");
            Bukkit.getScheduler().runTaskLater(instance, new Runnable() {

                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + p.getName() + " Server is still starting, please rejoin");

                }


            }, 10);
        }

    }

    public void removePlayer(Player p) {
        try {
            Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
            if (ch.pipeline().get("PacketInjector") != null) {
                ch.pipeline().remove("PacketInjector");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Object getNetworkManager(Object ep) {
        return Reflection.getFieldValue(PlayerConnection_networkManager, (Object) Reflection.getFieldValue(EntityPlayer_playerConnection, ep));
    }

    private Channel getChannel(Object networkManager) {
        Channel ch = null;
        try {
            ch = Reflection.getFieldValue(k, networkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ch;
    }
}