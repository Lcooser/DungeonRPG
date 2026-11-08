package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.animation.Direction;

public class FacingComponent implements Component {

    public Direction direction = Direction.DOWN;
}
