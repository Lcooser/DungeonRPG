package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

public class PlayerInputSystem extends IteratingSystem {

    private static final float SPRINT_MULTIPLIER = 1.6f;
    private static final float SPRINT_STAMINA_PER_SECOND = 30f;

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final Vector2 direction = new Vector2();

    public PlayerInputSystem() {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class
        ).exclude(DeadComponent.class).get(), 0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = velocities.get(entity);

        direction.set(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1;

        direction.nor();

        float speed = velocity.speed;

        if (isSprinting(entity, deltaTime)) {
            speed *= SPRINT_MULTIPLIER;
        }

        velocity.x = direction.x * speed;
        velocity.y = direction.y * speed;
    }

    private boolean isSprinting(Entity entity, float deltaTime) {
        StaminaComponent stamina = staminas.get(entity);

        boolean wantsToSprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && !direction.isZero()
            && stamina != null
            && !stamina.exhausted;

        if (!wantsToSprint) return false;

        if (!stamina.spend(SPRINT_STAMINA_PER_SECOND * deltaTime)) {
            stamina.exhausted = true;
            return false;
        }

        return true;
    }
}
