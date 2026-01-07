package com.matt.playtimerewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* Notes:
 * Linked to the text file in the world directory
 * this should be loaded once! from then on we commands
 * should be interfacing with the "in memory" version
 */
public class PlayTimeSave {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "playtimerewards_data.json";

    public Map<String, Long> playtimes = new HashMap<>();

    public static PlayTimeSave load(File configDir) {
        File file = new File(configDir, FILE_NAME);

        if (!file.exists()) {
            PlayTimeSave save = new PlayTimeSave();
            save.save(file);
            return save;
        }

        try (FileReader reader = new FileReader(file)) {
            PlayTimeSave loaded = GSON.fromJson(reader, PlayTimeSave.class);
            if (loaded.playtimes == null) loaded.playtimes = new HashMap<>();
            return loaded;
        } catch (Exception e) {
            e.printStackTrace();
            return new PlayTimeSave();
        }
    }

    public void save(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            System.out.println("[PlayTimeRewards] Writing JSON to: " + file.getAbsolutePath());
            GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long get(UUID uuid) {
        return playtimes.getOrDefault(uuid.toString(), 0L);
    }

    public void add(UUID uuid, long ticks) {
        playtimes.put(uuid.toString(), get(uuid) + ticks);
    }
}

