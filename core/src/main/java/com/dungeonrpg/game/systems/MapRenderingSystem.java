package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.world.DungeonMap;
import com.dungeonrpg.game.world.Theme;
import com.dungeonrpg.game.world.TileType;
import com.dungeonrpg.game.world.TilesetRenderer;

public class MapRenderingSystem extends EntitySystem {

    private final OrthographicCamera camera;
    private final DungeonMap map;
    private final Theme theme;
    // Themes without tileset art fall back to flat colored tiles.
    private final TilesetRenderer tileset;
    private final ShapeRenderer shapeRenderer;

    public MapRenderingSystem(OrthographicCamera camera, DungeonMap map, Theme theme) {
        super(3);

        this.camera = camera;
        this.map = map;
        this.theme = theme;
        this.tileset = theme.getTilesetPath() == null ? null : new TilesetRenderer(theme);
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        if (tileset != null) {
            tileset.render(camera, map, deltaTime);
            return;
        }

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
                    // Checkerboard: every other tile is slightly darker.
                    float shade = (x + y) % 2 == 0 ? 1f : 0.9f;

                    if (map.isBossRoom(x, y)) {
                        shapeRenderer.setColor(0.3f * shade, 0.13f * shade, 0.12f * shade, 1f);
                    } else {
                        Color floor = theme.getFloorColor();

                        shapeRenderer.setColor(floor.r * shade, floor.g * shade, floor.b * shade, 1f);
                    }
                } else if (map.isBorderWall(x, y)) {
                    shapeRenderer.setColor(theme.getWallColor());
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

        if (tileset != null) {
            tileset.dispose();
        }
    }
}
