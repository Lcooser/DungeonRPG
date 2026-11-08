package com.dungeonrpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.world.DungeonMap;

public class Hud {

    // Tamanho de referência do jogo, usado para escolher a escala do HUD.
    private static final float DESIGN_WIDTH = 640f;
    private static final float DESIGN_HEIGHT = 360f;

    private static final float SCREEN_PADDING = 6f;
    private static final float PORTRAIT_SIZE = 40f;
    private static final float INFO_PADDING = 4f;
    private static final float BAR_WIDTH = 96f;
    private static final float BAR_HEIGHT = 11f;
    private static final float ROW_SPACING = 2f;

    private final Stage stage;
    private final ScreenViewport viewport;
    private final TextureAtlas atlas;
    private final BitmapFont font;
    private final Texture portraitTexture;
    private final Texture iconTexture;
    private final PixelBar healthBar;
    private final PixelBar manaBar;
    private final PixelBar staminaBar;
    private final Minimap minimap;
    private final GameOverOverlay gameOver;
    private final Runnable onRestart;

    public Hud(CharacterClass characterClass, DungeonMap map, Runnable onRestart) {
        viewport = new ScreenViewport();
        applyPixelScale(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport);
        atlas = new TextureAtlas(Gdx.files.internal("ui/hud.atlas"));

        font = new BitmapFont(Gdx.files.internal("ui/pixel-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Cada classe tem o seu retrato em ui/portrait_<classe>.png.
        portraitTexture = new Texture(
            Gdx.files.internal("ui/portrait_" + characterClass.name().toLowerCase() + ".png")
        );
        portraitTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Versão pequena do rosto, desenhada no minimapa no lugar do jogador.
        iconTexture = new Texture(
            Gdx.files.internal("ui/portrait_" + characterClass.name().toLowerCase() + "_icon.png")
        );
        iconTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        healthBar = new PixelBar(atlas, "bar-red", font, BAR_WIDTH, BAR_HEIGHT);
        staminaBar = new PixelBar(atlas, "bar-yellow", font, BAR_WIDTH, BAR_HEIGHT);
        manaBar = characterClass.usesMana()
            ? new PixelBar(atlas, "bar-blue", font, BAR_WIDTH, BAR_HEIGHT)
            : null;

        minimap = new Minimap(atlas, map, iconTexture);

        Table info = new Table();
        info.add(new Label(characterClass.name(), new Label.LabelStyle(font, Color.WHITE)))
            .left()
            .padBottom(ROW_SPACING)
            .row();

        addBar(info, healthBar);
        addBar(info, manaBar);
        addBar(info, staminaBar);

        Table status = new Table();
        status.add(new Portrait(atlas, portraitTexture)).size(PORTRAIT_SIZE).top();
        status.add(info).padLeft(INFO_PADDING).top().left();

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(SCREEN_PADDING);
        root.add(status).expandX().top().left();
        root.add(minimap).top().right();

        stage.addActor(root);

        gameOver = new GameOverOverlay(atlas, font, onRestart);
        stage.addActor(gameOver);
        this.onRestart = onRestart;
    }

    private void addBar(Table table, PixelBar bar) {
        if (bar == null) return;

        table.add(bar).size(BAR_WIDTH, BAR_HEIGHT).left().padBottom(ROW_SPACING).row();
    }

    /** A mana é opcional, só as classes que a usam têm essa barra. */
    public void update(
        HealthComponent health,
        ManaComponent mana,
        StaminaComponent stamina,
        PositionComponent position
    ) {
        healthBar.setValue(health.current, health.max);
        staminaBar.setValue(stamina.current, stamina.max);

        if (manaBar != null && mana != null) {
            manaBar.setValue(mana.current, mana.max);
        }

        minimap.updatePlayer(position.x, position.y);
    }

    /** Mostra a tela de game over enquanto o personagem estiver morto. */
    public void setDead(boolean dead) {
        if (dead == gameOver.isShown()) return;

        if (dead) {
            gameOver.show();
            Gdx.input.setInputProcessor(stage);
        } else {
            gameOver.hide();
            Gdx.input.setInputProcessor(null);
        }
    }

    // Escala inteira, senão a pixel art fica com pixels de tamanhos diferentes.
    private void applyPixelScale(int width, int height) {
        int scale = Math.max(1, Math.round(Math.min(width / DESIGN_WIDTH, height / DESIGN_HEIGHT)));

        viewport.setUnitsPerPixel(1f / scale);
    }

    public void render(float deltaTime) {
        viewport.apply();

        if (gameOver.isInteractive() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            onRestart.run();
        }

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        applyPixelScale(width, height);
        viewport.update(width, height, true);
    }

    public void dispose() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }

        minimap.dispose();
        stage.dispose();
        atlas.dispose();
        font.dispose();
        portraitTexture.dispose();
        iconTexture.dispose();
    }
}
