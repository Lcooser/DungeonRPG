package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class StunComponent implements Component {

    public float timeLeft;

    public StunComponent(float timeLeft) {
        this.timeLeft = timeLeft;
    }
}
