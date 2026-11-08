package com.dungeonrpg.game.ui;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** Picks an integer UI scale so pixel art never gets uneven pixels. */
public final class PixelScale {

    private static final float DESIGN_WIDTH = 640f;
    private static final float DESIGN_HEIGHT = 360f;

    private PixelScale() {
    }

    public static void apply(ScreenViewport viewport, int width, int height) {
        int scale = Math.max(1, Math.round(Math.min(width / DESIGN_WIDTH, height / DESIGN_HEIGHT)));

        viewport.setUnitsPerPixel(1f / scale);
    }
}
