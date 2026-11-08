package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.dungeonrpg.game.components.FloatingTextComponent;
import com.dungeonrpg.game.components.PositionComponent;

/** Desenha os números de dano, que sobem e somem. A fonte pertence a quem criou o sistema. */
public class FloatingTextSystem extends IteratingSystem {

    // O texto só começa a sumir na segunda metade da vida.
    private static final float FADE_RATIO = 0.5f;

    private final ComponentMapper<FloatingTextComponent> texts =
        ComponentMapper.getFor(FloatingTextComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final OrthographicCamera camera;
    private final BitmapFont font;
    private final SpriteBatch batch = new SpriteBatch();
    private final GlyphLayout shadowLayout = new GlyphLayout();
    private final Color shadowColor = new Color(0f, 0f, 0f, 1f);
    private final Color textColor = new Color();

    public FloatingTextSystem(OrthographicCamera camera, BitmapFont font) {
        super(Family.all(FloatingTextComponent.class, PositionComponent.class).get(), 5);

        this.camera = camera;
        this.font = font;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FloatingTextComponent text = texts.get(entity);
        PositionComponent position = positions.get(entity);

        text.timeLeft -= deltaTime;
        position.y += FloatingTextComponent.RISE_SPEED * deltaTime;

        if (text.timeLeft <= 0f) {
            getEngine().removeEntity(entity);
            return;
        }

        float alpha = MathUtils.clamp(text.timeLeft / (FloatingTextComponent.DURATION * FADE_RATIO), 0f, 1f);

        textColor.set(text.color.r, text.color.g, text.color.b, alpha);
        shadowColor.a = alpha;

        text.layout.setText(font, text.text, textColor, 0f, Align.left, false);
        shadowLayout.setText(font, text.text, shadowColor, 0f, Align.left, false);

        float x = MathUtils.floor(position.x - text.layout.width / 2f);
        float y = MathUtils.floor(position.y);

        font.draw(batch, shadowLayout, x + 1f, y - 1f);
        font.draw(batch, text.layout, x, y);
    }

    public void dispose() {
        batch.dispose();
    }
}
