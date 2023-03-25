package me.korbsti.soaromaac.movementreplayer;

import me.korbsti.soaromaac.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class FileManager {

    Main plugin;

    String directoryPathFile = System.getProperty("user.dir") + File.separator + "plugins" + File.separator
            + "SoaromaSAC" + File.separator + "replayer";

    public FileManager(Main instance) {
        this.plugin = instance;
    }

    public void ifFolderExists() {
        if (new File(directoryPathFile).mkdir()) {
        }
        if (new File(directoryPathFile + File.separator + new SimpleDateFormat("MM.dd.yyyy").format(new Date(Instant
                .now().toEpochMilli()))).mkdir()) {
        }
    }

    public void checkPlayerTXT(Player p) {
        if (!new File(directoryPathFile + File.separator + new SimpleDateFormat("MM.dd.yyyy").format(new Date(Instant.now().toEpochMilli()))).mkdirs()) {
            try {
                new File(directoryPathFile + File.separator + new SimpleDateFormat("MM.dd.yyyy").format(new Date(Instant
                        .now().toEpochMilli())) + File.separator + p.getUniqueId() + ".txt").createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void writePlayerBlockData(Player p, int x, int y, int z) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                try {
                    String path = directoryPathFile + File.separator + new SimpleDateFormat("MM.dd.yyyy").format(
                            new Date(Instant
                                    .now().toEpochMilli())) + File.separator + p.getUniqueId() + ".txt";

                    FileWriter writer = new FileWriter(new File(path), true);
                    boolean canWrite = true;
                    FileReader fr = new FileReader(path);
                    BufferedReader br = new BufferedReader(fr);
                    String currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        if (currentLine.contains("b," + x + "," + y + "," + z)) {
                            canWrite = false;
                        }
                    }
                    if (canWrite) {
                        writer.write("b," + x + "," + y + "," + z + System.getProperty("line.separator"));
                    }
                    writer.close();
                    fr.close();
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

    }

    public void writePlayerMovementData(Player p, double x, double y, double z) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                try {
                    FileWriter writer = new FileWriter(new File(directoryPathFile + File.separator
                            + new SimpleDateFormat("MM.dd.yyyy").format(new Date(Instant
                            .now().toEpochMilli())) + File.separator + p.getUniqueId() + ".txt"), true);

                    writer.write("m," + x + "," + y + "," + z + System.getProperty("line.separator"));
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
