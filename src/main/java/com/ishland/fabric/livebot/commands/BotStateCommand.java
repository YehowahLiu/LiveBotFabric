package com.ishland.fabric.livebot.commands;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.data.LiveBotState;
import com.ishland.fabric.livebot.data.ServerInstance;
import com.ishland.fabric.livebot.entity.Bossbar;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BotStateCommand {
    private static final List<String> states = List.of("always", "online", "never");
    public static void register(@NotNull CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiveBotConfig config = LiveBotConfig.getInstance();
        commandDispatcher.register(
                CommandManager.literal("botstate")
                        .then(
                                CommandManager.literal("display")
                                        .then(
                                                CommandManager.argument("state", StringArgumentType.word())
                                                        .suggests((ctx,builder)->{
                                                            for (String state: states) {
                                                                if(state.startsWith(builder.getRemaining().toLowerCase()))
                                                                    builder.suggest(state);
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx->{
                                                            String state = StringArgumentType.getString(ctx, "state");
                                                            if(!states.contains(state)){
                                                                ctx.getSource().sendFeedback(
                                                                        new LiteralText("Error argument!")
                                                                                .setStyle(Style.EMPTY.withColor(Formatting.RED)),
                                                                        false);
                                                                return -1;
                                                            }
                                                            config.BOSSBAR_DISPLAY = LiveBotConfig.DisplayType.valueOf(state.toUpperCase());
                                                            sendState(ctx);
                                                            return 1;
                                                        })
                                        ).executes(ctx -> {
                                            sendState(ctx);
                                            return 1;
                                        })
                        )
                        .executes(ctx -> {
                            ServerPlayerEntity bot = ServerInstance.server.getPlayerManager()
                                    .getPlayer(LiveBotConfig.getInstance().STREAM_BOT);
                            ctx.getSource().sendFeedback(
                                    new LiteralText("Bot state: ")
                                            .append(
                                                    (
                                                            bot == null
                                                                    ? "Offline"
                                                                    : (
                                                                    bot.getCameraEntity() != bot
                                                                            ? "Spectating " + bot
                                                                            .getCameraEntity()
                                                                            .getDisplayName().asString()
                                                                            : "Normal"
                                                            )
                                                    )
                                            ),
                                    true);
                            if (bot != null) {
                                ctx.getSource().sendFeedback(
                                        new LiteralText("Bot current position: (")
                                                .append(String.valueOf(bot.getEntityWorld().getRegistryKey().getValue().toString()))
                                                .append(",")
                                                .append(String.valueOf(bot.getX()))
                                                .append(",")
                                                .append(String.valueOf(bot.getY()))
                                                .append(",")
                                                .append(String.valueOf(bot.getZ()))
                                                .append(")"),
                                        true);
                            }
                            ctx.getSource().sendFeedback(
                                    new LiteralText("Bot previous refresh position: (")
                                            .append(String.valueOf(LiveBotState.getInstance().x))
                                            .append(",")
                                            .append(String.valueOf(LiveBotState.getInstance().y))
                                            .append(",")
                                            .append(String.valueOf(LiveBotState.getInstance().z))
                                            .append(")"),
                                    true);
                            if (bot != null)
                                ctx.getSource().sendFeedback(
                                        new LiteralText("Distance: ")
                                                .append(String.valueOf(Math.sqrt(
                                                        Math.pow(LiveBotState.getInstance().x - bot.getX(), 2) +
                                                                Math.pow(LiveBotState.getInstance().y - bot.getY(), 2) +
                                                                Math.pow(LiveBotState.getInstance().z - bot.getZ(), 2)
                                                ))),
                                        true);
                            return 1;
                        })
        );
    }

    private static void sendState(CommandContext<ServerCommandSource> ctx) {
        Bossbar.getInstance().checkDisplay();
        String payload = "Bossbar of livebot is set to";
        switch (LiveBotConfig.getInstance().BOSSBAR_DISPLAY){
            case ONLINE -> payload += " display when bot online";
            case NEVER -> payload += " always display";
            default -> payload += " never display";
        }
        ctx.getSource().sendFeedback(new LiteralText(payload), false);
    }
}
