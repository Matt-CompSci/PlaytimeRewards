package com.matt.playtimerewards;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.io.File;
import java.text.NumberFormat;

public class PlayTimeAutosaveEvents {

    private static int autosaveTimer = 0;

    // 5 minutes = 20 ticks * 60 seconds * 5 minutes
    private static final int AUTOSAVE_INTERVAL = 20 * 60 * 5;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        autosaveTimer++;

        if (autosaveTimer >= AUTOSAVE_INTERVAL) {
            PlayTimeCache.save();
            autosaveTimer = 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Flush the cache to disk when a player leaves
        PlayTimeCache.save();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        // Final flush before shutdown
        PlayTimeCache.save();
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        var server = event.getServer();

        // Load your data
        File dataDir = server.getWorldPath(LevelResource.ROOT).resolve("data").toFile();
        PlayTimeCache.load(dataDir);
    }

}
