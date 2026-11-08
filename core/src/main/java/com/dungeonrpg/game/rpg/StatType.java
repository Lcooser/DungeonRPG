package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

/** Attributes the player can raise with stat points. */
public enum StatType {
    STRENGTH("STR"),
    AGILITY("AGI"),
    DEFENSE("DEF"),
    INTELLIGENCE("INT"),
    VITALITY("VIT");

    private final String label;

    StatType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int get(StatsComponent stats) {
        switch (this) {
            case STRENGTH:
                return stats.strength;
            case AGILITY:
                return stats.agility;
            case DEFENSE:
                return stats.defense;
            case INTELLIGENCE:
                return stats.intelligence;
            default:
                return stats.vitality;
        }
    }

    public void add(StatsComponent stats, int amount) {
        switch (this) {
            case STRENGTH:
                stats.strength += amount;
                break;
            case AGILITY:
                stats.agility += amount;
                break;
            case DEFENSE:
                stats.defense += amount;
                break;
            case INTELLIGENCE:
                stats.intelligence += amount;
                break;
            default:
                stats.vitality += amount;
        }
    }
}
