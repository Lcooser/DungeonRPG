package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class SlowComponent implements Component {

    // Fraction of the normal speed, 0.5 means half speed.
    public float factor;
    public float timeLeft;

    public SlowComponent(float factor, float timeLeft) {
        this.factor = factor;
        this.timeLeft = timeLeft;
    }
}
