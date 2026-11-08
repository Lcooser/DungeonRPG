package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dungeonrpg.game.item.Item;
import com.dungeonrpg.game.item.ItemIcons;

/** Equipped weapon: icon in a frame colored by rarity, with the name and damage above it. */
public class WeaponSlot extends Actor {

    public static final float SIZE = 28f;
    private static final float ICON_SIZE = 22f;
    private static final float TEXT_GAP = 3f;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);
    private static final Color EMPTY_COLOR = new Color(0.5f, 0.5f, 0.55f, 1f);

    private final ItemIcons icons;
    private final NinePatch frame;
    private final TextureRegion pixel;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout shadowLayout = new GlyphLayout();

    private Item shown;

    public WeaponSlot(TextureAtlas atlas, BitmapFont font, ItemIcons icons) {
        this.icons = icons;
        this.font = font;
        this.frame = atlas.createPatch("bar-frame");
        this.pixel = atlas.findRegion("pixel");

        setSize(SIZE, SIZE);
        setItem(null);
    }

    public void setItem(Item item) {
        if (item == shown && layout.runs.size > 0) return;

        shown = item;

        String text = item == null ? "NO WEAPON" : item.getName() + "  +" + item.getDamage();
        Color color = item == null ? EMPTY_COLOR : item.getRarity().getColor();

        layout.setText(font, text, color, 0f, 0, false);
        shadowLayout.setText(font, text, SHADOW_COLOR, 0f, 0, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1f, 1f, 1f, parentAlpha);
        frame.draw(batch, getX(), getY(), SIZE, SIZE);

        if (shown != null) {
            Color color = shown.getRarity().getColor();

            // 1px border in the rarity color, inside the dark frame.
            batch.setColor(color.r, color.g, color.b, parentAlpha);
            batch.draw(pixel, getX() + 1f, getY() + 1f, SIZE - 2f, 1f);
            batch.draw(pixel, getX() + 1f, getY() + SIZE - 2f, SIZE - 2f, 1f);
            batch.draw(pixel, getX() + 1f, getY() + 1f, 1f, SIZE - 2f);
            batch.draw(pixel, getX() + SIZE - 2f, getY() + 1f, 1f, SIZE - 2f);

            batch.setColor(1f, 1f, 1f, parentAlpha);
            batch.draw(icons.get(shown), getX() + (SIZE - ICON_SIZE) / 2f, getY() + (SIZE - ICON_SIZE) / 2f, ICON_SIZE, ICON_SIZE);
        }

        float textY = getY() + SIZE + TEXT_GAP + 8f;

        font.draw(batch, shadowLayout, getX() + 1f, textY - 1f);
        font.draw(batch, layout, getX(), textY);
    }
}
