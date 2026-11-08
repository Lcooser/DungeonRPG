package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.KnockbackComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.world.DungeonMap;

public class MovementSystem  extends IteratingSystem {

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<ColliderComponent> colliders =
        ComponentMapper.getFor(ColliderComponent.class);

    private final ComponentMapper<KnockbackComponent> knockbacks =
        ComponentMapper.getFor(KnockbackComponent.class);

    private final DungeonMap map;

    public MovementSystem(DungeonMap map){
        super(Family.all(
            PositionComponent.class,
            VelocityComponent.class
        ).get(), 1);

        this.map = map;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positions.get(entity);
        VelocityComponent velocity = velocities.get(entity);
        ColliderComponent collider = colliders.get(entity);

        float velocityX = velocity.x;
        float velocityY = velocity.y;

        // O empurrão de um golpe substitui o movimento normal e perde força até acabar.
        KnockbackComponent knockback = knockbacks.get(entity);

        if (knockback != null) {
            float strength = knockback.timeLeft / KnockbackComponent.DURATION;

            velocityX = knockback.x * strength;
            velocityY = knockback.y * strength;
            knockback.timeLeft -= deltaTime;

            if (knockback.timeLeft <= 0f) {
                entity.remove(KnockbackComponent.class);
            }
        }

        // Cada eixo é resolvido separadamente para o personagem deslizar nas paredes.
        float nextX = position.x + velocityX * deltaTime;

        if (!collidesAt(collider, nextX, position.y)) {
            position.x = nextX;
        }

        float nextY = position.y + velocityY * deltaTime;

        if (!collidesAt(collider, position.x, nextY)) {
            position.y = nextY;
        }
    }

    private boolean collidesAt(ColliderComponent collider, float x, float y) {
        if (collider == null) return false;

        return map.collides(
            x + collider.offsetX,
            y + collider.offsetY,
            collider.width,
            collider.height
        );
    }

}
