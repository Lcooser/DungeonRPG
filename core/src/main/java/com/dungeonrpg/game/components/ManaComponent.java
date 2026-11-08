package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class ManaComponent implements Component {

    public float current;
    public float max;

    public ManaComponent(float max) {
        this.max = max;
        this.current = max;
    }

    public boolean spend(float amount) {
        if (current < amount) return false;

        current -= amount;
        return true;
    }
}
