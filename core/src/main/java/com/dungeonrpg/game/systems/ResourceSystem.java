package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.tags.DeadComponent;

public class ResourceSystem extends IteratingSystem {

    private static final float MANA_REGEN_DELAY = 1f;
    private static final float STAMINA_REGEN_DELAY = 0.75f;
    private static final float STAMINA_RECOVERY_RATIO = 0.25f;

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    public ResourceSystem() {
        super(Family.all(
            StatsComponent.class,
            HealthComponent.class,
            StaminaComponent.class
        ).exclude(DeadComponent.class).get(), 1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StatsComponent entityStats = stats.get(entity);
        HealthComponent health = healths.get(entity);
        ManaComponent mana = manas.get(entity);
        StaminaComponent stamina = staminas.get(entity);

        // Os máximos acompanham os atributos, que mudam com equipamentos e nível.
        health.max = StatsCalculator.maxHealth(entityStats);
        health.current = Math.min(health.current, health.max);

        // Nem toda classe usa mana.
        if (mana != null) {
            regenerateMana(mana, entityStats, deltaTime);
        }

        stamina.max = StatsCalculator.maxStamina(entityStats);
        stamina.current = Math.min(stamina.current, stamina.max);
        stamina.timeSinceUse += deltaTime;

        if (stamina.timeSinceUse >= STAMINA_REGEN_DELAY) {
            stamina.current = Math.min(
                stamina.max,
                stamina.current + StatsCalculator.staminaRegen(entityStats) * deltaTime
            );
        }

        if (stamina.exhausted && stamina.current >= stamina.max * STAMINA_RECOVERY_RATIO) {
            stamina.exhausted = false;
        }
    }

    private void regenerateMana(ManaComponent mana, StatsComponent entityStats, float deltaTime) {
        mana.max = StatsCalculator.maxMana(entityStats);
        mana.timeSinceUse += deltaTime;

        if (mana.timeSinceUse >= MANA_REGEN_DELAY) {
            mana.current = Math.min(
                mana.max,
                mana.current + StatsCalculator.manaRegen(entityStats) * deltaTime
            );
        }

        mana.current = Math.min(mana.current, mana.max);
    }
}
