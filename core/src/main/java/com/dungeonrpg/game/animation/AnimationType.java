package com.dungeonrpg.game.animation;

public enum AnimationType {
    IDLE(0.2f, true),
    WALK(0.1f, true),
    TIRED(0.2f, true),
    ATTACK(0.08f, false),
    HURT(0.1f, false),
    DEATH(0.12f, false);

    private final float frameDuration;
    private final boolean looping;

    AnimationType(float frameDuration, boolean looping) {
        this.frameDuration = frameDuration;
        this.looping = looping;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public boolean isLooping() {
        return looping;
    }
}
