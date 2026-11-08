package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class ManaComponent implements Component {

    public float current;
    public float max;
    // Tempo desde o último gasto, a regeneração só começa depois de uma pausa.
    public float timeSinceUse;

    public ManaComponent(float max) {
        this.max = max;
        this.current = max;
    }

    public boolean spend(float amount) {
        if (current < amount) return false;

        current -= amount;
        timeSinceUse = 0f;
        return true;
    }
}
