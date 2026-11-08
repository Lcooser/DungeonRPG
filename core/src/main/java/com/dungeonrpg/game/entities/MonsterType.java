package com.dungeonrpg.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.item.LootTier;
import com.dungeonrpg.game.rpg.Ability;

/**
 * Every monster of the game. A family (skeleton, spider, slime) comes in three tiers that share the
 * same sheet and are told apart by tint and stats until each tier gets its own art: to swap it in,
 * point the sheet name of that entry to the new file.
 */
public enum MonsterType {
    SKELETON(monster("BONE SOLDIER", Family.SKELETON, 1, "skeleton").directional().sprite(72f, 32f)
        .hitbox(20f, 14f).health(120f).defense(5).strength(0).speed(70f).aggro(200f)
        .attack(Ability.SKELETON_SLASH).xp(20).loot(LootTier.TIER_1)),
    SKELETON_CAPTAIN(monster("BONE CAPTAIN", Family.SKELETON, 2, "skeleton").directional().sprite(76f, 32f)
        .hitbox(20f, 14f).health(220f).defense(9).strength(4).speed(78f).aggro(210f)
        .attack(Ability.SKELETON_SLASH).xp(36).loot(LootTier.TIER_2).tint(0.7f, 0.85f, 1f)),
    SKELETON_TYRANT(monster("BONE TYRANT", Family.SKELETON, 3, "skeleton").directional().sprite(84f, 34f)
        .hitbox(22f, 14f).health(420f).defense(14).strength(9).speed(86f).aggro(230f)
        .attack(Ability.SKELETON_SLASH).xp(80).loot(LootTier.TIER_3).tint(1f, 0.62f, 0.56f)),

    SPIDER(monster("CAVE SPIDER", Family.SPIDER, 1, "spider").directional().sprite(84f, 36f)
        .hitbox(30f, 16f).health(180f).defense(4).strength(0).speed(105f).aggro(220f)
        .attack(Ability.SPIDER_BITE).xp(35).loot(LootTier.TIER_1)),
    SPIDER_STALKER(monster("WEB STALKER", Family.SPIDER, 2, "spider").directional().sprite(88f, 36f)
        .hitbox(30f, 16f).health(300f).defense(8).strength(3).speed(118f).aggro(230f)
        .attack(Ability.SPIDER_BITE).xp(60).loot(LootTier.TIER_2).tint(0.62f, 1f, 0.8f)),
    SPIDER_WEAVER(monster("VENOM WEAVER", Family.SPIDER, 3, "spider").directional().sprite(96f, 38f)
        .hitbox(32f, 18f).health(520f).defense(12).strength(7).speed(125f).aggro(250f)
        .attack(Ability.SPIDER_BITE).xp(110).loot(LootTier.TIER_3).tint(0.92f, 0.58f, 1f)),

    SLIME(monster("GREEN SLIME", Family.SLIME, 1, "slime").sprite(60f, 28f)
        .hitbox(22f, 12f).health(80f).defense(0).strength(0).speed(55f).aggro(160f)
        .attack(Ability.SLIME_LUNGE).xp(10).loot(LootTier.TIER_1)),
    SLIME_OOZE(monster("ACID OOZE", Family.SLIME, 2, "slime").sprite(64f, 28f)
        .hitbox(24f, 12f).health(150f).defense(3).strength(3).speed(62f).aggro(170f)
        .attack(Ability.SLIME_LUNGE).xp(20).loot(LootTier.TIER_2).tint(1f, 0.9f, 0.45f)),
    SLIME_BLOB(monster("TOXIC BLOB", Family.SLIME, 3, "slime").sprite(72f, 30f)
        .hitbox(26f, 14f).health(280f).defense(6).strength(6).speed(70f).aggro(180f)
        .attack(Ability.SLIME_LUNGE).xp(45).loot(LootTier.TIER_3).tint(0.72f, 0.52f, 1f)),

    BROOD_GUARD(monster("BROOD GUARD", Family.SPIDER, 0, "spider").directional().sprite(96f, 40f)
        .hitbox(34f, 18f).health(380f).defense(8).strength(5).speed(100f).aggro(240f)
        .attack(Ability.SPIDER_BITE).xp(70).loot(LootTier.MINION).tint(0.72f, 0.62f, 0.95f)),
    SPIDER_QUEEN(monster("SPIDER QUEEN", Family.SPIDER, 0, "spider").directional().sprite(150f, 64f)
        .hitbox(52f, 26f).health(900f).defense(8).strength(0).speed(80f).aggro(260f)
        .attack(Ability.BOSS_BITE).xp(250).loot(LootTier.BOSS).boss());

    /** Groups monsters that look alike, used to pick a tier of the same kind. */
    public enum Family {
        SKELETON,
        SPIDER,
        SLIME
    }

    private final String displayName;
    private final Family family;
    // 1 to 3 for regular tiers, 0 for minions and bosses that are placed by hand.
    private final int tierLevel;
    private final String sheetName;
    private final boolean directional;
    private final float spriteSize;
    private final float bodySize;
    private final float colliderWidth;
    private final float colliderHeight;
    private final float health;
    private final int defense;
    private final int strength;
    private final float speed;
    private final float aggroRange;
    private final Ability attack;
    private final int xpReward;
    private final LootTier lootTier;
    private final Color tint;
    private final boolean boss;

    MonsterType(Builder builder) {
        displayName = builder.displayName;
        family = builder.family;
        tierLevel = builder.tierLevel;
        sheetName = builder.sheetName;
        directional = builder.directional;
        spriteSize = builder.spriteSize;
        bodySize = builder.bodySize;
        colliderWidth = builder.colliderWidth;
        colliderHeight = builder.colliderHeight;
        health = builder.health;
        defense = builder.defense;
        strength = builder.strength;
        speed = builder.speed;
        aggroRange = builder.aggroRange;
        attack = builder.attack;
        xpReward = builder.xpReward;
        lootTier = builder.lootTier;
        tint = builder.tint;
        boss = builder.boss;
    }

    private static Builder monster(String displayName, Family family, int tierLevel, String sheetName) {
        return new Builder(displayName, family, tierLevel, sheetName);
    }

    /** Readable way to describe a monster, since most fields are optional. */
    private static final class Builder {
        private final String displayName;
        private final Family family;
        private final int tierLevel;
        private final String sheetName;
        private boolean directional;
        private float spriteSize;
        private float bodySize;
        private float colliderWidth;
        private float colliderHeight;
        private float health;
        private int defense;
        private int strength;
        private float speed;
        private float aggroRange;
        private Ability attack;
        private int xpReward;
        private LootTier lootTier = LootTier.TIER_1;
        private Color tint;
        private boolean boss;

        private Builder(String displayName, Family family, int tierLevel, String sheetName) {
            this.displayName = displayName;
            this.family = family;
            this.tierLevel = tierLevel;
            this.sheetName = sheetName;
        }

        private Builder directional() {
            directional = true;
            return this;
        }

        private Builder sprite(float spriteSize, float bodySize) {
            this.spriteSize = spriteSize;
            this.bodySize = bodySize;
            return this;
        }

        private Builder hitbox(float width, float height) {
            colliderWidth = width;
            colliderHeight = height;
            return this;
        }

        private Builder health(float value) {
            health = value;
            return this;
        }

        private Builder defense(int value) {
            defense = value;
            return this;
        }

        private Builder strength(int value) {
            strength = value;
            return this;
        }

        private Builder speed(float value) {
            speed = value;
            return this;
        }

        private Builder aggro(float value) {
            aggroRange = value;
            return this;
        }

        private Builder attack(Ability value) {
            attack = value;
            return this;
        }

        private Builder xp(int value) {
            xpReward = value;
            return this;
        }

        private Builder loot(LootTier value) {
            lootTier = value;
            return this;
        }

        private Builder tint(float red, float green, float blue) {
            tint = new Color(red, green, blue, 1f);
            return this;
        }

        private Builder boss() {
            boss = true;
            return this;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public Family getFamily() {
        return family;
    }

    public int getTierLevel() {
        return tierLevel;
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

    public LootTier getLootTier() {
        return lootTier;
    }

    /** Color multiplied over the sprite, null for the original colors. */
    public Color getTint() {
        return tint;
    }

    public boolean isBoss() {
        return boss;
    }

    /** Monsters have defense and strength (their damage bonus), the rest comes from the type. */
    public StatsComponent createStats() {
        StatsComponent stats = new StatsComponent();
        stats.defense = defense;
        stats.strength = strength;

        return stats;
    }
}
