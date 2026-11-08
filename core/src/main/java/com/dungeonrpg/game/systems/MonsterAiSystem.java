package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AiComponent;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.SlowComponent;
import com.dungeonrpg.game.components.StunComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** IA simples: o monstro persegue o jogador quando o vê e ataca quando está ao alcance. */
public class MonsterAiSystem extends IteratingSystem {

    private static final Family LIVING_PLAYERS = Family.all(
        PlayerComponent.class,
        PositionComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    // Folga para o monstro começar o golpe um pouco antes de encostar.
    private static final float ATTACK_MARGIN = 4f;

    private final ComponentMapper<AiComponent> ais =
        ComponentMapper.getFor(AiComponent.class);

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<HurtComponent> hurts =
        ComponentMapper.getFor(HurtComponent.class);

    private final ComponentMapper<StunComponent> stuns =
        ComponentMapper.getFor(StunComponent.class);

    private final ComponentMapper<SlowComponent> slows =
        ComponentMapper.getFor(SlowComponent.class);

    public MonsterAiSystem() {
        super(Family.all(
            MonsterComponent.class,
            AiComponent.class,
            AttackComponent.class,
            FacingComponent.class,
            VelocityComponent.class,
            PositionComponent.class,
            ColliderComponent.class
        ).exclude(DeadComponent.class).get(), 0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = velocities.get(entity);
        AttackComponent attack = attacks.get(entity);

        velocity.x = 0f;
        velocity.y = 0f;

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(LIVING_PLAYERS);

        if (players.size() == 0 || attack.attacking || hurts.has(entity) || stuns.has(entity)) return;

        Entity player = players.first();

        float distanceX = Combat.centerX(player) - Combat.centerX(entity);
        float distanceY = Combat.centerY(player) - Combat.centerY(entity);
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance > ais.get(entity).aggroRange || distance == 0f) return;

        if (distance <= attack.primary().getRange() + ATTACK_MARGIN) {
            facings.get(entity).direction = Direction.fromVector(distanceX, distanceY);
            attack.requestedSlot = 0;
            return;
        }

        SlowComponent slow = slows.get(entity);
        float speed = slow == null ? velocity.speed : velocity.speed * slow.factor;

        velocity.x = distanceX / distance * speed;
        velocity.y = distanceY / distance * speed;
    }
}
