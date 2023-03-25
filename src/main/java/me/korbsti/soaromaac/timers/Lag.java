package me.korbsti.soaromaac.timers;

public class Lag implements Runnable {
    public static long LAST_TICK;
    public static int TICK_COUNT;
    public static long[] TICKS = new long[600];

    public static long getElapsed(int tickID) {
        long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (TICK_COUNT < ticks) {

            return 20.0D;
        }

        int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
        if (target < 0) {
            target = 0;
        }
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0D);
    }

    @Override
    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();

        TICK_COUNT += 1;
    }
}