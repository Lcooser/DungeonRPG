package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PixelButton extends Actor {

    private static final float GLYPH_HEIGHT = 7f;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private final NinePatch normal;
    private final NinePatch highlighted;
    private final BitmapFont font;
    private final GlyphLayout textLayout = new GlyphLayout();
    private final GlyphLayout shadowLayout = new GlyphLayout();
    private final ClickListener listener;
    private final String text;

    private float layoutAlpha = -1f;
    private boolean selected;

    public PixelButton(TextureAtlas atlas, BitmapFont font, String text, float width, float height, Runnable onClick) {
        this.normal = atlas.createPatch("button");
        this.highlighted = atlas.createPatch("button-over");
        this.font = font;
        this.text = text;

        setSize(width, height);

        listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        };

        addListener(listener);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Glyph colors are baked into the layout, so fading needs the alpha applied there.
    private void updateLayouts(float alpha) {
        if (alpha == layoutAlpha) return;

        layoutAlpha = alpha;

        textLayout.setText(font, text, new Color(1f, 1f, 1f, alpha), getWidth(), Align.center, false);
        shadowLayout.setText(
            font,
            text,
            new Color(SHADOW_COLOR.r, SHADOW_COLOR.g, SHADOW_COLOR.b, SHADOW_COLOR.a * alpha),
            getWidth(),
            Align.center,
            false
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = parentAlpha * getColor().a;

        batch.setColor(1f, 1f, 1f, alpha);
        updateLayouts(alpha);

        (selected || listener.isOver() ? highlighted : normal).draw(batch, getX(), getY(), getWidth(), getHeight());

        // The line box has 1px of space above the glyph.
        float textTop = getY() + MathUtils.floor((getHeight() + GLYPH_HEIGHT) / 2f) + 1f;

        font.draw(batch, shadowLayout, getX() + 1f, textTop - 1f);
        font.draw(batch, textLayout, getX(), textTop);
    }
}
