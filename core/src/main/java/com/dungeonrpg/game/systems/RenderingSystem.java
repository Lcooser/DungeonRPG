package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.SlowComponent;
import com.dungeonrpg.game.components.StunComponent;
import com.dungeonrpg.game.components.TextureComponent;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {

    private static final Color HURT_TINT = new Color(1f, 0.45f, 0.45f, 1f);
    private static final Color STUN_TINT = new Color(1f, 1f, 0.45f, 1f);
    private static final Color SLOW_TINT = new Color(0.55f, 0.8f, 1f, 1f);

    private static final ComponentMapper<PositionComponent> POSITIONS =
        ComponentMapper.getFor(PositionComponent.class);

    private static final ComponentMapper<TextureComponent> TEXTURES =
        ComponentMapper.getFor(TextureComponent.class);

    private final ComponentMapper<HurtComponent> hurts =
        ComponentMapper.getFor(HurtComponent.class);

    private final ComponentMapper<StunComponent> stuns =
        ComponentMapper.getFor(StunComponent.class);

    private final ComponentMapper<SlowComponent> slows =
        ComponentMapper.getFor(SlowComponent.class);

    private final OrthographicCamera camera;
    private final SpriteBatch batch;

    public RenderingSystem(OrthographicCamera camera) {
        super(Family.all(
            PositionComponent.class,
            TextureComponent.class
        ).get(), new DepthComparator(), 4);

        this.camera = camera;
        this.batch = new SpriteBatch();
    }

    @Override
    public void update(float deltaTime) {
        // As entidades se movem, então a ordem de desenho é refeita a cada frame.
        forceSort();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = POSITIONS.get(entity);
        TextureComponent sprite = TEXTURES.get(entity);

        if (sprite.region == null) return;

        batch.setColor(tintOf(entity));

        float x = position.x + sprite.offsetX;
        float y = position.y + sprite.offsetY;

        if (sprite.rotation == 0f) {
            batch.draw(sprite.region, x, y, sprite.width, sprite.height);
        } else {
            batch.draw(
                sprite.region,
                x, y,
                sprite.width / 2f, sprite.height / 2f,
                sprite.width, sprite.height,
                1f, 1f,
                sprite.rotation
            );
        }
    }

    // Hit flash wins over the status tints so damage is always readable.
    private Color tintOf(Entity entity) {
        if (hurts.has(entity)) return HURT_TINT;
        if (stuns.has(entity)) return STUN_TINT;
        if (slows.has(entity)) return SLOW_TINT;

        return Color.WHITE;
    }

    public void dispose() {
        batch.dispose();
    }

    /** Camadas maiores por cima e, na mesma camada, quem está mais abaixo na tela é desenhado depois. */
    private static class DepthComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity first, Entity second) {
            int layers = Integer.compare(TEXTURES.get(first).layer, TEXTURES.get(second).layer);

            if (layers != 0) return layers;

            return Float.compare(POSITIONS.get(second).y, POSITIONS.get(first).y);
        }
    }
}
