package com.dungeonrpg.game;

import com.badlogic.gdx.Game;
import com.dungeonrpg.game.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DungeonGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
