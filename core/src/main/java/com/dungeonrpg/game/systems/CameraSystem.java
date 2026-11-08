package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.world.DungeonMap;

public class CameraSystem extends IteratingSystem {

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final OrthographicCamera camera;
    private final DungeonMap map;

    public CameraSystem(OrthographicCamera camera, DungeonMap map) {
        super(Family.all(
            PlayerComponent.class,
            PositionComponent.class,
            TextureComponent.class
        ).get(), 2);

        this.camera = camera;
        this.map = map;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positions.get(entity);
        TextureComponent sprite = textures.get(entity);

        float halfViewportWidth = camera.viewportWidth / 2f;
        float halfViewportHeight = camera.viewportHeight / 2f;

        camera.position.x = MathUtils.clamp(
            position.x + sprite.offsetX + sprite.width / 2f,
            halfViewportWidth,
            map.getPixelWidth() - halfViewportWidth
        );

        camera.position.y = MathUtils.clamp(
            position.y + sprite.offsetY + sprite.height / 2f,
            halfViewportHeight,
            map.getPixelHeight() - halfViewportHeight
        );

        camera.update();
    }
}
