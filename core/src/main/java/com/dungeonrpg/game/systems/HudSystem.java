package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.BossComponent;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.ui.Hud;

public class HudSystem extends IteratingSystem {

    private static final Family BOSSES = Family.all(
        BossComponent.class,
        MonsterComponent.class,
        HealthComponent.class,
        PositionComponent.class,
        ColliderComponent.class
    ).get();

    private final ComponentMapper<MonsterComponent> monsters =
        ComponentMapper.getFor(MonsterComponent.class);

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<StatsComponent> stats =
        ComponentMapper.getFor(StatsComponent.class);

    private final ComponentMapper<LevelComponent> levels =
        ComponentMapper.getFor(LevelComponent.class);

    private final ComponentMapper<DeadComponent> deads =
        ComponentMapper.getFor(DeadComponent.class);

    private final Hud hud;

    public HudSystem(Hud hud) {
        super(Family.all(
            PlayerComponent.class,
            HealthComponent.class,
            StaminaComponent.class,
            PositionComponent.class,
            StatsComponent.class,
            LevelComponent.class
        ).get(), 6);

        this.hud = hud;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateBoss();
        hud.render(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        hud.update(
            healths.get(entity),
            manas.get(entity),
            staminas.get(entity),
            positions.get(entity),
            stats.get(entity),
            levels.get(entity)
        );

        hud.setDead(deads.has(entity));
    }

    private void updateBoss() {
        ImmutableArray<Entity> bosses = getEngine().getEntitiesFor(BOSSES);

        if (bosses.size() == 0) {
            hud.updateBoss(null, null, 0f, 0f, false);
            return;
        }

        Entity boss = bosses.first();

        hud.updateBoss(
            monsters.get(boss).type.getDisplayName(),
            healths.get(boss),
            Combat.centerX(boss),
            Combat.centerY(boss),
            deads.has(boss)
        );
    }

    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    public void dispose() {
        hud.dispose();
    }
}
