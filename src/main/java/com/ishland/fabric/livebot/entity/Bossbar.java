package com.ishland.fabric.livebot.entity;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.data.ServerInstance;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class Bossbar {
    private static final Identifier bossBarIdentifier = new Identifier("livebot", "bossbar");
    private static Bossbar INSTANCE;
    private CommandBossBar bossBar;

    public Bossbar() {
        INSTANCE = this;
        this.bossBar = ServerInstance.server.getBossBarManager().get(bossBarIdentifier);
        if(this.bossBar==null){
            this.bossBar = new CommandBossBar(bossBarIdentifier,  new LiteralText("LiveBotFabric BossBar"));
        }
    }

    public static Bossbar getInstance() {
        if (INSTANCE == null) INSTANCE = new Bossbar();
        return INSTANCE;
    }

    public void checkDisplay() {
        LiveBotConfig config = LiveBotConfig.getInstance();
        switch (config.BOSSBAR_DISPLAY){
            case NEVER -> this.bossBar.setVisible(false);
            case ONLINE -> this.bossBar.setVisible(LiveBot.getInstance().isOnline());
            default -> this.bossBar.setVisible(true);
        }
    }

    public void tick() {
        if (!this.bossBar.isVisible()) {
            return;
        }
        this.bossBar.addPlayers(ServerInstance.server.getPlayerManager().getPlayerList());
        this.bossBar.setColor(BossBar.Color.BLUE);
        this.bossBar.setName(
                new LiteralText("Bot State: ")
                        .setStyle(net.minecraft.text.Style.EMPTY.withBold(true))
                        .append(LiveBot.getInstance().getStateText())
        );
        this.bossBar.setPercent(LiveBot.getInstance().getPercent());
    }

    public void onPlayerConnect() {
        this.checkDisplay();
    }

    public void onPlayerDisconnect() {
        this.checkDisplay();
    }
}
