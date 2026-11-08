package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    // Valor alto para a barra de vida dos monstros só aparecer depois do primeiro dano.
    private static final float LONG_AGO = 999f;

    public float current;
    public float max;
    // Dano acumulado no frame, aplicado (com a defesa) pelo HealthSystem.
    public float pendingDamage;
    public float timeSinceDamage = LONG_AGO;

    public HealthComponent(float max) {
        this.max = max;
        this.current = max;
    }
}
