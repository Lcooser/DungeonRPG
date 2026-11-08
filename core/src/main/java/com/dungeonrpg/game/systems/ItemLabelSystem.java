package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.GroundItemComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Shows the name of dropped items, in the color of their rarity, once the player gets close. */
public class ItemLabelSystem extends IteratingSystem {

    private static final Family LIVING_PLAYERS = Family.all(
        PlayerComponent.class,
        PositionComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    private static final float LABEL_RANGE = 110f;
    private static final float LABEL_OFFSET_Y = 34f;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private final ComponentMapper<GroundItemComponent> groundItems =
        ComponentMapper.getFor(GroundItemComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final OrthographicCamera camera;
    private final BitmapFont font;
    private final SpriteBatch batch = new SpriteBatch();
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout shadowLayout = new GlyphLayout();

    private Entity player;

    public ItemLabelSystem(OrthographicCamera camera, BitmapFont font) {
        super(Family.all(GroundItemComponent.class, PositionComponent.class).get(), 5);

        this.camera = camera;
        this.font = font;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(LIVING_PLAYERS);
        player = players.size() == 0 ? null : players.first();

        if (player == null) return;

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positions.get(entity);
        float distanceX = Combat.centerX(player) - position.x;
        float distanceY = Combat.centerY(player) - position.y;

        if (distanceX * distanceX + distanceY * distanceY > LABEL_RANGE * LABEL_RANGE) return;

        GroundItemComponent ground = groundItems.get(entity);
        String name = ground.item.getName();

        layout.setText(font, name, ground.item.getRarity().getColor(), 0f, Align.left, false);
        shadowLayout.setText(font, name, SHADOW_COLOR, 0f, Align.left, false);

        float x = MathUtils.floor(position.x - layout.width / 2f);
        float y = MathUtils.floor(position.y + LABEL_OFFSET_Y);

        font.draw(batch, shadowLayout, x + 1f, y - 1f);
        font.draw(batch, layout, x, y);
    }

    public void dispose() {
        batch.dispose();
    }
}
