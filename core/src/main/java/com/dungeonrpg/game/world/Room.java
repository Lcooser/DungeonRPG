package com.dungeonrpg.game.world;

public class Room {

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }

    /** Considera uma margem de 1 tile para que as salas nunca fiquem coladas. */
    public boolean intersects(Room other) {
        return x - 1 < other.x + other.width
            && x + width + 1 > other.x
            && y - 1 < other.y + other.height
            && y + height + 1 > other.y;
    }
}
