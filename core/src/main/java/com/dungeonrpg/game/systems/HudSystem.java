package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.ui.Hud;

public class HudSystem extends IteratingSystem {

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<DeadComponent> deads =
        ComponentMapper.getFor(DeadComponent.class);

    private final Hud hud;

    public HudSystem(Hud hud) {
        super(Family.all(
            PlayerComponent.class,
            HealthComponent.class,
            StaminaComponent.class,
            PositionComponent.class
        ).get(), 5);

        this.hud = hud;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        hud.render(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        hud.update(
            healths.get(entity),
            manas.get(entity),
            staminas.get(entity),
            positions.get(entity)
        );

        hud.setDead(deads.has(entity));
    }

    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    public void dispose() {
        hud.dispose();
    }
}
