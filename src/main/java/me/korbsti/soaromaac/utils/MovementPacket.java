package me.korbsti.soaromaac.utils;


import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;

public class MovementPacket {

    Main plugin;

    Player player;
    double toX;
    double toY;
    double toZ;
    double fromX;
    double fromY;
    double fromZ;
    float pitch;
    float yaw;


    public MovementPacket(Player player, double toX, double toY, double toZ, double fromX, double fromY, double fromZ, Float pitch, Float yaw) {
        this.player = player;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.pitch = pitch;
        this.yaw = yaw;

    }

    public Player getPlayer() {
        return player;
    }

    public double getToX() {
        return toX;
    }

    public double getToY() {
        return toY;
    }

    public double getToZ() {
        return toZ;
    }

    public double getFromX() {
        return fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public double getFromZ() {
        return fromZ;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }


}
