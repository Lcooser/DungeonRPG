package com.dungeonrpg.game.world;

import com.badlogic.gdx.graphics.Color;

/**
 * Which tiles of a 16x8 tileset (64px tiles) are used for what. Every dungeon tileset shares the same
 * arrangement, but each one differs in what its decor, accents and wall faces are.
 * Row 0: floors, decor and shadows. Row 1: wall faces. Row 2: wall-top autotile.
 */
public class TilesetLayout {

    // Wall-top tile per mask of floor neighbors (bit 1 north, 2 east, 8 west). South is never set, because a
    // wall with floor to its south draws its face instead. Picked by matching the edge lines of each tile,
    // and the same for every tileset.
    private static final int[][] WALL_TOP = new int[16][];

    static {
        WALL_TOP[0] = new int[]{3, 8};
        WALL_TOP[1] = new int[]{10, 1};
        WALL_TOP[2] = new int[]{7};
        WALL_TOP[3] = new int[]{10};
        WALL_TOP[8] = new int[]{4};
        WALL_TOP[9] = new int[]{5};
        WALL_TOP[10] = new int[]{4};
        WALL_TOP[11] = new int[]{5};
    }

    public static final TilesetLayout CRYPT = new TilesetLayout(
        range(0, 8), range(8, 4), range(12, 4), range(0, 4), range(4, 4), true, new int[]{8, 15},
        new Color(1f, 0.62f, 0.58f, 1f)
    );

    public static final TilesetLayout CAVERNS = new TilesetLayout(
        range(0, 8), range(8, 5), new int[0], new int[]{0, 1, 2, 3, 8}, range(4, 4), false, new int[]{13, 14, 15},
        new Color(1f, 0.6f, 0.68f, 1f)
    );

    public static final TilesetLayout FORGE = new TilesetLayout(
        range(0, 8), range(8, 4), new int[]{12}, range(0, 4), range(4, 4), true, new int[]{8, 15},
        new Color(1f, 0.55f, 0.55f, 1f)
    );

    private final int[] floors;
    private final int[] decor;
    private final int[] shadows;
    private final int[] faces;
    // Wall faces that carry a torch or crystal; animated ones cycle through the frames, the rest are random variants.
    private final int[] accents;
    private final boolean animatedAccents;
    private final int[] rareFaces;
    private final Color bossFloorTint;

    private TilesetLayout(int[] floors, int[] decor, int[] shadows, int[] faces, int[] accents,
                          boolean animatedAccents, int[] rareFaces, Color bossFloorTint) {
        this.floors = floors;
        this.decor = decor;
        this.shadows = shadows;
        this.faces = faces;
        this.accents = accents;
        this.animatedAccents = animatedAccents;
        this.rareFaces = rareFaces;
        this.bossFloorTint = bossFloorTint;
    }

    private static int[] range(int first, int count) {
        int[] values = new int[count];

        for (int i = 0; i < count; i++) {
            values[i] = first + i;
        }

        return values;
    }

    public int[] getFloors() {
        return floors;
    }

    public int[] getDecor() {
        return decor;
    }

    public int[] getShadows() {
        return shadows;
    }

    public int[] getFaces() {
        return faces;
    }

    public int[] getAccents() {
        return accents;
    }

    public boolean hasAnimatedAccents() {
        return animatedAccents;
    }

    public int[] getRareFaces() {
        return rareFaces;
    }

    public Color getBossFloorTint() {
        return bossFloorTint;
    }

    public int[] getWallTop(int mask) {
        return WALL_TOP[mask];
    }
}
