package me.korbsti.soaromaac.api;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SoaromaAdminPunish extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    Main plugin;
    Player player;
    Player issued;
    String type;

    public SoaromaAdminPunish(Main instance, Player issued, Player recieved, String type) {
        this.plugin = instance;
        this.player = recieved;
        this.type = type;
        this.issued = issued;
    }

    public Player getPunishedPlayer() {
        return this.player;
    }

    public Player getIssuedPlayer() {
        return issued;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String returnType() {
        return type;
    }
}
