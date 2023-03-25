package me.korbsti.soaromaac.events.connection;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class JoinAndLeave implements Listener {
    Main plugin;

    String str = "YOU WHO DECOMPILED THIS, I HOPE YOU STUB YOUR TOE! I hope you get hurt >;[";

    public JoinAndLeave(Main instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        plugin.playerInstances.put(event.getPlayer().getName(), new PlayerInstance(event.getPlayer(), plugin));
        plugin.playerInstances.get(event.getPlayer().getName()).setDefaultPlayerVariables();
        if (event.getPlayer().hasPermission("sac.notify") && plugin.yamlConfig.getBoolean("other.updateNotifications")) {
            versionChecker(version -> {
                if (plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                } else {
                    event.getPlayer().sendMessage(plugin.configMessage.returnString("notUpToDate"));
                }
            });
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                if (event.getPlayer().hasPermission("sac.startup.choose")) {

                    if (plugin.configType.equals("none")) {

                        for (Object obj : plugin.yamlConfig.getList("messages.no-config")) {
                            event.getPlayer().sendMessage(plugin.hex.translateHexColorCodes("#", "/", obj.toString()));
                        }

                    }

                }

            }

        });

        if (plugin.violationFileYaml.get(event.getPlayer().getUniqueId() + ".violations") == null) {
            try {
                plugin.violationFileYaml.set(event.getPlayer().getUniqueId() + ".violations", 0);
                plugin.violationFileYaml.save(plugin.violationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plugin.fileManager.checkPlayerTXT(event.getPlayer());
        if (plugin.violationFileYaml.get(event.getPlayer().getUniqueId() + ".warnCount") == null) {
            try {
                plugin.violationFileYaml.set(event.getPlayer().getUniqueId() + ".warnCount", 0);
                plugin.violationFileYaml.save(plugin.violationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // plugin.injector.addPlayer(event.getPlayer(), plugin);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.injector.removePlayer(e.getPlayer());
        String playerName = e.getPlayer().getName();
        plugin.playerInstances.remove(playerName);
    }

    public void versionChecker(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 87702).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                Bukkit.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
