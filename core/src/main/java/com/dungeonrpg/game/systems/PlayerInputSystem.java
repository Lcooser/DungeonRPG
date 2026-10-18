package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

public class PlayerInputSystem extends IteratingSystem {

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final Vector2 direction = new Vector2();

    public PlayerInputSystem() {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class
        ).get(), 0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = velocities.get(entity);

        direction.set(0,0);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) direction.x += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) direction.x -= 1;

        direction.nor();

        velocity.x = direction.x * velocity.speed;
        velocity.y = direction.y * velocity.speed;
    }
}
