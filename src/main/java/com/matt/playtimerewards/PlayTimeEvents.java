package com.matt.playtimerewards;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayTimeEvents {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        PlayTimeCache.addTicks( player.getUUID(), 1 );
        long ticks = PlayTimeCache.getTicks( player.getUUID() );

        if (ticks > 0 && ticks % 72000 == 0) {
            PlayTimeRewards.reward(player, ticks);
        }
    }
}
