package com.matt.playtimerewards;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class PlayTimeRewardsCommands {

    public static void onRegisterCommands(RegisterCommandsEvent event ) {
        register( event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher ) {
        dispatcher.register(
                Commands.literal("playtime")
                        .then( Commands.literal( "self" )
                                .executes( ctx -> showPlaytime(ctx.getSource()))
                        )
                        .then( Commands.argument( "player", EntityArgument.player() )
                                .executes( ctx -> showOther( ctx.getSource(), EntityArgument.getPlayer( ctx, "player") ) )
                        )
                        .then( Commands.literal( "top" )
                                .executes( ctx -> showTop( ctx.getSource() ) )
                        )

                        .then( Commands.literal( "author" )
                                .executes( ctx -> showAuthor( ctx.getSource() ) )
                        )

                        .then( Commands.literal( "reload" )
                                .executes( ctx -> reloadConfig( ctx.getSource() ) )
                        )

                        .executes( ctx -> showPlaytime( ctx.getSource() )
                )
        );
    }

    private static String formatPlaytime(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return "%dh %dm".formatted(hours, minutes % 60);
    }

    private static void sendMessage(CommandSourceStack source, String msg) {
        source.sendSuccess(() -> Component.literal(msg), false);
    }

    private static void sendPlaytime(CommandSourceStack source, ServerPlayer player) {
        long ticks = PlayTimeCache.getTicks( player.getUUID() );
        String time = formatPlaytime(ticks);
        String msg = "%s has played for %s".formatted(player.getName().getString(), time);
        sendMessage(source, msg);
    }

    private static int reloadConfig( CommandSourceStack source ) {
        File configDir = FMLPaths.CONFIGDIR.get().toFile();
        PlayTimeRewards.ModConfig = PlayTimeConfig.load( configDir );

        source.sendSuccess( () -> Component.literal("[PlayTimeRewards] Config reloaded"), false );

        return 1;
    }

    private static int showPlaytime(CommandSourceStack source ) {
        Player player = source.getPlayer();

        if( player == null ) return 0;

        long ticks = PlayTimeCache.getTicks( player.getUUID() );
        String time = formatPlaytime(ticks);
        String msg = "%s has played for %s".formatted(player.getName().getString(), time);
        source.sendSuccess(() -> Component.literal(msg), false);

        return 1;
    }

    private static int showOther(CommandSourceStack source, ServerPlayer target) {
        sendPlaytime(source, target);
        return 1;
    }

    private static int showAuthor( CommandSourceStack source ) {
        source.sendSuccess(() -> Component.literal( "Mod Author: ii_Matt" ), false );
        return 1;
    }

    private static int showTop(CommandSourceStack source) {
        var server = source.getServer();

        // Pull all stored playtimes from your in-memory model
        Map<String, Long> all = PlayTimeCache.getAll(); // You add this accessor

        if (all.isEmpty()) {
            sendMessage(source, "No playtime data recorded yet.");
            return 1;
        }

        // Sort by ticks descending
        var sorted = all.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> -e.getValue()))
                .limit(10)
                .toList();

        sendMessage(source, "Top Playtimes:");

        int rank = 1;
        for (var entry : sorted) {
            UUID uuid = UUID.fromString(entry.getKey());
            long ticks = entry.getValue();

            // Resolve name using profile cache
            var profile = server.getProfileCache().get(uuid).orElse(null);
            String name = (profile != null)
                    ? profile.getName()
                    : uuid.toString(); // fallback

            String time = formatPlaytime(ticks);

            String line = "%d. %s — %s"
                    .formatted(rank++, name, time);

            sendMessage(source, line);
        }

        return 1;
    }



}
