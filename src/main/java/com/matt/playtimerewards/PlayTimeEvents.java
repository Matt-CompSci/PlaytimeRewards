package com.matt.playtimerewards;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayTimeEvents {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        var data = player.getPersistentData();
        long ticks = data.getLong("playtimerewards_playtime");
        data.putLong("playtimerewards_playtime", ticks + 1);

        if (ticks > 0 && ticks % 72000 == 0) {
            PlayTimeRewards.reward(player, ticks);
        }
    }
}
