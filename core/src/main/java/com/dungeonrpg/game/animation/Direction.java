package com.dungeonrpg.game.animation;

public enum Direction {
    DOWN(0, -1),
    UP(0, 1),
    RIGHT(1, 0),
    LEFT(-1, 0);

    private final int x;
    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /** Ângulo em graus, com a direita em 0, para girar efeitos desenhados virados para a direita. */
    public float getAngle() {
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    /** Na diagonal o lado é preferido, porque o sprite de perfil lê melhor em movimento. */
    public static Direction fromVector(float x, float y) {
        if (Math.abs(x) >= Math.abs(y)) {
            return x > 0 ? RIGHT : LEFT;
        }

        return y > 0 ? UP : DOWN;
    }
}
