package com.matt.playtimerewards;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeCache {

    private static PlayTimeSave diskModel;
    private static Map<String, Long> memoryMap = new HashMap<>();
    private static File saveFile;

    public static void load(File dataDir) {
        dataDir.mkdirs();
        saveFile = new File(dataDir, "playtimerewards_data.json");

        diskModel = PlayTimeSave.load(dataDir);

        memoryMap.clear();
        memoryMap.putAll(diskModel.playtimes);
    }

    public static void save() {
        diskModel.playtimes.clear();
        diskModel.playtimes.putAll(memoryMap);

        System.out.println("[PlayTimeRewards] Saving playtime data to: " + saveFile.getAbsolutePath());
        diskModel.save(saveFile);
    }


    public static long getTicks(UUID uuid) {
        return memoryMap.getOrDefault(uuid.toString(), 0L);
    }

    public static void addTicks(UUID uuid, long amount) {
        memoryMap.put(uuid.toString(), getTicks(uuid) + amount);
    }
}
