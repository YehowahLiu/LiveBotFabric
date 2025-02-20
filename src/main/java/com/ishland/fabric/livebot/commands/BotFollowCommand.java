package com.ishland.fabric.livebot.commands;

import com.ishland.fabric.livebot.entity.LiveBot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BotFollowCommand {

    private static final Logger logger = LogManager.getLogger("LiveBotFabric Command BotFollow");

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register(
                CommandManager.literal("botfollow")
                        .then(CommandManager.argument("target", EntityArgumentType.entity())
                                .executes(ctx -> {
                                    final Entity target =
                                            EntityArgumentType.getEntity(ctx, "target");
                                    logger.info(ctx.getSource().getDisplayName().asString() +
                                            " called with target: " + target.getUuid());

                                    if (!doSpectate(ctx, target)) return -1;

                                    return 1;
                                }))
                        .executes(ctx -> {
                            final Entity target = ctx.getSource().getEntity();
                            if (target == null) {
                                logger.info(ctx.getSource().getDisplayName().asString() +
                                        " called with no argument from non-entity");
                                ctx.getSource().sendFeedback(
                                        new LiteralText("This command can only be run by an entity!"),
                                        true);
                                return -1;
                            }
                            logger.info(ctx.getSource().getDisplayName().asString() +
                                    " called with no argument, using " + target.getUuid());

                            if (!doSpectate(ctx, target)) return -1;

                            return 1;
                        })
        );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean doSpectate(CommandContext<ServerCommandSource> ctx, Entity target) {
        ServerPlayerEntity bot = LiveBot.getInstance().getBot();
        if (bot == null) {
            ctx.getSource().sendFeedback(
                    new LiteralText("Bot is not online"), true);
            return false;
        }
        if (target == bot) {
            ctx.getSource().sendFeedback(new LiteralText("Cannot follow bot itself"), true);
            return false;
        }

        LiveBot.getInstance().teleport(target, true);

        ctx.getSource().sendFeedback(
                new LiteralText("Successfully made Bot spectate " +
                        target.getDisplayName().asString()), true);
        return true;
    }

}
