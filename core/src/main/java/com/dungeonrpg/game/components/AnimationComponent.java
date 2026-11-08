package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.animation.AnimationType;
import com.dungeonrpg.game.animation.CharacterAnimations;

public class AnimationComponent implements Component {

    public CharacterAnimations animations;
    public AnimationType type = AnimationType.IDLE;
    public float stateTime;
}
