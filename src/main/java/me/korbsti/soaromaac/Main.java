package me.korbsti.soaromaac;

import me.korbsti.soaromaac.combat.CollectionOfTools;
import me.korbsti.soaromaac.combat.Combat;
import me.korbsti.soaromaac.events.*;
import me.korbsti.soaromaac.events.connection.JoinAndLeave;
import me.korbsti.soaromaac.events.damage.EntityDamageEventClass;
import me.korbsti.soaromaac.hooks.PAPI;
import me.korbsti.soaromaac.movementreplayer.FileManager;
import me.korbsti.soaromaac.movementreplayer.WriteJS;
import me.korbsti.soaromaac.neuralnet.NeuralNetwork;
import me.korbsti.soaromaac.neuralnet.NeuralNetworkManager;
import me.korbsti.soaromaac.timers.Lag;
import me.korbsti.soaromaac.utils.*;
import me.korbsti.soaromaac.violations.Notify;
import me.korbsti.soaromaac.violations.ViolationChecker;
import me.korbsti.soaromaac.violations.ViolationLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends JavaPlugin implements Listener {
    public boolean autoViolationKick;
    public ArrayList<String> cheatNames = new ArrayList<>();
    public int badPacketsALimiter;
    public boolean checkBadPacketsA;
    public int autoClickerBms;
    public ArrayList<Player> playerNearbyEntities = new ArrayList<>();

    public LinkedHashSet<MovementPacket> checkMovementPackets = new LinkedHashSet<>();

    public GetType getType = new GetType(this);
    public AsyncNearbyBoat asyncNearbyBoat = new AsyncNearbyBoat(this);
    public double noSlowDownDMaxSpeed;
    public int noSlowDownDUntilCheat;
    public int irregularNumSampleNumB;
    public int irregularTimeCountB;
    public String configType;
    public boolean checkXray;
    public int timer;
    public int totalViolations;
    public int totalKicks;
    public double maxSpeedBoatQ;
    public boolean checkReachNumAPingIncrease;
    public double reachNumAPingIncrease;
    public int reachNumAPing;
    public String serverVersion = "";
    public int semiPredYUntilFlagA;
    public double semiPredYLimiterA;
    public int semiPredYUntilFlagB;
    public double semiPredYLimiterB;
    public boolean checkSemiPredA;
    public boolean checkSemiPredB;
    public int elytraFlightLimitB;
    public double elytraFlightSpeedMinB;
    public double maxSpeedBoatP;
    public double stepMaxSpeedB;
    public boolean checkStepB;
    public URL url;
    public double BhopUntilHacking;
    public int autoClickerBLimit;
    public double BhopUntilHackingAlternative;
    public Double blinkUntilFlaggingNum;
    public boolean isUsingFloodgate = false;
    public Boolean cancelEventIfHacking;
    public String ignorePlayers;
    public boolean checkAutoClickerA;
    public boolean checkBHop;
    public double speedMaxYN;
    public boolean checkAutoClickerB;
    public boolean checkBlink;
    public boolean checkCriticals;
    public boolean checkFastLadder;
    public boolean checkFastPlace;
    public int irrMovementPacketSamplesB;
    public boolean checkFly;
    public boolean enablePlayerViolationLog;
    public int playerViolationLog;
    public boolean enableKickLogger;
    public boolean checkGlide;
    public double checkGroundNum;
    public boolean ignoreZeroPing;
    public double irrMaxDistanceB;
    public double irrYAxisIgnoreB;
    public boolean fluidWalkDCheck;
    public boolean checkIrregularEvent;
    public boolean checkIrregularPitch;
    public boolean checkIrregularPlace;
    public boolean checkIrrMovement;
    public boolean checkGhostHand;
    public boolean checkLongJump;
    public int lowVio;
    public int mediumVio;
    public int highVio;
    public boolean checkNoFall;
    public boolean checkPingSpoofing;
    public int speedMaxYNFlag;
    public boolean checkPlayerLag;
    public double checkPlayerLagNum;
    public boolean checkPlayersGamemodeCombat;
    public boolean checkReach;
    public boolean checkReachBlockBreak;
    public boolean checkReachBlockPlace;
    public double checkReachNum;
    public double checkReachNumCreative;
    public boolean checkRegen;
    public boolean checkServerTPS;
    public boolean checkSpeed;
    public long disableACEnderDragon;
    public String lowString;
    public String mediumString;
    public int teleportSensitivity;
    public String highString;
    public boolean checkSpider;
    public boolean checkStep;
    public boolean checkVelocity;
    public boolean checkWalkOnFluid;
    public boolean clearAllViolationsTimer;
    public long clearAllViolationsTimerNum;
    public CollectionOfTools collectionTool = new CollectionOfTools(this);
    public File configFile;
    public File configFile2;
    public File discordFile;
    public File violationConfigFile;
    public boolean enableMovementReplay;
    public File xrayFile;
    public YamlConfiguration xrayYaml;
    public int xrayTimer;
    public WriteJS writeJS = new WriteJS(this);
    public List<String> xrayMaterials = new ArrayList<>();
    public YamlConfiguration violationConfigYaml;
    public YamlConfiguration discordYaml;
    public ConfigManager configManager = new ConfigManager(this, "main.yml", "discord.yml", "logger.yml", "xray.yml");
    public Double CPSUntilHacking;
    public Disabler death = new Disabler(this);
    public PreConfigChooser preConfigChooser = new PreConfigChooser(this);
    public boolean debugMode;
    public ArrayList<Integer> degrees = new ArrayList<>();
    public int disableAntiCheatXTime;
    public boolean enableAntiCheat;
    public boolean enableAutoBan;
    public Boolean enableViolationLogger;
    public EntityDamageEventClass entityDamage = new EntityDamageEventClass(this);
    public double fastPlaceFlagNum;
    public double fastPlaceSampleNum;
    public double fluidJumpsOnWaterUntilHacking;
    public double fluidWalkIrregularSpeed;
    public double smartCombatMovementChangeSpeedL;
    public double smartCombatMovementChangeSpeedKnockbackLM;
    public double fluidWalkUntilHacking;
    public double fluidWalkUntilHackingAlternative;
    public GetPing getPing = new GetPing(this);
    public double glideUntilHacking;
    public int sample;
    public double inAirJumpUntilHacking;
    public double inAirUntilCheckJump;
    public double inAirUpwardUntilHacking;
    public boolean enableDiscord;
    public double speedIncrease;
    public double inAirYCoodD;
    public boolean enableItemAttributeChecking;
    public PacketInjector injector;
    public Double inSlowableBlockUntilCheckB;
    public int attributeCounter;
    public double irregularCheckNumUntilHacking;
    public double irregularNumSampleNum;
    public double irregularPitchNegativeMax;
    public double irregularPitchPositiveMax;
    public double irregularTimeCount;
    public double irrMaxDistance;
    public double irrYAxisIgnore;
    public int irrMovementPacketSamples;
    public int maxElytraMili;
    public double levitationDownUntilHacking;
    public double longJumpBlockYNumTillIgnore;
    public double longJumpDistanceTillHacking;
    public int longJumpNumInAirTillCheckingLongJump;
    public ReturnMessages message = new ReturnMessages(this);
    public Movement movement = new Movement(this);
    public double noFallBlockHeight;
    public long noFallTimer;
    public boolean noSlowDownCheck;
    public double noSlowDownSpeedNum;
    public Notify notify = new Notify(this);
    public double number;
    public double roundedThresholdLow;
    public double roundedThresholdMedium;
    public double roundedThresholdHigh;
    public int irregularEventCountLow;
    public int irregularEventCountMedium;
    public int irregularEventCountHigh;
    public double numPlayerKickUntilBan;
    public double numUntilCheckingFastClimb;
    public GUIEvent ob = new GUIEvent(this);
    public HashMap<String, Double> onSlime = new HashMap<>();
    public double onSlimeTillCheckSpeed;
    public double pingUntilPingSpoofing;
    public PlayerWorldEvent playerWorldWorld = new PlayerWorldEvent(this);
    public double reachBlockBreakNum;
    public double reachBlockPlaceNum;
    public double serverTPSTillIgnore;
    public SettingValues settingValues = new SettingValues(this);
    public double shiftUntilCheckingNoSlow;
    public boolean smartCombatMovementChange;
    public double smartCombatMovementChangeNumber;
    public boolean checkNuker;
    public int maxMili;
    public int smartCombatMovementChangeTimer;
    public boolean spacedViolationMessages;
    public double spacedViolationNotificationsNum;
    public double speedCheckbHopAlternative;
    public double speedCheckMidAir;
    public double speedCheckMidAirAlternative;
    public double speedCheckWhenCrouching;
    public double speedCheckWhenInAirAlternative;
    public double speedCheckWhenInAirAlternativeNum;
    public double speedMaxClimbing;
    public double speedMaxInAir;
    public double speedMaxInVehicle;
    public double speedMaxInWater;
    public double speedMaxInWaterNum;
    public double speedMaxOnBlock;
    public double speedMaxOnGround;
    public double speedMaxOnIce;
    public boolean invertTPS;
    public double speedMaxUnderBlock;
    public double speedMaxWhenAscending;
    public double speedMaxWhenDescending;
    public double speedMaxXZ;
    public double speedMaxXZMaxL;
    public double speedMinimumWhenDescending;
    public boolean irrPlacement;
    public double spiderUpUntilHacking;
    public double spiderUpUntilHackingAlternative;
    public double stepBlockHeight;
    public TimeCheck timeCheck = new TimeCheck(this);
    public double tps;
    public int velocityFlagsUntilFlagging;
    public double velocitySpeedMin;
    public ViolationChecker violationChecker = new ViolationChecker(this);
    public File violationFile;
    public YamlConfiguration violationFileYaml;
    public double violationKickNumUntilKick;
    public ViolationLogger violationLogger = new ViolationLogger(this);
    public int violationLoggerNum;
    public boolean warnFlaggedPlayer;
    public List<String> worlds;
    public YamlConfiguration yamlConfig;
    public double iceIncrease;
    public int glideUntilHackingB;
    public YamlConfiguration yamlConfig2;
    public HexColourConvert hex = new HexColourConvert(this);
    public double speedMinimumWhenDescendingB;
    public boolean useUsageForAutoBan;
    public boolean enableApi;
    public int elytraFlightUntilHacking;
    public boolean checkBaritone;
    public int substringNum;
    public int packetSamples;
    public boolean checkFlightE;
    public double jumpAmplifierThreshold;
    public int baritoneLoopThreshold;
    public int baritoneFlag;
    public boolean checkElytraFlight;
    public double speedAmplifierThreshold;
    public int substringNumB;
    public int minFlagCount;
    public int minFlagCountB;
    public int minFlagCountC;
    public int packetSamplesB;
    public int baritoneLoopThresholdB;
    public int baritoneFlagB;
    public int substringNumC;
    public int packetSamplesC;
    public int baritoneLoopThresholdC;
    public int baritoneFlagC;
    public int allowedBlockTillFlagNum;
    public double minDistance;
    public double doubleBlockNum;
    public int baritoneReset;
    public boolean checkNoSlowDownC;
    public double maxSpeedElytra;
    public Discord discord = new Discord(this);
    public int discordThreshold;
    public ConfigMessage configMessage = new ConfigMessage(this);
    public FileManager fileManager = new FileManager(this);
    public ConfigGUI configGUI = new ConfigGUI(this);
    public boolean checkIrrStartup;
    public double speedMaxA;
    public double speedMaxB;
    public double limiterB;
    public boolean checkMedianSpeed;
    public int sampleMedianSpeed;
    public double maxMedianSpeed;
    public double axisYIgnoreMedian;

    public boolean trainNeuralNetwork;

    public boolean checkNeuralAnalysis;
    public boolean neuralWrong = false;
    public NeuralNetworkManager neuralNetworkManager = new NeuralNetworkManager(this);

    public HashMap<String, PlayerInstance> playerInstances = new HashMap<>();



    public NeuralNetwork neuralNetwork;

    public HashMap<Material, Double> toolHits = new HashMap<>();

    @Override
    public void onDisable() {
        asyncNearbyBoat.cancelled = true;

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            injector.removePlayer(allPlayers.getPlayer());
        }
        Bukkit.getLogger().info("Attempting to cancel async and sync tasks of SoaromaSAC");
        if(Bukkit.getScheduler().getPendingTasks().size() > 0) {
            for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
                if (task.getOwner().equals(this)) {
                    task.cancel();
                }
            }
        }
        asyncNearbyBoat.task.cancel();
        asyncNearbyBoat.mainTask.cancel();
        for (BukkitTask task : Bukkit.getServer().getScheduler().getPendingTasks()) {
            if (task.getOwner() == Bukkit.getPluginManager().getPlugin("SoaromaSAC")) {
                task.cancel();
            }
        }
        Bukkit.getServer().getScheduler().cancelTasks(this);
        Bukkit.getLogger().info("Successfully cancelled async and sync tasks");
    }

    @Override
    public void onEnable() {
        serverVersion = Bukkit.getServer().getBukkitVersion();
        if (serverVersion.contains("1.18.2")) {
            serverVersion = "2";
        } else {
            serverVersion = "1";
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new EntityDamageEventClass(this), this);
        this.injector = new PacketInjector();
        pm.registerEvents(new PlayerTeleport(this), this);
        pm.registerEvents(new Disabler(this), this);
        pm.registerEvents(new BlockEvents(this), this);
        pm.registerEvents(new Combat(this), this);
        pm.registerEvents(new JoinAndLeave(this), this);
        pm.registerEvents(new PlayerChat(this), this);
        pm.registerEvents(new GUIEvent(this), this);
        pm.registerEvents(new ClickEvent(this), this);
        pm.registerEvents(new StopCommands(this), this);
        pm.registerEvents(new EnterEvents(this), this);
        pm.registerEvents(new FishingRod(this), this);
        getCommand("sacreload").setExecutor(new Commands(this));
        getCommand("sacnotify").setExecutor(new Commands(this));
        getCommand("sacgui").setExecutor(new Commands(this));
        getCommand("sacwarn").setExecutor(new Commands(this));
        getCommand("sackick").setExecutor(new Commands(this));
        getCommand("sacunban").setExecutor(new Commands(this));
        getCommand("sacping").setExecutor(new Commands(this));
        getCommand("sacmute").setExecutor(new Commands(this));
        getCommand("sacfreeze").setExecutor(new Commands(this));
        getCommand("sacuser").setExecutor(new Commands(this));
        getCommand("sachashclear").setExecutor(new Commands(this));
        getCommand("sacreport").setExecutor(new Commands(this));
        getCommand("sacreports").setExecutor(new Commands(this));
        getCommand("sacppicp").setExecutor(new Commands(this));
        getCommand("sactps").setExecutor(new Commands(this));
        getCommand("sacreplay").setExecutor(new Commands(this));
        getCommand("sacadmin").setExecutor(new Commands(this));
        getCommand("sacconfigchoose").setExecutor(new Commands(this));
        getCommand("sachistory").setExecutor(new Commands(this));
        getCommand("sacvio").setExecutor(new Commands(this));
        getCommand("sactrainneuralnetwork").setExecutor(new Commands(this));
        collectionTool.addTools();
        Metrics metrics = new Metrics(this, 9683);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_violations", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // (This is useless as there is already a player chart by
                // default.)
                return totalViolations;
            }
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("total_kicks", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // (This is useless as there is already a player chart by
                // default.)
                return totalKicks;
            }
        }));
        configManager.saveDefaultConfiguration();
        configManager.resetValues();
        for (int i = -1; i != 362; i++) {
            degrees.add(i);
        }
        if (enableDiscord) {
            try {
                url = new URL(discordYaml.getString("webhook"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        violationLogger.createCustomConfig();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPI().register();
        }
        try {
            violationFileYaml.save(violationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            playerInstances.put(allPlayers.getName(), new PlayerInstance(allPlayers, this));
            addHashMaps(allPlayers);
            injector.removePlayer(allPlayers);
            Main instance = this;
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    if (allPlayers.isOnline()) {
                        injector.addPlayer(allPlayers.getPlayer(), instance);
                    }
                }
            }, 60);
            if (violationFileYaml.get(allPlayers.getUniqueId() + ".violations") == null) {
                try {
                    violationFileYaml.set(allPlayers.getPlayer().getUniqueId() + ".violations", 0);
                    violationFileYaml.save(violationFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileManager.checkPlayerTXT(allPlayers);
            if (violationFileYaml.get(allPlayers.getUniqueId() + ".warnCount") == null) {
                try {
                    violationFileYaml.set(allPlayers.getUniqueId() + ".warnCount", 0);
                    violationFileYaml.save(violationFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        violationLogger.makeFolder();
        fileManager.ifFolderExists();
        configType = yamlConfig.getString("other.config-type");

        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            isUsingFloodgate = true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if ("none".equals(configType)) {
                    for (Object obj : yamlConfig.getList("messages.no-config")) {
                        Bukkit.getLogger().info(hex.translateHexColorCodes("#", "/", obj.toString()));
                    }
                }
            }
        });
        if (clearAllViolationsTimer) {
            Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String name = player.getName();
                        PlayerInstance playerInstance = playerInstances.get(name);
                        playerInstance.num = 0;
                        playerInstance.discordNum= 0;
                        playerInstance.cheatsFlagged.clear();

                    }
                }
            }, 0, clearAllViolationsTimerNum * 20);
        }
        if (checkServerTPS) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
            Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable() {
                /** Long secend; long secstart; int ticks;. */
                @Override
                public void run() {
                    /*
                     * secstart = System.currentTimeMillis() / 1000; if
                     * (secstart == secend) { ticks++; } else { secend =
                     * secstart; tps = (tps == 0) ? ticks : ((tps + ticks) / 2);
                     * ticks = 1; } if(invertTPS) { if(tps >
                     * serverTPSTillIgnore) { tps = 0; } else { tps = 19.9; } }
                     */
                    tps = Lag.getTPS();
                    if (invertTPS) {
                        if (tps > serverTPSTillIgnore) {
                            tps = 0;
                        } else {
                            tps = 19.9;
                        }
                    }
                    // Bukkit.broadcastMessage("TPS: " + tps);
                }
            }, 100L, 1L);
        } else {
            tps = 25.0;
        }
        for (Object obj : yamlConfig.getList("messages.cheat-names")) {
            cheatNames.add(String.valueOf(obj));
        }

        asyncNearbyBoat.runAsyncThread();
        asyncNearbyBoat.runMainAsyncThread();
    }

    public void addHashMaps(Player p) {
        PlayerInstance playerInstance = new PlayerInstance(p, this);
        playerInstance.setDefaultPlayerVariables();
        this.playerInstances.put(p.getName(), playerInstance);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        playerInstances.put(e.getPlayer().getName(), new PlayerInstance(e.getPlayer(), this));
        addHashMaps(e.getPlayer());
        String playerName = e.getPlayer().getName();
        Main instance = this;
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                injector.addPlayer(e.getPlayer(), instance);
            }
        }, 50);
        if (yamlConfig.getBoolean("other.enableModifiedClientBroadcast")) {
            BukkitScheduler sch = Bukkit.getServer().getScheduler();
            sch.runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {

                    PlayerInstance playerInstance = playerInstances.get(playerName);

                    if (playerInstance.data.isEmpty()) {
                        for (Object packet : playerInstance.customPayload) {
                            Object minecraftKey = Reflection.getFieldValue(packet, "tag");
                            playerInstance.key.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "key")));
                            playerInstance.namespace.add(String.valueOf(Reflection.getFieldValue(minecraftKey, "namespace")));
                            playerInstance.data.add(String.valueOf(Reflection.getFieldValue(packet, "data")));
                        }
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("sac.display.custompayload")) {
                            for (Object str : yamlConfig.getList("messages.sacmodified")) {
                                String string = String.valueOf(str).replace("{user}", p.getName()).replace("{data}", playerInstance.data.toString()).replace("{key}", playerInstance.key.toString()).replace("{namespace}", playerInstance.namespace.toString());
                                p.sendMessage(hex.translateHexColorCodes("#", "/", string));
                            }
                        }
                    }
                }
            }, 30L);
        }
    }
}
