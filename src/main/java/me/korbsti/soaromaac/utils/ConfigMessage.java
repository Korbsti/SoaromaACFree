package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;

public class ConfigMessage {

    Main plugin;

    public ConfigMessage(Main instance) {
        this.plugin = instance;
    }

    public String returnString(String str) {
        return plugin.hex.translateHexColorCodes("#", "/", plugin.yamlConfig.getString("messages." + str));
    }
}
