package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class KnockbackComponent implements Component {

    public static final float DURATION = 0.18f;

    public float x;
    public float y;
    public float timeLeft = DURATION;

    public KnockbackComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
