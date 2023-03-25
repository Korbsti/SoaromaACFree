package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class PreConfigChooser {


    Main plugin;

    public PreConfigChooser(Main instance) {
        this.plugin = instance;
    }


    public void preConfigChooser(String str, CommandSender sender) {
        try {
            plugin.configFile.delete();
            InputStream input = plugin.getResource(str + ".yml");

            java.nio.file.Files.copy(input, Path.of(plugin.configManager.directoryPathFile + File.separator + "main.yml"));
            plugin.configManager.saveDefaultConfiguration();
            plugin.configManager.resetValues();
            sender.sendMessage(plugin.configMessage.returnString("no-config-success").replace("{type}", str));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
