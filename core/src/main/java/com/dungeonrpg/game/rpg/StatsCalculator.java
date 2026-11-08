package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.components.StatsComponent;

/** Fórmulas que transformam atributos em valores de jogo. */
public final class StatsCalculator {

    private static final float DEFENSE_SCALE = 50f;

    private StatsCalculator() {
    }

    public static float maxHealth(StatsComponent stats) {
        return 50f + stats.vitality * 10f;
    }

    public static float maxMana(StatsComponent stats) {
        return 10f + stats.intelligence * 10f;
    }

    public static float maxStamina(StatsComponent stats) {
        return 50f + (stats.vitality + stats.agility) * 5f;
    }

    public static float manaRegen(StatsComponent stats) {
        return 2f + stats.intelligence * 0.3f;
    }

    public static float staminaRegen(StatsComponent stats) {
        return 10f + stats.agility;
    }

    public static float moveSpeed(StatsComponent stats) {
        return 100f + stats.agility * 10f;
    }

    /** A defesa reduz o dano em porcentagem, e todo golpe causa pelo menos 1 de dano. */
    public static float reduceDamage(float damage, StatsComponent stats) {
        return Math.max(1f, damage * DEFENSE_SCALE / (DEFENSE_SCALE + stats.defense));
    }
}
