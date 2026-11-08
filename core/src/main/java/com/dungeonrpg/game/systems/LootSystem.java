package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.LootComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.item.Item;
import com.dungeonrpg.game.item.ItemDrops;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.DeadComponent;

import java.util.Random;

/** Rolls the loot of monsters that just died and drops the result where they fell. */
public class LootSystem extends IteratingSystem {

    private final ComponentMapper<LootComponent> loots =
        ComponentMapper.getFor(LootComponent.class);

    private final ItemDrops drops;
    private final CharacterClass playerClass;
    private final Random random = new Random();

    public LootSystem(ItemDrops drops, CharacterClass playerClass) {
        super(Family.all(
            LootComponent.class,
            DeadComponent.class,
            PositionComponent.class,
            ColliderComponent.class
        ).get(), 1);

        this.drops = drops;
        this.playerClass = playerClass;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Remove first so the roll happens once, no matter how long the corpse stays.
        LootComponent loot = loots.get(entity);
        entity.remove(LootComponent.class);

        Item item = loot.tier.roll(random, playerClass);

        if (item != null) {
            drops.drop(item, Combat.centerX(entity), Combat.centerY(entity));
        }
    }
}
