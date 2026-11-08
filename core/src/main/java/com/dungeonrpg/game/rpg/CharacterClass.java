package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

public enum CharacterClass {
    // O guerreiro vive de stamina, o mago de mana.
    WARRIOR(10, 6, 8, 2, 10),
    MAGE(2, 6, 3, 12, 6);

    private final int strength;
    private final int agility;
    private final int defense;
    private final int intelligence;
    private final int vitality;

    CharacterClass(int strength, int agility, int defense, int intelligence, int vitality) {
        this.strength = strength;
        this.agility = agility;
        this.defense = defense;
        this.intelligence = intelligence;
        this.vitality = vitality;
    }

    public StatsComponent createStats() {
        StatsComponent stats = new StatsComponent();

        stats.strength = strength;
        stats.agility = agility;
        stats.defense = defense;
        stats.intelligence = intelligence;
        stats.vitality = vitality;

        return stats;
    }
}
