package me.korbsti.soaromaac.api;

import me.korbsti.soaromaac.Main;
import org.bukkit.entity.Player;

public class SoaromaAPI {

    Main plugin;

    public SoaromaAPI(Main plugin) {
        this.plugin = plugin;
    }

    public void setDisabler(Player p, int x) {
        plugin.death.disablerACPlayer(p, x);
    }

    public void setPlayerViolation(String playerName, int x) {
        plugin.playerInstances.get(playerName).num = x;
    }

    public int getPlayerViolation(String playerName) {
        return plugin.playerInstances.get(playerName).num;
    }

    public Integer getTotalViolationAmount(Player p) {
        return this.plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations");
    }


}
