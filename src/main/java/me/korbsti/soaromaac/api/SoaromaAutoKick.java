package me.korbsti.soaromaac.api;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SoaromaAutoKick extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    Main plugin;
    Player player;
    boolean cancelled = false;

    public SoaromaAutoKick(Main instance, Player p) {
        this.plugin = instance;
        this.player = p;
    }

    public Player getPunishedPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean getCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean setCancelled) {
        this.cancelled = setCancelled;
    }

}
