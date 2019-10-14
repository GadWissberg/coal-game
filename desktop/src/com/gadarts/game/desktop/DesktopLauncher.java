package com.gadarts.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gadarts.game.CoalGame;
import com.gadarts.game.utils.Settings;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Coal";
        handleScreen(config);
        new LwjglApplication(new CoalGame(), config);
        Gdx.app.setLogLevel(Settings.LOG_LEVEL);
    }

    private static void handleScreen(LwjglApplicationConfiguration config) {
        if (Settings.FULL_SCREEN) {
            config.width = 1920;
            config.height = 1080;
            config.fullscreen = true;
        } else {
            config.width = 1280;
            config.height = 720;
        }
        config.samples = 3;
    }
}
