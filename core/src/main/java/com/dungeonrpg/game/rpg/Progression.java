package com.dungeonrpg.game.rpg;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.StatsComponent;

/** Level and stat point rules. */
public final class Progression {

    public static final int STAT_POINTS_PER_LEVEL = 3;
    private static final int BASE_XP = 50;
    private static final int XP_PER_LEVEL = 25;

    private static final ComponentMapper<LevelComponent> LEVELS = ComponentMapper.getFor(LevelComponent.class);
    private static final ComponentMapper<StatsComponent> STATS = ComponentMapper.getFor(StatsComponent.class);

    private Progression() {
    }

    public static int xpToNextLevel(int level) {
        return BASE_XP + XP_PER_LEVEL * (level - 1);
    }

    /** Adds XP and returns how many levels were gained. */
    public static int addXp(LevelComponent level, int amount) {
        level.xp += amount;

        int gained = 0;

        while (level.xp >= xpToNextLevel(level.level)) {
            level.xp -= xpToNextLevel(level.level);
            level.level++;
            level.statPoints += STAT_POINTS_PER_LEVEL;
            gained++;
        }

        return gained;
    }

    public static boolean spendPoint(Entity entity, StatType stat) {
        LevelComponent level = LEVELS.get(entity);
        StatsComponent stats = STATS.get(entity);

        if (level == null || stats == null || level.statPoints <= 0) return false;

        stat.add(stats, 1);
        level.statPoints--;

        return true;
    }
}
