package com.matt.playtimerewards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;


public class PlayTimeConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "playtimerewards.json";

    public boolean announceHours = true;
    public boolean rewardHourly = true;
    public String hourlyCommand = "give <player> Diamond";

    public static class HourReward {
        public int hour;
        public String command;

        public HourReward(int hour, String command) {
            this.hour = hour;
            this.command = command;
        }
    }

    public List<HourReward> specificRewards = List.of(
            new HourReward(1, "give <player> Diamond"),
            new HourReward(5, "give <player> Diamond"),
            new HourReward(16, "give <player> Diamond") );

    public static PlayTimeConfig load(File configDir) {
        File file = new File(configDir, FILE_NAME);

        if (!file.exists()) {
            PlayTimeConfig cfg = new PlayTimeConfig();
            cfg.save(file);
            return cfg;
        }

        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, PlayTimeConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new PlayTimeConfig();
        }
    }

    public void save(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
