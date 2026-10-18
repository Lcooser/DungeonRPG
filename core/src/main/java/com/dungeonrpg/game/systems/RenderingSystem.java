package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;

public class RenderingSystem extends IteratingSystem {

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final OrthographicCamera camera;
    private final SpriteBatch batch;

    public RenderingSystem(OrthographicCamera camera) {
        super(Family.all(
            PositionComponent.class,
            TextureComponent.class
        ).get(), 2);

        this.camera = camera;
        this.batch = new SpriteBatch();
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positions.get(entity);
        TextureComponent sprite = textures.get(entity);

        batch.draw(
            sprite.texture,
            position.x,
            position.y,
            sprite.width,
            sprite.height
        );
    }

    public void dispose() {
        batch.dispose();
    }
}
