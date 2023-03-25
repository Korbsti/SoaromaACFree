package me.korbsti.soaromaac.combat;

import me.korbsti.soaromaac.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class CollectionOfTools {
    Main plugin;

    public CollectionOfTools(Main instance) {
        this.plugin = instance;
    }

    public void addTools() {
        plugin.toolHits.put(Material.WOODEN_SWORD, 5.0);
        plugin.toolHits.put(Material.WOODEN_SHOVEL, 3.5);
        plugin.toolHits.put(Material.WOODEN_PICKAXE, 3.0);
        plugin.toolHits.put(Material.WOODEN_AXE, 8.0);
        plugin.toolHits.put(Material.WOODEN_HOE, 2.0);
        plugin.toolHits.put(Material.STONE_SWORD, 6.0);
        plugin.toolHits.put(Material.STONE_SHOVEL, 4.5);
        plugin.toolHits.put(Material.STONE_PICKAXE, 4.0);
        plugin.toolHits.put(Material.STONE_AXE, 10.0);
        plugin.toolHits.put(Material.STONE_HOE, 2.0);
        plugin.toolHits.put(Material.GOLDEN_SWORD, 5.0);
        plugin.toolHits.put(Material.GOLDEN_SHOVEL, 3.5);
        plugin.toolHits.put(Material.GOLDEN_PICKAXE, 3.0);
        plugin.toolHits.put(Material.GOLDEN_AXE, 8.0);
        plugin.toolHits.put(Material.GOLDEN_HOE, 2.0);
        plugin.toolHits.put(Material.IRON_SWORD, 7.0);
        plugin.toolHits.put(Material.IRON_SHOVEL, 5.5);
        plugin.toolHits.put(Material.IRON_PICKAXE, 5.0);
        plugin.toolHits.put(Material.IRON_AXE, 10.0);
        plugin.toolHits.put(Material.IRON_HOE, 2.0);
        plugin.toolHits.put(Material.DIAMOND_SWORD, 8.0);
        plugin.toolHits.put(Material.DIAMOND_SHOVEL, 6.5);
        plugin.toolHits.put(Material.DIAMOND_PICKAXE, 6.0);
        plugin.toolHits.put(Material.DIAMOND_AXE, 10.0);
        plugin.toolHits.put(Material.DIAMOND_HOE, 2.0);
        plugin.toolHits.put(Material.NETHERITE_SWORD, 9.0);
        plugin.toolHits.put(Material.NETHERITE_SHOVEL, 7.5);
        plugin.toolHits.put(Material.NETHERITE_PICKAXE, 7.0);
        plugin.toolHits.put(Material.NETHERITE_AXE, 11.0);
        plugin.toolHits.put(Material.NETHERITE_HOE, 2.0);
    }

    public Boolean checkDamage(Player player, Entity entity, ItemStack item, double damageDealt) {
        Material mat = item.getType();
        if (plugin.toolHits.get(mat) == null) {
            return false;
        }
        double originalToolHit = plugin.toolHits.get(mat);
        double addingToolHit = 0.0;
        double level;
        if (entity.getName().equals("Shulker Bullet") || entity instanceof Shulker) {
            return false;
        }

        Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
        if (itemEnchantments.containsKey(Enchantment.DAMAGE_ALL)) {
            level = 0.5 * itemEnchantments.get(Enchantment.DAMAGE_ALL) + 0.5;
            addingToolHit += level;
        }
        if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            level = 3 * player.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier();
            addingToolHit += level;
        }

        if ((entity instanceof Spider || entity instanceof CaveSpider || entity instanceof Bee
                || entity instanceof Silverfish || entity instanceof Endermite)
                && itemEnchantments.containsKey(Enchantment.DAMAGE_ARTHROPODS)) {
            level = 2.5 * itemEnchantments.get(Enchantment.DAMAGE_ARTHROPODS);
            addingToolHit += level;
        }
        if (itemEnchantments.containsKey(Enchantment.DAMAGE_UNDEAD) && (entity instanceof Skeleton
                || entity instanceof Zombie || entity instanceof ZombieVillager || entity instanceof Wither
                || entity instanceof WitherSkeleton || entity instanceof PigZombie || entity instanceof SkeletonHorse
                || entity instanceof ZombieHorse || entity instanceof Stray || entity instanceof Husk
                || entity instanceof Phantom || entity instanceof Drowned || entity instanceof Zoglin)) {
            level = 2.5 * itemEnchantments.get(Enchantment.DAMAGE_UNDEAD);
            addingToolHit += level;
        }

        return damageDealt == (addingToolHit + originalToolHit * 1.5);
    }
}
