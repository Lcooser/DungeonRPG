package com.dungeonrpg.game.item;

import com.badlogic.gdx.graphics.Color;

/** Item quality. The icon columns follow the progression drawn on weapon_icons.png. */
public enum Rarity {
    COMMON("COMMON", new Color(0.78f, 0.78f, 0.8f, 1f), 1f, 0, 0, 1),
    RARE("RARE", new Color(0.35f, 0.62f, 1f, 1f), 1.6f, 1, 2, 3),
    EPIC("EPIC", new Color(0.72f, 0.42f, 1f, 1f), 2.4f, 2, 4, 5),
    LEGENDARY("LEGENDARY", new Color(1f, 0.62f, 0.15f, 1f), 3.4f, 4, 6, 6),
    UNIQUE("UNIQUE", new Color(1f, 0.3f, 0.3f, 1f), 4.6f, 7, 7, 7);

    private final String label;
    private final Color color;
    private final float damageMultiplier;
    // Upper bound of the random attribute bonus rolled on items of this rarity.
    private final int maxBonus;
    private final int firstIconColumn;
    private final int lastIconColumn;

    Rarity(String label, Color color, float damageMultiplier, int maxBonus, int firstIconColumn, int lastIconColumn) {
        this.label = label;
        this.color = color;
        this.damageMultiplier = damageMultiplier;
        this.maxBonus = maxBonus;
        this.firstIconColumn = firstIconColumn;
        this.lastIconColumn = lastIconColumn;
    }

    public String getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getMaxBonus() {
        return maxBonus;
    }

    public int getFirstIconColumn() {
        return firstIconColumn;
    }

    public int getLastIconColumn() {
        return lastIconColumn;
    }
}
