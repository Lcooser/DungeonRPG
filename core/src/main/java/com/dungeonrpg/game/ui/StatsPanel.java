package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.rpg.StatType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

/** Attribute list with a "+" button per stat, shown while there are points to spend. */
public class StatsPanel extends Table {

    private static final float LABEL_WIDTH = 46f;
    private static final float BUTTON_SIZE = 11f;
    private static final float COLUMN_GAP = 8f;
    private static final float ROW_GAP = 2f;
    private static final Color POINTS_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);

    private final Map<StatType, Label> labels = new EnumMap<>(StatType.class);
    private final Map<StatType, PixelButton> buttons = new EnumMap<>(StatType.class);
    private final Map<StatType, Integer> shownValues = new EnumMap<>(StatType.class);
    private final Label pointsLabel;

    private int shownPoints = -1;

    public StatsPanel(TextureAtlas atlas, BitmapFont font, Consumer<StatType> onSpend) {
        Label.LabelStyle white = new Label.LabelStyle(font, Color.WHITE);

        int column = 0;

        for (StatType stat : StatType.values()) {
            Label label = new Label(stat.getLabel(), white);
            PixelButton button = new PixelButton(atlas, font, "+", BUTTON_SIZE, BUTTON_SIZE, () -> onSpend.accept(stat));

            labels.put(stat, label);
            buttons.put(stat, button);

            addCell(label, button, column);
            column++;
        }

        pointsLabel = new Label("", new Label.LabelStyle(font, POINTS_COLOR));
        add(pointsLabel).left().padLeft(COLUMN_GAP);
    }

    private void addCell(Label label, PixelButton button, int column) {
        add(label).width(LABEL_WIDTH).left().padLeft(column % 2 == 1 ? COLUMN_GAP : 0f).padBottom(ROW_GAP);
        add(button).size(BUTTON_SIZE).padBottom(ROW_GAP);

        if (column % 2 == 1) {
            row();
        }
    }

    public void update(StatsComponent stats, LevelComponent level) {
        for (StatType stat : StatType.values()) {
            int value = stat.get(stats);
            Integer previous = shownValues.get(stat);

            if (previous == null || previous != value) {
                shownValues.put(stat, value);
                labels.get(stat).setText(stat.getLabel() + " " + value);
            }

            // Invisible actors keep their space, so the layout does not jump when points run out.
            buttons.get(stat).setVisible(level.statPoints > 0);
        }

        if (level.statPoints != shownPoints) {
            shownPoints = level.statPoints;
            pointsLabel.setText(level.statPoints > 0 ? "PTS " + level.statPoints : "");
        }
    }
}
