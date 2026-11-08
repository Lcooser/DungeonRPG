package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.HurtComponent;

public class HurtSystem extends IteratingSystem {

    private final ComponentMapper<HurtComponent> hurts =
        ComponentMapper.getFor(HurtComponent.class);

    public HurtSystem() {
        super(Family.all(HurtComponent.class).get(), 2);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HurtComponent hurt = hurts.get(entity);
        hurt.timeLeft -= deltaTime;

        if (hurt.timeLeft <= 0f) {
            entity.remove(HurtComponent.class);
        }
    }
}
