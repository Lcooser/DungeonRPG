package com.dungeonrpg.game.effects;

/**
 * Effects drawn from sheets of 192px cells, facing right (the game rotates them). Each entry names the
 * file, the row of the effect inside it and how many frames it has.
 */
public enum EffectType {
    AXE_SLASH(Files.COMBAT, 0, 5, 0.05f, false, 0.4f),
    SWORD_SLASH(Files.COMBAT, 1, 5, 0.05f, false, 0.4f),
    HIT_SPARK(Files.COMBAT, 2, 4, 0.06f, false, 0.35f),
    BLOOD(Files.COMBAT, 3, 4, 0.07f, false, 0.35f),
    FIREBALL(Files.COMBAT, 4, 4, 0.06f, true, 0.4f),
    FIREBALL_EXPLOSION(Files.COMBAT, 5, 6, 0.07f, false, 0.5f),
    MAGIC_BOLT(Files.COMBAT, 6, 4, 0.06f, true, 0.35f),
    MAGIC_IMPACT(Files.COMBAT, 7, 6, 0.06f, false, 0.4f),

    WHIRLWIND(Files.SKILLS, 0, 8, 0.08f, false, 0.62f),
    GROUND_SLAM(Files.SKILLS, 1, 8, 0.07f, false, 0.6f),
    RAGE_AURA(Files.SKILLS, 2, 8, 0.09f, true, 0.5f),
    FROST_NOVA(Files.SKILLS, 4, 8, 0.07f, false, 0.7f),
    ARCANE_BLINK(Files.SKILLS, 5, 8, 0.07f, false, 0.45f),

    // Monster projectiles, 4 looping frames each.
    ARROW(Files.VFX + "arrow_projectile.png", 0, 4, 0.08f, true, 0.45f),
    CROSSBOW_BOLT(Files.VFX + "crossbow_bolt_projectile.png", 0, 4, 0.08f, true, 0.45f),
    NECROTIC_BOLT(Files.VFX + "necrotic_bolt_projectile.png", 0, 4, 0.08f, true, 0.5f),
    VENOM_GLOB(Files.VFX + "venom_glob_projectile.png", 0, 4, 0.08f, true, 0.5f),
    IMP_FIREBALL(Files.VFX + "imp_fireball_projectile.png", 0, 4, 0.08f, true, 0.5f),
    MAGMA_BREATH(Files.VFX + "magma_breath_projectile.png", 0, 4, 0.08f, true, 0.6f),

    // Boss attacks, 6 frames each.
    SOUL_BARRAGE(Files.VFX + "soul_barrage_vfx.png", 0, 6, 0.08f, false, 0.7f),
    DEATH_NOVA(Files.VFX + "death_nova_vfx.png", 0, 6, 0.09f, false, 1.1f),
    SUMMON_UNDEAD(Files.VFX + "summon_undead_vfx.png", 0, 6, 0.09f, false, 0.8f),
    VENOM_SPIT(Files.VFX + "venom_spit_vfx.png", 0, 6, 0.08f, false, 0.6f),
    WEB_TRAP(Files.VFX + "web_trap_vfx.png", 0, 6, 0.12f, false, 0.7f),
    BROOD_SUMMONING(Files.VFX + "brood_summoning_vfx.png", 0, 6, 0.09f, false, 0.8f),
    DRAGON_BREATH(Files.VFX + "dragon_breath_vfx.png", 0, 6, 0.08f, false, 0.8f),
    METEOR_RAIN(Files.VFX + "meteor_rain_vfx.png", 0, 6, 0.12f, false, 0.9f),
    LAVA_ERUPTION(Files.VFX + "lava_eruption_vfx.png", 0, 6, 0.09f, false, 1.2f);

    /** Paths of the effect files, with the folder shared by the single-row strips. */
    private static final class Files {
        private static final String VFX = "sprites/vfx/";
        private static final String COMBAT = VFX + "combat_effects_sheet.png";
        private static final String SKILLS = VFX + "skill_effects_sheet.png";
    }

    private final String path;
    private final int row;
    private final int frames;
    private final float frameDuration;
    private final boolean looping;
    private final float scale;

    EffectType(String path, int row, int frames, float frameDuration, boolean looping, float scale) {
        this.path = path;
        this.row = row;
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.scale = scale;
    }

    public String getPath() {
        return path;
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
