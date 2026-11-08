package com.dungeonrpg.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.entities.MonsterFactory;
import com.dungeonrpg.game.entities.MonsterType;

/** Scatters monsters across the rooms, sparing the spawn room and guarding the boss room with the boss. */
public class MonsterSpawner {

    private static final int ROOM_AREA_PER_MONSTER = 45;
    private static final int MAX_PER_ROOM = 4;
    private static final float SKELETON_CHANCE = 0.5f;
    private static final float SLIME_CHANCE = 0.3f;
    private static final float BOSS_ESCORT_OFFSET = 64f;

    private final MonsterFactory factory;

    public MonsterSpawner(MonsterFactory factory) {
        this.factory = factory;
    }

    public void populate(DungeonMap map) {
        if (map.getBossRoom() != null) {
            spawnBoss(map.getBossRoom());
        }

        for (Room room : map.getRooms()) {
            if (room == map.getSpawnRoom() || room == map.getBossRoom()) continue;

            int count = MathUtils.clamp(room.width * room.height / ROOM_AREA_PER_MONSTER, 1, MAX_PER_ROOM);

            for (int i = 0; i < count; i++) {
                // Keep away from the walls so monsters never spawn stuck.
                int tileX = room.x + 1 + MathUtils.random(room.width - 3);
                int tileY = room.y + 1 + MathUtils.random(room.height - 3);

                factory.spawn(
                    randomType(),
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
        factory.spawn(MonsterType.SPIDER, centerX - BOSS_ESCORT_OFFSET, centerY + BOSS_ESCORT_OFFSET);
        factory.spawn(MonsterType.SPIDER, centerX + BOSS_ESCORT_OFFSET, centerY - BOSS_ESCORT_OFFSET);
    }

    private MonsterType randomType() {
        float roll = MathUtils.random();

        if (roll < SKELETON_CHANCE) return MonsterType.SKELETON;
        if (roll < SKELETON_CHANCE + SLIME_CHANCE) return MonsterType.SLIME;

        return MonsterType.SPIDER;
    }
}
