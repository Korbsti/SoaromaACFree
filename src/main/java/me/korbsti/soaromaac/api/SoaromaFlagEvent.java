package me.korbsti.soaromaac.api;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SoaromaFlagEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    Main plugin;
    Player flaggedPlayer;
    String cheatT;

    public SoaromaFlagEvent(Main instance, Player p, String cheat) {
        this.plugin = instance;
        this.flaggedPlayer = p;
        this.cheatT = cheat;

    }

    public Player getFlaggedPlayer() {
        return this.flaggedPlayer;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Integer getViolationAmount(String username) {
        return this.plugin.playerInstances.get(username).num;
    }

    public Integer getTotalViolationAmount(Player p) {
        return this.plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations");
    }

    public String getCheckFlagged() {
        return cheatT;
    }

    public void setDisabler(int x) {
        plugin.death.disablerACPlayer(flaggedPlayer, x);
    }

    public void setPlayerViolation(String playerName, int x) {
        plugin.playerInstances.get(playerName).num = x;
    }


}
