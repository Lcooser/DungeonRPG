package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.entities.MonsterType;

public class MonsterComponent implements Component {

    public MonsterType type;

    public MonsterComponent(MonsterType type) {
        this.type = type;
    }
}
