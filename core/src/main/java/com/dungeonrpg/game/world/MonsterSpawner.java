package com.dungeonrpg.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.entities.MonsterType.Family;
import com.dungeonrpg.game.entities.MonsterFactory;
import com.dungeonrpg.game.entities.MonsterType;

/** Scatters monsters across the rooms, sparing the spawn room and guarding the boss room with the boss. */
public class MonsterSpawner {

    private static final int ROOM_AREA_PER_MONSTER = 45;
    private static final int MAX_PER_ROOM = 4;
    private static final float SKELETON_CHANCE = 0.5f;
    private static final float SLIME_CHANCE = 0.3f;
    // Rooms farther from the spawn (0 near, 1 farthest) hold stronger tiers.
    private static final float EARLY_ROOMS = 0.3f;
    private static final float MIDDLE_ROOMS = 0.65f;
    private static final float BOSS_ESCORT_OFFSET = 64f;

    private final MonsterFactory factory;

    public MonsterSpawner(MonsterFactory factory) {
        this.factory = factory;
    }

    public void populate(DungeonMap map) {
        if (map.getBossRoom() != null) {
            spawnBoss(map.getBossRoom());
        }

        Room spawn = map.getSpawnRoom();
        float farthest = 1f;

        for (Room room : map.getRooms()) {
            farthest = Math.max(farthest, distance(room, spawn));
        }

        for (Room room : map.getRooms()) {
            if (room == spawn || room == map.getBossRoom()) continue;

            float depth = distance(room, spawn) / farthest;

            int count = MathUtils.clamp(room.width * room.height / ROOM_AREA_PER_MONSTER, 1, MAX_PER_ROOM);

            for (int i = 0; i < count; i++) {
                // Keep away from the walls so monsters never spawn stuck.
                int tileX = room.x + 1 + MathUtils.random(room.width - 3);
                int tileY = room.y + 1 + MathUtils.random(room.height - 3);

                factory.spawn(
                    randomType(rollTier(depth)),
                    (tileX + 0.5f) * DungeonMap.TILE_SIZE,
                    (tileY + 0.5f) * DungeonMap.TILE_SIZE
                );
            }
        }
    }

    private void spawnBoss(Room room) {
        float centerX = (room.centerX() + 0.5f) * DungeonMap.TILE_SIZE;
        float centerY = (room.centerY() + 0.5f) * DungeonMap.TILE_SIZE;

        factory.spawn(MonsterType.SPIDER_QUEEN, centerX, centerY);

        // Two escorts so the room is not just a duel from the start.
        factory.spawn(MonsterType.BROOD_GUARD, centerX - BOSS_ESCORT_OFFSET, centerY + BOSS_ESCORT_OFFSET);
        factory.spawn(MonsterType.BROOD_GUARD, centerX + BOSS_ESCORT_OFFSET, centerY - BOSS_ESCORT_OFFSET);
    }

    private float distance(Room from, Room to) {
        float dx = from.centerX() - to.centerX();
        float dy = from.centerY() - to.centerY();

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private int rollTier(float depth) {
        float roll = MathUtils.random();

        if (depth < EARLY_ROOMS) return roll < 0.9f ? 1 : 2;
        if (depth < MIDDLE_ROOMS) return roll < 0.3f ? 1 : roll < 0.9f ? 2 : 3;

        return roll < 0.2f ? 2 : 3;
    }

    private MonsterType randomType(int tier) {
        float roll = MathUtils.random();
        Family family;

        if (roll < SKELETON_CHANCE) {
            family = Family.SKELETON;
        } else if (roll < SKELETON_CHANCE + SLIME_CHANCE) {
            family = Family.SLIME;
        } else {
            family = Family.SPIDER;
        }

        for (MonsterType type : MonsterType.values()) {
            if (type.getFamily() == family && type.getTierLevel() == tier) return type;
        }

        throw new IllegalStateException("No monster for " + family + " tier " + tier);
    }
}
