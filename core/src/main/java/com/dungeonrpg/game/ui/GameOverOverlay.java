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

/** Tela de game over: escurece o jogo e mostra um painel com o botão de reiniciar. */
public class GameOverOverlay extends Table {

    private static final float SHOW_DELAY = 1.4f;
    private static final float FADE_DURATION = 0.6f;
    private static final float TITLE_SCALE = 2f;
    private static final float PANEL_PADDING = 12f;
    private static final float TITLE_SPACING = 12f;
    private static final float BUTTON_WIDTH = 84f;
    private static final float BUTTON_HEIGHT = 20f;
    private static final Color DIM_COLOR = new Color(0f, 0f, 0f, 0.7f);
    private static final Color TITLE_COLOR = new Color(0.85f, 0.2f, 0.2f, 1f);

    private boolean shown;

    public GameOverOverlay(TextureAtlas atlas, BitmapFont font, Runnable onRestart) {
        setFillParent(true);
        setBackground(new TextureRegionDrawable(atlas.findRegion("pixel")).tint(DIM_COLOR));

        Label title = new Label("GAME OVER", new Label.LabelStyle(font, TITLE_COLOR));
        title.setFontScale(TITLE_SCALE);

        Table panel = new Table();
        panel.setBackground(new NinePatchDrawable(atlas.createPatch("panel")));
        panel.pad(PANEL_PADDING);
        panel.add(title).padBottom(TITLE_SPACING).row();
        panel.add(new PixelButton(atlas, font, "RESTART", BUTTON_WIDTH, BUTTON_HEIGHT, onRestart))
            .size(BUTTON_WIDTH, BUTTON_HEIGHT);

        add(panel);

        hide();
    }

    /** Aparece depois de uma pausa, para a animação de morte poder terminar. */
    public void show() {
        if (shown) return;

        shown = true;
        setVisible(false);
        setTouchable(Touchable.disabled);
        getColor().a = 0f;

        clearActions();
        addAction(Actions.sequence(
            Actions.delay(SHOW_DELAY),
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

    /** Só aceita cliques e teclas depois que o painel terminou de aparecer. */
    public boolean isInteractive() {
        return shown && getTouchable() == Touchable.enabled;
    }
}
