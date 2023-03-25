package me.korbsti.soaromaac.combat;

import me.korbsti.soaromaac.Main;
import me.korbsti.soaromaac.utils.PlayerInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Combat implements Listener {
    private final HashMap<String, Integer> combatThing = new HashMap<>();

    Main plugin;

    public Combat(Main instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageCheck(EntityDamageByEntityEvent event) {
        if (!plugin.enableAntiCheat || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore) || event.getDamager() == null || event == null) {
            return;
        }
        try {
            if (event.getDamager() instanceof EnderDragon && event.getEntity() instanceof Player p) {
                if (p.hasPermission("sac.bypass")) {
                    return;
                } else {
                    plugin.death.disablerACPlayer(p, (int) plugin.disableACEnderDragon);
                }
            }
            if (((event.getCause() == DamageCause.ENTITY_EXPLOSION && event.getEntity() instanceof Player) || event.getDamager() instanceof TNTPrimed) && event.getEntity() instanceof Player p) {
                String name = p.getName();
                PlayerInstance playerInstance = plugin.playerInstances.get(name);
                playerInstance.playerEnableAntiCheat = false;
                BukkitScheduler bacon = Bukkit.getServer().getScheduler();
                if (playerInstance.explosionID != null) {
                    bacon.cancelTask(playerInstance.explosionID);
                }
                int id = bacon.scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        playerInstance.playerEnableAntiCheat =  true;
                        playerInstance.explosionID = null;
                    }
                }, 60L);
                playerInstance.explosionID = id;
            }
            Entity damager = event.getDamager();
            Entity damageee = event.getEntity();
            if (damager instanceof Player player) {
                String playerName = damager.getName();
                PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
                if (event.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
                    if (plugin.checkAutoClickerA) {
                        if (playerInstance.playerClicks != null)
                            playerInstance.playerClicks++;
                    }
                    if (plugin.checkAutoClickerB) {

                        if (playerInstance.autoClickerB == null) {
                            playerInstance.autoClickerB = Instant.now();
                        }

                        if (playerInstance.autoClickerBTemp >= 2) {
                            Long time = Instant.now().toEpochMilli() - playerInstance.autoClickerB.toEpochMilli();
                            if (playerInstance.autoClickBInner != null) {
                                if (Math.abs(playerInstance.autoClickBInner - time) < plugin.autoClickerBms) {
                                    if (playerInstance.autoClickerBFlag > plugin.autoClickerBLimit) {
                                        plugin.notify.notify(player, plugin.message.type(33), plugin.message.cheat(2), plugin.notify.level(plugin.autoClickerBLimit + plugin.roundedThresholdLow, plugin.autoClickerBLimit + plugin.roundedThresholdMedium, plugin.autoClickerBLimit + plugin.roundedThresholdHigh, playerInstance.autoClickerBFlag));
                                        plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.message.type(33), plugin.message.cheat(2));
                                        if (plugin.cancelEventIfHacking) {
                                            event.setCancelled(true);
                                        }
                                        if (plugin.debugMode) {
                                            Bukkit.broadcastMessage("Exceeded autoClickerBLimit");
                                        }

                                    }
                                    playerInstance.autoClickerBFlag++;
                                } else {
                                    playerInstance.autoClickerBFlag = (long) 0;
                                }
                            }

                            playerInstance.autoClickBInner = time;
                            playerInstance.autoClickerB = null;
                            playerInstance.autoClickerBTemp = (long) 0;
                        }

                        playerInstance.autoClickerBTemp++;

                    }
                }
            }
            try {
                if (damageee instanceof Player && plugin.smartCombatMovementChange) {
                    boolean wasPunch = false;
                    boolean wasKnockback = false;
                    if (damager instanceof Projectile && damageee instanceof Player) {
                        Projectile p = (Projectile) event.getDamager();

                        if (p.getShooter() instanceof Player) {
                            if (p.toString().contains("Arrow")) {
                                Player pd = (Player) p.getShooter();
                                Map<Enchantment, Integer> mainHandItem = pd.getInventory().getItemInMainHand().getEnchantments();
                                Map<Enchantment, Integer> secondHandItem = pd.getInventory().getItemInOffHand().getEnchantments();
                                if (mainHandItem.toString().contains("punch")) {
                                    wasPunch = true;
                                }
                                if (secondHandItem.toString().contains("punch")) {
                                    wasPunch = true;
                                }
                            }
                        }
                    }
                    if (damager instanceof Player p) {
                        Map<Enchantment, Integer> mainHandItem = p.getInventory().getItemInMainHand().getEnchantments();
                        Map<Enchantment, Integer> secondHandItem = p.getInventory().getItemInOffHand().getEnchantments();
                        if (mainHandItem.toString().contains("knockback")) {
                            wasKnockback = true;
                        }
                        if (secondHandItem.toString().contains("knockback")) {
                            wasKnockback = true;
                        }
                    }

                    String name = damageee.getName();
                    PlayerInstance playerInstance = plugin.playerInstances.get(name);
                    playerInstance.allMovementChange = 1.0;
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    if (combatThing.get(name) != null) {
                        scheduler.cancelTask(combatThing.get(name));
                    }
                    playerInstance.allMovementChangeRunning=true;
                    if (wasPunch) {
                        playerInstance.smartMovementPunch= true;
                    }
                    if (wasKnockback) {
                        playerInstance.smartMovementKnockback = true;
                    }

                    int id = scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            playerInstance.allMovementChangeRunning = false;
                            playerInstance.smartMovementPunch = false;
                            playerInstance.smartMovementKnockback =  false;
                            playerInstance.allMovementChange = 0.0;
                            combatThing.put(name, null);
                        }
                    }, plugin.smartCombatMovementChangeTimer);
                    combatThing.put(name, id);
                }
                DamageCause cause = event.getCause();
                if (!(damager instanceof Player) || cause == DamageCause.PROJECTILE || cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION || !plugin.enableAntiCheat || damager instanceof Arrow || damager instanceof Creeper || damager instanceof TNT || damager instanceof TNTPrimed || (plugin.checkServerTPS && plugin.tps < plugin.serverTPSTillIgnore) || damager == null || damager instanceof LlamaSpit || cause == DamageCause.THORNS) {
                    return;
                }
            } catch (Exception e) {
                Bukkit.getLogger().info("[SAC] An error has occured while registering an entity...");
            }

            Player player = (Player) event.getDamager();
            String playerName = player.getName();
            PlayerInstance playerInstance = plugin.playerInstances.get(playerName);
            if (player.getGameMode() == GameMode.CREATIVE && plugin.checkPlayersGamemodeCombat) {
                return;
            }
            if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && plugin.checkCriticals) {
                boolean flag = Objects.equals(playerInstance.playerY, playerInstance.beforePlayerY) && player.getLocation().add(0, -0.4, 0).getBlock().getType() != Material.AIR && player instanceof Player && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && player.getVehicle() == null && player.getLocation().getY() <= damageee.getLocation().getY() && !player.getLocation().add(0, 2.1, 0).getBlock().getType().isSolid();
                if (flag && event.getDamage() > 0.0F && !player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                    boolean t = plugin.collectionTool.checkDamage(player, event.getEntity(), player.getInventory().getItemInMainHand(), event.getDamage());
                    if (t) {
                        if (plugin.cancelEventIfHacking) {
                            event.setCancelled(true);
                        }
                        plugin.notify.notify(player, plugin.cheatNames.get(24), plugin.message.cheat(1), plugin.mediumString);
                        plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(24), plugin.message.cheat(1));
                        if (plugin.debugMode) {
                            Bukkit.broadcastMessage("N/A Config, internal code exceeded, note that custom enchants may flag the anti-cheat for criticals");
                        }
                    }
                }
            }
            if (plugin.checkVelocity && event.getEntity() instanceof Player p && event.getDamage() > 0.0) {
                playerInstance.hit = true;
            }
            if (plugin.checkReach) {
                if (player.hasPermission("sac.bypass") || player.isBlocking()) {
                    return;
                }
                Entity taking = event.getEntity();
                Location locationDamager = player.getLocation();
                Location locationDamagee = taking.getLocation();
                double LocationDamagerX = locationDamager.getX();
                double LocationDamagerY = locationDamager.getY() + 1.0;
                double LocationDamagerZ = locationDamager.getZ();
                double LocationDamageeX = locationDamagee.getX();
                double LocationDamageeY = locationDamagee.getY();
                double LocationDamageeZ = locationDamagee.getZ();
                double range;
                range = Math.sqrt(Math.pow(LocationDamagerX - LocationDamageeX, 2) + Math.pow(LocationDamagerY - LocationDamageeY, 2) + Math.pow(LocationDamagerZ - LocationDamageeZ, 2));
                int x = 0;
                if (taking instanceof EnderDragon) {
                    x = 6;
                }
                if (plugin.checkReachNumAPingIncrease) {
                    if (playerInstance.playerPing > plugin.reachNumAPing) {
                        x += plugin.reachNumAPingIncrease;
                    }
                }

                if (range >= plugin.checkReachNum + x) {

                    plugin.notify.notify(player, plugin.cheatNames.get(22), plugin.message.cheat(1), plugin.notify.level(plugin.checkReachNum + plugin.roundedThresholdLow, plugin.checkReachNum + plugin.roundedThresholdMedium, plugin.checkReachNum + plugin.roundedThresholdHigh, range));
                    plugin.violationChecker.violationChecker(player, playerInstance.num++, false, plugin.cheatNames.get(22), plugin.message.cheat(1));
                    if (plugin.cancelEventIfHacking) {
                        event.setCancelled(true);
                    }
                    if (plugin.debugMode) {
                        Bukkit.broadcastMessage("Exceeded checkReachNum");
                    }
                }
            }
        } catch (Exception exc) {
        }
    }

}

//try {
// item.getItemMeta().getPersistentDataContainer().get(,
// paramPersistentDataType);
// Class<?> clazz = Class.forName("org.bukkit.craftbukkit." +
// plugin.getPing.getServerVersion(plugin) + ".inventory.CraftItemStack");
// Class<?> clazz2 = Reflection.getClass("{nms}.ItemStack");
// for (Method method : clazz.getMethods()){
// Bukkit.broadcastMessage("" + method.getName());
// }
// Method method = clazz.getDeclaredMethod("asNMSCopy", ItemStack.class);
// Object itemMain = method.invoke(clazz, item);
// clazz2.cast(itemMain);
// Bukkit.broadcastMessage("" + copiedItem);
// ItemMeta meta = copiedItem.getItemMeta();
// Bukkit.broadcastMessage("" + meta);
// Reflection.getClass("CraftItemStack")
/*
 * Collection<AttributeModifier> metaA =
 * item.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE); if
 * (metaA != null) { metaA.forEach(attribute ->
 * Bukkit.broadcastMessage(attribute.toString())); }
 */
//} catch (Exception e) {
//	e.printStackTrace();
//}