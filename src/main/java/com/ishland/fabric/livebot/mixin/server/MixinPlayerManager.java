package com.ishland.fabric.livebot.mixin.server;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.entity.Bossbar;
import com.ishland.fabric.livebot.entity.LiveBot;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(
            method = "onPlayerConnect",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (LiveBotConfig.getInstance().STREAM_BOT.equals(player.getDisplayName().asString())) {
            LiveBot.getInstance().onBotJoin(player);
        }
        Bossbar.getInstance().onPlayerConnect(player);
    }

    @Inject(
            method = "remove",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onPlayerLeft(ServerPlayerEntity player, CallbackInfo ci) {
        if (LiveBotConfig.getInstance().STREAM_BOT.equals(player.getDisplayName().asString())) {
            LiveBot.getInstance().onBotLeft();
        }
        Bossbar.getInstance().onPlayerDisconnect(player);
    }
}
