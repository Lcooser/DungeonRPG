package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Teclas de teste para exercitar vida e mana até o combate existir: 1 dano, 2 gasta mana (ou stamina sem mana), 3 restaura tudo. */
public class DebugInputSystem extends IteratingSystem {

    private static final float DEBUG_DAMAGE = 15f;
    private static final float DEBUG_RESOURCE_COST = 20f;

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    public DebugInputSystem() {
        super(Family.all(
            PlayerComponent.class,
            HealthComponent.class,
            StaminaComponent.class
        ).get(), 0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = healths.get(entity);
        ManaComponent mana = manas.get(entity);
        StaminaComponent stamina = staminas.get(entity);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            health.pendingDamage += DEBUG_DAMAGE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (mana != null) {
                mana.spend(DEBUG_RESOURCE_COST);
            } else {
                stamina.spend(DEBUG_RESOURCE_COST);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            health.current = health.max;

            if (mana != null) {
                mana.current = mana.max;
            }

            stamina.current = stamina.max;
            stamina.exhausted = false;
            entity.remove(DeadComponent.class);
        }
    }
}
