package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.dungeonrpg.game.components.AttachedEffectComponent;
import com.dungeonrpg.game.components.BuffComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.SlowComponent;
import com.dungeonrpg.game.components.StunComponent;
import com.dungeonrpg.game.rpg.Combat;

/** Ticks stun, slow and buff timers and keeps attached effects following their owner. */
public class StatusEffectSystem extends EntitySystem {

    private static final Family STUNNED = Family.all(StunComponent.class).get();
    private static final Family SLOWED = Family.all(SlowComponent.class).get();
    private static final Family BUFFED = Family.all(BuffComponent.class).get();
    private static final Family ATTACHED = Family.all(AttachedEffectComponent.class, PositionComponent.class).get();

    private final ComponentMapper<StunComponent> stuns = ComponentMapper.getFor(StunComponent.class);
    private final ComponentMapper<SlowComponent> slows = ComponentMapper.getFor(SlowComponent.class);
    private final ComponentMapper<BuffComponent> buffs = ComponentMapper.getFor(BuffComponent.class);
    private final ComponentMapper<AttachedEffectComponent> attachments =
        ComponentMapper.getFor(AttachedEffectComponent.class);
    private final ComponentMapper<PositionComponent> positions = ComponentMapper.getFor(PositionComponent.class);

    public StatusEffectSystem() {
        super(2);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : getEngine().getEntitiesFor(STUNNED)) {
            StunComponent stun = stuns.get(entity);
            stun.timeLeft -= deltaTime;

            if (stun.timeLeft <= 0f) {
                entity.remove(StunComponent.class);
            }
        }

        for (Entity entity : getEngine().getEntitiesFor(SLOWED)) {
            SlowComponent slow = slows.get(entity);
            slow.timeLeft -= deltaTime;

            if (slow.timeLeft <= 0f) {
                entity.remove(SlowComponent.class);
            }
        }

        for (Entity entity : getEngine().getEntitiesFor(BUFFED)) {
            BuffComponent buff = buffs.get(entity);
            buff.timeLeft -= deltaTime;

            if (buff.timeLeft <= 0f) {
                if (buff.aura != null) {
                    getEngine().removeEntity(buff.aura);
                }

                entity.remove(BuffComponent.class);
            }
        }

        for (Entity effect : getEngine().getEntitiesFor(ATTACHED)) {
            AttachedEffectComponent attachment = attachments.get(effect);

            PositionComponent position = positions.get(effect);
            position.x = Combat.centerX(attachment.owner);
            position.y = Combat.centerY(attachment.owner) + attachment.offsetY;
        }
    }
}
