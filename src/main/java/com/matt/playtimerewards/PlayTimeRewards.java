package com.matt.playtimerewards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import java.io.File;
import java.util.Objects;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PlayTimeRewards.MODID)
public class PlayTimeRewards {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "playtimerewards";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static PlayTimeConfig ModConfig;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PlayTimeRewards(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        File configDir = FMLPaths.CONFIGDIR.get().toFile();
        ModConfig = PlayTimeConfig.load(configDir);

        NeoForge.EVENT_BUS.addListener(PlayTimeRewardsCommands::onRegisterCommands);
        NeoForge.EVENT_BUS.register(new PlayTimeEvents());
    }

    private static void runServerCommand(Player player, String rawCommand) {
        if (rawCommand == null || rawCommand.isBlank()) return;

        // Replace <player> with the actual username
        String cmd = rawCommand.replace("<player>", player.getName().getString());

        var server = player.getServer();
        if (server == null) return;

        // Execute as server console (no spam output)
        server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                cmd
        );
    }

    public static void reward( Player player, long ticks ) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if( ModConfig.announceHours ) {
            String msg = String.format( "[PlayTimeRewards] Congrats! %s has played for %d hours", player.getName(), hours );
            Objects.requireNonNull(player.getServer()).getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
        }

        if( ModConfig.rewardHourly ) {
            runServerCommand( player, ModConfig.hourlyCommand );
        }

        for (var reward : ModConfig.specificRewards) {
            if (reward.hour == hours) {
                runServerCommand(player, reward.command);

                String msg = String.format( "[PlayTimeRewards] Congrats! %s reached a playtime milestone of %d so receieved another reward!", player.getName(), hours );
                Objects.requireNonNull(player.getServer()).getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
            }
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Loaded PlayTime Rewards");
    }


}
