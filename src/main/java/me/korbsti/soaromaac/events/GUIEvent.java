package me.korbsti.soaromaac.events;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.api.SoaromaAdminPunish;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

public class GUIEvent implements Listener {
    Main plugin;

    public GUIEvent(Main instance) {
        this.plugin = instance;
    }

    public void openViolationGUI(Player p) {
        p.closeInventory();

        Inventory inv = Bukkit.getServer().createInventory(p, 54, ChatColor.translateAlternateColorCodes('&', plugin.yamlConfig.getString("GUI.Vio.GUIName")));
        ArrayList<Integer> playerNums = new ArrayList<Integer>();
        ArrayList<Player> playerPos = new ArrayList<Player>();
        for (Player pp : Bukkit.getOnlinePlayers()) {
            PlayerInstance playerInstance = plugin.playerInstances.get(pp.getName());
            playerNums.add(playerInstance.num);
            playerPos.add(pp);
        }

        for (int i = 0; i != playerNums.size() - 1; i++) {
            if (playerNums.get(i) < playerNums.get(i + 1)) {
                playerNums.add(i);
                playerNums.remove(i);
                playerPos.add(playerPos.get(i));
                playerPos.remove(i);
                i = -1;
            }
        }


        for (int x = 0; x != playerNums.size(); x++) {
            Player player = playerPos.get(x);
            PlayerInstance playerInstance = plugin.playerInstances.get(player.getName());
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
            playerHeadMeta.setOwningPlayer(player);
            ArrayList<String> lore1 = new ArrayList<>();


            for (Object str : plugin.yamlConfig.getList("messages.sacinfo")) {
                String line = str.toString();
                String name = player.getName();
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
                    b = "" + player.isFlying();
                }
                try {
                    user = name;
                } catch (Exception e) {
                    user = "null";
                }
                try {
                    playerIP = player.getAddress().toString();
                } catch (Exception e) {
                    playerIP = "null";
                }
                try {
                    currentViolations = playerInstance.num.toString();
                } catch (Exception e) {
                    currentViolations = "null";
                }
                try {
                    totalViolation = plugin.violationFileYaml.getInt(player.getUniqueId() + ".violations") + "";
                } catch (Exception e) {
                    totalViolation = "null";
                }
                try {
                    playerUUID = player.getUniqueId().toString();
                } catch (Exception e) {
                    playerUUID = "null";
                }
                try {
                    playerSpeed = playerInstance.playerSpeed.toString();
                } catch (Exception e) {
                    playerSpeed = "null";
                }
                try {
                    coords = "X: " + playerInstance.playerX.toString().substring(0, 5) + " Y: " + playerInstance.playerY.toString().substring(0, 5) + " Z: "
                            + playerInstance.playerZ.toString().substring(0, 5);
                } catch (Exception e) {
                    coords = "null";
                }
                try {
                    isInVeh = String.valueOf(player.isInsideVehicle());
                } catch (Exception e) {
                    isInVeh = "null";
                }
                try {
                    world = String.valueOf(player.getWorld());
                } catch (Exception e) {
                    world = "null";
                }
                try {
                    flight = String.valueOf(player.getAllowFlight());
                } catch (Exception e) {
                    flight = "null";
                }
                try {
                    isFlying = b;
                } catch (Exception e) {
                    isFlying = "null";
                }
                try {
                    warns = plugin.violationFileYaml.getInt(player.getUniqueId() + ".warnCount") + "";
                } catch (Exception e) {
                    warns = "null";
                }

                lore1.add(
                        plugin.hex
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
                                                .replace("{warns}", warns)));
            }

            playerHeadMeta.setLore(lore1);
            playerHeadMeta.setDisplayName(player.getName());
            playerHead.setItemMeta(playerHeadMeta);
            inv.setItem(x, playerHead);
            if (x == 53) {
                break;
            }
        }

        p.openInventory(inv);

    }


    public void banGUI(Player p, Player playerName) {
        p.closeInventory();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());
        if (playerInstance.muted == null) {
            playerInstance.muted = false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount= 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = playerUUID.toString();
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Ban Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();

        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml
                .getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);

        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.yamlConfig.getString("GUI.Ban.FirstTime.DisplayName"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////
        ItemStack playerBan1 = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 1);
        ItemMeta playerBanMeta1 = playerBan.getItemMeta();
        playerBanMeta1.setDisplayName(plugin.yamlConfig.getString("GUI.Ban.SecondTime.DisplayName"));
        playerBan1.setItemMeta(playerBanMeta1);
        /////////////////////////////////////////
        ItemStack playerBan2 = new ItemStack(Material.ORANGE_STAINED_GLASS, 1);
        ItemMeta playerBanMeta2 = playerBan.getItemMeta();
        playerBanMeta2.setDisplayName(plugin.yamlConfig.getString("GUI.Ban.ThirdTime.DisplayName"));
        playerBan2.setItemMeta(playerBanMeta2);
        /////////////////////////////////////////
        ItemStack playerBan3 = new ItemStack(Material.BLUE_STAINED_GLASS, 1);
        ItemMeta playerBanMeta3 = playerBan.getItemMeta();
        playerBanMeta3.setDisplayName(plugin.yamlConfig.getString("GUI.Ban.FourthTime.DisplayName"));
        playerBan3.setItemMeta(playerBanMeta3);
        ItemStack playerBan4;
        playerBan4 = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta playerBanMeta4;
        playerBanMeta4 = playerBan.getItemMeta();
        playerBanMeta4.setDisplayName(plugin.yamlConfig.getString("GUI.Ban.FifthTime.DisplayName"));
        playerBan4.setItemMeta(playerBanMeta4);
        /////////////////////////////////////////

        ItemStack side = new ItemStack(Material.BLUE_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9) {
            playerList.setItem(a, side);
        }

        playerList.setItem(4, playerHead);
        playerList.setItem(20, playerBan);
        playerList.setItem(21, playerBan1);
        playerList.setItem(22, playerBan2);
        playerList.setItem(23, playerBan3);
        playerList.setItem(24, playerBan4);

        p.openInventory(playerList);
    }

    public void freezeGUI(Player p, Player playerName) {
        p.closeInventory();

        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());

        if (playerInstance.muted == null) {
            playerInstance.muted =  false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount = 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = "" + playerUUID;
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Freeze Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml
                .getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);
        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.ICE, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bFreeze Player"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////

        ItemStack side = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9)
            playerList.setItem(a, side);

        playerList.setItem(4, playerHead);
        playerList.setItem(22, playerBan);
        p.openInventory(playerList);
    }


    public void kickGUI(Player p, Player playerName) {
        p.closeInventory();

        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());

        if (playerInstance.muted == null) {
            playerInstance.muted =  false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount = 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = playerUUID.toString();
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Kick Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml.getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);
        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bKick Player"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////

        ItemStack side = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9) {
            playerList.setItem(a, side);
        }

        playerList.setItem(4, playerHead);
        playerList.setItem(22, playerBan);
        p.openInventory(playerList);
    }

    public void muteGUI(Player p, Player playerName) {
        p.closeInventory();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());

        if (playerInstance.muted == null) {
            playerInstance.muted =  false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount = 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = playerUUID.toString();
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Warn Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml
                .getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);
        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bClear Warns"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////
        ItemStack playerKick = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta playerKickMeta = playerKick.getItemMeta();
        playerKickMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bWarn"));

        ItemStack side = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9) {
            playerList.setItem(a, side);
        }

        playerKick.setItemMeta(playerKickMeta);
        playerList.setItem(4, playerHead);
        playerList.setItem(24, playerKick);
        playerList.setItem(20, playerBan);
        p.openInventory(playerList);
    }

    public void playerGUIPunish(Player p, Player playerName) {
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());

        if (playerInstance.muted == null) {
            playerInstance.muted =  false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount = 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = playerUUID.toString();
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Punishment Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml
                .getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);
        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.IRON_BARS, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bBan Menu"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////
        ItemStack playerMute = new ItemStack(Material.BOOK, 1);
        ItemMeta playerMuteMeta = playerBan.getItemMeta();
        playerMuteMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bWarn Menu"));
        playerMute.setItemMeta(playerMuteMeta);
        /////////////////////////////////////////
        ItemStack playerMuteReal = new ItemStack(Material.BARRIER, 1);
        ItemMeta playerMuteMetaReal = playerBan.getItemMeta();
        playerMuteMetaReal.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bMute Menu"));
        playerMuteReal.setItemMeta(playerMuteMetaReal);
        /////////////////////////////////////////
        ItemStack playerFreeze = new ItemStack(Material.ICE, 1);
        ItemMeta playerFreezeMeta = playerBan.getItemMeta();
        playerFreezeMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bFreeze Menu"));
        playerFreeze.setItemMeta(playerFreezeMeta);
        /////////////////////////////////////////
        ItemStack playerKick = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta playerKickMeta = playerBan.getItemMeta();
        playerKickMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bKick Menu"));

        ItemStack side = new ItemStack(Material.CYAN_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9) {
            playerList.setItem(a, side);
        }

        playerKick.setItemMeta(playerKickMeta);
        playerList.setItem(4, playerHead);
        playerList.setItem(24, playerMute); // playerMute, 25
        playerList.setItem(23, playerMuteReal); // playerMuteReal, 40
        playerList.setItem(22, playerKick); // playerKick, 22
        playerList.setItem(20, playerBan); // playerBan, 19
        playerList.setItem(21, playerFreeze); // playerFreeze 37
        p.openInventory(playerList);
    }

    public void realMuteGUI(Player p, Player playerName) {
        p.closeInventory();
        PlayerInstance playerInstance = plugin.playerInstances.get(playerName.getName());

        if (playerInstance.muted == null) {
            playerInstance.muted =  false;
        }
        if (playerInstance.warnCount == null) {
            playerInstance.warnCount = 0.0;
        }
        UUID playerUUID = playerName.getUniqueId();
        String playerUUIDS = playerUUID.toString();
        Inventory playerList = Bukkit.getServer().createInventory(p, 54, "Mute Menu");
        //////////////////////////////////////////////
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(playerName);
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Username &8-> #00bf5f/" + playerName.getName()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/UUID &8-> #00bf5f/" + playerUUIDS));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Online &8-> #00bf5f/" + playerName.isOnline()));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Muted &8-> #00bf5f/" + playerInstance.muted));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Warns &8-> #00bf5f/" + plugin.violationFileYaml
                .getInt(p.getUniqueId() + ".warnCount")));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Violations &8-> #00bf5f/" + playerInstance.num));
        lore1.add(plugin.hex.translateHexColorCodes("#", "/", "#26ff92/Total-Violations &8-> #00bf5f/"
                + plugin.violationFileYaml.getInt(p.getUniqueId() + ".violations")));

        playerHeadMeta.setLore(lore1);
        playerHeadMeta.setDisplayName(playerName.getName());
        playerHead.setItemMeta(playerHeadMeta);
        /////////////////////////////////////////
        ItemStack playerBan = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta playerBanMeta = playerBan.getItemMeta();
        playerBanMeta.setDisplayName(plugin.hex.translateHexColorCodes("#", "/", "&bMute Player"));
        playerBan.setItemMeta(playerBanMeta);
        /////////////////////////////////////////

        ItemStack side = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

        for (int x = 0; x != 9; x++) {
            playerList.setItem(x, side);
        }
        for (int i = 45; i != 54; i++) {
            playerList.setItem(i, side);
        }
        for (int z = 0; z != 54 - 9; z += 9) {
            playerList.setItem(z, side);
        }
        for (int a = 8; a < 48; a += 9) {
            playerList.setItem(a, side);
        }

        playerList.setItem(4, playerHead);
        playerList.setItem(22, playerBan);
        p.openInventory(playerList);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        PlayerInstance playerInstance = plugin.playerInstances.get(p.getName());
        Player targetPlayer = playerInstance.playerDouble.get(p);
        PlayerInstance targetPlayerInstance = null;
        if(targetPlayer != null) if(plugin.playerInstances.get(targetPlayer.getName()) != null) targetPlayerInstance = plugin.playerInstances.get(targetPlayer.getName());
        if (ChatColor.translateAlternateColorCodes('&', plugin.yamlConfig.getString("GUI.Vio.GUIName")).equals(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            return;
        }

        if (plugin.yamlConfig.getString("GUI.Admin.GUIName").equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            if (!p.hasPermission("sac.gui.admin")) {
                p.closeInventory();
                return;
            }
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                if (!playerInstance.currentPath.equals("")) {
                    if (plugin.yamlConfig.isConfigurationSection(playerInstance.currentPath + "." + e
                            .getCurrentItem().getItemMeta().getDisplayName())) {
                        if (!playerInstance.currentPath.equals("")) {
                            playerInstance.currentPath= playerInstance.currentPath + "." + e.getCurrentItem().getItemMeta().getDisplayName();
                        } else {
                            playerInstance.currentPath =  e.getCurrentItem().getItemMeta().getDisplayName();
                        }
                        plugin.configGUI.openGUI(p);
                        return;
                    }
                    playerInstance.currentPath = playerInstance.currentPath + "." + e.getCurrentItem().getItemMeta().getDisplayName();
                    if (plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName().equals("ArrayList")) {
                        p.sendMessage(plugin.hex.translateHexColorCodes("#", "/", plugin.configMessage.returnString("cannotSetArray")));
                        playerInstance.typingKey = false;
                        p.closeInventory();
                        return;
                    }
                    playerInstance.typingKey = true;
                    p.sendMessage(plugin.hex.translateHexColorCodes("#", "/", plugin.yamlConfig.getString(
                            "messages.configMsg").replace("{value}", "" + plugin.yamlConfig.get(playerInstance.currentPath)).replace(
                            "{type}", plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName())));
                    p.closeInventory();

                    return;
                }

                if (plugin.yamlConfig.isConfigurationSection(playerInstance.currentPath)) {
                    if (!playerInstance.currentPath.equals("")) {
                        playerInstance.currentPath = playerInstance.currentPath + "." + e.getCurrentItem().getItemMeta().getDisplayName();
                    } else {
                        playerInstance.currentPath =  e.getCurrentItem().getItemMeta().getDisplayName();
                    }
                    plugin.configGUI.openGUI(p);
                    return;
                }

                playerInstance.currentPath = playerInstance.currentPath + "." + e.getCurrentItem().getItemMeta().getDisplayName();
                playerInstance.typingKey =  true;
                p.sendMessage(plugin.hex.translateHexColorCodes("#", "/", plugin.yamlConfig.getString("messages.configMsg").replace("{value}", "" + plugin.yamlConfig.get(playerInstance.currentPath)).replace("{type}", plugin.yamlConfig.get(playerInstance.currentPath).getClass().getSimpleName())));
                p.closeInventory();

                return;
            }

        }

        if ("Punishment Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            int slot = e.getSlot();
            if (slot == 24) {
                muteGUI(p, playerInstance.playerDouble.get(p));
            }
            if (slot == 22) {
                kickGUI(p, playerInstance.playerDouble.get(p));
            }
            if (slot == 20) {
                banGUI(p, playerInstance.playerDouble.get(p));
            }
            if (slot == 23) {
                realMuteGUI(p, playerInstance.playerDouble.get(p));
            }
            if (slot == 21) {
                freezeGUI(p, playerInstance.playerDouble.get(p));
            }
            return;
        }
        if ("Warn Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            if (!p.hasPermission("sac.warn")) {
                p.sendMessage(plugin.configMessage.returnString("noPerm"));
                p.closeInventory();
                return;
            }
            if (e.getSlot() == 24) {
                if (!p.hasPermission("sac.warn")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }
                if (targetPlayerInstance.warnCount == null) {
                    targetPlayerInstance.warnCount = 0.0;
                }
                targetPlayerInstance.warnCount++;
                p.sendMessage(plugin.configMessage.returnString("warnedPlayer"));
                targetPlayer.sendMessage(plugin.configMessage.returnString("warnedPlayerMessage"));
                plugin.violationFileYaml.set(targetPlayer.getUniqueId() + ".warnCount",
                        plugin.violationFileYaml.getInt(targetPlayer.getUniqueId() + ".warnCount") + 1);
                plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "warn");

                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "warn");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }
                try {
                    plugin.violationFileYaml.save(plugin.violationFile);
                } catch (IOException ee) {
                    ee.printStackTrace();
                }

                p.closeInventory();
            }
            if (e.getSlot() == 20) {
                if (!p.hasPermission("sac.warn")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }
                targetPlayerInstance.warnCount = 0.0;
                p.sendMessage(plugin.configMessage.returnString("clearWarning"));
                plugin.violationFileYaml.set(targetPlayer.getUniqueId() + ".warnCount", 0);
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "clearWarn");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }
                try {
                    plugin.violationFileYaml.save(plugin.violationFile);
                } catch (IOException ee) {
                    ee.printStackTrace();
                }

                p.closeInventory();
            }
        }
        if ("Kick Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            if (!p.hasPermission("sac.kick")) {
                p.sendMessage(plugin.configMessage.returnString("noPerm"));

                p.closeInventory();
                return;
            }
            if (e.getSlot() == 22) {
                if (!p.hasPermission("sac.kick")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));
                    p.closeInventory();
                    return;
                }
                targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.configMessage.returnString(
                        "kickMessage")));
                plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "kick");

                p.closeInventory();

                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "kick");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }
            }
        }
        if ("Mute Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            if (!p.hasPermission("sac.mute")) {
                p.sendMessage(plugin.configMessage.returnString("noPerm"));
                p.closeInventory();
                return;
            }
            if (e.getSlot() == 22) {
                if (!p.hasPermission("sac.mute")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }
                if (targetPlayerInstance.muted == null) {
                    targetPlayerInstance.muted =  false;
                }
                if (!targetPlayerInstance.muted) {
                    targetPlayer.sendMessage(plugin.configMessage.returnString("mutedPlayerMessage"));
                    p.sendMessage(plugin.configMessage.returnString("mutedPlayer"));
                    targetPlayerInstance.muted = true;
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "muted");

                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "muted");
                                Bukkit.getPluginManager().callEvent(d);
                            }
                        });
                    }

                } else {
                    targetPlayer.sendMessage(plugin.configMessage.returnString("unmutedPlayerMessage"));
                    p.sendMessage(plugin.configMessage.returnString("unmutedPlayer"));
                    targetPlayerInstance.muted = false;
                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "unmuted");
                                Bukkit.getPluginManager().callEvent(d);
                            }
                        });
                    }
                }
                p.closeInventory();
            }
        }
        if ("Freeze Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            if (!p.hasPermission("sac.freeze")) {
                p.sendMessage(plugin.configMessage.returnString("noPerm"));
                p.closeInventory();
                return;
            }
            if (e.getSlot() == 22) {
                if (!p.hasPermission("sac.freeze")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));
                    p.closeInventory();
                    return;
                }
                if (targetPlayerInstance.playerFrozen == null) {
                    targetPlayerInstance.playerFrozen = false;
                }
                if (!targetPlayerInstance.playerFrozen) {
                    p.sendMessage(plugin.configMessage.returnString("freezeMessage"));
                    targetPlayerInstance.cancelX = targetPlayer.getLocation().getX();
                    targetPlayerInstance.cancelY = targetPlayer.getLocation().getY();
                    targetPlayerInstance.cancelZ = targetPlayer.getLocation().getZ();
                    targetPlayer.sendMessage(plugin.configMessage.returnString("freezeMessagePlayer"));
                    targetPlayerInstance.playerFrozen =  true;
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "frozen");

                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "frozen");
                                Bukkit.getPluginManager().callEvent(d);
                            }
                        });
                    }

                } else {
                    p.sendMessage(plugin.configMessage.returnString("unfreezeMessage"));
                    targetPlayer.sendMessage(plugin.configMessage.returnString("unfreezeMessagePlayer"));
                    targetPlayerInstance.playerFrozen = false;

                    if (plugin.enableApi) {
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "unfrozen");
                                Bukkit.getPluginManager().callEvent(d);
                            }
                        });
                    }

                }
                p.closeInventory();
            }
        }
        if ("Ban Menu".equalsIgnoreCase(p.getOpenInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || Material.AIR.equals(e.getCurrentItem().getType()) || e.getSlot() == 4) {
                return;
            }
            if (!p.hasPermission("sac.ban")) {
                p.sendMessage(plugin.configMessage.returnString("noPerm"));
                p.closeInventory();
                return;
            }
            if (e.getSlot() == 20) {
                if (!p.hasPermission("sac.ban")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));
                    p.closeInventory();
                    return;
                }
                String banner = null;

                if (plugin.yamlConfig.getBoolean("GUI.Ban.FirstTime.useUsage")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString(
                            "GUI.Ban.FirstTime.usage").replace("{player}", targetPlayer.getName()));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                } else {

                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(targetPlayer.getName(), plugin.yamlConfig.getString("messages.banMessage"),
                            new Date((long) (System.currentTimeMillis()
                                    + 60 * 60 * 1000 * plugin.yamlConfig.getDouble("GUI.Ban.FirstTime.Time"))),
                            banner);
                    targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                            plugin.yamlConfig.getString("messages.banMessage")));

                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");


                }
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "ban");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            }
            if (e.getSlot() == 21) {
                if (!p.hasPermission("sac.ban")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }
                if (plugin.yamlConfig.getBoolean("GUI.Ban.SecondTime.useUsage")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString(
                            "GUI.Ban.SecondTime.usage").replace("{player}", targetPlayer.getName()));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                } else {

                    String banner = null;
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(targetPlayer.getName(), plugin.yamlConfig.getString("messages.banMessage"), new Date(
                            System.currentTimeMillis() + 60L * 60 * 1000 * plugin.yamlConfig.getInt(
                                    "GUI.Ban.SecondTime.Time")), banner);
                    targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                            plugin.yamlConfig.getString("messages.banMessage")));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                }
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "ban");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            }
            if (e.getSlot() == 22) {
                if (!p.hasPermission("sac.ban")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }

                if (plugin.yamlConfig.getBoolean("GUI.Ban.ThirdTime.useUsage")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString("GUI.Ban.ThirdTime.usage").replace("{player}", targetPlayer.getName()));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                } else {

                    String banner = null;
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(targetPlayer.getName(), plugin.yamlConfig.getString("messages.banMessage"),
                            new Date((long) (System.currentTimeMillis()
                                    + 60 * 60 * 1000 * plugin.yamlConfig.getDouble("GUI.Ban.ThirdTime.Time"))),
                            banner);
                    targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                            plugin.yamlConfig.getString("messages.banMessage")));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                }
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "ban");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }
            }
            if (e.getSlot() == 23) {
                if (!p.hasPermission("sac.ban")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));

                    p.closeInventory();
                    return;
                }

                if (plugin.yamlConfig.getBoolean("GUI.Ban.FourthTime.useUsage")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString(
                            "GUI.Ban.FourthTime.usage").replace("{player}", targetPlayer.getName()));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                } else {

                    String banner = null;
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(targetPlayer.getName(), plugin.yamlConfig.getString("messages.banMessage"),
                            new Date((long) (System.currentTimeMillis()
                                    + 60 * 60 * 1000 * plugin.yamlConfig.getDouble("GUI.Ban.FourthTime.Time"))),
                            banner);
                    targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                            plugin.yamlConfig.getString("messages.banMessage")));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                }
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "ban");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            }
            if (e.getSlot() == 24) {
                if (!p.hasPermission("sac.ban")) {
                    p.sendMessage(plugin.configMessage.returnString("noPerm"));
                    p.closeInventory();
                    return;
                }

                if (plugin.yamlConfig.getBoolean("GUI.Ban.FifthTime.useUsage")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.yamlConfig.getString(
                            "GUI.Ban.FifthTime.usage").replace("{player}", targetPlayer.getName()));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                } else {
                    String banner = null;
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(targetPlayer.getName(), plugin.yamlConfig.getString("messages.banMessage"),
                            new Date((long) (System.currentTimeMillis()
                                    + 60 * 60 * 1000 * plugin.yamlConfig.getDouble("GUI.Ban.FifthTime.Time"))),
                            banner);
                    targetPlayer.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                            plugin.yamlConfig.getString("messages.banMessage")));
                    plugin.violationLogger.writePunishViolation(targetPlayer, p.getName(), "ban");

                }
                if (plugin.enableApi) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            SoaromaAdminPunish d = new SoaromaAdminPunish(plugin, p, targetPlayer, "ban");
                            Bukkit.getPluginManager().callEvent(d);
                        }
                    });
                }

            }
        }
    }
}
