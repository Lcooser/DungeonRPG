package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

public enum CharacterClass {
    // O bárbaro vive de stamina e não tem mana, o mago usa mana para habilidades e stamina para correr.
    BARBARIAN(false, 10, 6, 8, 2, 10),
    MAGE(true, 2, 6, 3, 12, 6);

    private final boolean usesMana;
    private final int strength;
    private final int agility;
    private final int defense;
    private final int intelligence;
    private final int vitality;

    CharacterClass(boolean usesMana, int strength, int agility, int defense, int intelligence, int vitality) {
        this.usesMana = usesMana;
        this.strength = strength;
        this.agility = agility;
        this.defense = defense;
        this.intelligence = intelligence;
        this.vitality = vitality;
    }

    public boolean usesMana() {
        return usesMana;
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
