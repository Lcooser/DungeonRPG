package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class LevelComponent implements Component {

    public int level = 1;
    public int xp;
    // Unspent points the player can put into any attribute.
    public int statPoints;
}
