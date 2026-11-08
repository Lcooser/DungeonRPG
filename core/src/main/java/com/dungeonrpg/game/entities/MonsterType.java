package com.dungeonrpg.game.entities;

import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.rpg.Ability;

public enum MonsterType {
    // sheet, directional, sprite size, body size, hitbox w/h, health, defense, speed, aggro range, attack, xp
    SKELETON("skeleton", true, 72f, 32f, 20f, 14f, 120f, 5, 70f, 200f, Ability.SKELETON_SLASH, 20),
    SPIDER("spider", true, 84f, 36f, 30f, 16f, 180f, 4, 105f, 220f, Ability.SPIDER_BITE, 35),
    SLIME("slime", false, 60f, 28f, 22f, 12f, 80f, 0, 55f, 160f, Ability.SLIME_LUNGE, 10);

    private final String sheetName;
    private final boolean directional;
    private final float spriteSize;
    private final float bodySize;
    private final float colliderWidth;
    private final float colliderHeight;
    private final float health;
    private final int defense;
    private final float speed;
    private final float aggroRange;
    private final Ability attack;
    private final int xpReward;

    MonsterType(
        String sheetName,
        boolean directional,
        float spriteSize,
        float bodySize,
        float colliderWidth,
        float colliderHeight,
        float health,
        int defense,
        float speed,
        float aggroRange,
        Ability attack,
        int xpReward
    ) {
        this.sheetName = sheetName;
        this.directional = directional;
        this.spriteSize = spriteSize;
        this.bodySize = bodySize;
        this.colliderWidth = colliderWidth;
        this.colliderHeight = colliderHeight;
        this.health = health;
        this.defense = defense;
        this.speed = speed;
        this.aggroRange = aggroRange;
        this.attack = attack;
        this.xpReward = xpReward;
    }

    public String getSheetPath() {
        return "sprites/" + sheetName + "_sheet.png";
    }

    public boolean isDirectional() {
        return directional;
    }

    public float getSpriteSize() {
        return spriteSize;
    }

    public float getBodySize() {
        return bodySize;
    }

    public float getColliderWidth() {
        return colliderWidth;
    }

    public float getColliderHeight() {
        return colliderHeight;
    }

    public float getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAggroRange() {
        return aggroRange;
    }

    public Ability getAttack() {
        return attack;
    }

    public int getXpReward() {
        return xpReward;
    }

    /** Monsters only have defense, health and damage come from the type and its ability. */
    public StatsComponent createStats() {
        StatsComponent stats = new StatsComponent();
        stats.defense = defense;

        return stats;
    }
}
