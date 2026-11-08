package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/** Keeps an effect centered on its owner, with an optional vertical offset. */
public class AttachedEffectComponent implements Component {

    public Entity owner;
    public float offsetY;

    public AttachedEffectComponent(Entity owner, float offsetY) {
        this.owner = owner;
        this.offsetY = offsetY;
    }
}
