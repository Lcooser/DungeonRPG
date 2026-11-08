package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.animation.AnimationType;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AnimationComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<AnimationComponent> animations =
        ComponentMapper.getFor(AnimationComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    public AnimationSystem() {
        super(Family.all(
            AnimationComponent.class,
            FacingComponent.class,
            VelocityComponent.class,
            TextureComponent.class
        ).get(), 2);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AnimationComponent animation = animations.get(entity);
        FacingComponent facing = facings.get(entity);
        VelocityComponent velocity = velocities.get(entity);
        TextureComponent sprite = textures.get(entity);

        boolean moving = velocity.x != 0 || velocity.y != 0;

        if (moving) {
            facing.direction = directionOf(velocity);
        }

        changeType(animation, moving ? AnimationType.WALK : AnimationType.IDLE);

        animation.stateTime += deltaTime;

        sprite.region = animation.animations
            .get(animation.type, facing.direction)
            .getKeyFrame(animation.stateTime);
    }

    private Direction directionOf(VelocityComponent velocity) {
        // Na diagonal o lado é preferido, porque o sprite de perfil lê melhor em movimento.
        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            return velocity.x > 0 ? Direction.RIGHT : Direction.LEFT;
        }

        return velocity.y > 0 ? Direction.UP : Direction.DOWN;
    }

    private void changeType(AnimationComponent animation, AnimationType type) {
        if (animation.type == type) return;

        animation.type = type;
        animation.stateTime = 0f;
    }
}
