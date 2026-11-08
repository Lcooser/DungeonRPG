package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.dungeonrpg.game.world.DungeonMap;

/** Arrow that points at the boss with the distance in tiles, shown under the minimap. */
public class BossCompass extends Actor {

    public static final float HEIGHT = 15f;
    private static final float TEXT_GAP = 4f;
    private static final float NEARBY_TILES = 8f;
    private static final Color TEXT_COLOR = new Color(1f, 0.55f, 0.5f, 1f);
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private final TextureRegion arrow;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout shadowLayout = new GlyphLayout();

    private boolean active;
    private float angle;
    private int shownTiles = -1;

    public BossCompass(TextureAtlas atlas, BitmapFont font, float width) {
        this.arrow = atlas.findRegion("arrow");
        this.font = font;

        setSize(width, HEIGHT);
    }

    /** Positions are in world pixels. */
    public void update(float playerX, float playerY, float bossX, float bossY, boolean active) {
        this.active = active;

        float dx = bossX - playerX;
        float dy = bossY - playerY;

        angle = (float) Math.toDegrees(Math.atan2(dy, dx));

        int tiles = Math.round((float) Math.sqrt(dx * dx + dy * dy) / DungeonMap.TILE_SIZE);

        if (tiles != shownTiles) {
            shownTiles = tiles;

            String text = tiles <= NEARBY_TILES ? "BOSS NEARBY" : "BOSS " + tiles;
            layout.setText(font, text, TEXT_COLOR, 0f, Align.left, false);
            shadowLayout.setText(font, text, SHADOW_COLOR, 0f, Align.left, false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!active) return;

        batch.setColor(1f, 1f, 1f, parentAlpha);

        float size = arrow.getRegionWidth();
        float arrowY = getY() + (HEIGHT - size) / 2f;

        batch.draw(arrow, getX(), arrowY, size / 2f, size / 2f, size, size, 1f, 1f, angle);

        float textX = getX() + size + TEXT_GAP;
        float textTop = getY() + HEIGHT / 2f + 4f;

        font.draw(batch, shadowLayout, textX + 1f, textTop - 1f);
        font.draw(batch, layout, textX, textTop);
    }
}
