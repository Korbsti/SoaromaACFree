package me.korbsti.soaromaac.violations;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class ViolationLogger {
    String dir = System.getProperty("user.dir");
    String path = dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "logs";
    Main plugin;

    String userPath = dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "logs"
            + File.separator + "kick.txt";

    String reportPath = dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "logs"
            + File.separator + "reports.txt";

    String userPathLog = dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "logs";

    File kickFile = new File(userPath);

    File reportFile = new File(reportPath);

    public ViolationLogger(Main instance) {
        this.plugin = instance;
    }

    public void createCustomConfig() {
        plugin.violationFile = new File(userPathLog + File.separator + "violations.yml");
        if (!plugin.violationFile.exists()) {
            try {
                plugin.violationFile.createNewFile();
                plugin.violationFileYaml.save(plugin.violationFile);
            } catch (IOException e) {
            }
        }
        plugin.violationFile = new File(userPathLog, "violations.yml");
        plugin.violationFileYaml = YamlConfiguration.loadConfiguration(plugin.violationFile);
    }

    public void kickWriter(Player player) {
        if (kickFile.exists() && !kickFile.isDirectory()) {
            Date date = new Date();
            String line = "Kicked player " + player.getName() + " on " + date + " with ip of " + player.getAddress()
                    + System.lineSeparator();
            FileWriter writer;
            try {
                writer = new FileWriter(userPath, true);
                writer.write(line);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeReportLog(CommandSender player, String report) {
        if (reportFile.exists() && !reportFile.isDirectory()) {
            Date date = new Date();
            String line = plugin.hex
                    .translateHexColorCodes("#", "/",
                            plugin.yamlConfig.getString("messages.reportLog").replace("{player}", player.getName())
                                    .replace("{report}", report).replace("{date}", date.toString()))
                    + System.lineSeparator();
            FileWriter writer;
            try {
                writer = new FileWriter(reportPath, true);
                writer.write(line);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void recieveRecentReports(int x, CommandSender sender) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(reportFile));
            String string;
            ArrayList<String> send = new ArrayList<>();
            while ((string = br.readLine()) != null) {
                send.add(string);
            }
            int size = send.size();
            if (size == 0) {
                sender.sendMessage(plugin.configMessage.returnString("noReports"));
                br.close();
                return;
            }
            size -= x;
            if (size < 0) {
                size = 0;
            }
            for (int i = size; i != send.size(); i++) {
                sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", send.get(i)));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recieveRecentPlayerViolations(int x, CommandSender sender, String p) {
        File file = new File(userPathLog + File.separator + "PlayerLog" + File.separator + p + ".txt");
        try {
            if (!file.exists()) {
                sender.sendMessage(plugin.configMessage.returnString("noFile"));
            } else {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String string;
                ArrayList<String> send = new ArrayList<>();
                while ((string = br.readLine()) != null) {
                    send.add(string);
                }
                int size = send.size();
                if (size == 0) {
                    sender.sendMessage(plugin.configMessage.returnString("fileEmpty"));
                    br.close();
                    return;
                }
                size -= x;
                if (size < 0) {
                    size = 0;
                }
                for (int i = size; i != send.size(); i++) {
                    sender.sendMessage(plugin.hex.translateHexColorCodes("#", "/", send.get(i)));
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void makeFolder() {
        File file = new File(path);
        if (file.mkdirs()) {
            Bukkit.getLogger().info("Generated SoaromaSAC log folder...");
        }
        if (!kickFile.exists()) {
            File kickFile = new File(userPath);
            try {
                kickFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!reportFile.exists()) {
            File reportFile = new File(reportPath);
            try {
                reportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeViolation(Player p, String violationMessage) {
        String name = p.getName();
        PlayerInstance playerInstance = plugin.playerInstances.get(name);
        if(playerInstance == null){
            return;
        }
        if (playerInstance.playerLogCount <= plugin.playerViolationLog) {
            playerInstance.playerLogCount++;
            return;
        } else {
            playerInstance.playerLogCount = 0;
        }

        File file = new File(userPathLog + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                Date date = new Date();
                String line = violationMessage + " on " + date + System.lineSeparator();
                FileWriter writer;
                writer = new FileWriter(path + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt",
                        true);
                writer.write(line);
                writer.close();

            } else {
                Date date = new Date();
                String line = violationMessage + " on " + date + System.lineSeparator();
                FileWriter writer;
                writer = new FileWriter(path + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt",
                        true);
                writer.write(line);
                writer.close();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void writePunishViolation(Player p, String user, String punishment) {
        File file = new File(userPathLog + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt");
        try {

            if (!file.exists()) {
                file.createNewFile();
                Date date = new Date();
                String line = "&3" + user + " " + punishment + " " + p.getName() + " on " + date + System.lineSeparator();
                FileWriter writer;
                writer = new FileWriter(path + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt",
                        true);
                writer.write(line);
                writer.close();

            } else {
                Date date = new Date();
                String line = "&3" + user + " " + punishment + " " + p.getName() + " on " + date + System.lineSeparator();
                FileWriter writer;
                writer = new FileWriter(path + File.separator + "PlayerLog" + File.separator + p.getName() + ".txt",
                        true);
                writer.write(line);
                writer.close();

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
