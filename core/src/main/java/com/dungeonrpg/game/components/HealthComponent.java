package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    public float current;
    public float max;
    // Dano acumulado no frame, aplicado (com a defesa) pelo HealthSystem.
    public float pendingDamage;

    public HealthComponent(float max) {
        this.max = max;
        this.current = max;
    }
}
