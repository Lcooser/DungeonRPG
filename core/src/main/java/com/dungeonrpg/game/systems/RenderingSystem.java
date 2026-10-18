package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

import java.awt.*;

public class RenderingSystem extends IteratingSystem {

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ShapeRenderer renderer;

    public RenderingSystem(OrthographicCamera camera) {
        super(Family.all(
            PositionComponent.class,
            PlayerComponent.class
        ).get(), 2);

        renderer = new ShapeRenderer();
        renderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update(float deltaTime) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        super.update(deltaTime);
        renderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positions.get(entity);

        renderer.setColor(0.9f, 0.6f, 0.2f, 1f);
        renderer.rect(position.x, position.y, 32, 32);
    }

    public void dispose(){
        renderer.dispose();
    }

}
