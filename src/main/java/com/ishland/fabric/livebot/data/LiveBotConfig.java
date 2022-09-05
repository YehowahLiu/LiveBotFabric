package com.ishland.fabric.livebot.data;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.ishland.fabric.livebot.LiveBotFabric;

import java.io.*;

public class LiveBotConfig {

    private static final Gson gson = new Gson();
    private static final File data = new File("./LiveBotFabric/config.json");
    private static final LiveBotConfig INSTANCE;

    static {
        if (!data.exists()) {
            data.getParentFile().mkdirs();
            try (
                    final FileOutputStream out = new FileOutputStream(data);
                    final OutputStreamWriter writer = new OutputStreamWriter(out)
            ) {
                gson.toJson(new LiveBotConfig(), writer);
            } catch (Throwable t) {
                Throwables.throwIfUnchecked(t);
                throw new RuntimeException(t);
            }
        }
        try (
                final FileInputStream out = new FileInputStream(data);
                final InputStreamReader reader = new InputStreamReader(out)
        ) {
            INSTANCE = gson.fromJson(reader, LiveBotConfig.class);
            LiveBotFabric.logger.info("Loaded config " + INSTANCE.toString());
        } catch (Throwable t) {
            Throwables.throwIfUnchecked(t);
            throw new RuntimeException(t);
        }
    }

    public String STREAM_BOT = "undefined";
    public double REFRESH_DISTANCE = 96;
    public int REFRESH_DELAY = 5;
    public boolean BOSSBAR_DISPLAY = true;
    public boolean BOSSBAR_DISPLAY_OFFLINE = false;

    private LiveBotConfig() {
    }

    public static LiveBotConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "LiveBotConfig{" +
                "STREAM_BOT='" + this.STREAM_BOT + '\'' +
                ", REFRESH_DISTANCE=" + this.REFRESH_DISTANCE +
                ", REFRESH_DELAY=" + this.REFRESH_DELAY +
                ", BOSSBAR_DISPLAY="+ this.BOSSBAR_DISPLAY +
                ", BOSSBAR_DISPLAY_OFFLINE="+ this.BOSSBAR_DISPLAY_OFFLINE +
                '}';
    }
}
