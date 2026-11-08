package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

/** XP granted once when the entity dies, removed after it is paid out. */
public class XpRewardComponent implements Component {

    public int amount;

    public XpRewardComponent(int amount) {
        this.amount = amount;
    }
}
