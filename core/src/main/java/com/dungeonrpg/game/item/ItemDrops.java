package com.dungeonrpg.game.item;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.dungeonrpg.game.components.GroundItemComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;

/** Puts items on the floor: the weapon icon over a glow in the color of its rarity. */
public class ItemDrops {

    private static final float ICON_SIZE = 36f;
    private static final float GLOW_SIZE = 64f;
    private static final float GLOW_ALPHA = 0.75f;
    // Glows sit under every character, icons are sorted with them.
    private static final int GLOW_LAYER = -1;

    private final Engine engine;
    private final ItemIcons icons;

    public ItemDrops(Engine engine, ItemIcons icons) {
        this.engine = engine;
        this.icons = icons;
    }

    public void drop(Item item, float x, float y) {
        Entity glow = new Entity();

        PositionComponent glowPosition = new PositionComponent();
        glowPosition.x = x;
        glowPosition.y = y;

        TextureComponent glowSprite = new TextureComponent();
        glowSprite.region = icons.getGlow();
        glowSprite.width = GLOW_SIZE;
        glowSprite.height = GLOW_SIZE * 0.6f;
        glowSprite.offsetX = -GLOW_SIZE / 2f;
        glowSprite.offsetY = -GLOW_SIZE * 0.3f;
        glowSprite.layer = GLOW_LAYER;

        Color color = item.getRarity().getColor();
        glowSprite.tint = new Color(color.r, color.g, color.b, GLOW_ALPHA);

        glow.add(glowPosition);
        glow.add(glowSprite);
        engine.addEntity(glow);

        Entity entity = new Entity();

        PositionComponent position = new PositionComponent();
        position.x = x;
        position.y = y;

        TextureComponent sprite = new TextureComponent();
        sprite.region = icons.get(item);
        sprite.width = ICON_SIZE;
        sprite.height = ICON_SIZE;
        sprite.offsetX = -ICON_SIZE / 2f;
        sprite.offsetY = 0f;

        entity.add(position);
        entity.add(sprite);
        entity.add(new GroundItemComponent(item, glow));
        engine.addEntity(entity);
    }
}
