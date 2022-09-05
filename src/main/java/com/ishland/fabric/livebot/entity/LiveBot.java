package com.ishland.fabric.livebot.entity;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.data.LiveBotState;
import com.ishland.fabric.livebot.data.ServerInstance;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class LiveBot {
    public static final Logger logger = LogManager.getLogger("LiveBotFabric LiveBot");
    private static LiveBot INSTANCE;
    private ServerPlayerEntity bot = null;
    private int nextRefresh = 0;

    public static LiveBot getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiveBot();
        }
        return INSTANCE;
    }

    public void onBotJoin(ServerPlayerEntity player) {
        this.bot = player;
        Bossbar.getInstance().checkDisplay();
    }

    public void onBotLeft() {
        this.bot = null;
        Bossbar.getInstance().checkDisplay();
    }

    public ServerPlayerEntity getBot() {
        if (bot == null) {
            bot = ServerInstance.server.getPlayerManager().getPlayer(LiveBotConfig.getInstance().STREAM_BOT);
        }
        return bot;
    }

    public boolean isOnline() {
        return getBot() != null && !bot.isDisconnected();
    }

    public void teleport(Entity target,
                         boolean spectate, boolean updateTwice) {
        logger.info("Moved Bot to " + target.getDisplayName().asString() + " " + target.getUuid());
        if (!bot.isSpectator())
            bot.changeGameMode(GameMode.SPECTATOR);
        bot.teleport(
                (ServerWorld) target.world,
                target.getX(),
                target.getY(),
                target.getZ(),
                target.getYaw(),
                target.getPitch()
        );
        if (spectate) {
            LiveBotState state = LiveBotState.getInstance();
            state.followedEntity = target.getUuid();
            state.dim = bot.getEntityWorld().getRegistryKey().getValue().toString();
            state.x = target.getX();
            state.y = target.getY();
            state.z = target.getZ();
            state.yaw = target.getYaw();
            state.pitch = target.getPitch();
            bot.setCameraEntity(target);
        } else
            LiveBotState.getInstance().followedEntity = null;
        if (updateTwice) getInstance().nextRefresh = LiveBotConfig.getInstance().REFRESH_DELAY;
    }

    public void teleport(Entity target, boolean spectate) {
        teleport(target, spectate, true);
    }


    public static void tick() {
        if (getInstance().getBot() != null) {
            getInstance().updateView();
        }
    }

    public void updateView() {
        Entity target = getEntity(LiveBotState.getInstance().followedEntity);
        if (bot == null || target == null) return;
        if (nextRefresh >= 0) nextRefresh--;
        if (!bot.isSpectator())
            bot.changeGameMode(GameMode.SPECTATOR);
        if (LiveBotState.getInstance().followedEntity.equals(bot.getUuid()))
            LiveBotState.getInstance().followedEntity = null;
        if (bot.getCameraEntity().equals(bot) ||
                !bot.getEntityWorld().getRegistryKey().getValue().toString()
                        .equals(LiveBotState.getInstance().dim) ||
                (
                        Math.pow(LiveBotState.getInstance().x - target.getX(), 2) +
                                Math.pow(LiveBotState.getInstance().y - target.getY(), 2) +
                                Math.pow(LiveBotState.getInstance().z - target.getZ(), 2)
                ) > Math.pow(LiveBotConfig.getInstance().REFRESH_DISTANCE, 2))
            teleport(target, true);
        if (nextRefresh == 0)
            teleport(target, true, false);
    }

    private Entity getEntity(UUID uuid) {
        for (ServerWorld world : ServerInstance.server.getWorlds()) {
            final Entity entity = world.getEntity(uuid);
            if (entity != null) return entity;
        }
        return null;
    }

    public Text getStateText() {
        return (
                bot == null
                        ? new LiteralText("Offline")
                        .setStyle(net.minecraft.text.Style.EMPTY.withBold(true)
                                .withColor(Formatting.RED))
                        : (
                        this.isSpectateOthers()
                                ? new LiteralText(
                                "Spectating " + bot
                                        .getCameraEntity()
                                        .getDisplayName().asString())
                                .setStyle(net.minecraft.text.Style.EMPTY.withBold(true)
                                        .withColor(Formatting.GREEN))
                                : new LiteralText("Idle")
                                .setStyle(net.minecraft.text.Style.EMPTY.withBold(true)
                                        .withColor(Formatting.DARK_GREEN))
                )
        );
    }

    public boolean isSpectateOthers() {
        return bot != null && bot.getCameraEntity() != bot;
    }

    public float getPercent() {
        if (this.isSpectateOthers()) {
            return (float) (1F - (
                    Math.sqrt(
                            Math.pow(LiveBotState.getInstance().x - bot.getX(), 2) +
                                    Math.pow(LiveBotState.getInstance().y - bot.getY(), 2) +
                                    Math.pow(LiveBotState.getInstance().z - bot.getZ(), 2)
                    ) / (LiveBotConfig.getInstance().REFRESH_DISTANCE + 4)
            ));
        }
        return 0;
    }
}
