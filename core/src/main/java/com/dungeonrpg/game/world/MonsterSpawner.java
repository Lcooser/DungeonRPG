package com.dungeonrpg.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.dungeonrpg.game.entities.MonsterFactory;
import com.dungeonrpg.game.entities.MonsterType;

/** Distribui monstros pelas salas da masmorra, poupando a sala onde o jogador nasce. */
public class MonsterSpawner {

    private static final int ROOM_AREA_PER_MONSTER = 45;
    private static final int MAX_PER_ROOM = 4;
    private static final float SKELETON_CHANCE = 0.5f;
    private static final float SLIME_CHANCE = 0.3f;

    private final MonsterFactory factory;

    public MonsterSpawner(MonsterFactory factory) {
        this.factory = factory;
    }

    public void populate(DungeonMap map) {
        for (Room room : map.getRooms()) {
            if (room == map.getSpawnRoom()) continue;

            int count = MathUtils.clamp(room.width * room.height / ROOM_AREA_PER_MONSTER, 1, MAX_PER_ROOM);

            for (int i = 0; i < count; i++) {
                // Longe das paredes, para o monstro não nascer preso.
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

    private MonsterType randomType() {
        float roll = MathUtils.random();

        if (roll < SKELETON_CHANCE) return MonsterType.SKELETON;
        if (roll < SKELETON_CHANCE + SLIME_CHANCE) return MonsterType.SLIME;

        return MonsterType.SPIDER;
    }
}
