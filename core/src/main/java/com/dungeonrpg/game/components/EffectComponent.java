package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Efeito visual animado. Os que não repetem somem sozinhos quando a animação termina. */
public class EffectComponent implements Component {

    public Animation<TextureRegion> animation;
    public boolean looping;
    public float stateTime;
}
