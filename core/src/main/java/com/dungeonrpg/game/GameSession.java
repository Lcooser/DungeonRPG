package com.dungeonrpg.game;

import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Difficulty;
import com.dungeonrpg.game.world.Theme;

/** Choices made on the title screen, kept across runs. */
public class GameSession {

    public static final int MAX_NAME_LENGTH = 12;
    private static final String DEFAULT_NAME = "HERO";

    private CharacterClass characterClass = CharacterClass.BARBARIAN;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private Theme theme = Theme.CRYPT;
    private String playerName = DEFAULT_NAME;

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        String trimmed = name == null ? "" : name.trim();

        playerName = trimmed.isEmpty() ? DEFAULT_NAME : trimmed;
    }
}
