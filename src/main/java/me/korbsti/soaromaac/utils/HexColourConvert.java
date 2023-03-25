package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColourConvert {

    private final String creditForHexSnippet = "Elementeral";

    private static final char COLOR_CHAR = '\u00A7';

    Main plugin;

    public HexColourConvert(Main instance) {
        this.plugin = instance;
    }

    public String translateHexColorCodes(String startTag, String endTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public String returnSACInfo(Player targetPlayer) {
        String totalLine = "";
        PlayerInstance playerInstance = plugin.playerInstances.get(targetPlayer.getName());
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
                coords = "X: " + playerInstance.playerX + " Y: " + playerInstance.playerY + " Z: "
                        + playerInstance.playerZ;
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
            totalLine += plugin.hex
                    .translateHexColorCodes("#", "/",
                            line.replace("{user}", user)
                                    .replace("{player-ip}", playerIP)
                                    .replace("{current-vio}", currentViolations)
                                    .replace("{total-vio}", totalViolation)
                                    .replace("{player-uuid}", playerUUID)
                                    .replace("{player-speed}", playerSpeed)
                                    .replace("{coords}", coords)
                                    .replace("{is-in-veh}", isInVeh)
                                    .replace("{world}", world)
                                    .replace("{flight}", flight)
                                    .replace("{isflying}", isFlying)
                                    .replace("{warns}", warns)) + "\n";
        }
        return totalLine;
    }

}
