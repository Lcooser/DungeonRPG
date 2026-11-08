package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.item.Item;

public class EquipmentComponent implements Component {

    // Null while nothing is equipped.
    public Item weapon;
}
