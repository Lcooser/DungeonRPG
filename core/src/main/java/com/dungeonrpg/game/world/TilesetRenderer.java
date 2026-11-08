package com.dungeonrpg.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * Draws a dungeon from a 16x8 tileset of 64px tiles.
 * Row 0: floors (0-7), decor (8-11), wall shadows (12-15). Row 1: wall faces (0-3), torch frames (4-7),
 * cracked wall (8), cobweb (15). Row 2: wall-top autotile.
 */
public class TilesetRenderer {

    private static final int SOURCE_TILE = 64;
    private static final int COLUMNS = 16;
    private static final int ROWS = 8;

    private static final int FLOOR_VARIANTS = 8;
    private static final int DECOR_FIRST = 8;
    private static final int DECOR_COUNT = 4;
    private static final int SHADOW_FIRST = 12;
    private static final int SHADOW_COUNT = 4;
    private static final int FACE_VARIANTS = 4;
    private static final int TORCH_FIRST = 4;
    private static final int TORCH_FRAMES = 4;
    private static final int CRACKED_FACE = 8;
    private static final int COBWEB_FACE = 15;
    private static final float TORCH_FRAME_TIME = 0.14f;

    // Wall-top tile per mask of floor neighbors (bit 1 north, 2 east, 8 west). South is never set, because a
    // wall with floor to its south draws its face instead. Picked by matching the edge lines of each tile.
    private static final int[][] WALL_TOP = new int[16][];

    static {
        WALL_TOP[0] = new int[]{3, 8};
        WALL_TOP[1] = new int[]{10, 1};
        WALL_TOP[2] = new int[]{7};
        WALL_TOP[3] = new int[]{10};
        WALL_TOP[8] = new int[]{4};
        WALL_TOP[9] = new int[]{5};
        WALL_TOP[10] = new int[]{4};
        WALL_TOP[11] = new int[]{5};
    }

    private static final Color BOSS_FLOOR = new Color(1f, 0.62f, 0.58f, 1f);

    private final Texture texture;
    private final TextureRegion[][] regions = new TextureRegion[ROWS][COLUMNS];
    private final SpriteBatch batch = new SpriteBatch();
    private float time;

    public TilesetRenderer(String path) {
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

                batch.setColor(bossFloor ? BOSS_FLOOR : Color.WHITE);
                batch.draw(region, x * size, y * size, size, size);
            }
        }

        batch.end();
    }

    private TextureRegion floor(DungeonMap map, int x, int y) {
        int hash = hash(x, y);

        // The tile under a wall face gets a soft shadow.
        if (map.getTile(x, y + 1) == TileType.WALL) {
            return regions[0][SHADOW_FIRST + hash % SHADOW_COUNT];
        }

        // A few tiles carry bones, puddles or rubble.
        if (hash % 23 == 0) {
            return regions[0][DECOR_FIRST + (hash / 23) % DECOR_COUNT];
        }

        return regions[0][hash % FLOOR_VARIANTS];
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

        int[] options = WALL_TOP[mask];

        return regions[2][options[hash % options.length]];
    }

    private TextureRegion wallFace(int hash) {
        // Torches burn on a few faces, out of phase so they do not flicker together.
        if (hash % 9 == 0) {
            int frame = (int) (time / TORCH_FRAME_TIME + hash) % TORCH_FRAMES;

            return regions[1][TORCH_FIRST + frame];
        }

        if (hash % 17 == 1) return regions[1][CRACKED_FACE];
        if (hash % 29 == 2) return regions[1][COBWEB_FACE];

        return regions[1][hash % FACE_VARIANTS];
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
