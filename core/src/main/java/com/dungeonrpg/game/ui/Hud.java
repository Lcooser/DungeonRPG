package com.dungeonrpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.StaminaComponent;

public class Hud {

    private static final float SCREEN_PADDING = 8f;

    private final Stage stage;
    private final Skin skin;
    private final ResourceBar healthBar;
    private final ResourceBar manaBar;
    private final ResourceBar staminaBar;

    public Hud(float viewportWidth, float viewportHeight) {
        stage = new Stage(new FitViewport(viewportWidth, viewportHeight));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        healthBar = new ResourceBar(skin, "HP", new Color(0.8f, 0.15f, 0.15f, 1f));
        manaBar = new ResourceBar(skin, "MP", new Color(0.2f, 0.4f, 0.9f, 1f));
        staminaBar = new ResourceBar(skin, "SP", new Color(0.9f, 0.75f, 0.2f, 1f));

        Table root = new Table();
        root.setFillParent(true);
        root.top().left().pad(SCREEN_PADDING);

        healthBar.addTo(root);
        manaBar.addTo(root);
        staminaBar.addTo(root);

        stage.addActor(root);
    }

    public void update(HealthComponent health, ManaComponent mana, StaminaComponent stamina) {
        healthBar.update(health.current, health.max);
        manaBar.update(mana.current, mana.max);
        staminaBar.update(stamina.current, stamina.max);
    }

    public void render(float deltaTime) {
        stage.getViewport().apply();
        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
