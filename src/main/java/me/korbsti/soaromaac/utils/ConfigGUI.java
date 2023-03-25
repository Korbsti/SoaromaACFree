package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ConfigGUI {

    Main plugin;

    public ConfigGUI(Main instance) {
        this.plugin = instance;
    }

    public void openGUI(Player p) {
        if (!p.hasPermission(" ")) {
            p.sendMessage(plugin.configMessage.returnString("noPerm"));
            return;
        }
        PlayerInstance playerInstance = plugin.playerInstances.get(p.getName());
        Inventory inv = Bukkit.getServer().createInventory(p, 54, plugin.yamlConfig.getString("GUI.Admin.GUIName"));
        ArrayList<ItemStack> stacking = new ArrayList<>();
        for (String str : plugin.yamlConfig.getConfigurationSection(playerInstance.currentPath).getKeys(false)) {
            if (plugin.yamlConfig.isConfigurationSection(playerInstance.currentPath + "." + str)) {
                ItemStack stack = new ItemStack(Material.getMaterial(plugin.yamlConfig.getString("GUI.Admin.sectionsItem")), 1);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(str);
                stack.setItemMeta(meta);
                stacking.add(stack);
                continue;
            }
            ItemStack stack = new ItemStack(Material.getMaterial(plugin.yamlConfig.getString("GUI.Admin.keyItem")), 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(str);
            stack.setItemMeta(meta);
            stacking.add(stack);

        }

        for (int i = 0; i != stacking.size(); i++) {
            if (i == 54) {
                break;
            }
            inv.setItem(i, stacking.get(i));

        }
        p.openInventory(inv);

    }

}
