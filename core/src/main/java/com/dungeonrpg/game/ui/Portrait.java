package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Rosto do personagem dentro de uma moldura de latão. */
public class Portrait extends Actor {

    private static final float FRAME_SIZE = 40f;
    private static final float FRAME_BORDER = 4f;

    private final TextureRegion frame;
    private final Texture face;

    public Portrait(TextureAtlas atlas, Texture face) {
        this.frame = atlas.findRegion("portrait-frame");
        this.face = face;

        setSize(FRAME_SIZE, FRAME_SIZE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1f, 1f, 1f, parentAlpha);

        batch.draw(
            face,
            getX() + FRAME_BORDER,
            getY() + FRAME_BORDER,
            FRAME_SIZE - FRAME_BORDER * 2f,
            FRAME_SIZE - FRAME_BORDER * 2f
        );

        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
