package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

public enum CharacterClass {
    // O bárbaro vive de stamina e não tem mana, o mago usa mana para habilidades e stamina para correr.
    BARBARIAN(false, 10, 6, 8, 2, 10,
        Ability.AXE_SWING, Ability.WHIRLWIND, Ability.GROUND_SLAM, Ability.BERSERKER_RAGE),
    MAGE(true, 2, 6, 3, 12, 6,
        Ability.MAGIC_BOLT, Ability.FIREBALL, Ability.FROST_NOVA, Ability.ARCANE_BLINK);

    private final boolean usesMana;
    private final Ability[] abilities;
    private final int strength;
    private final int agility;
    private final int defense;
    private final int intelligence;
    private final int vitality;

    CharacterClass(
        boolean usesMana,
        int strength,
        int agility,
        int defense,
        int intelligence,
        int vitality,
        Ability... abilities
    ) {
        this.usesMana = usesMana;
        this.abilities = abilities;
        this.strength = strength;
        this.agility = agility;
        this.defense = defense;
        this.intelligence = intelligence;
        this.vitality = vitality;
    }

    public boolean usesMana() {
        return usesMana;
    }

    /** Basic attack first, then the three skills. */
    public Ability[] getAbilities() {
        return abilities;
    }

    /** Each class has its sheets at sprites/characters/<class>_sheet.png and <class>_tired.png. */
    public String getSheetPath() {
        return "sprites/characters/" + name().toLowerCase() + "_sheet.png";
    }

    public String getTiredSheetPath() {
        return "sprites/characters/" + name().toLowerCase() + "_tired.png";
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
