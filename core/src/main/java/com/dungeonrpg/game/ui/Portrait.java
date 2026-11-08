package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Character face inside a brass frame. The frame art is 40px, drawn at any integer multiple. */
public class Portrait extends Actor {

    public static final float DEFAULT_SIZE = 40f;
    private static final float FRAME_ART_SIZE = 40f;
    private static final float FRAME_ART_BORDER = 4f;

    private final TextureRegion frame;
    private final Texture face;

    public Portrait(TextureAtlas atlas, Texture face) {
        this(atlas, face, DEFAULT_SIZE);
    }

    public Portrait(TextureAtlas atlas, Texture face, float size) {
        this.frame = atlas.findRegion("portrait-frame");
        this.face = face;

        setSize(size, size);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float border = FRAME_ART_BORDER * getWidth() / FRAME_ART_SIZE;

        batch.setColor(1f, 1f, 1f, parentAlpha);

        batch.draw(face, getX() + border, getY() + border, getWidth() - border * 2f, getHeight() - border * 2f);
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
