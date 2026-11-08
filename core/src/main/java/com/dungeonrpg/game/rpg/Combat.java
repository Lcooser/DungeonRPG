package com.dungeonrpg.game.rpg;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.KnockbackComponent;
import com.dungeonrpg.game.components.PositionComponent;

/** Funções compartilhadas pelos sistemas de combate. */
public final class Combat {

    private static final ComponentMapper<PositionComponent> POSITIONS =
        ComponentMapper.getFor(PositionComponent.class);

    private static final ComponentMapper<ColliderComponent> COLLIDERS =
        ComponentMapper.getFor(ColliderComponent.class);

    private static final ComponentMapper<HealthComponent> HEALTHS =
        ComponentMapper.getFor(HealthComponent.class);

    private Combat() {
    }

    /** Caixa de colisão da entidade em coordenadas de mundo. */
    public static Rectangle bounds(Entity entity, Rectangle out) {
        PositionComponent position = POSITIONS.get(entity);
        ColliderComponent collider = COLLIDERS.get(entity);

        return out.set(
            position.x + collider.offsetX,
            position.y + collider.offsetY,
            collider.width,
            collider.height
        );
    }

    public static float centerX(Entity entity) {
        ColliderComponent collider = COLLIDERS.get(entity);

        return POSITIONS.get(entity).x + collider.offsetX + collider.width / 2f;
    }

    public static float centerY(Entity entity) {
        ColliderComponent collider = COLLIDERS.get(entity);

        return POSITIONS.get(entity).y + collider.offsetY + collider.height / 2f;
    }

    /** Registra o dano bruto, que o HealthSystem aplica com a defesa, e empurra o alvo para longe. */
    public static void hit(Entity target, float damage, float directionX, float directionY, float knockback) {
        HealthComponent health = HEALTHS.get(target);

        health.pendingDamage += damage;

        if (knockback > 0f) {
            target.add(new KnockbackComponent(directionX * knockback, directionY * knockback));
        }
    }
}
