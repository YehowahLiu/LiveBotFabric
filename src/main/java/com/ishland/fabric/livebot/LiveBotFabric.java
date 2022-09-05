package com.ishland.fabric.livebot;

import com.ishland.fabric.livebot.data.LiveBotConfig;
import com.ishland.fabric.livebot.data.LiveBotState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class LiveBotFabric {

    public static final Logger logger = LogManager.getLogger("LiveBotFabric Main");

    private static LiveBotFabric instance = null;

    private TimerTask savingTask = null;

    private LiveBotFabric() {
        instance = this;
    }

    public static LiveBotFabric getInstance() {
        return instance != null ? instance : new LiveBotFabric();
    }

    // Warning: The server instance is not available right here
    public void onLoad() {
        logger.info("Loading data...");
        LiveBotState.getInstance().load();
        LiveBotConfig.getInstance();
        savingTask = new TimerTask() {
            @Override
            public void run() {
                LiveBotState.getInstance().save();
            }
        };
        new Timer(true).schedule(savingTask, 30 * 1000, 60 * 1000);
    }

    public void onDisable() {
        savingTask.cancel();
        logger.info("Saving data...");
        LiveBotState.getInstance().save();
    }
}
