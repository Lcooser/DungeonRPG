package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.item.LootTier;

/** Marks an entity that rolls for loot once when it dies. */
public class LootComponent implements Component {

    public LootTier tier;

    public LootComponent(LootTier tier) {
        this.tier = tier;
    }
}
