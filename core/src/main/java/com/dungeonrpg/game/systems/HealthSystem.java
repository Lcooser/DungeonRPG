package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.audio.SoundId;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.DeadComponent;

public class HealthSystem extends IteratingSystem {

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final Effects effects;

    public HealthSystem(Effects effects) {
        super(Family.all(HealthComponent.class).exclude(DeadComponent.class).get(), 1);

        this.effects = effects;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = healths.get(entity);

        health.timeSinceDamage += deltaTime;

        if (health.pendingDamage > 0f) {
            applyDamage(entity, health);
        }

        if (health.current <= 0f) {
            die(entity);
        }
    }

    private void applyDamage(Entity entity, HealthComponent health) {
        StatsComponent entityStats = stats.get(entity);

        float damage = entityStats == null
            ? health.pendingDamage
            : StatsCalculator.reduceDamage(health.pendingDamage, entityStats);

        health.current = Math.max(0f, health.current - damage);
        health.pendingDamage = 0f;
        health.timeSinceDamage = 0f;

        entity.add(new HurtComponent());
        effects.spawnHit(entity, damage);
        effects.playSound(SoundId.HIT);
    }

    private void die(Entity entity) {
        VelocityComponent velocity = velocities.get(entity);

        if (velocity != null) {
            velocity.x = 0f;
            velocity.y = 0f;
        }

        AttackComponent attack = attacks.get(entity);

        if (attack != null) {
            attack.attacking = false;
            attack.requestedSlot = -1;
        }

        entity.add(new DeadComponent());
        effects.playSound(SoundId.DEATH);
    }
}
