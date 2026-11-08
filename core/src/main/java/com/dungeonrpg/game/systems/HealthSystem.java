package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.DeadComponent;

public class HealthSystem extends IteratingSystem {

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    public HealthSystem() {
        super(Family.all(HealthComponent.class).exclude(DeadComponent.class).get(), 1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = healths.get(entity);

        if (health.pendingDamage > 0f) {
            StatsComponent entityStats = stats.get(entity);

            float damage = entityStats == null
                ? health.pendingDamage
                : StatsCalculator.reduceDamage(health.pendingDamage, entityStats);

            health.current = Math.max(0f, health.current - damage);
            health.pendingDamage = 0f;
        }

        if (health.current <= 0f) {
            die(entity);
        }
    }

    private void die(Entity entity) {
        VelocityComponent velocity = velocities.get(entity);

        if (velocity != null) {
            velocity.x = 0f;
            velocity.y = 0f;
        }

        entity.add(new DeadComponent());
    }
}
