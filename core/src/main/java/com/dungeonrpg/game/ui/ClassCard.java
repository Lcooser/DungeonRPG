package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.StatsCalculator;

/** Selectable card showing a class portrait, base resources and abilities. */
public class ClassCard extends Table {

    private static final float PORTRAIT_SIZE = 64f;
    private static final float PADDING = 8f;
    private static final float LINE_SPACING = 3f;
    private static final Color RESOURCE_COLOR = new Color(1f, 1f, 1f, 1f);
    private static final Color ABILITY_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);

    private final Drawable normalBackground;
    private final Drawable selectedBackground;

    public ClassCard(
        TextureAtlas atlas,
        BitmapFont font,
        CharacterClass characterClass,
        Texture face,
        Runnable onSelect
    ) {
        normalBackground = new NinePatchDrawable(atlas.createPatch("panel"));
        selectedBackground = new NinePatchDrawable(atlas.createPatch("panel-selected"));

        // Tables ignore clicks on empty space by default, the whole card should be clickable.
        setTouchable(Touchable.enabled);
        pad(PADDING);
        setBackground(normalBackground);

        Label.LabelStyle white = new Label.LabelStyle(font, RESOURCE_COLOR);
        Label.LabelStyle gold = new Label.LabelStyle(font, ABILITY_COLOR);

        StatsComponent stats = characterClass.createStats();

        add(new Portrait(atlas, face, PORTRAIT_SIZE)).size(PORTRAIT_SIZE).padBottom(LINE_SPACING * 2f).row();
        add(new Label(characterClass.name(), gold)).padBottom(LINE_SPACING * 2f).row();

        add(new Label("HP " + Math.round(StatsCalculator.maxHealth(stats)), white)).padBottom(LINE_SPACING).row();

        if (characterClass.usesMana()) {
            add(new Label("MP " + Math.round(StatsCalculator.maxMana(stats)), white)).padBottom(LINE_SPACING).row();
        }

        add(new Label("SP " + Math.round(StatsCalculator.maxStamina(stats)), white)).padBottom(LINE_SPACING * 2f).row();

        add(new Label("J " + label(characterClass.getPrimaryAbility()), white)).padBottom(LINE_SPACING).row();
        add(new Label("K " + label(characterClass.getSecondaryAbility()), white)).row();

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSelect.run();
            }
        });
    }

    public void setSelected(boolean selected) {
        setBackground(selected ? selectedBackground : normalBackground);
    }

    private String label(Ability ability) {
        return ability.name().replace('_', ' ');
    }
}
