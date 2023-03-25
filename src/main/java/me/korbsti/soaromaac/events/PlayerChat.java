package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PlayerChat implements Listener {
    Main plugin;

    public PlayerChat(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        /*
        if(event.getMessage().contains("switchneural")){
            Bukkit.broadcastMessage("Switched to:" + !plugin.neuralWrong);
            plugin.neuralWrong = !plugin.neuralWrong;
        }*/

        Player player = event.getPlayer();
        String playerName = player.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
        if (playerInstance.typingKey == null)
            playerInstance.typingKey= false;
        if (playerInstance.typingKey) {
            event.setCancelled(true);
            if (event.getMessage().equals("cancel")) {
                player.sendMessage(plugin.configMessage.returnString("cancel"));
                return;
            }
            if (plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName().equals(plugin.getType.returnType(event.getMessage()))) {
                if (plugin.getType.returnType(event.getMessage()).equals("Double")) {
                    if (event.getMessage().split("\\.").length != 2) {
                        player.sendMessage(plugin.configMessage.returnString("onlyDouble"));
                        playerInstance.typingKey =  false;
                        return;
                    }
                }
                player.sendMessage(plugin.configMessage.returnString("keyChanged"));
                boolean isString = plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName().equals("String");
                boolean isBoolean = playerInstance.currentPath.split(playerInstance.currentPath.split("\\.")[playerInstance.currentPath.split("\\.").length - 1]).length == 2 && plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName().equals("Boolean");

                String dir = System.getProperty("user.dir");

                try {
                    plugin.enableAntiCheat = false;
                    plugin.yamlConfig.set(playerInstance.currentPath, event.getMessage());

                    BufferedReader br = new BufferedReader(new FileReader(new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "main.yml")));
                    File placeholder = new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "placeholder.yml");
                    File original = new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "main.yml");
                    if (placeholder.exists()) {
                        placeholder.delete();
                        placeholder.createNewFile();
                    } else {
                        placeholder.createNewFile();
                    }
                    FileWriter writer = new FileWriter(placeholder);
                    String string;
                    String replaceWith = playerInstance.currentPath.split("\\.")[playerInstance.currentPath.split("\\.").length - 1];
                    boolean yes = true;
                    while ((string = br.readLine()) != null) {
                        if (isString) {
                            string = string.replace(replaceWith + ":", replaceWith + ":" + " '" + event.getMessage() + "'!@#$!@#adada231dqwawd");

                        } else if (string.contains(replaceWith + ":") && isBoolean) {
                            if (yes) {
                                yes = false;
                                writer.write(string + System.lineSeparator());
                                continue;
                            } else {
                                string = string.replace(replaceWith + ":", replaceWith + ":" + " " + event.getMessage() + "!@#$!@#adada231dqwawd");
                            }
                        } else {
                            string = string.replace(replaceWith + ":", replaceWith + ":" + " " + event.getMessage() + "!@#$!@#adada231dqwawd");

                        }
                        if (string.contains("!@#$!@#adada231dqwawd")) {
                            string = string.substring(0, string.indexOf("!@#$!@#adada231dqwawd"));
                        }
                        writer.write(string + System.lineSeparator());
                    }
                    writer.close();
                    original.delete();
                    original.createNewFile();
                    writer = new FileWriter(original);
                    br = new BufferedReader(new FileReader(new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "placeholder.yml")));
                    string = "";
                    // replaceWith =
                    // plugin.currentPath.get(player.getName()).split(".")[plugin.currentPath.get(player.getName()).length()];
                    while ((string = br.readLine()) != null) {
                        writer.write(string + System.lineSeparator());
                    }
                    writer.close();
                    br.close();
                    placeholder.delete();

                    plugin.yamlConfig = YamlConfiguration.loadConfiguration(plugin.configFile);
                    plugin.configManager.resetValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                 * try { plugin.yamlConfig.save(plugin.configFile); } catch
                 * (IOException e) { // TODO Auto-generated catch block
                 * e.printStackTrace(); }
                 */
            } else {
                player.sendMessage(plugin.configMessage.returnString("invalidType"));
            }
            playerInstance.typingKey = false;
        }

        if (playerInstance.muted == null) {
            playerInstance.muted = false;
        }
        if (playerInstance.muted) {
            player.sendMessage(plugin.configMessage.returnString("mutedPlayerMessage"));
            event.setCancelled(true);
        }
    }
}
