package me.korbsti.soaromaac;

import me.korbsti.soaromaac.api.SoaromaAdminPunish;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Commands implements CommandExecutor {
    Main plugin;

    public Commands(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PlayerInstance playerInstance;
        if(sender instanceof Player) playerInstance = plugin.playerInstances.get(((Player) sender).getName());
        else playerInstance = null;
        if (label.equalsIgnoreCase("sacconfigchoose")) {
            if (!sender.hasPermission("sac.startup.choose")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("no-config-id"));
                return true;
            }
            if (args[0].equals("1")) {
                plugin.preConfigChooser.preConfigChooser("default", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("2")) {
                plugin.preConfigChooser.preConfigChooser("insensitive", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("3")) {
                plugin.preConfigChooser.preConfigChooser("sensitive", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("4")) {
                plugin.preConfigChooser.preConfigChooser("auto-insensitive", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("5")) {
                plugin.preConfigChooser.preConfigChooser("recommended", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("6")) {
                plugin.preConfigChooser.preConfigChooser("gameratbest9", sender);
                plugin.configManager.reloadCustomConfig();
            } else if (args[0].equals("7")) {
                plugin.preConfigChooser.preConfigChooser("movement", sender);
                plugin.configManager.reloadCustomConfig();
            } else {
                sender.sendMessage(plugin.configMessage.returnString("no-config-id"));
                return false;
            }
            return true;

        }

        if (label.equalsIgnoreCase("sactrainneuralnetwork")) {
            plugin.trainNeuralNetwork = !plugin.trainNeuralNetwork;
            plugin.neuralNetwork.stopNetworkFromLearning = false;
            Bukkit.broadcastMessage("Neural Network Is Now: " + plugin.trainNeuralNetwork);
            return true;
        }

        if (label.equalsIgnoreCase("sachistory")) {
            if (!sender.hasPermission("sac.history")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("wrongArgsHistory"));
                return true;
            }

            try {
                int amount = Integer.valueOf(args[1]);
                plugin.violationLogger.recieveRecentPlayerViolations(amount, sender, args[0]);
            } catch (Exception e) {
                sender.sendMessage(plugin.configMessage.returnString("wrongArgsHistory"));
                return true;
            }

        }

        if (label.equalsIgnoreCase("sacvio")) {
            if (!sender.hasPermission("sac.sacvio")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("Player command only you potato!");
                return false;
            }
            plugin.ob.openViolationGUI((Player) sender);

        }

        if ("sacadmin".equalsIgnoreCase(label)) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Player command only you potato!");
                return false;
            }
            if (!sender.hasPermission("sac.admin")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }

            playerInstance.currentPath= "";
            plugin.configGUI.openGUI((Player) sender);
        }

        if ("sacreplay".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.replay")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (!plugin.enableMovementReplay) {
                sender.sendMessage(plugin.configMessage.returnString("replayNotEnabled"));

                return false;
            }
            if (args.length == 0 || args.length == 1) {
                sender.sendMessage(plugin.configMessage.returnString("wrongArgsReplay"));
                return false;
            }
            if (!new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + args[1]).exists()) {
                sender.sendMessage(plugin.configMessage.returnString("replayWrongDate"));
                return false;
            }

            if (!new File(System.getProperty("user.dir") + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "replayer" + File.separator + args[1] + File.separator + args[0] + ".txt").exists()) {
                sender.sendMessage(plugin.configMessage.returnString("replayWrongUUID"));
                return false;
            }

            plugin.writeJS.writeJS(args[0], args[1]);

            sender.sendMessage(plugin.configMessage.returnString("replayMade"));

        }

        if ("sacreport".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.report")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (args.length <= 0) {
                sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
                return true;
            }

            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                msg.append(args[i]);
                msg.append(" ");
            }
            String report = msg.toString();
            plugin.violationLogger.writeReportLog(sender, report);

            if (plugin.yamlConfig.getBoolean("other.enableReportBroadcastToAdmins")) {
                for (Player pp : Bukkit.getOnlinePlayers()) {

                    if (pp.hasPermission("sac.recieveReport")) {
                        pp.sendMessage(plugin.configMessage.returnString("recieveReport").replace("{report}", report).replace("{player}", sender.getName()));
                    }
                }
            }
            sender.sendMessage(plugin.configMessage.returnString("wroteReport"));
            return true;
        }
        if ("sacreports".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.reportList")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("reportsListWrongArgument"));
                return true;
            }

            try {
                int amount = Integer.valueOf(args[0]);
                plugin.violationLogger.recieveRecentReports(amount, sender);
            } catch (Exception e) {
                sender.sendMessage(plugin.configMessage.returnString("reportsListWrongArgument"));
                return true;
            }
        }
        if ("sactps".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.tps")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }

            if (!plugin.checkServerTPS) {
                sender.sendMessage(plugin.configMessage.returnString("serverTPSNotTrue"));
                return false;
            }
            double value = plugin.tps;
            sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", plugin.configMessage.returnString("tps").replace("{tps}", "" + value)));

        }
        if ("sacppicp".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.display.custompayload")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
                return true;
            }
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            playerInstance = plugin.playerInstances.get(targetPlayer.getName());

            String playerName = targetPlayer.getName();
            // Bukkit.broadcastMessage("" + plugin.data.get(playerName));
            if (playerInstance.data.isEmpty()) {
                for (Object packet : playerInstance.customPayload) {
                    Object minecraftKey = Reflection.getFieldValue(packet, "tag");
                    playerInstance.key.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "key")));
                    playerInstance.namespace.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "namespace")));
                    playerInstance.data.add(String.valueOf(Reflection.getFieldValue(packet, "data")));
                }
            }
            for (Object str : plugin.yamlConfig.getList("messages.sacmodified")) {
                String string = String.valueOf(str).replace("{user}", playerName).replace("{data}", "" + playerInstance.data).replace("{key}", "" + playerInstance.key).replace("{namespace}", "" + playerInstance.namespace);
                sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", string));
            }

        }
        if ("sacuser".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.user")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
                return true;
            }
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            playerInstance = plugin.playerInstances.get(targetPlayer.getName());

            if (targetPlayer == null) {
                sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                return true;
            }
            playerInstance = plugin.playerInstances.get(targetPlayer.getName());
            for (Object str : plugin.yamlConfig.getList("messages.sacinfo")) {
                String line = str.toString();
                String name = targetPlayer.getName();
                String b = playerInstance.elytraUse.toString();
                String user = "null";
                String playerIP = "null";
                String currentViolations = "null";
                String totalViolation = "null";
                String playerUUID = "null";
                String playerSpeed = "null";
                String coords = "null";
                String isInVeh = "null";
                String world = "null";
                String flight = "null";
                String isFlying = "null";
                String warns = "null";
                if (!Boolean.valueOf(b)) {
                    b = "" + targetPlayer.isFlying();
                }
                try {
                    user = name;
                } catch (Exception e) {
                    user = "null";
                }
                try {
                    playerIP = targetPlayer.getAddress().toString();
                } catch (Exception e) {
                    playerIP = "null";
                }
                try {
                    currentViolations = playerInstance.num.toString();
                } catch (Exception e) {
                    currentViolations = "null";
                }
                try {
                    totalViolation = plugin.violationFileYaml.getInt(targetPlayer.getUniqueId() + ".violations") + "";
                } catch (Exception e) {
                    totalViolation = "null";
                }
                try {
                    playerUUID = targetPlayer.getUniqueId().toString();
                } catch (Exception e) {
                    playerUUID = "null";
                }
                try {
                    playerSpeed = playerInstance.playerSpeed.toString();
                } catch (Exception e) {
                    playerSpeed = "null";
                }
                try {
                    coords = "X: " + playerInstance.playerX + " Y: " + playerInstance.playerY + " Z: " + playerInstance.playerZ;
                } catch (Exception e) {
                    coords = "null";
                }
                try {
                    isInVeh = String.valueOf(targetPlayer.isInsideVehicle());
                } catch (Exception e) {
                    isInVeh = "null";
                }
                try {
                    world = String.valueOf(targetPlayer.getWorld());
                } catch (Exception e) {
                    world = "null";
                }
                try {
                    flight = String.valueOf(targetPlayer.getAllowFlight());
                } catch (Exception e) {
                    flight = "null";
                }
                try {
                    isFlying = b;
                } catch (Exception e) {
                    isFlying = "null";
                }
                try {
                    warns = plugin.violationFileYaml.getInt(targetPlayer.getUniqueId() + ".warnCount") + "";
                } catch (Exception e) {
                    warns = "null";
                }

                sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", line.replace("{user}", user).replace("{player-ip}", playerIP).replace("{current-vio}", currentViolations).replace("{total-vio}", totalViolation).replace("{player-uuid}", playerUUID).replace("{player-speed}", playerSpeed).replace("{coords}", coords).replace("{is-in-veh}", isInVeh).replace("{world}", world).replace("{flight}", flight).replace("{isflying}", isFlying).replace("{warns}", warns)));
            }
        }

        if ("sacwarn".equalsIgnoreCase(label) && args.length != 0) {
            if (args.length == 1) {
                sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
                return false;
            }

            if (sender instanceof Player) {
                if (sender.hasPermission("sac.warn")) {
                    Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                    playerInstance = plugin.playerInstances.get(targetsender.getName());
                    if (targetsender != null) {
                        if (playerInstance.warnCount == null) {
                            playerInstance.warnCount = 0.0;
                        }

                        if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                            sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                            return false;
                        }

                        StringBuilder msg = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            msg.append(args[i]);
                            msg.append(" ");
                        }
                        playerInstance.warnCount++;
                        sender.sendMessage(plugin.configMessage.returnString("warnedPlayer"));
                        targetsender.sendMessage(plugin.configMessage.returnString("warnMessageFormat").replace("{reason}", msg));
                        plugin.violationFileYaml.set(targetsender.getUniqueId() + ".warnCount", plugin.violationFileYaml.getInt(targetsender.getUniqueId() + ".warnCount") + 1);
                        try {
                            plugin.violationFileYaml.save(plugin.violationFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("notOnline"));

                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                }
            } else {
                Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                playerInstance = plugin.playerInstances.get(targetsender.getName());
                if (targetsender != null) {
                    if (playerInstance.warnCount == null) {
                        playerInstance.warnCount =  0.0;
                    }
                    if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                        sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                        return false;
                    }
                    StringBuilder msg = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        msg.append(args[i]);
                        msg.append(" ");
                    }
                    playerInstance.warnCount++;
                    sender.sendMessage(plugin.configMessage.returnString("warnedPlayer"));
                    targetsender.sendMessage(plugin.configMessage.returnString("warnMessageFormat").replace("{reason}", msg));
                    plugin.violationFileYaml.set(targetsender.getUniqueId() + ".warnCount", plugin.violationFileYaml.getInt(targetsender.getUniqueId() + ".warnCount") + 1);
                    try {
                        plugin.violationFileYaml.save(plugin.violationFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                }
            }
            return true;
        }

        if ("sacnotify".equalsIgnoreCase(label) && args.length == 0) {
            sender.sendMessage(plugin.configMessage.returnString("invalidArgumentsNotify"));
            return true;
        }

        if ("sacnotify".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                StringBuilder second = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    second.append(args[i]);
                }
                if (sender.hasPermission("sac.alertCommandGet") && "enable".equalsIgnoreCase(second.toString())) {
                    playerInstance.enableNotifications = true;
                    sender.sendMessage(plugin.configMessage.returnString("notificationsEnabled"));

                }
                if (sender.hasPermission("sac.alertCommandGet") && "disable".equalsIgnoreCase(second.toString())) {
                    playerInstance.enableNotifications = true;
                    sender.sendMessage(plugin.configMessage.returnString("notificationsDisabled"));

                }
            } else {
                sender.sendMessage("This is an in-game command!");
            }
        }
        if ("sacwarn".equalsIgnoreCase(label) && args.length == 0) {
            sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
            return true;
        }
        if ("sacmute".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.mute")) {
                    Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                    playerInstance = plugin.playerInstances.get(targetsender.getName());
                    if (targetsender != null) {

                        if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                            sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                            return false;
                        }

                        if (playerInstance.muted == null) {
                            playerInstance.muted = false;
                        }

                        if (!playerInstance.muted) {
                            targetsender.sendMessage(plugin.configMessage.returnString("mutedPlayerMessage"));
                            sender.sendMessage(plugin.configMessage.returnString("mutedPlayer"));
                            playerInstance.muted = true;

                            if (plugin.enableApi) {
                                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!(sender instanceof Player)) {
                                            return;
                                        }
                                        SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetsender, "muted");
                                        Bukkit.getPluginManager().callEvent(d);
                                    }
                                });
                            }

                        } else {
                            targetsender.sendMessage(plugin.configMessage.returnString("unmutedPlayerMessage"));
                            sender.sendMessage(plugin.configMessage.returnString("unmutedPlayer"));
                            playerInstance.muted = false;

                            if (plugin.enableApi) {
                                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!(sender instanceof Player)) {
                                            return;
                                        }
                                        SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetsender, "ummuted");
                                        Bukkit.getPluginManager().callEvent(d);
                                    }
                                });
                            }
                        }
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                }
            } else {
                Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                playerInstance = plugin.playerInstances.get(targetsender.getName());
                if (targetsender != null) {
                    if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                        sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                        return false;
                    }
                    if (playerInstance.muted == null) {
                        playerInstance.muted = false;
                    }
                    if (!playerInstance.muted) {
                        targetsender.sendMessage(plugin.configMessage.returnString("mutedPlayerMessage"));
                        sender.sendMessage(plugin.configMessage.returnString("mutedPlayer"));
                        playerInstance.muted =  true;

                        if (plugin.enableApi) {
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if (!(sender instanceof Player)) {
                                        return;
                                    }
                                    SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetsender, "muted");
                                    Bukkit.getPluginManager().callEvent(d);
                                }
                            });
                        }

                    } else {
                        targetsender.sendMessage(plugin.configMessage.returnString("unmutedPlayerMessage"));
                        sender.sendMessage(plugin.configMessage.returnString("unmutedPlayer"));
                        playerInstance.muted = false;

                        if (plugin.enableApi) {
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if (!(sender instanceof Player)) {
                                        return;
                                    }
                                    SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetsender, "unmuted");
                                    Bukkit.getPluginManager().callEvent(d);
                                }
                            });
                        }

                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                }
            }
            return true;
        }
        if (("sacgui".equalsIgnoreCase(label) && args.length == 0) || ("sacgui".equalsIgnoreCase(label) && args.length >= 2)) {
            sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));

            return true;
        }

        if ("sacgui".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.gui")) {
                    Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                    playerInstance = plugin.playerInstances.get(targetsender.getName());
                    if (targetsender != null) {

                        if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                            sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                            return false;
                        }

                        plugin.ob.playerGUIPunish(((Player) sender).getPlayer(), targetsender);
                        playerInstance.playerDouble.put(((Player) sender).getPlayer(), targetsender);
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("notOnline"));

                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                }
                return true;
            } else {
                sender.sendMessage("This is an in-game command only!");
            }
        }
        if (("sackick".equalsIgnoreCase(label) && args.length == 0) || ("sackick".equalsIgnoreCase(label) && args.length >= 2)) {
            sender.sendMessage("Proper usage, sackick <player>");
            return true;
        }
        if ("sackick".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.kick")) {
                    Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                    if (targetsender != null) {

                        if (targetsender != sender && targetsender.hasPermission("sac.punish.bypass")) {
                            sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                            return false;
                        }

                        targetsender.kickPlayer(plugin.configMessage.returnString("kickMessage"));

                        plugin.violationLogger.writePunishViolation(targetsender, sender.getName(), "kick");
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                        return false;
                    }

                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (!(sender instanceof Player)) {
                                    return;
                                }
                                SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetsender, "kick");
                                Bukkit.getPluginManager().callEvent(d);

                            }
                        });
                    }

                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                }
            } else {
                Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                if (targetsender != null) {
                    targetsender.sendMessage(plugin.configMessage.returnString("kickMessage"));
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                }
            }
            return true;
        }
        if (("sacunban".equalsIgnoreCase(label) && args.length == 0) || ("sacunban".equalsIgnoreCase(label) && args.length >= 2)) {
            sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
            return true;
        }
        if ("sacunban".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.unban")) {
                    String targetsender = args[0];
                    if (targetsender != null) {
                        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                        if (banList.isBanned(targetsender)) {
                            banList.pardon(targetsender);
                            sender.sendMessage(plugin.configMessage.returnString("unbanMessage"));
                        } else {
                            sender.sendMessage(plugin.configMessage.returnString("unbanMessageError"));
                        }
                        return true;
                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                    return true;
                }
            } else {
                String targetsender = args[0];
                if (targetsender != null) {
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    if (banList.isBanned(targetsender)) {
                        banList.pardon(targetsender);
                        sender.sendMessage(plugin.configMessage.returnString("unbanMessage"));
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("unbanMessageError"));

                    }
                    return true;
                }
            }
        }
        if ("sacreload".equalsIgnoreCase(label)) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.reload")) {
                    sender.sendMessage(ChatColor.BLUE + "Reloading SAC is not safe, restarting is safer : Reloaded SimpleAntiCheat");
                    plugin.enableAntiCheat = false;
                    plugin.yamlConfig = YamlConfiguration.loadConfiguration(plugin.configFile);
                    plugin.configManager.resetValues();

                    return true;
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "Reloading SAC is not safe, restarting is safer : Reloaded SimpleAntiCheat");
                plugin.enableAntiCheat = false;
                plugin.yamlConfig = YamlConfiguration.loadConfiguration(plugin.configFile);
                plugin.configManager.resetValues();
                return true;
            }
        }
        if (("sacping".equalsIgnoreCase(label) && args.length == 0) || ("sacping".equalsIgnoreCase(label) && args.length >= 2)) {
            sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
            return true;
        }

        if ("sacping".equalsIgnoreCase(label) && args.length == 1) {
            if (sender instanceof Player) {
                if (sender.hasPermission("sac.ping")) {
                    Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                    if (targetsender != null) {
                        String replace = plugin.yamlConfig.getString("messages.playerPingMessage").replace("{ping}", playerInstance.playerPing.toString());
                        sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", replace));
                    } else {
                        sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                    }
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("noPerm"));
                }
            } else {
                Player targetsender = Bukkit.getServer().getPlayer(args[0]);
                if (targetsender != null) {
                    sender.sendMessage("Ping: " + playerInstance.playerPing);
                } else {
                    sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                }
            }
            return true;
        }
        if ("sachashclear".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.hashclear")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                return true;
            }
            sender.sendMessage(plugin.configMessage.returnString("clearedHashMaps"));

            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.addHashMaps(p);
                    }
                }

            });

        }

        if ("sacfreeze".equalsIgnoreCase(label)) {
            if (!sender.hasPermission("sac.freeze")) {
                sender.sendMessage(plugin.configMessage.returnString("noPerm"));

                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(plugin.configMessage.returnString("invalidArguments"));
                return true;
            }
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            playerInstance = plugin.playerInstances.get(targetPlayer.getName());

            if (targetPlayer == null) {
                sender.sendMessage(plugin.configMessage.returnString("notOnline"));
                return true;
            }
            if (targetPlayer != sender && targetPlayer.hasPermission("sac.punish.bypass")) {
                sender.sendMessage(plugin.configMessage.returnString("isStaff"));
                return false;
            }
            if (playerInstance.playerFrozen == null) {
                playerInstance.playerFrozen =  false;
            }
            if (!playerInstance.playerFrozen) {
                sender.sendMessage(plugin.configMessage.returnString("freezeMessage"));
                playerInstance.cancelX=  targetPlayer.getLocation().getX();
                playerInstance.cancelY =  targetPlayer.getLocation().getY();
                playerInstance.cancelZ= targetPlayer.getLocation().getZ();
                targetPlayer.sendMessage(plugin.configMessage.returnString("freezeMessagePlayer"));
                playerInstance.playerFrozen = true;
                plugin.violationLogger.writePunishViolation(targetPlayer, sender.getName(), "frozen");
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (!(sender instanceof Player)) {
                                return;
                            }
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetPlayer, "frozen");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            } else {
                sender.sendMessage(plugin.configMessage.returnString("unfreezeMessage"));
                targetPlayer.sendMessage(plugin.configMessage.returnString("unfreezeMessagePlayer"));
                playerInstance = plugin.playerInstances.get(targetPlayer.getName());
                playerInstance.playerFrozen = false;

                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (!(sender instanceof Player)) {
                                return;
                            }
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, (Player) sender, targetPlayer, "unfrozen");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            }
        }
        return false;
    }
}
