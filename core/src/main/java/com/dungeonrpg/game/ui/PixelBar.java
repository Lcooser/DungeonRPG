package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

/** Barra de recurso pixelada, com o valor escrito dentro. */
public class PixelBar extends Actor {

    private static final float FRAME_INSET = 2f;
    private static final float SMOOTHING_SPEED = 10f;
    // A fonte tem 7px de altura, o texto fica no miolo de uma barra de 11px.
    private static final float TEXT_TOP_OFFSET = 9f;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private final NinePatch frame;
    private final TextureRegion fill;
    private final BitmapFont font;
    private final GlyphLayout textLayout = new GlyphLayout();
    private final GlyphLayout shadowLayout = new GlyphLayout();

    private float current;
    private float max = 1f;
    // A barra anima até o valor real, o recurso em si muda na hora.
    private float displayed;
    private boolean initialized;

    private int shownCurrent = -1;
    private int shownMax = -1;

    public PixelBar(TextureAtlas atlas, String fillName, BitmapFont font, float width, float height) {
        this.frame = atlas.createPatch("bar-frame");
        this.fill = atlas.findRegion(fillName);
        this.font = font;

        setSize(width, height);
    }

    public void setValue(float current, float max) {
        this.current = current;
        this.max = Math.max(max, 1f);

        if (!initialized) {
            displayed = current;
            initialized = true;
        }

        int currentText = MathUtils.floor(current);
        int maxText = MathUtils.floor(this.max);

        // O texto só é remontado quando o número exibido muda, para não gerar lixo a cada frame.
        if (currentText != shownCurrent || maxText != shownMax) {
            shownCurrent = currentText;
            shownMax = maxText;

            String text = currentText + "/" + maxText;
            textLayout.setText(font, text, Color.WHITE, getWidth(), Align.center, false);
            shadowLayout.setText(font, text, SHADOW_COLOR, getWidth(), Align.center, false);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        displayed += (current - displayed) * Math.min(1f, delta * SMOOTHING_SPEED);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1f, 1f, 1f, parentAlpha);

        frame.draw(batch, getX(), getY(), getWidth(), getHeight());

        float ratio = MathUtils.clamp(displayed / max, 0f, 1f);
        float fillWidth = MathUtils.floor((getWidth() - FRAME_INSET * 2f) * ratio);

        if (fillWidth > 0f) {
            batch.draw(fill, getX() + FRAME_INSET, getY() + FRAME_INSET, fillWidth, getHeight() - FRAME_INSET * 2f);
        }

        float textTop = getY() + TEXT_TOP_OFFSET;

        font.draw(batch, shadowLayout, getX() + 1f, textTop - 1f);
        font.draw(batch, textLayout, getX(), textTop);
    }
}
