package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
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
import com.dungeonrpg.game.entities.MonsterType;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/**
 * Monster behavior: melee monsters chase and strike, ranged ones keep their distance and shoot, and
 * bosses add their special abilities on top.
 */
public class MonsterAiSystem extends IteratingSystem {

    private static final Family LIVING_PLAYERS = Family.all(
        PlayerComponent.class,
        PositionComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    // Margin so a monster starts its melee swing slightly before touching.
    private static final float ATTACK_MARGIN = 4f;
    // Ranged monsters shoot a little inside their projectile range, so shots do not fizzle at the edge.
    private static final float SHOOT_RANGE_RATIO = 0.85f;
    // A ranged monster backs off when the player gets closer than this part of its preferred range.
    private static final float RETREAT_RATIO = 0.55f;
    private static final float RETREAT_SPEED = 0.8f;
    private static final float DECISION_INTERVAL = 0.6f;
    private static final float SPECIAL_CHANCE = 0.65f;
    // Self-centered blasts are only worth casting when the player is inside them (plus a margin).
    private static final float AREA_MARGIN = 30f;

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

    private final ComponentMapper<MonsterComponent> monsters =
        ComponentMapper.getFor(MonsterComponent.class);

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
        AiComponent ai = ais.get(entity);

        velocity.x = 0f;
        velocity.y = 0f;
        ai.decisionTimer -= deltaTime;

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(LIVING_PLAYERS);

        if (players.size() == 0 || attack.attacking || hurts.has(entity) || stuns.has(entity)) return;

        Entity player = players.first();

        float distanceX = Combat.centerX(player) - Combat.centerX(entity);
        float distanceY = Combat.centerY(player) - Combat.centerY(entity);
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance > ai.aggroRange || distance == 0f) return;

        float aimX = distanceX / distance;
        float aimY = distanceY / distance;

        // The monster always looks at the player, even while backing away.
        facings.get(entity).direction = Direction.fromVector(distanceX, distanceY);
        attack.aimX = aimX;
        attack.aimY = aimY;
        attack.targetX = Combat.centerX(player);
        attack.targetY = Combat.centerY(player);

        MonsterType type = monsters.get(entity).type;

        if (attack.abilities.length > 1 && ai.decisionTimer <= 0f) {
            ai.decisionTimer = DECISION_INTERVAL;

            int special = pickSpecial(attack, distance);

            if (special > 0 && MathUtils.random() < SPECIAL_CHANCE) {
                attack.requestedSlot = special;
                return;
            }
        }

        Ability basic = attack.primary();
        boolean ranged = basic.getKind() != Ability.Kind.MELEE;
        float reach = ranged ? basic.getFlightRange() * SHOOT_RANGE_RATIO : basic.getRange() + ATTACK_MARGIN;

        SlowComponent slow = slows.get(entity);
        float speed = slow == null ? velocity.speed : velocity.speed * slow.factor;

        if (type.isRanged() && distance < type.getPreferredRange() * RETREAT_RATIO) {
            velocity.x = -aimX * speed * RETREAT_SPEED;
            velocity.y = -aimY * speed * RETREAT_SPEED;
            return;
        }

        if (distance <= reach) {
            attack.requestedSlot = 0;
            return;
        }

        velocity.x = aimX * speed;
        velocity.y = aimY * speed;
    }

    // Returns a ready special ability that makes sense at this distance, or -1.
    private int pickSpecial(AttackComponent attack, float distance) {
        int[] ready = new int[attack.abilities.length];
        int count = 0;

        for (int slot = 1; slot < attack.abilities.length; slot++) {
            if (attack.cooldowns[slot] > 0f || !isUseful(attack.abilities[slot], distance)) continue;

            ready[count++] = slot;
        }

        return count == 0 ? -1 : ready[MathUtils.random(count - 1)];
    }

    private boolean isUseful(Ability ability, float distance) {
        switch (ability.getKind()) {
            case AREA:
                return ability.isTargeted() || distance <= ability.getRange() + AREA_MARGIN;
            case PROJECTILE:
                return distance <= ability.getFlightRange();
            default:
                return true;
        }
    }
}
