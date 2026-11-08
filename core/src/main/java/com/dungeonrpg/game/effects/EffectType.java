package com.dungeonrpg.game.effects;

/** Effects drawn from the effect sheets: one row per effect, facing right (the game rotates them). */
public enum EffectType {
    AXE_SLASH(Sheet.COMBAT, 0, 5, 0.05f, false, 0.4f),
    SWORD_SLASH(Sheet.COMBAT, 1, 5, 0.05f, false, 0.4f),
    HIT_SPARK(Sheet.COMBAT, 2, 4, 0.06f, false, 0.35f),
    BLOOD(Sheet.COMBAT, 3, 4, 0.07f, false, 0.35f),
    FIREBALL(Sheet.COMBAT, 4, 4, 0.06f, true, 0.4f),
    FIREBALL_EXPLOSION(Sheet.COMBAT, 5, 6, 0.07f, false, 0.5f),
    MAGIC_BOLT(Sheet.COMBAT, 6, 4, 0.06f, true, 0.35f),
    MAGIC_IMPACT(Sheet.COMBAT, 7, 6, 0.06f, false, 0.4f),
    WHIRLWIND(Sheet.SKILLS, 0, 8, 0.08f, false, 0.62f),
    GROUND_SLAM(Sheet.SKILLS, 1, 8, 0.07f, false, 0.6f),
    RAGE_AURA(Sheet.SKILLS, 2, 8, 0.09f, true, 0.5f),
    FROST_NOVA(Sheet.SKILLS, 4, 8, 0.07f, false, 0.7f),
    ARCANE_BLINK(Sheet.SKILLS, 5, 8, 0.07f, false, 0.45f);

    /** Sheets of 192px cells, see tools/normalize_effects.py. */
    public enum Sheet {
        COMBAT("sprites/effects_sheet.png"),
        SKILLS("sprites/skill_effects_sheet.png");

        private final String path;

        Sheet(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    private final Sheet sheet;
    private final int row;
    private final int frames;
    private final float frameDuration;
    private final boolean looping;
    private final float scale;

    EffectType(Sheet sheet, int row, int frames, float frameDuration, boolean looping, float scale) {
        this.sheet = sheet;
        this.row = row;
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.scale = scale;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public int getRow() {
        return row;
    }

    public int getFrames() {
        return frames;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public boolean isLooping() {
        return looping;
    }

    /** Factor that shrinks the sheet cell to its on-screen size. */
    public float getScale() {
        return scale;
    }
}
