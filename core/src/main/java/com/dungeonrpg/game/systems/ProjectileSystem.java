package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.ProjectileComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.world.DungeonMap;

public class ProjectileSystem extends IteratingSystem {

    private static final Family PLAYERS = Family.all(
        PlayerComponent.class,
        HealthComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    private static final Family MONSTERS = Family.all(
        MonsterComponent.class,
        HealthComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    private final ComponentMapper<ProjectileComponent> projectiles =
        ComponentMapper.getFor(ProjectileComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final DungeonMap map;
    private final Effects effects;
    private final Rectangle projectileBox = new Rectangle();
    private final Rectangle targetBox = new Rectangle();

    public ProjectileSystem(DungeonMap map, Effects effects) {
        super(Family.all(ProjectileComponent.class, PositionComponent.class).get(), 1);

        this.map = map;
        this.effects = effects;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ProjectileComponent projectile = projectiles.get(entity);
        PositionComponent position = positions.get(entity);

        Ability ability = projectile.ability;
        float step = ability.getProjectileSpeed() * deltaTime;
        position.x += projectile.directionX * step;
        position.y += projectile.directionY * step;
        projectile.timeLeft -= deltaTime;
        projectile.distanceTravelled += step;

        float size = ability.getHitSize();
        projectileBox.set(position.x - size / 2f, position.y - size / 2f, size, size);

        if (map.collides(projectileBox.x, projectileBox.y, projectileBox.width, projectileBox.height)) {
            impact(entity, projectile, position, null);
            return;
        }

        Family targets = projectile.fromPlayer ? MONSTERS : PLAYERS;

        for (Entity target : getEngine().getEntitiesFor(targets)) {
            if (projectileBox.overlaps(Combat.bounds(target, targetBox))) {
                impact(entity, projectile, position, target);
                return;
            }
        }

        // Out of reach without hitting anything: the spell burns out instead of flying forever.
        if (projectile.distanceTravelled >= ability.getFlightRange() || projectile.timeLeft <= 0f) {
            if (ability.getFizzleEffect() != null) {
                effects.spawn(ability.getFizzleEffect(), position.x, position.y, 0f);
            }

            getEngine().removeEntity(entity);
        }
    }

    private void impact(Entity entity, ProjectileComponent projectile, PositionComponent position, Entity directTarget) {
        Ability ability = projectile.ability;
        float radius = ability.getRange();

        if (radius > 0f) {
            Family targets = projectile.fromPlayer ? MONSTERS : PLAYERS;

            for (Entity target : getEngine().getEntitiesFor(targets)) {
                float distanceX = Combat.centerX(target) - position.x;
                float distanceY = Combat.centerY(target) - position.y;

                if (distanceX * distanceX + distanceY * distanceY <= radius * radius) {
                    Combat.hit(target, projectile.damage, projectile.directionX, projectile.directionY, ability.getKnockback());
                }
            }
        } else if (directTarget != null) {
            Combat.hit(directTarget, projectile.damage, projectile.directionX, projectile.directionY, ability.getKnockback());
        }

        if (ability.getImpactEffect() != null) {
            effects.spawn(ability.getImpactEffect(), position.x, position.y, 0f);
        }

        getEngine().removeEntity(entity);
    }
}
