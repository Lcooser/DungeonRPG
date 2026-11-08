package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

public enum CharacterClass {
    // O bárbaro vive de stamina e não tem mana, o mago usa mana para habilidades e stamina para correr.
    BARBARIAN(false, Ability.AXE_SWING, Ability.HEAVY_SWING, 10, 6, 8, 2, 10),
    MAGE(true, Ability.MAGIC_BOLT, Ability.FIREBALL, 2, 6, 3, 12, 6);

    private final boolean usesMana;
    private final Ability primaryAbility;
    private final Ability secondaryAbility;
    private final int strength;
    private final int agility;
    private final int defense;
    private final int intelligence;
    private final int vitality;

    CharacterClass(
        boolean usesMana,
        Ability primaryAbility,
        Ability secondaryAbility,
        int strength,
        int agility,
        int defense,
        int intelligence,
        int vitality
    ) {
        this.usesMana = usesMana;
        this.primaryAbility = primaryAbility;
        this.secondaryAbility = secondaryAbility;
        this.strength = strength;
        this.agility = agility;
        this.defense = defense;
        this.intelligence = intelligence;
        this.vitality = vitality;
    }

    public boolean usesMana() {
        return usesMana;
    }

    public Ability getPrimaryAbility() {
        return primaryAbility;
    }

    public Ability getSecondaryAbility() {
        return secondaryAbility;
    }

    /** Cada classe tem as suas folhas em sprites/<classe>_sheet.png e sprites/<classe>_tired.png. */
    public String getSheetPath() {
        return "sprites/" + name().toLowerCase() + "_sheet.png";
    }

    public String getTiredSheetPath() {
        return "sprites/" + name().toLowerCase() + "_tired.png";
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
