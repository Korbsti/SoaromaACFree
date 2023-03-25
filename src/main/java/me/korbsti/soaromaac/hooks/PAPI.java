package me.korbsti.soaromaac.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PAPI extends PlaceholderExpansion {
    public Main plugin;

    /*
     * public PlaceHolders(SoaromaFM ourPlugin) { // this is the plugin that is
     * registering the placeholder and the identifier for our placeholder. //
     * the format for placeholders is this: // %<placeholder
     * identifier>_<anything you define as an identifier in your method below>%
     * // the placeholder identifier can be anything you want as long as it is
     * not already taken by another // registered placeholder. super(); // this
     * is so we can access our main class below this.ourPlugin = ourPlugin; }
     */

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        // placeholder: %customplaceholder_staff_count%
        // always check if the player is null for placeholders related to the
        // player!
        if (p == null) {
            return "";
        }
        String name = p.getName();
        String uuid = p.getUniqueId().toString();

        if (identifier.equals("current-violations")) {
            return String.valueOf(plugin.playerInstances.get(name).num);
        }
        if (identifier.equals("total-violations")) {
            return "" + plugin.violationFileYaml.getInt(uuid + ".violations");
        }
        if (identifier.equals("warns")) {
            return "" + plugin.violationFileYaml.getInt(uuid + ".warnCount");
        }
        // placeholder: %customplaceholder_is_staff%

        // anything else someone types is invalid because we never defined
        // %customplaceholder_<what they want a value for>%
        // we can just return null so the placeholder they specified is not
        // replaced.
        return null;
    }

    @Override
    public boolean canRegister() {
        return (plugin = (Main) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        // TODO Auto-generated method stub
        return "SoaromaSAC";
    }

    @Override
    public String getVersion() {
        // TODO Auto-generated method stub
        return plugin.getDescription().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return "SoaromaSAC";
    }

}

/*
 * if (identifier.equals("married_to_uuid")) { return
 * String.valueOf(plugin.playerDataManager.getMarriedToUUID(uuid)); } if
 * (identifier.equals("married_to")) { return
 * String.valueOf(plugin.playerDataManager.getMarriedTo(uuid)); }
 */