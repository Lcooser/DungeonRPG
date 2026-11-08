package com.dungeonrpg.game.item;

import com.dungeonrpg.game.rpg.StatType;

/** A rolled weapon: base item plus the damage and attribute bonus it came with. */
public class Item {

    private final WeaponType type;
    private final Rarity rarity;
    private final int iconColumn;
    private final String name;
    private final int damage;
    private final StatType bonusStat;
    private final int bonusAmount;

    public Item(WeaponType type, Rarity rarity, int iconColumn, String name, int damage, StatType bonusStat, int bonusAmount) {
        this.type = type;
        this.rarity = rarity;
        this.iconColumn = iconColumn;
        this.name = name;
        this.damage = damage;
        this.bonusStat = bonusStat;
        this.bonusAmount = bonusAmount;
    }

    public WeaponType getType() {
        return type;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getIconColumn() {
        return iconColumn;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    /** Null when the item has no attribute bonus. */
    public StatType getBonusStat() {
        return bonusStat;
    }

    public int getBonusAmount() {
        return bonusAmount;
    }

    /** Rough power used to decide whether a drop beats the equipped weapon. */
    public int getScore() {
        return damage + bonusAmount * 2;
    }
}
