package com.dungeonrpg.game.rpg;

public enum Difficulty {
    EASY("EASY", 0.75f, 1.1f),
    MEDIUM("MEDIUM", 1f, 1f),
    HARD("HARD", 1.3f, 0.95f);

    private final String label;
    private final float monsterSpeedMultiplier;
    private final float playerSpeedMultiplier;

    Difficulty(String label, float monsterSpeedMultiplier, float playerSpeedMultiplier) {
        this.label = label;
        this.monsterSpeedMultiplier = monsterSpeedMultiplier;
        this.playerSpeedMultiplier = playerSpeedMultiplier;
    }

    public String getLabel() {
        return label;
    }

    public float getMonsterSpeedMultiplier() {
        return monsterSpeedMultiplier;
    }

    public float getPlayerSpeedMultiplier() {
        return playerSpeedMultiplier;
    }
}
