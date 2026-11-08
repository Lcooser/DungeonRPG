package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.tags.DeadComponent;

/** Barra de vida sobre a cabeça dos monstros, visível só depois que eles levam dano. */
public class MonsterHealthBarSystem extends IteratingSystem {

    private static final float VISIBLE_TIME = 3f;
    private static final float FADE_TIME = 0.5f;
    private static final float BAR_WIDTH = 24f;
    private static final float BAR_HEIGHT = 4f;
    private static final float BORDER = 1f;
    // Posição da barra na altura do sprite, logo acima da cabeça.
    private static final float HEAD_RATIO = 0.9f;

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final OrthographicCamera camera;
    private final SpriteBatch batch = new SpriteBatch();
    private final Texture pixel;

    public MonsterHealthBarSystem(OrthographicCamera camera) {
        super(Family.all(
            MonsterComponent.class,
            HealthComponent.class,
            PositionComponent.class,
            TextureComponent.class
        ).exclude(DeadComponent.class).get(), 5);

        this.camera = camera;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
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
        HealthComponent health = healths.get(entity);

        if (health.timeSinceDamage > VISIBLE_TIME) return;

        PositionComponent position = positions.get(entity);
        TextureComponent sprite = textures.get(entity);

        float alpha = MathUtils.clamp((VISIBLE_TIME - health.timeSinceDamage) / FADE_TIME, 0f, 1f);
        float ratio = MathUtils.clamp(health.current / health.max, 0f, 1f);

        float x = MathUtils.floor(position.x + sprite.offsetX + sprite.width / 2f - BAR_WIDTH / 2f);
        float y = MathUtils.floor(position.y + sprite.offsetY + sprite.height * HEAD_RATIO);

        // Contorno escuro, fundo, vida e um brilho no topo.
        batch.setColor(0.07f, 0.05f, 0.05f, alpha);
        batch.draw(pixel, x - BORDER, y - BORDER, BAR_WIDTH + BORDER * 2f, BAR_HEIGHT + BORDER * 2f);

        batch.setColor(0.3f, 0.07f, 0.07f, alpha);
        batch.draw(pixel, x, y, BAR_WIDTH, BAR_HEIGHT);

        float fill = MathUtils.floor(BAR_WIDTH * ratio);

        batch.setColor(0.8f, 0.15f, 0.15f, alpha);
        batch.draw(pixel, x, y, fill, BAR_HEIGHT);

        batch.setColor(0.95f, 0.45f, 0.4f, alpha);
        batch.draw(pixel, x, y + BAR_HEIGHT - 1f, fill, 1f);

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        batch.dispose();
        pixel.dispose();
    }
}
