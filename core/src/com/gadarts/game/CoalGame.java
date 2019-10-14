package com.gadarts.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class CoalGame extends Game {

    @Override
    public void create() {
        setScreen(new BattleScreen());
        Gdx.input.setCursorCatched(true);
    }

}
