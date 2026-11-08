package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.rpg.Ability;

public class ProjectileComponent implements Component {

    public static final float LIFETIME = 1.6f;

    public Ability ability;
    public boolean fromPlayer;
    public float damage;
    public float directionX;
    public float directionY;
    public float timeLeft = LIFETIME;
}
