package com.dungeonrpg.game.item;

import com.dungeonrpg.game.rpg.CharacterClass;

import java.util.Random;

/**
 * How generous a monster is. The chance to drop anything is rolled first, then the rarity from the
 * weights below (COMMON, RARE, EPIC, LEGENDARY, UNIQUE). A zero weight means the tier can never drop it.
 */
public enum LootTier {
    TIER_1(0.12f, 82f, 17.9f, 0f, 0.1f, 0f),
    TIER_2(0.22f, 62f, 29f, 8f, 0.9f, 0.1f),
    TIER_3(0.35f, 55f, 30f, 13f, 1.9f, 0.1f),
    MINION(0.25f, 45f, 35f, 16f, 3.5f, 0.5f),
    BOSS(1f, 0f, 35f, 40f, 20f, 5f);

    private final float dropChance;
    private final float[] weights;

    LootTier(float dropChance, float... weights) {
        this.dropChance = dropChance;
        this.weights = weights;
    }

    public float getDropChance() {
        return dropChance;
    }

    /** Returns a weapon for the class, or null when nothing drops. */
    public Item roll(Random random, CharacterClass characterClass) {
        if (random.nextFloat() >= dropChance) return null;

        return ItemGenerator.generate(rollRarity(random), characterClass, random);
    }

    Rarity rollRarity(Random random) {
        float total = 0f;

        for (float weight : weights) {
            total += weight;
        }

        float roll = random.nextFloat() * total;
        Rarity[] rarities = Rarity.values();

        for (int i = 0; i < rarities.length; i++) {
            roll -= weights[i];

            if (roll < 0f) return rarities[i];
        }

        return rarities[rarities.length - 1];
    }
}
