package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Controla o ataque de jogador e monstros: custo, animação, momento do golpe e dano. */
public class AttackSystem extends IteratingSystem {

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

    // A área do golpe é mais larga que o alcance, para acertar alvos um pouco fora da linha.
    private static final float HIT_WIDTH_RATIO = 1.5f;
    private static final float SLASH_FORWARD_RATIO = 0.55f;
    private static final float EFFECT_HEIGHT = 10f;
    private static final float PROJECTILE_SPAWN_DISTANCE = 14f;

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<PlayerComponent> players =
        ComponentMapper.getFor(PlayerComponent.class);

    private final ComponentMapper<HurtComponent> hurts =
        ComponentMapper.getFor(HurtComponent.class);

    private final Effects effects;
    private final Rectangle hitBox = new Rectangle();
    private final Rectangle targetBox = new Rectangle();

    public AttackSystem(Effects effects) {
        super(Family.all(
            AttackComponent.class,
            FacingComponent.class,
            PositionComponent.class,
            ColliderComponent.class
        ).exclude(DeadComponent.class).get(), 1);

        this.effects = effects;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttackComponent attack = attacks.get(entity);
        attack.cooldownLeft = Math.max(0f, attack.cooldownLeft - deltaTime);

        // Levar um golpe interrompe o ataque em andamento.
        if (hurts.has(entity)) {
            attack.attacking = false;
            attack.requested = null;
            return;
        }

        if (!attack.attacking) {
            tryStart(entity, attack);
            return;
        }

        stop(entity);
        attack.time += deltaTime;

        if (!attack.hitDone && attack.time >= attack.current.getWindup()) {
            attack.hitDone = true;
            perform(entity, attack.current);
        }

        if (attack.time >= AttackComponent.DURATION) {
            attack.attacking = false;
        }
    }

    private void tryStart(Entity entity, AttackComponent attack) {
        Ability requested = attack.requested;
        attack.requested = null;

        if (requested == null || attack.cooldownLeft > 0f || !pay(entity, requested)) return;

        attack.current = requested;
        attack.attacking = true;
        attack.hitDone = false;
        attack.time = 0f;
        attack.cooldownLeft = requested.getCooldown();

        stop(entity);
    }

    private boolean pay(Entity entity, Ability ability) {
        switch (ability.getCostType()) {
            case STAMINA:
                StaminaComponent stamina = staminas.get(entity);
                return stamina != null && stamina.spend(ability.getCost());
            case MANA:
                ManaComponent mana = manas.get(entity);
                return mana != null && mana.spend(ability.getCost());
            default:
                return true;
        }
    }

    private void stop(Entity entity) {
        VelocityComponent velocity = velocities.get(entity);

        if (velocity != null) {
            velocity.x = 0f;
            velocity.y = 0f;
        }
    }

    private void perform(Entity entity, Ability ability) {
        float damage = damageOf(entity, ability);

        if (ability.getKind() == Ability.Kind.MELEE) {
            meleeHit(entity, ability, damage);
        } else {
            launchProjectile(entity, ability, damage);
        }
    }

    private float damageOf(Entity entity, Ability ability) {
        StatsComponent entityStats = stats.get(entity);

        if (entityStats == null) return ability.getBaseDamage();

        return ability.getDamageType() == Ability.DamageType.PHYSICAL
            ? StatsCalculator.physicalDamage(entityStats, ability.getBaseDamage())
            : StatsCalculator.magicalDamage(entityStats, ability.getBaseDamage());
    }

    private void meleeHit(Entity entity, Ability ability, float damage) {
        Direction direction = facings.get(entity).direction;
        float directionX = direction.getX();
        float directionY = direction.getY();

        float centerX = Combat.centerX(entity);
        float centerY = Combat.centerY(entity);
        float reach = ability.getRange();
        float width = reach * HIT_WIDTH_RATIO;

        if (directionX != 0f) {
            hitBox.set(directionX > 0f ? centerX : centerX - reach, centerY - width / 2f, reach, width);
        } else {
            hitBox.set(centerX - width / 2f, directionY > 0f ? centerY : centerY - reach, width, reach);
        }

        ImmutableArray<Entity> targets = getEngine().getEntitiesFor(players.has(entity) ? MONSTERS : PLAYERS);

        for (Entity target : targets) {
            if (hitBox.overlaps(Combat.bounds(target, targetBox))) {
                Combat.hit(target, damage, directionX, directionY, ability.getKnockback());
            }
        }

        if (ability.getEffect() != null) {
            effects.spawn(
                ability.getEffect(),
                centerX + directionX * reach * SLASH_FORWARD_RATIO,
                centerY + directionY * reach * SLASH_FORWARD_RATIO + EFFECT_HEIGHT,
                direction.getAngle()
            );
        }
    }

    private void launchProjectile(Entity entity, Ability ability, float damage) {
        Direction direction = facings.get(entity).direction;

        effects.spawnProjectile(
            ability,
            players.has(entity),
            damage,
            Combat.centerX(entity) + direction.getX() * PROJECTILE_SPAWN_DISTANCE,
            Combat.centerY(entity) + direction.getY() * PROJECTILE_SPAWN_DISTANCE,
            direction
        );
    }
}
