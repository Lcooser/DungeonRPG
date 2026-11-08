package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/** Linha do HUD com nome, barra e valor de um recurso (vida, mana ou stamina). */
public class ResourceBar {

    private static final float NAME_WIDTH = 28f;
    private static final float BAR_WIDTH = 140f;
    private static final float BAR_HEIGHT = 12f;
    private static final float VALUE_PADDING = 6f;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.12f, 0.85f);

    private final ProgressBar bar;
    private final Label valueLabel;
    private final Label nameLabel;

    private int shownCurrent = -1;
    private int shownMax = -1;

    public ResourceBar(Skin skin, String name, Color color) {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = tinted(skin, BACKGROUND_COLOR);
        style.knobBefore = tinted(skin, color);

        bar = new ProgressBar(0f, 1f, 0.1f, false, style);
        nameLabel = new Label(name, skin);
        valueLabel = new Label("", skin);
    }

    private Drawable tinted(Skin skin, Color color) {
        Drawable drawable = skin.newDrawable("white", color);
        drawable.setMinWidth(0f);
        drawable.setMinHeight(BAR_HEIGHT);

        return drawable;
    }

    public void addTo(Table table) {
        table.add(nameLabel).width(NAME_WIDTH).left();
        table.add(bar).width(BAR_WIDTH).height(BAR_HEIGHT);
        table.add(valueLabel).padLeft(VALUE_PADDING).left();
        table.row().padTop(4f);
    }

    public void update(float current, float max) {
        float safeMax = Math.max(max, 1f);

        if (bar.getMaxValue() != safeMax) {
            bar.setRange(0f, safeMax);
        }

        bar.setValue(current);

        // O texto só é remontado quando o número exibido muda, para não gerar lixo a cada frame.
        int currentText = MathUtils.floor(current);
        int maxText = MathUtils.floor(safeMax);

        if (currentText != shownCurrent || maxText != shownMax) {
            shownCurrent = currentText;
            shownMax = maxText;
            valueLabel.setText(currentText + "/" + maxText);
        }
    }
}
