package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.animation.AnimationType;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AnimationComponent;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HurtComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.tags.DeadComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<AnimationComponent> animations =
        ComponentMapper.getFor(AnimationComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final ComponentMapper<HurtComponent> hurts =
        ComponentMapper.getFor(HurtComponent.class);

    private final ComponentMapper<MonsterComponent> monsters =
        ComponentMapper.getFor(MonsterComponent.class);

    private final ComponentMapper<DeadComponent> deads =
        ComponentMapper.getFor(DeadComponent.class);

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

        // Monsters face the player (set by their AI) even while backing away, so only the player follows its movement.
        if (moving && !monsters.has(entity)) {
            facing.direction = Direction.fromVector(velocity.x, velocity.y);
        }

        changeType(animation, chooseType(entity, moving));

        animation.stateTime += deltaTime;

        // The swing follows the attack clock so the impact frame lands when the damage does.
        AttackComponent attack = attacks.get(entity);

        if (animation.type == AnimationType.ATTACK && attack != null) {
            // Multi-hit skills (whirlwind) loop the swing for their whole duration.
            animation.stateTime = attack.current.getHits() > 1
                ? attack.time % Ability.ANIMATION_LENGTH
                : attack.time;
        }

        sprite.region = animation.animations
            .get(animation.type, facing.direction)
            .getKeyFrame(animation.stateTime);
    }

    private AnimationType chooseType(Entity entity, boolean moving) {
        // The dead keep the last frame, since the death animation does not repeat.
        if (deads.has(entity)) return AnimationType.DEATH;

        AttackComponent attack = attacks.get(entity);

        // A swinging player keeps the attack animation even while being hit.
        if (attack != null && attack.attacking) return AnimationType.ATTACK;

        if (hurts.has(entity)) return AnimationType.HURT;

        if (moving) return AnimationType.WALK;

        StaminaComponent stamina = staminas.get(entity);

        return stamina != null && stamina.exhausted ? AnimationType.TIRED : AnimationType.IDLE;
    }

    private void changeType(AnimationComponent animation, AnimationType type) {
        if (animation.type == type) return;

        animation.type = type;
        animation.stateTime = 0f;
    }
}
