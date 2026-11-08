package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class StaminaComponent implements Component {

    public float current;
    public float max;
    // Tempo desde o último gasto, a regeneração só começa depois de uma pausa.
    public float timeSinceUse;
    // Ao esvaziar, só volta a correr depois de recuperar parte da barra.
    public boolean exhausted;

    public StaminaComponent(float max) {
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
