package com.dungeonrpg.game;

import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Difficulty;

/** Choices made on the title screen, kept across runs. */
public class GameSession {

    public static final int MAX_NAME_LENGTH = 12;
    private static final String DEFAULT_NAME = "HERO";

    private CharacterClass characterClass = CharacterClass.BARBARIAN;
    private Difficulty difficulty = Difficulty.MEDIUM;
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        String trimmed = name == null ? "" : name.trim();

        playerName = trimmed.isEmpty() ? DEFAULT_NAME : trimmed;
    }
}
