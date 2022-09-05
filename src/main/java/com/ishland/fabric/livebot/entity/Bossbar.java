package com.ishland.fabric.livebot.entity;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.data.ServerInstance;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class Bossbar extends CommandBossBar {
    private static final Identifier bossBarIdentifier = new Identifier("livebot", "bossbar");
    private static Bossbar INSTANCE;

    public Bossbar() {
        super(bossBarIdentifier, new LiteralText("LiveBotFabric BossBar"));
        INSTANCE = this;
    }

    public static Bossbar getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = (Bossbar) ServerInstance.server.getBossBarManager().get(bossBarIdentifier);
        if (INSTANCE == null) INSTANCE = new Bossbar();
        return INSTANCE;
    }

    public void checkDisplay() {
        LiveBotConfig config = LiveBotConfig.getInstance();
        if (config.BOSSBAR_DISPLAY) {
            if (config.BOSSBAR_DISPLAY_OFFLINE) {
                this.setVisible(true);
            } else {
                this.setVisible(LiveBot.getInstance().isOnline());
            }
        }else {
            this.setVisible(false);
        }
    }

    public void tick() {
        if (!this.isVisible()) {
            return;
        }
        this.addPlayers(ServerInstance.server.getPlayerManager().getPlayerList());
        this.setColor(BossBar.Color.BLUE);
        this.setName(
                new LiteralText("Bot State: ")
                        .setStyle(net.minecraft.text.Style.EMPTY.withBold(true))
                        .append(LiveBot.getInstance().getStateText())
        );
        this.setPercent(LiveBot.getInstance().getPercent());
    }

    @Override
    public void onPlayerDisconnect(ServerPlayerEntity player) {
        super.onPlayerDisconnect(player);
        LiveBotConfig config = LiveBotConfig.getInstance();
        if (config.STREAM_BOT.equals(player.getDisplayName().asString())) {
            if (!config.BOSSBAR_DISPLAY_OFFLINE) {
                this.setVisible(false);
            }
        }
    }
}
