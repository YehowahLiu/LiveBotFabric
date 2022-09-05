package com.ishland.fabric.livebot.commands;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.entity.Bossbar;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BotBossbarCommand {

    private static final Logger logger = LogManager.getLogger("LiveBotFabric Command BotHere");

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiveBotConfig config = LiveBotConfig.getInstance();
        commandDispatcher.register(
                CommandManager.literal("botbossbar")
                        .then(CommandManager.literal("enable")
                                .executes(ctx->{
                                    config.BOSSBAR_DISPLAY = true;
                                    config.BOSSBAR_DISPLAY_OFFLINE = true;
                                    Bossbar.getInstance().checkDisplay();
                                    logger.info("Set bossbar display method to enable");
                                    return 1;
                                }))
                        .then(CommandManager.literal("online")
                                .executes(ctx->{
                                    config.BOSSBAR_DISPLAY = true;
                                    config.BOSSBAR_DISPLAY_OFFLINE = false;
                                    Bossbar.getInstance().checkDisplay();
                                    logger.info("Set bossbar display method to online");
                                    return 1;
                                }))
                        .then(CommandManager.literal("disable")
                                .executes(ctx->{
                                    config.BOSSBAR_DISPLAY = false;
                                    config.BOSSBAR_DISPLAY_OFFLINE = false;
                                    Bossbar.getInstance().checkDisplay();
                                    logger.info("Set bossbar display method to disable");
                                    return 1;
                                }))
        );
    }
}
