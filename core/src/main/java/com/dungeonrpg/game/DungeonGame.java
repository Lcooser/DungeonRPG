package com.dungeonrpg.game;

import com.badlogic.gdx.Game;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DungeonGame extends Game {

    private CharacterClass playerClass = CharacterClass.BARBARIAN;

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    public CharacterClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(CharacterClass playerClass) {
        this.playerClass = playerClass;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
