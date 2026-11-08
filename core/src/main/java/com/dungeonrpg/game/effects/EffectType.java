package com.dungeonrpg.game.effects;

/** Efeitos da folha effects_sheet.png: uma linha por efeito, desenhados virados para a direita. */
public enum EffectType {
    AXE_SLASH(0, 5, 0.05f, false, 0.4f),
    SWORD_SLASH(1, 5, 0.05f, false, 0.4f),
    HIT_SPARK(2, 4, 0.06f, false, 0.35f),
    BLOOD(3, 4, 0.07f, false, 0.35f),
    FIREBALL(4, 4, 0.06f, true, 0.4f),
    FIREBALL_EXPLOSION(5, 6, 0.07f, false, 0.5f),
    MAGIC_BOLT(6, 4, 0.06f, true, 0.35f),
    MAGIC_IMPACT(7, 6, 0.06f, false, 0.4f);

    private final int row;
    private final int frames;
    private final float frameDuration;
    private final boolean looping;
    private final float scale;

    EffectType(int row, int frames, float frameDuration, boolean looping, float scale) {
        this.row = row;
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.scale = scale;
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

    /** Fator que reduz a célula da folha para o tamanho em tela. */
    public float getScale() {
        return scale;
    }
}
