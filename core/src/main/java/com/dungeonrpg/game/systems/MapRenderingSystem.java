package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.world.DungeonMap;
import com.dungeonrpg.game.world.TileType;

public class MapRenderingSystem extends EntitySystem {

    private final OrthographicCamera camera;
    private final DungeonMap map;
    private final ShapeRenderer shapeRenderer;

    public MapRenderingSystem(OrthographicCamera camera, DungeonMap map) {
        super(3);

        this.camera = camera;
        this.map = map;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        int tileSize = DungeonMap.TILE_SIZE;

        int minTileX = Math.max(0, MathUtils.floor((camera.position.x - camera.viewportWidth / 2f) / tileSize));
        int maxTileX = Math.min(map.getWidth() - 1, MathUtils.floor((camera.position.x + camera.viewportWidth / 2f) / tileSize));
        int minTileY = Math.max(0, MathUtils.floor((camera.position.y - camera.viewportHeight / 2f) / tileSize));
        int maxTileY = Math.min(map.getHeight() - 1, MathUtils.floor((camera.position.y + camera.viewportHeight / 2f) / tileSize));

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = minTileX; x <= maxTileX; x++) {
            for (int y = minTileY; y <= maxTileY; y++) {
                if (map.getTile(x, y) == TileType.FLOOR) {
                    boolean alternate = (x + y) % 2 == 0;

                    if (map.isBossRoom(x, y)) {
                        shapeRenderer.setColor(alternate ? 0.3f : 0.27f, alternate ? 0.14f : 0.12f, 0.13f, 1f);
                    } else {
                        shapeRenderer.setColor(alternate ? 0.22f : 0.2f, alternate ? 0.19f : 0.17f, 0.16f, 1f);
                    }
                } else if (map.isBorderWall(x, y)) {
                    shapeRenderer.setColor(0.38f, 0.38f, 0.46f, 1f);
                } else {
                    continue;
                }

                shapeRenderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
