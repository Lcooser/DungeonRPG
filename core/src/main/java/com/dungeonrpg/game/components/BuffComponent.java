package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class BuffComponent implements Component {

    public float timeLeft;
    public float damageMultiplier = 1f;
    public float speedMultiplier = 1f;
    // Visual that follows the owner while the buff lasts.
    public Entity aura;
}
