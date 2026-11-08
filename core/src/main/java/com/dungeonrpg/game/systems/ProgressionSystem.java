package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Keeps the player's movement speed in sync with agility, which changes when stat points are spent. */
public class ProgressionSystem extends IteratingSystem {

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final float speedMultiplier;

    public ProgressionSystem(float speedMultiplier) {
        super(Family.all(
            PlayerComponent.class,
            StatsComponent.class,
            VelocityComponent.class
        ).get(), 1);

        this.speedMultiplier = speedMultiplier;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        velocities.get(entity).speed = StatsCalculator.moveSpeed(stats.get(entity)) * speedMultiplier;
    }
}
