package com.dungeonrpg.game.world;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonMap {

    public static final int TILE_SIZE = 32;

    private static final int ROOM_ATTEMPTS = 40;
    private static final int MIN_ROOM_SIZE = 6;
    private static final int MAX_ROOM_SIZE = 12;
    private static final float COLLISION_EPSILON = 0.001f;

    private final int width;
    private final int height;
    private final Random random;
    private final TileType[][] tiles;
    private final List<Room> rooms = new ArrayList<>();
    private Room bossRoom;

    public DungeonMap(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.random = new Random(seed);
        this.tiles = new TileType[width][height];

        generate();
        bossRoom = findBossRoom();
    }

    // The boss lives in the room farthest from the spawn, so reaching it takes a real trip.
    private Room findBossRoom() {
        Room spawn = rooms.get(0);
        Room farthest = null;
        float farthestDistance = 0f;

        for (Room room : rooms) {
            float dx = room.centerX() - spawn.centerX();
            float dy = room.centerY() - spawn.centerY();
            float distance = dx * dx + dy * dy;

            if (room != spawn && distance > farthestDistance) {
                farthest = room;
                farthestDistance = distance;
            }
        }

        return farthest;
    }

    private void generate() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = TileType.WALL;
            }
        }

        for (int i = 0; i < ROOM_ATTEMPTS; i++) {
            int roomWidth = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            int roomHeight = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
            int roomX = 1 + random.nextInt(width - roomWidth - 2);
            int roomY = 1 + random.nextInt(height - roomHeight - 2);

            Room room = new Room(roomX, roomY, roomWidth, roomHeight);

            if (overlapsAnyRoom(room)) continue;

            carveRoom(room);

            if (!rooms.isEmpty()) {
                connect(rooms.get(rooms.size() - 1), room);
            }

            rooms.add(room);
        }
    }

    private boolean overlapsAnyRoom(Room room) {
        for (Room other : rooms) {
            if (room.intersects(other)) return true;
        }

        return false;
    }

    private void carveRoom(Room room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                tiles[x][y] = TileType.FLOOR;
            }
        }
    }

    /** Liga duas salas com um corredor de 2 tiles de largura em formato de L. */
    private void connect(Room from, Room to) {
        int startX = from.centerX();
        int startY = from.centerY();
        int endX = to.centerX();
        int endY = to.centerY();

        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            carveFloor(x, startY);
            carveFloor(x, startY + 1);
        }

        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
            carveFloor(endX, y);
            carveFloor(endX + 1, y);
        }
    }

    private void carveFloor(int x, int y) {
        // A borda do mapa permanece sempre como parede.
        if (x <= 0 || y <= 0 || x >= width - 1 || y >= height - 1) return;

        tiles[x][y] = TileType.FLOOR;
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return TileType.WALL;

        return tiles[x][y];
    }

    public boolean isSolid(int x, int y) {
        return getTile(x, y).isSolid();
    }

    /** Uma parede só é desenhada quando encosta em algum chão, o resto fica como vazio. */
    public boolean isBorderWall(int x, int y) {
        if (getTile(x, y) != TileType.WALL) return false;

        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetY = -1; offsetY <= 1; offsetY++) {
                if (getTile(x + offsetX, y + offsetY) == TileType.FLOOR) return true;
            }
        }

        return false;
    }

    /** Recebe um retângulo em coordenadas de mundo (pixels). */
    public boolean collides(float x, float y, float rectWidth, float rectHeight) {
        int minTileX = MathUtils.floor(x / TILE_SIZE);
        int maxTileX = MathUtils.floor((x + rectWidth - COLLISION_EPSILON) / TILE_SIZE);
        int minTileY = MathUtils.floor(y / TILE_SIZE);
        int maxTileY = MathUtils.floor((y + rectHeight - COLLISION_EPSILON) / TILE_SIZE);

        for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
            for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
                if (isSolid(tileX, tileY)) return true;
            }
        }

        return false;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    /** The boss room, or null if the map only has one room. */
    public Room getBossRoom() {
        return bossRoom;
    }

    public boolean isBossRoom(int x, int y) {
        return bossRoom != null
            && x >= bossRoom.x && x < bossRoom.x + bossRoom.width
            && y >= bossRoom.y && y < bossRoom.y + bossRoom.height;
    }

    public Room getSpawnRoom() {
        return rooms.get(0);
    }

    public float getSpawnX() {
        return (getSpawnRoom().centerX() + 0.5f) * TILE_SIZE;
    }

    public float getSpawnY() {
        return (getSpawnRoom().centerY() + 0.5f) * TILE_SIZE;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getPixelWidth() {
        return width * TILE_SIZE;
    }

    public float getPixelHeight() {
        return height * TILE_SIZE;
    }
}
