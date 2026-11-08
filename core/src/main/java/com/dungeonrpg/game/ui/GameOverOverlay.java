package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Full-screen end panel with a title and two buttons, used for both game over and victory. */
public class GameOverOverlay extends Table {

    private static final float FADE_DURATION = 0.6f;
    private static final float TITLE_SCALE = 2f;
    private static final float PANEL_PADDING = 12f;
    private static final float TITLE_SPACING = 12f;
    private static final float BUTTON_WIDTH = 84f;
    private static final float BUTTON_HEIGHT = 20f;
    private static final float BUTTON_GAP = 8f;
    private static final Color DIM_COLOR = new Color(0f, 0f, 0f, 0.7f);

    private final float showDelay;
    private boolean shown;

    public GameOverOverlay(
        TextureAtlas atlas,
        BitmapFont font,
        String title,
        Color titleColor,
        float showDelay,
        Runnable onRetry,
        Runnable onMenu
    ) {
        this.showDelay = showDelay;

        setFillParent(true);
        setBackground(new TextureRegionDrawable(atlas.findRegion("pixel")).tint(DIM_COLOR));

        Label titleLabel = new Label(title, new Label.LabelStyle(font, titleColor));
        titleLabel.setFontScale(TITLE_SCALE);

        Table panel = new Table();
        panel.setBackground(new NinePatchDrawable(atlas.createPatch("panel")));
        panel.pad(PANEL_PADDING);
        panel.add(titleLabel).colspan(2).padBottom(TITLE_SPACING).row();
        panel.add(new PixelButton(atlas, font, "TRY AGAIN", BUTTON_WIDTH, BUTTON_HEIGHT, onRetry))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT)
            .padRight(BUTTON_GAP / 2f);
        panel.add(new PixelButton(atlas, font, "MAIN MENU", BUTTON_WIDTH, BUTTON_HEIGHT, onMenu))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT)
            .padLeft(BUTTON_GAP / 2f);

        add(panel);

        hide();
    }

    /** Appears after a pause so the death animation can finish first. */
    public void show() {
        if (shown) return;

        shown = true;
        setVisible(false);
        setTouchable(Touchable.disabled);
        getColor().a = 0f;

        clearActions();
        addAction(Actions.sequence(
            Actions.delay(showDelay),
            Actions.visible(true),
            Actions.touchable(Touchable.enabled),
            Actions.fadeIn(FADE_DURATION)
        ));
    }

    public void hide() {
        shown = false;

        clearActions();
        setVisible(false);
        setTouchable(Touchable.disabled);
    }

    public boolean isShown() {
        return shown;
    }

    /** Ignores clicks and keys until the panel has finished appearing. */
    public boolean isInteractive() {
        return shown && getTouchable() == Touchable.enabled;
    }
}
