package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.EffectComponent;
import com.dungeonrpg.game.components.TextureComponent;

public class EffectSystem extends IteratingSystem {

    private final ComponentMapper<EffectComponent> effects =
        ComponentMapper.getFor(EffectComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    public EffectSystem() {
        super(Family.all(EffectComponent.class, TextureComponent.class).get(), 2);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EffectComponent effect = effects.get(entity);
        effect.stateTime += deltaTime;

        textures.get(entity).region = effect.animation.getKeyFrame(effect.stateTime, effect.looping);

        if (!effect.looping && effect.animation.isAnimationFinished(effect.stateTime)) {
            getEngine().removeEntity(entity);
        }
    }
}
