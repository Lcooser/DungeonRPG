package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.audio.SoundId;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.EquipmentComponent;
import com.dungeonrpg.game.components.GroundItemComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.item.Equipment;
import com.dungeonrpg.game.item.Item;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Makes dropped items bob and equips them when the player walks over an upgrade. */
public class PickupSystem extends IteratingSystem {

    private static final Family LIVING_PLAYERS = Family.all(
        PlayerComponent.class,
        EquipmentComponent.class,
        PositionComponent.class,
        ColliderComponent.class
    ).exclude(DeadComponent.class).get();

    private static final float PICKUP_RADIUS = 22f;
    private static final float BOB_SPEED = 3f;
    private static final float BOB_HEIGHT = 3f;
    private static final float ICON_LIFT = 6f;
    private static final float TEXT_OFFSET_Y = 44f;
    private static final Color NOT_BETTER_COLOR = new Color(0.65f, 0.65f, 0.7f, 1f);

    private final ComponentMapper<GroundItemComponent> groundItems =
        ComponentMapper.getFor(GroundItemComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final Effects effects;

    public PickupSystem(Effects effects) {
        super(Family.all(GroundItemComponent.class, PositionComponent.class, TextureComponent.class).get(), 2);

        this.effects = effects;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GroundItemComponent ground = groundItems.get(entity);
        PositionComponent position = positions.get(entity);

        ground.time += deltaTime;
        textures.get(entity).offsetY = ICON_LIFT + MathUtils.sin(ground.time * BOB_SPEED) * BOB_HEIGHT;

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(LIVING_PLAYERS);

        if (players.size() == 0) return;

        Entity player = players.first();
        float distanceX = Combat.centerX(player) - position.x;
        float distanceY = Combat.centerY(player) - position.y;

        if (distanceX * distanceX + distanceY * distanceY > PICKUP_RADIUS * PICKUP_RADIUS) return;

        Item item = ground.item;
        float textX = Combat.centerX(player);
        float textY = Combat.centerY(player) + TEXT_OFFSET_Y;

        if (Equipment.isUpgrade(player, item)) {
            Equipment.equip(player, item);

            effects.spawnText(textX, textY, item.getName(), item.getRarity().getColor());
            effects.playSound(SoundId.EQUIP);

            getEngine().removeEntity(ground.glow);
            getEngine().removeEntity(entity);
        } else if (!ground.hinted) {
            ground.hinted = true;
            effects.spawnText(textX, textY, "NOT AN UPGRADE", NOT_BETTER_COLOR);
        }
    }
}
