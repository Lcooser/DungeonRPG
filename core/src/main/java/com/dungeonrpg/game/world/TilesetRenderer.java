package com.dungeonrpg.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/** Draws a dungeon from a 16x8 tileset of 64px tiles, using the theme layout to pick each tile. */
public class TilesetRenderer {

    private static final int SOURCE_TILE = 64;
    private static final int COLUMNS = 16;
    private static final int ROWS = 8;
    private static final int FLOOR_ROW = 0;
    private static final int FACE_ROW = 1;
    private static final int WALL_TOP_ROW = 2;
    private static final float TORCH_FRAME_TIME = 0.14f;

    private final Texture texture;
    private final TextureRegion[][] regions = new TextureRegion[ROWS][COLUMNS];
    private final SpriteBatch batch = new SpriteBatch();
    private final TilesetLayout layout;
    private float time;

    public TilesetRenderer(Theme theme) {
        this.layout = theme.getLayout();
        String path = theme.getTilesetPath();

        // Mipmaps keep the 64px art clean when it is drawn at half size.
        texture = new Texture(Gdx.files.internal(path), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                regions[row][column] = new TextureRegion(
                    texture, column * SOURCE_TILE, row * SOURCE_TILE, SOURCE_TILE, SOURCE_TILE
                );
            }
        }
    }

    public void render(OrthographicCamera camera, DungeonMap map, float deltaTime) {
        time += deltaTime;

        int size = DungeonMap.TILE_SIZE;
        int minX = Math.max(0, MathUtils.floor((camera.position.x - camera.viewportWidth / 2f) / size));
        int maxX = Math.min(map.getWidth() - 1, MathUtils.floor((camera.position.x + camera.viewportWidth / 2f) / size));
        int minY = Math.max(0, MathUtils.floor((camera.position.y - camera.viewportHeight / 2f) / size));
        int maxY = Math.min(map.getHeight() - 1, MathUtils.floor((camera.position.y + camera.viewportHeight / 2f) / size));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                TextureRegion region;
                boolean bossFloor = false;

                if (map.getTile(x, y) == TileType.FLOOR) {
                    region = floor(map, x, y);
                    bossFloor = map.isBossRoom(x, y);
                } else if (map.isBorderWall(x, y)) {
                    region = wall(map, x, y);
                } else {
                    continue;
                }

                batch.setColor(bossFloor ? layout.getBossFloorTint() : Color.WHITE);
                batch.draw(region, x * size, y * size, size, size);
            }
        }

        batch.end();
    }

    private TextureRegion floor(DungeonMap map, int x, int y) {
        int hash = hash(x, y);
        int[] shadows = layout.getShadows();

        // The tile under a wall face gets a soft shadow.
        if (shadows.length > 0 && map.getTile(x, y + 1) == TileType.WALL) {
            return pick(FLOOR_ROW, shadows, hash);
        }

        // A few tiles carry bones, puddles or rubble.
        if (hash % 23 == 0) {
            return pick(FLOOR_ROW, layout.getDecor(), hash / 23);
        }

        return pick(FLOOR_ROW, layout.getFloors(), hash);
    }

    private TextureRegion wall(DungeonMap map, int x, int y) {
        int hash = hash(x, y);

        if (map.getTile(x, y - 1) == TileType.FLOOR) {
            return wallFace(hash);
        }

        int mask = 0;

        if (map.getTile(x, y + 1) == TileType.FLOOR) mask |= 1;
        if (map.getTile(x + 1, y) == TileType.FLOOR) mask |= 2;
        if (map.getTile(x - 1, y) == TileType.FLOOR) mask |= 8;

        return pick(WALL_TOP_ROW, layout.getWallTop(mask), hash);
    }

    private TextureRegion wallFace(int hash) {
        int[] accents = layout.getAccents();

        // Torches or crystals sit on a few faces; torches flicker out of phase so they do not blink together.
        if (hash % 9 == 0) {
            int index = layout.hasAnimatedAccents() ? (int) (time / TORCH_FRAME_TIME) + hash % accents.length : hash / 9;

            return pick(FACE_ROW, accents, index);
        }

        if (hash % 17 == 1) return pick(FACE_ROW, layout.getRareFaces(), hash / 17);

        return pick(FACE_ROW, layout.getFaces(), hash);
    }

    private TextureRegion pick(int row, int[] columns, int index) {
        return regions[row][columns[index % columns.length]];
    }

    // Stable per-tile pseudo random value, so the map does not shimmer between frames.
    private int hash(int x, int y) {
        int value = x * 73856093 ^ y * 19349663;

        return (value ^ (value >>> 13)) & 0x7fffffff;
    }

    public void dispose() {
        batch.dispose();
        texture.dispose();
    }
}
