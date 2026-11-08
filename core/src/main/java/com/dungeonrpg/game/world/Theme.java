package com.dungeonrpg.game.world;

import com.badlogic.gdx.graphics.Color;
import com.dungeonrpg.game.entities.MonsterType;
import com.dungeonrpg.game.entities.MonsterType.Family;

/** A dungeon: its monsters, its boss and how its map looks. */
public enum Theme {
    CRYPT("FORGOTTEN CRYPT", "maps/crypt_tileset.png", TilesetLayout.CRYPT, MonsterType.LICH_KING,
        new Color(0.22f, 0.22f, 0.27f, 1f), new Color(0.3f, 0.3f, 0.38f, 1f),
        Family.SKELETON_WARRIOR, Family.SKELETON_ARCHER, Family.NECROMANCER),
    CAVERNS("INFESTED CAVERNS", "maps/caverns_tileset.png", TilesetLayout.CAVERNS, MonsterType.SPIDER_QUEEN,
        new Color(0.2f, 0.17f, 0.12f, 1f), new Color(0.36f, 0.3f, 0.22f, 1f),
        Family.GIANT_SPIDER, Family.VENOM_BROODLING, Family.CULTIST_HUNTER, Family.SLIME),
    FORGE("INFERNAL FORGE", "maps/forge_tileset.png", TilesetLayout.FORGE, MonsterType.MAGMA_DRAGON,
        new Color(0.24f, 0.12f, 0.09f, 1f), new Color(0.4f, 0.22f, 0.16f, 1f),
        Family.FIRE_IMP, Family.EMBER_KNIGHT, Family.MAGMA_HOUND);

    private final String label;
    // Tileset image under assets, or null while the theme has no art and falls back to flat colors.
    private final String tilesetPath;
    private final TilesetLayout layout;
    private final MonsterType boss;
    private final Color floorColor;
    private final Color wallColor;
    private final Family[] families;

    Theme(String label, String tilesetPath, TilesetLayout layout, MonsterType boss, Color floorColor, Color wallColor, Family... families) {
        this.label = label;
        this.tilesetPath = tilesetPath;
        this.layout = layout;
        this.boss = boss;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
        this.families = families;
    }

    public String getLabel() {
        return label;
    }

    public String getTilesetPath() {
        return tilesetPath;
    }

    public TilesetLayout getLayout() {
        return layout;
    }

    public MonsterType getBoss() {
        return boss;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    public Color getWallColor() {
        return wallColor;
    }

    /** Monster families that live in this dungeon. */
    public Family[] getFamilies() {
        return families;
    }
}
