package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.BuffComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.SlowComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.StunComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.world.DungeonMap;

/** Runs attacks and skills for players and monsters: cost, cooldown, timing and effects. */
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

    // The melee box is wider than the reach so slightly off-line targets still get hit.
    private static final float HIT_WIDTH_RATIO = 1.5f;
    private static final float SLASH_FORWARD_RATIO = 0.55f;
    private static final float EFFECT_HEIGHT = 10f;
    private static final float PROJECTILE_SPAWN_DISTANCE = 14f;
    // Half of a typical hitbox, so a target counts when its body touches the blast, not only its center.
    private static final float TARGET_RADIUS = 10f;
    private static final float BLINK_STEP = 8f;

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<ColliderComponent> colliders =
        ComponentMapper.getFor(ColliderComponent.class);

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

    private final ComponentMapper<StunComponent> stuns =
        ComponentMapper.getFor(StunComponent.class);

    private final ComponentMapper<BuffComponent> buffs =
        ComponentMapper.getFor(BuffComponent.class);

    private final Effects effects;
    private final DungeonMap map;
    private final Rectangle hitBox = new Rectangle();
    private final Rectangle targetBox = new Rectangle();

    public AttackSystem(Effects effects, DungeonMap map) {
        super(Family.all(
            AttackComponent.class,
            FacingComponent.class,
            PositionComponent.class,
            ColliderComponent.class
        ).exclude(DeadComponent.class).get(), 1);

        this.effects = effects;
        this.map = map;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttackComponent attack = attacks.get(entity);

        for (int slot = 0; slot < attack.cooldowns.length; slot++) {
            attack.cooldowns[slot] = Math.max(0f, attack.cooldowns[slot] - deltaTime);
        }

        // Monsters stagger when hit and everyone stops when stunned, but the player keeps acting while
        // surrounded, otherwise a group of enemies could lock the character out of fighting back.
        if (hurts.has(entity) && !players.has(entity) || stuns.has(entity)) {
            attack.attacking = false;
            attack.requestedSlot = -1;
            return;
        }

        if (!attack.attacking) {
            tryStart(entity, attack);
            return;
        }

        stop(entity);
        attack.time += deltaTime;

        Ability ability = attack.current;

        while (attack.hitsDone < ability.getHits()
            && attack.time >= ability.getWindup() + attack.hitsDone * ability.getHitInterval()) {
            attack.hitsDone++;
            perform(entity, attack, ability);
        }

        if (attack.time >= ability.getDuration()) {
            attack.attacking = false;
        }
    }

    private void tryStart(Entity entity, AttackComponent attack) {
        int slot = attack.requestedSlot;
        attack.requestedSlot = -1;

        if (slot < 0 || slot >= attack.abilities.length) return;

        Ability ability = attack.abilities[slot];

        if (attack.cooldowns[slot] > 0f || !pay(entity, ability)) return;

        attack.cooldowns[slot] = ability.getCooldown();

        // Buffs and blinks are instant, the character keeps moving and no attack animation plays.
        if (ability.getKind() == Ability.Kind.BUFF) {
            applyBuff(entity, ability);
            return;
        }

        if (ability.getKind() == Ability.Kind.TELEPORT) {
            blink(entity, ability);
            return;
        }

        attack.current = ability;
        attack.attacking = true;
        attack.hitsDone = 0;
        attack.time = 0f;

        stop(entity);

        if (ability.isEffectAttached() && ability.getEffect() != null) {
            effects.spawnAttached(ability.getEffect(), entity, EFFECT_HEIGHT);
        }
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

    private void perform(Entity entity, AttackComponent attack, Ability ability) {
        float damage = damageOf(entity, attack, ability);

        switch (ability.getKind()) {
            case MELEE:
                meleeHit(entity, ability, damage);
                break;
            case AREA:
                areaHit(entity, ability, damage);
                break;
            default:
                launchProjectile(entity, ability, damage);
        }
    }

    private float damageOf(Entity entity, AttackComponent attack, Ability ability) {
        float damage = ability.getDamageMultiplier() > 0f
            ? rawDamage(entity, attack.primary()) * ability.getDamageMultiplier()
            : rawDamage(entity, ability);

        BuffComponent buff = buffs.get(entity);

        if (buff != null && ability.getDamageType() == Ability.DamageType.PHYSICAL) {
            damage *= buff.damageMultiplier;
        }

        return damage;
    }

    private float rawDamage(Entity entity, Ability ability) {
        StatsComponent entityStats = stats.get(entity);

        if (entityStats == null) return ability.getBaseDamage();

        return ability.getDamageType() == Ability.DamageType.PHYSICAL
            ? StatsCalculator.physicalDamage(entityStats, ability.getBaseDamage())
            : StatsCalculator.magicalDamage(entityStats, ability.getBaseDamage());
    }

    private ImmutableArray<Entity> targetsOf(Entity entity) {
        return getEngine().getEntitiesFor(players.has(entity) ? MONSTERS : PLAYERS);
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

        for (Entity target : targetsOf(entity)) {
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

    // Circular blast around the caster, or around a point in front of it.
    private void areaHit(Entity entity, Ability ability, float damage) {
        Direction direction = facings.get(entity).direction;
        float casterX = Combat.centerX(entity);
        float casterY = Combat.centerY(entity);
        float blastX = casterX + direction.getX() * ability.getForward();
        float blastY = casterY + direction.getY() * ability.getForward();
        float reach = ability.getRange() + TARGET_RADIUS;

        for (Entity target : targetsOf(entity)) {
            float offsetX = Combat.centerX(target) - blastX;
            float offsetY = Combat.centerY(target) - blastY;
            float distance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);

            if (distance > reach) continue;

            // Push away from the blast center, falling back to the facing when the target is right on it.
            float pushX = distance > 0f ? offsetX / distance : direction.getX();
            float pushY = distance > 0f ? offsetY / distance : direction.getY();

            Combat.hit(target, damage, pushX, pushY, ability.getKnockback());

            if (ability.getStunSeconds() > 0f) {
                target.add(new StunComponent(ability.getStunSeconds()));
            }

            if (ability.getSlowSeconds() > 0f) {
                target.add(new SlowComponent(ability.getSlowFactor(), ability.getSlowSeconds()));
            }
        }

        if (ability.getEffect() != null && !ability.isEffectAttached()) {
            effects.spawn(ability.getEffect(), blastX, blastY + EFFECT_HEIGHT, 0f);
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

    private void applyBuff(Entity entity, Ability ability) {
        BuffComponent buff = buffs.get(entity);

        // Reusing an active buff refreshes it instead of stacking a second aura.
        if (buff == null) {
            buff = new BuffComponent();
            buff.aura = effects.spawnAttached(ability.getEffect(), entity, EFFECT_HEIGHT);
            entity.add(buff);
        }

        buff.timeLeft = ability.getBuffSeconds();
        buff.damageMultiplier = ability.getBuffDamage();
        buff.speedMultiplier = ability.getBuffSpeed();
    }

    // Moves in the direction being walked (or faced when standing still) until the distance or a wall is reached.
    private void blink(Entity entity, Ability ability) {
        VelocityComponent velocity = velocities.get(entity);
        Direction facing = facings.get(entity).direction;

        float directionX = facing.getX();
        float directionY = facing.getY();

        if (velocity != null && (velocity.x != 0f || velocity.y != 0f)) {
            float length = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
            directionX = velocity.x / length;
            directionY = velocity.y / length;
        }

        PositionComponent position = positions.get(entity);
        ColliderComponent collider = colliders.get(entity);

        effects.spawn(ability.getEffect(), Combat.centerX(entity), Combat.centerY(entity) + EFFECT_HEIGHT, 0f);

        float travelled = 0f;

        while (travelled + BLINK_STEP <= ability.getBlinkDistance()) {
            float nextX = position.x + directionX * BLINK_STEP;
            float nextY = position.y + directionY * BLINK_STEP;

            if (map.collides(nextX + collider.offsetX, nextY + collider.offsetY, collider.width, collider.height)) break;

            position.x = nextX;
            position.y = nextY;
            travelled += BLINK_STEP;
        }

        effects.spawn(ability.getEffect(), Combat.centerX(entity), Combat.centerY(entity) + EFFECT_HEIGHT, 0f);
    }
}
