package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dungeonrpg.game.world.DungeonMap;
import com.dungeonrpg.game.world.TileType;

/** Fog-of-war minimap centered on the player. The boss room and its skull marker are always visible. */
public class Minimap extends Actor {

    private static final int VIEW_TILES_X = 48;
    private static final int VIEW_TILES_Y = 32;
    private static final int PIXELS_PER_TILE = 2;
    private static final int FRAME_BORDER = 3;
    private static final int REVEAL_RADIUS = 7;
    private static final int ICON_SIZE = 10;
    private static final Color ICON_OUTLINE_COLOR = new Color(0.9f, 0.75f, 0.35f, 1f);

    private static final int FLOOR_COLOR = Color.rgba8888(0.55f, 0.48f, 0.35f, 1f);
    private static final int BOSS_FLOOR_COLOR = Color.rgba8888(0.6f, 0.2f, 0.2f, 1f);
    private static final float MARKER_PULSE_SPEED = 4f;
    private static final int WALL_COLOR = Color.rgba8888(0.27f, 0.24f, 0.32f, 1f);

    private final DungeonMap map;
    private final boolean[][] revealed;
    private final Pixmap pixmap;
    private final Texture texture;
    private final NinePatch frame;
    private final TextureRegion pixel;
    private final TextureRegion bossMarker;
    private final Texture playerIcon;

    private float playerTileX;
    private float playerTileY;
    private int lastRevealX = -1;
    private int lastRevealY = -1;
    private boolean dirty;
    private boolean bossAlive;
    private float time;

    public Minimap(TextureAtlas atlas, DungeonMap map, Texture playerIcon) {
        this.map = map;
        this.playerIcon = playerIcon;
        this.revealed = new boolean[map.getWidth()][map.getHeight()];
        this.frame = atlas.createPatch("minimap-frame");
        this.pixel = atlas.findRegion("pixel");
        this.bossMarker = atlas.findRegion("boss-marker");

        pixmap = new Pixmap(map.getWidth(), map.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        paintBossRoom();
        bossAlive = map.getBossRoom() != null;

        texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        setSize(
            VIEW_TILES_X * PIXELS_PER_TILE + FRAME_BORDER * 2,
            VIEW_TILES_Y * PIXELS_PER_TILE + FRAME_BORDER * 2
        );
    }

    // The boss room is drawn from the start so the player knows where to head.
    private void paintBossRoom() {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.isBossRoom(x, y) && map.getTile(x, y) == TileType.FLOOR) {
                    pixmap.drawPixel(x, toPixmapY(y), BOSS_FLOOR_COLOR);
                }
            }
        }
    }

    public void setBossAlive(boolean alive) {
        bossAlive = alive && map.getBossRoom() != null;
    }

    /** Takes the player position in world coordinates (pixels). */
    public void updatePlayer(float worldX, float worldY) {
        playerTileX = worldX / DungeonMap.TILE_SIZE;
        playerTileY = worldY / DungeonMap.TILE_SIZE;

        int tileX = (int) playerTileX;
        int tileY = (int) playerTileY;

        if (tileX != lastRevealX || tileY != lastRevealY) {
            reveal(tileX, tileY);
            lastRevealX = tileX;
            lastRevealY = tileY;
        }
    }

    private void reveal(int centerX, int centerY) {
        for (int offsetX = -REVEAL_RADIUS; offsetX <= REVEAL_RADIUS; offsetX++) {
            for (int offsetY = -REVEAL_RADIUS; offsetY <= REVEAL_RADIUS; offsetY++) {
                if (offsetX * offsetX + offsetY * offsetY > REVEAL_RADIUS * REVEAL_RADIUS) continue;

                revealTile(centerX + offsetX, centerY + offsetY);
            }
        }
    }

    private void revealTile(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= map.getWidth() || tileY >= map.getHeight()) return;
        if (revealed[tileX][tileY]) return;

        revealed[tileX][tileY] = true;

        if (map.getTile(tileX, tileY) == TileType.FLOOR) {
            pixmap.drawPixel(tileX, toPixmapY(tileY), map.isBossRoom(tileX, tileY) ? BOSS_FLOOR_COLOR : FLOOR_COLOR);
            dirty = true;
        } else if (map.isBorderWall(tileX, tileY)) {
            pixmap.drawPixel(tileX, toPixmapY(tileY), WALL_COLOR);
            dirty = true;
        }
    }

    // The pixmap origin is at the top while the map origin is at the bottom.
    private int toPixmapY(int tileY) {
        return map.getHeight() - 1 - tileY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time += delta;

        // The GPU upload has to happen outside the batch, so it lives in act.
        if (dirty) {
            texture.draw(pixmap, 0, 0);
            dirty = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int viewTilesX = Math.min(VIEW_TILES_X, map.getWidth());
        int viewTilesY = Math.min(VIEW_TILES_Y, map.getHeight());

        int windowX = MathUtils.clamp(Math.round(playerTileX) - viewTilesX / 2, 0, map.getWidth() - viewTilesX);
        int windowY = MathUtils.clamp(Math.round(playerTileY) - viewTilesY / 2, 0, map.getHeight() - viewTilesY);

        float contentX = getX() + FRAME_BORDER;
        float contentY = getY() + FRAME_BORDER;
        float contentWidth = viewTilesX * PIXELS_PER_TILE;
        float contentHeight = viewTilesY * PIXELS_PER_TILE;

        batch.setColor(0f, 0f, 0f, 0.7f * parentAlpha);
        batch.draw(pixel, contentX, contentY, contentWidth, contentHeight);

        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(
            texture,
            contentX, contentY, contentWidth, contentHeight,
            windowX, map.getHeight() - (windowY + viewTilesY), viewTilesX, viewTilesY,
            false, false
        );

        if (bossAlive) {
            drawBossMarker(batch, parentAlpha, windowX, windowY, contentX, contentY, contentWidth, contentHeight);
        }

        drawPlayerIcon(batch, parentAlpha, windowX, windowY, contentX, contentY, contentWidth, contentHeight);

        batch.setColor(1f, 1f, 1f, parentAlpha);
        frame.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    // When the boss room is outside the visible window the skull sticks to the minimap edge, pointing the way.
    private void drawBossMarker(
        Batch batch,
        float parentAlpha,
        int windowX,
        int windowY,
        float contentX,
        float contentY,
        float contentWidth,
        float contentHeight
    ) {
        float size = bossMarker.getRegionWidth();
        float tileX = map.getBossRoom().centerX() + 0.5f;
        float tileY = map.getBossRoom().centerY() + 0.5f;

        float markerX = MathUtils.clamp(
            MathUtils.floor(contentX + (tileX - windowX) * PIXELS_PER_TILE - size / 2f),
            contentX,
            contentX + contentWidth - size
        );

        float markerY = MathUtils.clamp(
            MathUtils.floor(contentY + (tileY - windowY) * PIXELS_PER_TILE - size / 2f),
            contentY,
            contentY + contentHeight - size
        );

        float pulse = 0.7f + 0.3f * MathUtils.sin(time * MARKER_PULSE_SPEED);

        batch.setColor(1f, 1f, 1f, parentAlpha * pulse);
        batch.draw(bossMarker, markerX, markerY);
    }

    // The character face marks the position, with a border to stand out from the map.
    private void drawPlayerIcon(
        Batch batch,
        float parentAlpha,
        int windowX,
        int windowY,
        float contentX,
        float contentY,
        float contentWidth,
        float contentHeight
    ) {
        float outlined = ICON_SIZE + 2f;

        float iconX = MathUtils.clamp(
            MathUtils.floor(contentX + (playerTileX - windowX) * PIXELS_PER_TILE - outlined / 2f),
            contentX,
            contentX + contentWidth - outlined
        );

        float iconY = MathUtils.clamp(
            MathUtils.floor(contentY + (playerTileY - windowY) * PIXELS_PER_TILE - outlined / 2f),
            contentY,
            contentY + contentHeight - outlined
        );

        batch.setColor(ICON_OUTLINE_COLOR.r, ICON_OUTLINE_COLOR.g, ICON_OUTLINE_COLOR.b, parentAlpha);
        batch.draw(pixel, iconX, iconY, outlined, outlined);

        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(playerIcon, iconX + 1f, iconY + 1f, ICON_SIZE, ICON_SIZE);
    }

    public void dispose() {
        texture.dispose();
        pixmap.dispose();
    }
}
