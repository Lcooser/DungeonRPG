package com.dungeonrpg.game;

import com.badlogic.gdx.Game;
import com.dungeonrpg.game.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DungeonGame extends Game {

    private final GameSession session = new GameSession();

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    public GameSession getSession() {
        return session;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
