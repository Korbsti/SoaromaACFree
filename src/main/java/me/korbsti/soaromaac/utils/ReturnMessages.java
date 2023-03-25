package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;

public class ReturnMessages {

    Main plugin;

    public ReturnMessages(Main instance) {
        plugin = instance;
    }


    public String cheat(int x) {
        switch (x) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            case 9:
                return "I";
            case 10:
                return "J";
            case 11:
                return "K";
            case 12:
                return "L";
            case 13:
                return "M";
            case 14:
                return "N";
            case 15:
                return "O";
            case 16:
                return "P";
            case 17:
                return "Q";
        }
        return "null";
    }

    public void Messages(int x, double y, double z) {
        switch (x) {
            case 1:
                Bukkit.broadcastMessage(
                        "Exceeded irregularCheckNumUntilHacking, irregularNumSampleNum and was lower than or equal too irregularTimeCount");
                break;
            case 2:
                Bukkit.broadcastMessage(
                        "Players pitch exceeded irregularPitchPositiveMax or was lower than irregularPitchNegativeMax");
                break;
            case 3:
                Bukkit.broadcastMessage("Exceeded speedCheckMidAir and BhopUntilHacking | " + "speed was: " + y);
                break;
            case 4:

                Bukkit.broadcastMessage("Exceeded stepBlockHeight" + " | height was " + y);
                break;
            case 5:
                Bukkit.broadcastMessage(
                        "Within longJumpBlockYNumTillIgnore and exceeded longJumpDistanceTillHacking and longJumpNumInAirTillCheckingLongJump "
                                + "distance: " + y);
                break;
            case 6:
                Bukkit.broadcastMessage("Exceeded pingUntilPingSpoofing");
                break;
            case 7:
                Bukkit.broadcastMessage("Exceeded spiderUpUntilHacking");
                break;
            case 8:
                Bukkit.broadcastMessage("Exceeded spiderUpUntilHackingAlternative");
                break;
            case 9:
                Bukkit.broadcastMessage("Exceeded noFallBlockHeight and noFallTimer");
                break;
            case 10:
                Bukkit.broadcastMessage(
                        "Exceeded noSlowDownSpeedNum or inslowableBlockUntilFlag or speedCheckWhenCrouching and shiftUntilCheckingNoSlow");
                break;
            case 11:
                Bukkit.broadcastMessage("Exceeded speedMaxInVehicle " + "| speed: " + y);
                break;
            case 12:
                Bukkit.broadcastMessage("Exceeded speedMaxOnGround " + "| speed: " + y);
                break;
            case 13:
                Bukkit.broadcastMessage("Exceeded speedMaxOnIce " + "| speed: " + y);
                break;
            case 14:
                Bukkit.broadcastMessage("Exceeded speedMaxWhenAscending " + "| speed: " + y);
                break;
            case 15:
                Bukkit.broadcastMessage("Exceeded speedCheckWhenInAirAlternative " + "| speed: " + y);

                break;
            case 16:
                Bukkit.broadcastMessage("Exceeded speedMaxWhenDescending " + "| speed: " + y);
                break;
            case 17:
                Bukkit.broadcastMessage("Exceeded speedMaxInAir " + "| speed: " + y);
                break;
            case 18:
                Bukkit.broadcastMessage("Exceeded fluidWalkUntilHackingAlternative");
                break;
            case 19:
                Bukkit.broadcastMessage(
                        "Exceeded BhopUntilHackingAlternative and speedCheckbHopAlternative " + "| speed: " + y);
                break;
            case 21:
                Bukkit.broadcastMessage("Exceeded fluidWalkUntilHacking");
                break;
            case 20:
                Bukkit.broadcastMessage("Exceeded fluidJumpsOnWaterUntilHacking and fluidWalkIrregularSpeed");
                break;
            case 22:
                Bukkit.broadcastMessage("Exceeded speedMaxClimbing " + "| speed: " + y);
                break;
            case 23:
                Bukkit.broadcastMessage("Exceeded levitationDownUntilHacking");
                break;
            case 24:
                Bukkit.broadcastMessage("Exceeded inAirUpwardUntilHacking ");
                break;
            case 25:
                Bukkit.broadcastMessage("Exceeded inAirJumpUntilHacking");
                break;
            case 26:
                Bukkit.broadcastMessage("Exceeds speedMaxInWater " + "| speed: " + y);
                break;
            case 27:
                Bukkit.broadcastMessage("Exceeds speedMaxUnderBlock " + "| speed: " + y);
                break;
            case 28:
                Bukkit.broadcastMessage("Was under speedMinimumWhenDescending " + "| speed: " + y);
                break;
            case 29:
                Bukkit.broadcastMessage("Exceeded checkGroundNumA");
                break;
            case 30:
                Bukkit.broadcastMessage("Exceeded speedMaxXZL and speedMaxXZMaxL " + "| speed: " + y);
                break;
            case 31:
                Bukkit.broadcastMessage("Exceeded speedMaxOnBlock " + "| speed: " + y);
                break;
            case 32:
                Bukkit.broadcastMessage("Exceeded inAirYCoodD");
                break;
            case 33:
                Bukkit.broadcastMessage("Lower than velocitySpeedMin and exceeded velocityFlagsUntilFlagging");
                break;
            case 34:
                Bukkit.broadcastMessage(
                        "Distance was lower than irrMaxDistance and exceeded irrMovementPacketSamples && irrYAxisIgnore");
                break;
            case 35:
                Bukkit.broadcastMessage("SpeedMaxYL exceeded " + "| speed: " + y);
                break;
            case 36:
                Bukkit.broadcastMessage(
                        "Exceded fluidWalkDCheck, this is hardcoded into the AC there are no config options");
                break;
            case 37:
                Bukkit.broadcastMessage("Exceeded thresholdLimitBaritone and packetSamples");
                break;
            case 38:
                Bukkit.broadcastMessage("Exceeded elytraFlightUntilHacking");
                break;
            case 39:
                Bukkit.broadcastMessage("Flagged noSlowDownCheckC (boolean)");
                break;
            case 40:
                Bukkit.broadcastMessage("Exceeded maxElytraSpeed : " + y);
                break;
            case 41:
                Bukkit.broadcastMessage("Exceeded maxMili : " + y);
                break;
            case 42:
                Bukkit.broadcastMessage("Exceeded checkFlightE and checkFly : ");
                break;
            case 43:
                Bukkit.broadcastMessage("Below speedMinimumWhenDescendingB and exceeded glideUntilHackingB");
                break;
            case 44:
                Bukkit.broadcastMessage("Exceeded limiterB and speedMaxB, speed: " + y);
                break;
            case 45:
                Bukkit.broadcastMessage("Exceeded maxMedianSpeed: " + y + " , and first packet and last packet was below axisYIgnore");
                break;
            case 46:
                Bukkit.broadcastMessage("Exceeded speedMaxA, speed: " + y);
                break;
            case 47:
                Bukkit.broadcastMessage("Exceeded speed P, speed: " + y);
                break;
            case 48:
                Bukkit.broadcastMessage("Exceeded elytraB limit and was lower than B speed limit for elytra " + y);
                break;
            case 49:
                Bukkit.broadcastMessage("Exceeded noSlowDownDMaxSpeed and noSlowDownDUntilCheat | speed: " + y);
                break;
            case 50:
                Bukkit.broadcastMessage("Exceeded A limiter for semi prediction: " + y);
                break;
            case 51:
                Bukkit.broadcastMessage("Exceeded B limiter for semi prediction: " + y);
                break;
        }
    }

    public String type(int x) {
        try {
            return plugin.cheatNames.get(x - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }
}
