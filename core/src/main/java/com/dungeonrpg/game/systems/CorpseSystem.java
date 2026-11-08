package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.tags.DeadComponent;

/** Remove o corpo dos monstros algum tempo depois da morte. */
public class CorpseSystem extends IteratingSystem {

    private static final float CORPSE_LIFETIME = 4f;

    private final ComponentMapper<DeadComponent> deads =
        ComponentMapper.getFor(DeadComponent.class);

    public CorpseSystem() {
        super(Family.all(MonsterComponent.class, DeadComponent.class).get(), 1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeadComponent dead = deads.get(entity);
        dead.time += deltaTime;

        if (dead.time >= CORPSE_LIFETIME) {
            getEngine().removeEntity(entity);
        }
    }
}
