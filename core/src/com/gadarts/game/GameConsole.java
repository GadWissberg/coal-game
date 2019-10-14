package com.gadarts.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;

public class GameConsole {
    private static GameConsole instance = new GameConsole();
    private final Label label;
    private StringBuilder stringBuilder;

    private GameConsole() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.RED);
        stringBuilder = new StringBuilder();
        label = new Label(stringBuilder, style);
        label.setPosition(0, 10);
    }

    public static Label error(String message) {
        System.out.println(message);
        StringBuilder stringBuilder = instance.stringBuilder;
        stringBuilder.setLength(0);
        stringBuilder.append(message);
        instance.label.setText(stringBuilder);
        return instance.label;
    }
}
