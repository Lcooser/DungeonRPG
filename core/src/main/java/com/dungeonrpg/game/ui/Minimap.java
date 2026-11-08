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

/** Minimapa com névoa: só mostra o que o jogador já explorou, centrado nele. */
public class Minimap extends Actor {

    private static final int VIEW_TILES_X = 48;
    private static final int VIEW_TILES_Y = 32;
    private static final int PIXELS_PER_TILE = 2;
    private static final int FRAME_BORDER = 3;
    private static final int REVEAL_RADIUS = 7;
    private static final float BLINK_PERIOD = 0.5f;

    private static final int FLOOR_COLOR = Color.rgba8888(0.55f, 0.48f, 0.35f, 1f);
    private static final int WALL_COLOR = Color.rgba8888(0.27f, 0.24f, 0.32f, 1f);

    private final DungeonMap map;
    private final boolean[][] revealed;
    private final Pixmap pixmap;
    private final Texture texture;
    private final NinePatch frame;
    private final TextureRegion pixel;

    private float playerTileX;
    private float playerTileY;
    private int lastRevealX = -1;
    private int lastRevealY = -1;
    private boolean dirty;
    private float time;

    public Minimap(TextureAtlas atlas, DungeonMap map) {
        this.map = map;
        this.revealed = new boolean[map.getWidth()][map.getHeight()];
        this.frame = atlas.createPatch("minimap-frame");
        this.pixel = atlas.findRegion("pixel");

        pixmap = new Pixmap(map.getWidth(), map.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        setSize(
            VIEW_TILES_X * PIXELS_PER_TILE + FRAME_BORDER * 2,
            VIEW_TILES_Y * PIXELS_PER_TILE + FRAME_BORDER * 2
        );
    }

    /** Recebe a posição do jogador em coordenadas de mundo (pixels). */
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
            pixmap.drawPixel(tileX, toPixmapY(tileY), FLOOR_COLOR);
            dirty = true;
        } else if (map.isBorderWall(tileX, tileY)) {
            pixmap.drawPixel(tileX, toPixmapY(tileY), WALL_COLOR);
            dirty = true;
        }
    }

    // O pixmap tem a origem no topo e o mapa tem a origem embaixo.
    private int toPixmapY(int tileY) {
        return map.getHeight() - 1 - tileY;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        time += delta;

        // O envio para a GPU precisa acontecer fora do batch, por isso fica no act.
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

        if (time % (BLINK_PERIOD * 2f) < BLINK_PERIOD) {
            batch.setColor(1f, 0.85f, 0.2f, parentAlpha);
            batch.draw(
                pixel,
                contentX + (playerTileX - windowX) * PIXELS_PER_TILE - 1f,
                contentY + (playerTileY - windowY) * PIXELS_PER_TILE - 1f,
                3f,
                3f
            );
        }

        batch.setColor(1f, 1f, 1f, parentAlpha);
        frame.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
        pixmap.dispose();
    }
}
