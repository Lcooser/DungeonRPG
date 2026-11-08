package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.XpRewardComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.rpg.Progression;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

/** Pays out the XP of dead monsters to the player and handles level ups. */
public class ExperienceSystem extends IteratingSystem {

    private static final Family LIVING_PLAYERS = Family.all(
        PlayerComponent.class,
        LevelComponent.class,
        HealthComponent.class,
        StaminaComponent.class,
        PositionComponent.class
    ).exclude(DeadComponent.class).get();

    private static final Color XP_COLOR = new Color(0.45f, 0.9f, 0.5f, 1f);
    private static final Color LEVEL_UP_COLOR = new Color(1f, 0.85f, 0.3f, 1f);
    private static final float TEXT_OFFSET_Y = 40f;
    private static final float LEVEL_TEXT_OFFSET_Y = 54f;

    private final ComponentMapper<XpRewardComponent> rewards =
        ComponentMapper.getFor(XpRewardComponent.class);

    private final ComponentMapper<LevelComponent> levels =
        ComponentMapper.getFor(LevelComponent.class);

    private final ComponentMapper<HealthComponent> healths =
        ComponentMapper.getFor(HealthComponent.class);

    private final ComponentMapper<ManaComponent> manas =
        ComponentMapper.getFor(ManaComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final Effects effects;

    public ExperienceSystem(Effects effects) {
        super(Family.all(XpRewardComponent.class, DeadComponent.class).get(), 1);

        this.effects = effects;
    }

    @Override
    protected void processEntity(Entity monster, float deltaTime) {
        int amount = rewards.get(monster).amount;
        monster.remove(XpRewardComponent.class);

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(LIVING_PLAYERS);

        if (players.size() == 0) return;

        Entity player = players.first();
        float x = Combat.centerX(player);
        float y = Combat.centerY(player) + TEXT_OFFSET_Y;

        effects.spawnText(x, y, "+" + amount + " XP", XP_COLOR);

        if (Progression.addXp(levels.get(player), amount) > 0) {
            levelUp(player, x, y);
        }
    }

    private void levelUp(Entity player, float x, float y) {
        // Full refill on level up, using the maximums from before the new points are spent.
        HealthComponent health = healths.get(player);
        health.current = health.max;

        ManaComponent mana = manas.get(player);

        if (mana != null) {
            mana.current = mana.max;
        }

        StaminaComponent stamina = staminas.get(player);
        stamina.current = stamina.max;
        stamina.exhausted = false;

        effects.spawnLevelUp(player);
        effects.spawnText(x, y + LEVEL_TEXT_OFFSET_Y - TEXT_OFFSET_Y, "LEVEL UP", LEVEL_UP_COLOR);
    }
}
