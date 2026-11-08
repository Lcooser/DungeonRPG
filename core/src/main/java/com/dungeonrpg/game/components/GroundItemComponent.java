package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dungeonrpg.game.item.Item;

/** An item lying on the floor, with the glow entity drawn under it. */
public class GroundItemComponent implements Component {

    public Item item;
    public Entity glow;
    public float time;
    // Set once the player walked over a weapon that was not an upgrade, so the hint is shown only once.
    public boolean hinted;

    public GroundItemComponent(Item item, Entity glow) {
        this.item = item;
        this.glow = glow;
    }
}
