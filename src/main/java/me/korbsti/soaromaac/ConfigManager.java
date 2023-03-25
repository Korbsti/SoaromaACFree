package me.korbsti.soaromaac;

import me.korbsti.soaromaac.neuralnet.NeuralNetwork;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {
    String dir = System.getProperty("user.dir");
    public String directoryPathFile = dir + File.separator + "plugins" + File.separator + "SoaromaSAC";
    Main plugin;

    public ConfigManager(Main instance, String filename, String discordFile, String violationFile, String xrayFile) {
        File file = new File(directoryPathFile);
        if (file.mkdirs()) {
            Bukkit.getLogger().info("Generated SoaromaSAC configuration folder...");
        }
        this.plugin = instance;

        plugin.configFile = new File(plugin.getDataFolder(), filename);
        plugin.discordFile = new File(plugin.getDataFolder(), discordFile);
        plugin.violationConfigFile = new File(plugin.getDataFolder(), violationFile);
        plugin.xrayFile = new File(plugin.getDataFolder(), xrayFile);
        if (!plugin.configFile.exists()) {
            saveDefaultConfiguration();
        }
        if (!plugin.xrayFile.exists()) {
            try {
                plugin.xrayFile.createNewFile();

                FileWriter write = new FileWriter(directoryPathFile + File.separator + "xray.yml", true);
                write.write("checkXray: false" + System.lineSeparator());
                write.write("# Timer is in ticks, remember 20 ticks = 1 second, so 6000 = 5 minutes" + System.lineSeparator());
                write.write("timer: 6000" + System.lineSeparator());
                write.write("cheat: 'Xray'" + System.lineSeparator());
                write.write("# The first ROW is the material you want to be checked for, the first ROW of numbers is the maximium amount of blocks u can mine for that specific block in the time of 'timer' above" + System.lineSeparator());
                write.write("# The second ROW of numbers is the 'OTHER' amount of blocks needed to mine for the first row of numbers to be exceeded to flag" + System.lineSeparator());
                write.write("# The third ROW of numbers is the Y axis the block has to be mined from, so for example if the number is 30, whatever is below the Y axis of 30" + System.lineSeparator());
                write.write("# Will be added to a list internally and be checked for" + System.lineSeparator());
                write.write("# Note you can add more materials to the list and configure to your hearts desire" + System.lineSeparator());
                write.write("xray-config:" + System.lineSeparator());
                write.write("     - DIAMOND_ORE 25 100 30" + System.lineSeparator());
                write.write("     - DEEPSLATE_DIAMOND_ORE 25 100 30" + System.lineSeparator());
                write.close();

                // DEEPSLATE_DIAMOND_ORE

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (!plugin.discordFile.exists()) {
            try {
                java.nio.file.Files.copy(plugin.getResource("discord.yml"), Path.of(plugin.discordFile.getPath()));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (!plugin.violationConfigFile.exists()) {
            try {
                plugin.violationConfigFile.createNewFile();
                FileWriter write = new FileWriter(directoryPathFile + File.separator + "logger.yml", true);
                write.write("log-system:" + System.lineSeparator());

                write.write("    enableKickLogger: true" + System.lineSeparator());

                write.write("    enableViolationLogger: true" + System.lineSeparator());

                write.write("    violationLoggerNum: 30" + System.lineSeparator());

                write.write("    enablePlayerViolationLog: true" + System.lineSeparator());

                write.write("    playerViolationLog: 15" + System.lineSeparator());
                write.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!new File(directoryPathFile + File.separator + "NeuralNetworkSavedData").mkdirs()) ;
        if (!new File(directoryPathFile + File.separator + "logs" + File.separator + "PlayerLog").mkdirs()) ;
        plugin.yamlConfig = YamlConfiguration.loadConfiguration(plugin.configFile);

        plugin.discordYaml = YamlConfiguration.loadConfiguration(plugin.discordFile);

        plugin.violationFileYaml = YamlConfiguration.loadConfiguration(plugin.violationConfigFile);

        plugin.xrayYaml = YamlConfiguration.loadConfiguration(plugin.xrayFile);
    }

    public void ConfigManager2(Main instance, String filename) {
        this.plugin = instance;
        plugin.configFile2 = new File(plugin.getDataFolder(), filename);

        if (!plugin.configFile2.exists()) {
            saveDefaultConfiguration();
        }

        plugin.yamlConfig2 = YamlConfiguration.loadConfiguration(plugin.configFile2);
    }

    public void reloadCustomConfig() {
        if (plugin.configFile == null) {
            plugin.configFile = new File(plugin.getDataFolder(), "main.yml");
        }
        plugin.yamlConfig = YamlConfiguration.loadConfiguration(plugin.configFile);

        // Look for defaults in the jar
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(plugin.getResource("main.yml"), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            plugin.yamlConfig.setDefaults(defConfig);
        }
    }

    @SuppressWarnings("unchecked")
    public void resetValues() {
        try {
            plugin.enablePlayerViolationLog = plugin.violationFileYaml.getBoolean("log-system.enablePlayerViolationLog");
            plugin.playerViolationLog = plugin.violationFileYaml.getInt("log-system.playerViolationLog");
            plugin.enableKickLogger = plugin.violationFileYaml.getBoolean("log-system.enableKickLogger");
            plugin.enableItemAttributeChecking = plugin.yamlConfig.getBoolean("item-attribute.enableItemAttributeChecking");
            plugin.noSlowDownDMaxSpeed = plugin.yamlConfig.getDouble("movement.noSlowDownCheck.noSlowDownDMaxSpeed");
            plugin.speedMaxYNFlag = plugin.yamlConfig.getInt("movement.checkSpeed.speedMaxYNFlag");
            plugin.noSlowDownDUntilCheat = plugin.yamlConfig.getInt("movement.noSlowDownCheck.noSlowDownDUntilCheat");
            plugin.checkPlayersGamemodeCombat = plugin.yamlConfig.getBoolean("combat.checkPlayersGamemodeCombat");
            plugin.checkReachNumCreative = plugin.yamlConfig.getDouble("combat.checkReach.checkReachNumCreative");
            plugin.irregularPitchPositiveMax = plugin.yamlConfig.getDouble("movement.checkIrregularPitch.irregularPitchPositiveMaxA");
            plugin.numUntilCheckingFastClimb = plugin.yamlConfig.getDouble("movement.checkFastLadder.numUntilCheckingFastClimbA");
            plugin.irregularPitchNegativeMax = plugin.yamlConfig.getDouble("movement.checkIrregularPitch.irregularPitchNegativeMaxA");
            plugin.enableMovementReplay = plugin.yamlConfig.getBoolean("other.enableMovementReplay");
            plugin.smartCombatMovementChangeSpeedL = plugin.yamlConfig.getDouble("combat.smartCombatMovementChange.smartCombatMovementChangeSpeedPunchLM");
            plugin.irregularTimeCount = plugin.yamlConfig.getDouble("timer-checks.checkIrregularEvent.irregularTimeCountA");
            plugin.ignoreZeroPing = plugin.yamlConfig.getBoolean("other.ignoreZeroPing");
            plugin.smartCombatMovementChangeSpeedKnockbackLM = plugin.yamlConfig.getDouble("combat.smartCombatMovementChange.smartCombatMovementChangeSpeedKnockbackLM");
            plugin.elytraFlightSpeedMinB = plugin.yamlConfig.getDouble("movement.checkElytraFlight.elytraFlightSpeedMinB");
            plugin.checkGhostHand = plugin.yamlConfig.getBoolean("interaction.checkGhostHand");
            plugin.checkIrregularPitch = plugin.yamlConfig.getBoolean("movement.checkIrregularPitch.checkIrregularPitch");
            plugin.elytraFlightLimitB = plugin.yamlConfig.getInt("movement.checkElytraFlight.elytraFlightLimitB");

            plugin.checkReachNum = plugin.yamlConfig.getDouble("combat.checkReach.checkReachNumA");
            plugin.fluidWalkUntilHacking = plugin.yamlConfig.getDouble("movement.checkWalkOnFluid.fluidWalkUntilHackingC");
            plugin.badPacketsALimiter = plugin.yamlConfig.getInt("badpackets.limiterA");
            plugin.fluidWalkIrregularSpeed = plugin.yamlConfig.getDouble("movement.checkWalkOnFluid.fluidWalkIrregularSpeedB");
            plugin.checkBadPacketsA = plugin.yamlConfig.getBoolean("badpackets.checkBadPacketsA");
            plugin.fluidJumpsOnWaterUntilHacking = plugin.yamlConfig.getDouble("movement.checkWalkOnFluid.fluidJumpsOnWaterUntilHackingB");
            plugin.maxElytraMili = plugin.yamlConfig.getInt("timer-checks.checkIrregularEvent.maxElytraMili");
            plugin.fluidWalkUntilHackingAlternative = plugin.yamlConfig.getDouble("movement.checkWalkOnFluid.fluidWalkUntilHackingAlternativeA");
            plugin.speedMaxYN = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxYN");
            plugin.speedMaxOnGround = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxOnGroundE");
            plugin.speedMaxInAir = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxInAirC");
            plugin.fluidWalkDCheck = plugin.yamlConfig.getBoolean("movement.checkWalkOnFluid.fluidWalkDCheck");
            plugin.checkCriticals = plugin.yamlConfig.getBoolean("combat.checkCriticals");
            plugin.irregularNumSampleNum = plugin.yamlConfig.getDouble("timer-checks.checkIrregularEvent.irregularNumSampleNumA");
            plugin.checkFlightE = plugin.yamlConfig.getBoolean("movement.checkFly.checkFlightE");
            plugin.speedMaxWhenAscending = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxWhenAscendingH");
            plugin.speedMaxWhenDescending = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxWhenDescendingI");
            plugin.speedCheckWhenCrouching = plugin.yamlConfig.getDouble("movement.noSlowDownCheck.speedCheckWhenCrouchingA");
            plugin.irregularNumSampleNumB = plugin.yamlConfig.getInt("timer-checks.checkIrregularEvent.irregularNumSampleNumB");
            plugin.irregularTimeCountB = plugin.yamlConfig.getInt("timer-checks.checkIrregularEvent.irregularTimeCountB");

            plugin.speedMaxClimbing = plugin.yamlConfig.getDouble("movement.checkFastLadder.speedMaxClimbingA");
            plugin.speedMaxInVehicle = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxInVehicleD");
            plugin.speedMaxOnIce = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxOnIceF");
            plugin.speedCheckMidAirAlternative = plugin.yamlConfig.getDouble("movement.checkSpeed.speedCheckMidAirAlternativeJ");
            plugin.invertTPS = plugin.yamlConfig.getBoolean("other.checkServerTPS.invertTPS");
            plugin.speedCheckMidAir = plugin.yamlConfig.getDouble("movement.checkBHop.speedCheckMidAirA");
            plugin.iceIncrease = plugin.yamlConfig.getDouble("movement.iceIncrease");
            plugin.enableApi = plugin.yamlConfig.getBoolean("other.enableAPI");
            plugin.BhopUntilHacking = plugin.yamlConfig.getDouble("movement.checkBHop.BhopUntilHackingA");
            plugin.speedCheckWhenInAirAlternative = plugin.yamlConfig.getDouble("movement.checkSpeed.speedCheckWhenInAirAlternativeA");
            plugin.speedCheckWhenInAirAlternativeNum = plugin.yamlConfig.getDouble("movement.checkSpeed.speedCheckWhenInAirAlternativeNumA");
            plugin.noSlowDownSpeedNum = plugin.yamlConfig.getDouble("movement.noSlowDownCheck.noSlowDownSpeedNumB");
            plugin.violationKickNumUntilKick = plugin.yamlConfig.getDouble("flag-system.violationKickNumUntilKick");
            plugin.teleportSensitivity = plugin.yamlConfig.getInt("flag-system.teleportSensitivity");
            plugin.inSlowableBlockUntilCheckB = plugin.yamlConfig.getDouble("movement.noSlowDownCheck.inSlowableBlockUntilCheckB");
            plugin.noFallBlockHeight = plugin.yamlConfig.getDouble("movement.checkNoFall.noFallBlockHeightA");
            plugin.spiderUpUntilHacking = plugin.yamlConfig.getDouble("movement.checkSpider.spiderUpUntilHackingA");
            plugin.spiderUpUntilHackingAlternative = plugin.yamlConfig.getDouble("movement.checkSpider.spiderUpUntilHackingAlternativeB");
            plugin.inAirUpwardUntilHacking = plugin.yamlConfig.getDouble("movement.checkFly.inAirUpwardUntilHackingB");
            plugin.inAirJumpUntilHacking = plugin.yamlConfig.getDouble("movement.checkFly.inAirJumpUntilHackingA");
            plugin.levitationDownUntilHacking = plugin.yamlConfig.getDouble("movement.checkFly.levitationDownUntilHackingC");
            plugin.discordThreshold = plugin.discordYaml.getInt("limiter");
            plugin.shiftUntilCheckingNoSlow = plugin.yamlConfig.getDouble("movement.noSlowDownCheck.shiftUntilCheckingNoSlowA");
            plugin.onSlimeTillCheckSpeed = plugin.yamlConfig.getDouble("movement.checkSpeed.onSlimeTillCheckSpeed");
            plugin.pingUntilPingSpoofing = plugin.yamlConfig.getDouble("other.checkPingSpoofing.pingUntilPingSpoofingA");
            plugin.noFallTimer = plugin.yamlConfig.getLong("movement.checkNoFall.noFallTimerA");
            plugin.glideUntilHacking = plugin.yamlConfig.getDouble("movement.checkGlide.glideUntilHackingA");
            plugin.checkGlide = plugin.yamlConfig.getBoolean("movement.checkGlide.checkGlide");
            plugin.inAirUntilCheckJump = plugin.yamlConfig.getDouble("movement.checkFly.inAirUntilCheckJumpB");
            plugin.stepBlockHeight = plugin.yamlConfig.getDouble("movement.checkStep.stepBlockHeightA");
            plugin.longJumpDistanceTillHacking = plugin.yamlConfig.getDouble("movement.checkLongJump.longJumpDistanceTillHackingA");
            plugin.longJumpNumInAirTillCheckingLongJump = (int) plugin.yamlConfig.getDouble("movement.checkLongJump.longJumpNumInAirTillCheckingLongJumpA");
            plugin.speedMinimumWhenDescending = plugin.yamlConfig.getDouble("movement.checkGlide.speedMinimumWhenDescendingA");
            plugin.longJumpBlockYNumTillIgnore = plugin.yamlConfig.getDouble("movement.checkLongJump.longJumpBlockYNumTillIgnoreA");
            plugin.checkPlayerLagNum = plugin.yamlConfig.getDouble("other.checkPlayerLag.checkPlayerLagNum");
            plugin.reachBlockPlaceNum = plugin.yamlConfig.getDouble("interaction.checkReachBlockPlace.reachBlockPlaceNumA");
            plugin.reachBlockBreakNum = plugin.yamlConfig.getDouble("interaction.checkReachBlockBreak.reachBlockBreakNumA");
            plugin.serverTPSTillIgnore = plugin.yamlConfig.getDouble("other.checkServerTPS.serverTPSTillIgnore");
            plugin.speedMaxOnBlock = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxOnBlockM");
            plugin.numPlayerKickUntilBan = plugin.yamlConfig.getDouble("flag-system.enableAutoBan.numPlayerKickUntilBan");
            plugin.irregularCheckNumUntilHacking = plugin.yamlConfig.getDouble("timer-checks.checkIrregularEvent.irregularCheckNumUntilHackingA");
            plugin.CPSUntilHacking = plugin.yamlConfig.getDouble("interaction.checkAutoClicker.CPSUntilHackingA");
            plugin.disableAntiCheatXTime = plugin.yamlConfig.getInt("other.disableAntiCheatXTime");
            plugin.spacedViolationNotificationsNum = plugin.yamlConfig.getDouble("spacedViolationMessages.spacedViolationNotificationsNum");
            plugin.number = plugin.yamlConfig.getDouble("spacedViolationMessages.spacedViolationNotificationsNum");
            plugin.checkReach = plugin.yamlConfig.getBoolean("combat.checkReach.checkReach");
            plugin.noSlowDownCheck = plugin.yamlConfig.getBoolean("movement.noSlowDownCheck.noSlowDownCheck");
            plugin.enableAutoBan = plugin.yamlConfig.getBoolean("flag-system.enableAutoBan.enableAutoBan");
            plugin.checkServerTPS = plugin.yamlConfig.getBoolean("other.checkServerTPS.checkServerTPS");
            plugin.warnFlaggedPlayer = plugin.yamlConfig.getBoolean("flag-system.warnFlaggedPlayer");
            plugin.checkIrregularEvent = plugin.yamlConfig.getBoolean("timer-checks.checkIrregularEvent.checkIrregularEvent");
            plugin.checkPlayerLag = plugin.yamlConfig.getBoolean("other.checkPlayerLag.checkPlayerLag");
            plugin.checkPingSpoofing = plugin.yamlConfig.getBoolean("other.checkPingSpoofing.checkPingSpoofing");
            plugin.velocityFlagsUntilFlagging = plugin.yamlConfig.getInt("combat.checkVelocity.velocityFlagsUntilFlagging");
            plugin.checkIrrStartup = plugin.yamlConfig.getBoolean("movement.checkIrrStartup.checkIrrStartup");
            plugin.speedMaxA = plugin.yamlConfig.getDouble("movement.checkIrrStartup.speedMaxA");
            plugin.speedMaxB = plugin.yamlConfig.getDouble("movement.checkIrrStartup.speedMaxB");
            plugin.limiterB = plugin.yamlConfig.getInt("movement.checkIrrStartup.limiterB");

            plugin.sampleMedianSpeed = plugin.yamlConfig.getInt("movement.checkMedianSpeed.sample");
            plugin.maxMedianSpeed = plugin.yamlConfig.getDouble("movement.checkMedianSpeed.maxMedianSpeed");
            plugin.axisYIgnoreMedian = plugin.yamlConfig.getDouble("movement.checkMedianSpeed.axisYIgnore");
            plugin.checkMedianSpeed = plugin.yamlConfig.getBoolean("movement.checkMedianSpeed.checkMedianSpeed");

            plugin.checkVelocity = plugin.yamlConfig.getBoolean("combat.checkVelocity.checkVelocity");
            plugin.checkStep = plugin.yamlConfig.getBoolean("movement.checkStep.checkStep");
            plugin.speedMaxInWater = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxInWaterG");
            plugin.checkNoFall = plugin.yamlConfig.getBoolean("movement.checkNoFall.checkNoFall");
            plugin.irrPlacement = plugin.yamlConfig.getBoolean("interaction.irrPlacement");
            plugin.checkSpider = plugin.yamlConfig.getBoolean("movement.checkSpider.checkSpider");
            plugin.velocitySpeedMin = plugin.yamlConfig.getDouble("combat.checkVelocity.velocitySpeedMin");
            plugin.checkLongJump = plugin.yamlConfig.getBoolean("movement.checkLongJump.checkLongJump");
            plugin.checkReachBlockBreak = plugin.yamlConfig.getBoolean("interaction.checkReachBlockBreak.checkReachBlockBreak");
            plugin.checkReachBlockPlace = plugin.yamlConfig.getBoolean("interaction.checkReachBlockPlace.checkReachBlockPlace");
            plugin.autoViolationKick = plugin.yamlConfig.getBoolean("flag-system.autoViolationKick");
            plugin.checkAutoClickerA = plugin.yamlConfig.getBoolean("interaction.checkAutoClicker.checkAutoClickerA");
            plugin.cancelEventIfHacking = plugin.yamlConfig.getBoolean("flag-system.cancelEventIfHacking");
            plugin.enableAntiCheat = plugin.yamlConfig.getBoolean("flag-system.enableAntiCheat");
            plugin.checkWalkOnFluid = plugin.yamlConfig.getBoolean("movement.checkWalkOnFluid.checkWalkOnFluid");
            plugin.checkSpeed = plugin.yamlConfig.getBoolean("movement.checkSpeed.checkSpeed");
            plugin.checkFly = plugin.yamlConfig.getBoolean("movement.checkFly.checkFly");
            plugin.checkFastLadder = plugin.yamlConfig.getBoolean("movement.checkFastLadder.checkFastLadder");
            plugin.spacedViolationMessages = plugin.yamlConfig.getBoolean("spacedViolationMessages.spacedViolationMessages");
            plugin.checkBHop = plugin.yamlConfig.getBoolean("movement.checkBHop.checkBHop");
            plugin.maxSpeedBoatQ = plugin.yamlConfig.getDouble("movement.checkSpeed.maxSpeedBoatQ");
            plugin.speedMaxUnderBlock = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxUnderBlockB");
            plugin.debugMode = plugin.yamlConfig.getBoolean("other.debugMode");
            plugin.speedMaxInWaterNum = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxInWaterNumK");
            plugin.checkFastPlace = plugin.yamlConfig.getBoolean("timer-checks.checkFastPlace.checkFastPlace");
            plugin.fastPlaceFlagNum = plugin.yamlConfig.getDouble("timer-checks.checkFastPlace.fastPlaceFlagNumA");
            plugin.violationLoggerNum = plugin.violationFileYaml.getInt("log-system.violationLoggerNum");
            plugin.enableViolationLogger = plugin.violationFileYaml.getBoolean("log-system.enableViolationLogger");
            plugin.BhopUntilHackingAlternative = plugin.yamlConfig.getDouble("movement.checkBHop.BhopUntilHackingAlternativeB");
            plugin.fastPlaceSampleNum = plugin.yamlConfig.getDouble("timer-checks.checkFastPlace.fastPlaceSampleNumA");
            plugin.speedCheckbHopAlternative = plugin.yamlConfig.getDouble("movement.checkBHop.speedCheckbHopAlternativeB");
            plugin.checkRegen = plugin.yamlConfig.getBoolean("combat.checkRegen");
            plugin.smartCombatMovementChange = plugin.yamlConfig.getBoolean("combat.smartCombatMovementChange.smartCombatMovementChange");
            plugin.smartCombatMovementChangeNumber = plugin.yamlConfig.getDouble("combat.smartCombatMovementChange.smartCombatMovementChangeNumber");
            plugin.smartCombatMovementChangeTimer = plugin.yamlConfig.getInt("combat.smartCombatMovementChange.smartCombatMovementChangeTimer");

            plugin.baritoneFlag = plugin.yamlConfig.getInt("other.checkBaritone.baritoneFlag");
            plugin.maxSpeedBoatP = plugin.yamlConfig.getDouble("movement.checkSpeed.maxSpeedBoatP");
            plugin.packetSamples = plugin.yamlConfig.getInt("other.checkBaritone.packetSamples");

            plugin.baritoneFlagB = plugin.yamlConfig.getInt("other.checkBaritone.baritoneFlagB");
            plugin.packetSamplesB = plugin.yamlConfig.getInt("other.checkBaritone.packetSamplesB");

            plugin.xrayMaterials = (List<String>) plugin.xrayYaml.getList("xray-config");
            plugin.xrayTimer = plugin.xrayYaml.getInt("timer");
            plugin.checkXray = plugin.xrayYaml.getBoolean("checkXray");

            plugin.irrMovementPacketSamplesB = plugin.yamlConfig.getInt("movement.checkIrrMovement.irrMovementPacketSamplesB");
            plugin.irrMaxDistanceB = plugin.yamlConfig.getDouble("movement.checkIrrMovement.irrMaxDistanceB");
            plugin.irrYAxisIgnoreB = plugin.yamlConfig.getDouble("movement.checkIrrMovement.irrYAxisIgnoreB");
            plugin.baritoneLoopThreshold = plugin.yamlConfig.getInt("other.checkBaritone.baritoneLoopThreshold");

            plugin.baritoneLoopThresholdB = plugin.yamlConfig.getInt("other.checkBaritone.baritoneLoopThresholdB");
            plugin.checkNeuralAnalysis = plugin.yamlConfig.getBoolean("other.checkNeuralAnalysis.checkNeuralAnalysis");
            plugin.worlds = (List<String>) plugin.yamlConfig.getList("flag-system.blacklisted-worlds");
            plugin.inAirYCoodD = plugin.yamlConfig.getDouble("movement.checkFly.inAirYCoodD");
            plugin.clearAllViolationsTimerNum = plugin.yamlConfig.getLong("flag-system.clearAllViolationsTimerNum");
            plugin.clearAllViolationsTimer = plugin.yamlConfig.getBoolean("flag-system.clearAllViolationsTimer");
            plugin.speedMaxXZ = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxXZL");
            plugin.speedMaxXZMaxL = plugin.yamlConfig.getDouble("movement.checkSpeed.speedMaxXZMaxL");
            plugin.irrMaxDistance = plugin.yamlConfig.getDouble("movement.checkIrrMovement.irrMaxDistance");
            plugin.checkIrrMovement = plugin.yamlConfig.getBoolean("movement.checkIrrMovement.checkIrrMovement");

            plugin.lowString = plugin.yamlConfig.getString("thresholders.low-string");
            plugin.mediumString = plugin.yamlConfig.getString("thresholders.medium-string");
            plugin.highString = plugin.yamlConfig.getString("thresholders.high-string");

            plugin.roundedThresholdLow = plugin.yamlConfig.getDouble("thresholders.roundedThresholdLow");
            plugin.roundedThresholdMedium = plugin.yamlConfig.getDouble("thresholders.roundedThresholdMedium");
            plugin.roundedThresholdHigh = plugin.yamlConfig.getDouble("thresholders.roundedThresholdHigh");
            plugin.irregularEventCountLow = plugin.yamlConfig.getInt("thresholders.irregularEventCountLow");
            plugin.irregularEventCountMedium = plugin.yamlConfig.getInt("thresholders.irregularEventCountMedium");
            plugin.irregularEventCountHigh = plugin.yamlConfig.getInt("thresholders.irregularEventCountHigh");
            plugin.disableACEnderDragon = plugin.yamlConfig.getLong("other.disableACEnderDragon");
            plugin.irrMovementPacketSamples = plugin.yamlConfig.getInt("movement.checkIrrMovement.irrMovementPacketSamples");
            plugin.useUsageForAutoBan = plugin.yamlConfig.getBoolean("flag-system.enableAutoBan.useUsageForAutoBan");
            plugin.irrYAxisIgnore = plugin.yamlConfig.getDouble("movement.checkIrrMovement.irrYAxisIgnore");
            plugin.checkBaritone = plugin.yamlConfig.getBoolean("other.checkBaritone.checkBaritone");
            plugin.substringNum = plugin.yamlConfig.getInt("other.checkBaritone.substringNum");
            plugin.baritoneReset = plugin.yamlConfig.getInt("other.checkBaritone.baritoneReset");
            plugin.substringNumB = plugin.yamlConfig.getInt("other.checkBaritone.substringNumB");
            plugin.checkElytraFlight = plugin.yamlConfig.getBoolean("movement.checkElytraFlight.checkElytraFlight");
            plugin.elytraFlightUntilHacking = plugin.yamlConfig.getInt("movement.checkElytraFlight.elytraFlightUntilHacking");
            plugin.checkNoSlowDownC = plugin.yamlConfig.getBoolean("movement.noSlowDownCheck.checkNoSlowDownC");
            plugin.maxSpeedElytra = plugin.yamlConfig.getDouble("movement.checkSpeed.maxSpeedElytra");
            plugin.speedAmplifierThreshold = plugin.yamlConfig.getDouble("movement.checkSpeed.speedAmplifierThreshold");
            plugin.jumpAmplifierThreshold = plugin.yamlConfig.getDouble("movement.checkSpeed.jumpAmplifierThreshold");
            plugin.maxMili = plugin.yamlConfig.getInt("other.checkNuker.maxMili");
            plugin.checkNuker = plugin.yamlConfig.getBoolean("other.checkNuker.checkNuker");
            plugin.substringNumC = plugin.yamlConfig.getInt("other.checkBaritone.substringNumC");
            plugin.packetSamplesC = plugin.yamlConfig.getInt("other.checkBaritone.packetSamplesC");
            plugin.baritoneLoopThresholdC = plugin.yamlConfig.getInt("other.checkBaritone.baritoneLoopThresholdC");
            plugin.baritoneFlagC = plugin.yamlConfig.getInt("other.checkBaritone.baritoneFlagC");
            plugin.minFlagCount = plugin.yamlConfig.getInt("other.checkBaritone.minFlagCount");
            plugin.minFlagCountB = plugin.yamlConfig.getInt("other.checkBaritone.minFlagCountB");
            plugin.minFlagCountC = plugin.yamlConfig.getInt("other.checkBaritone.minFlagCountC");
            plugin.allowedBlockTillFlagNum = plugin.yamlConfig.getInt("other.checkBaritone.allowedBlockTillFlagNum");
            plugin.doubleBlockNum = plugin.yamlConfig.getDouble("other.checkBaritone.doubleBlockNum");
            plugin.minDistance = plugin.yamlConfig.getDouble("other.checkBaritone.minDistance");
            plugin.sample = plugin.yamlConfig.getInt("other.checkNuker.sample");
            plugin.speedIncrease = plugin.yamlConfig.getDouble("item-attribute.speedIncrease");
            plugin.attributeCounter = plugin.yamlConfig.getInt("item-attribute.counter");
            plugin.speedMinimumWhenDescendingB = plugin.yamlConfig.getDouble("movement.checkGlide.speedMinimumWhenDescendingB");
            plugin.glideUntilHackingB = plugin.yamlConfig.getInt("movement.checkGlide.glideUntilHackingB");
            plugin.enableDiscord = plugin.discordYaml.getBoolean("enableWebhook");
            plugin.checkStepB = plugin.yamlConfig.getBoolean("movement.checkStep.checkStepB");
            plugin.stepMaxSpeedB = plugin.yamlConfig.getDouble("movement.checkStep.stepMaxSpeedB");

            plugin.autoClickerBLimit = plugin.yamlConfig.getInt("interaction.checkAutoClicker.autoClickerBLimit");

            plugin.checkSemiPredA = plugin.yamlConfig.getBoolean("movement.checkSemiPred.checkSemiPredA");
            plugin.checkSemiPredB = plugin.yamlConfig.getBoolean("movement.checkSemiPred.checkSemiPredB");
            plugin.checkAutoClickerB = plugin.yamlConfig.getBoolean("interaction.checkAutoClicker.checkAutoClickerB");
            plugin.semiPredYUntilFlagA = plugin.yamlConfig.getInt("movement.checkSemiPred.semiPredYUntilFlagA");
            plugin.semiPredYUntilFlagB = plugin.yamlConfig.getInt("movement.checkSemiPred.semiPredYUntilFlagB");
            plugin.semiPredYLimiterA = plugin.yamlConfig.getDouble("movement.checkSemiPred.semiPredYLimiterA");
            plugin.semiPredYLimiterB = plugin.yamlConfig.getDouble("movement.checkSemiPred.semiPredYLimiterB");
            plugin.checkReachNumAPingIncrease = plugin.yamlConfig.getBoolean("combat.checkReach.reachNumAPingIncrease");
            plugin.reachNumAPingIncrease = plugin.yamlConfig.getDouble("combat.checkReach.reachNumAPingIncrease");
            plugin.reachNumAPing = plugin.yamlConfig.getInt("combat.checkReach.reachNumAPing");
            plugin.autoClickerBms = plugin.yamlConfig.getInt("interaction.checkAutoClicker.autoClickerBms");
            plugin.ignorePlayers = plugin.yamlConfig.getString("flag-system.ignorePlayers");
            plugin.trainNeuralNetwork = plugin.yamlConfig.getBoolean("movement.neuralAnalysis.trainNeuralNetwork");
            plugin.checkNeuralAnalysis = plugin.yamlConfig.getBoolean("movement.neuralAnalysis.checkNeuralAnalysis");
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {


                    File file = new File(plugin.getDataFolder(),File.separator + "NeuralNetworkSavedData" + File.separator + plugin.yamlConfig.getString("movement.neuralAnalysis.neuralNetworkData"));
                    if(file.exists()) {
                        plugin.neuralNetwork = plugin.neuralNetworkManager.loadNetwork(file);
                    } else {
                        plugin.neuralNetwork = new NeuralNetwork(plugin, 2, plugin.yamlConfig.getInt("movement.neuralAnalysis.hiddenLayerSize"), plugin.yamlConfig.getInt("movement.neuralAnalysis.hiddenNodesSize"), 1, plugin.yamlConfig.getDouble("movement.neuralAnalysis.learningRate"), plugin.yamlConfig.getDouble("movement.neuralAnalysis.weightDecay"));
                    }

                }
            });


            if (plugin.enableDiscord) {
                plugin.url = new URL(plugin.discordYaml.getString("webhook"));
            }
            if (plugin.yamlConfig.getInt("other.versionStringResetter") != 1059) {
                String dir = System.getProperty("user.dir");

                try {
                    BufferedReader br = new BufferedReader(new FileReader(new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "main.yml")));
                    File previousFile = new File(dir + File.separator + "plugins" + File.separator + "SoaromaSAC" + File.separator + "previousMain.yml");
                    if (previousFile.exists()) {
                        previousFile.delete();
                        previousFile.createNewFile();
                    } else {
                        previousFile.createNewFile();
                    }
                    FileWriter writer = new FileWriter(previousFile);
                    String string;
                    while ((string = br.readLine()) != null) {
                        writer.write(string + System.lineSeparator());
                    }
                    br.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                plugin.configFile.delete();
                Bukkit.getLogger().warning("----------------------------");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning(" An error has occured setting config variables for SoaromaSAC");
                Bukkit.getLogger().warning(" Your main.yml file has been automatically replaced with default");
                Bukkit.getLogger().warning(" If this error occurs then that means your configuration file had some values missing");
                Bukkit.getLogger().warning(" Or versionStringResetter was set to a different number");
                Bukkit.getLogger().warning(" A backup copy of your previous main.yml has been made in the SoaromaSAC folder, renamed to previousMain.yml");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning(" ");
                Bukkit.getLogger().warning("----------------------------");
                plugin.configManager = new ConfigManager(plugin, "main.yml", "discord.yml", "logger.yml", "xray.yml");
                plugin.configManager.saveDefaultConfiguration();
                plugin.configManager.resetValues();
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("----------------------------");
            Bukkit.getLogger().warning("AN EXTREME ERROR HAS OCCURRED, PLEASE KILL YOUR SERVER AND DELETE BOTH main.yml and discord.yml");
            Bukkit.getLogger().warning("----------------------------");
            e.printStackTrace();
        }
    }

    public void saveDefaultConfiguration() {
        try {
            InputStream input = plugin.getResource(plugin.configFile.getName());

            java.nio.file.Files.copy(input, plugin.configFile.toPath());
        } catch (IOException e) {
        }
    }
}