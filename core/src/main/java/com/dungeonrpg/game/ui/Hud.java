package com.dungeonrpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Progression;
import com.dungeonrpg.game.rpg.StatType;
import com.dungeonrpg.game.world.DungeonMap;

public class Hud {

    /** Actions the HUD triggers but does not own. */
    public interface Listener {
        void onRetry();

        void onMenu();

        void onSpendPoint(StatType stat);
    }

    private static final Color GAME_OVER_COLOR = new Color(0.85f, 0.2f, 0.2f, 1f);
    private static final Color VICTORY_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);
    private static final Color BOSS_NAME_COLOR = new Color(1f, 0.55f, 0.5f, 1f);
    private static final float VICTORY_DELAY = 2.2f;
    private static final float BOSS_BAR_WIDTH = 220f;
    private static final float BOSS_BAR_RANGE = 320f;
    private static final float BANNER_TOP = 34f;
    private static final float BANNER_SECONDS = 7f;
    private static final float BANNER_FADE = 1f;
    private static final Color LEVEL_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);
    // Leaves time for the death animation before the panel appears.
    private static final float GAME_OVER_DELAY = 1.4f;

    private static final float SCREEN_PADDING = 6f;
    private static final float PORTRAIT_SIZE = 40f;
    private static final float INFO_PADDING = 4f;
    private static final float BAR_WIDTH = 96f;
    private static final float BAR_HEIGHT = 11f;
    private static final float ROW_SPACING = 2f;
    private static final float STATS_TOP_SPACING = 6f;
    private static final float PLAYER_CENTER_OFFSET = 16f;

    private final Stage stage;
    private final ScreenViewport viewport;
    private final TextureAtlas atlas;
    private final BitmapFont font;
    private final Texture portraitTexture;
    private final Texture iconTexture;
    private final PixelBar healthBar;
    private final PixelBar manaBar;
    private final PixelBar staminaBar;
    private final PixelBar xpBar;
    private final Label levelLabel;
    private final StatsPanel statsPanel;
    private final Minimap minimap;
    private final Hotbar hotbar;
    private final BossCompass bossCompass;
    private final PixelBar bossBar;
    private final Label bossNameLabel;
    private final Table bossBarLayer;
    private final GameOverOverlay gameOver;
    private final GameOverOverlay victory;
    private final Listener listener;
    private final Vector2 pointer = new Vector2();

    private int shownLevel = -1;
    private float playerCenterX;
    private float playerCenterY;
    private String shownBossName;

    public Hud(
        CharacterClass characterClass,
        String playerName,
        DungeonMap map,
        String bossName,
        Effects effects,
        Listener listener
    ) {
        this.listener = listener;

        viewport = new ScreenViewport();
        PixelScale.apply(viewport, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport);
        atlas = new TextureAtlas(Gdx.files.internal("ui/hud.atlas"));

        font = new BitmapFont(Gdx.files.internal("ui/pixel-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Each class has its portrait at ui/portrait_<class>.png and a small icon for the minimap.
        portraitTexture = new Texture(
            Gdx.files.internal("ui/portrait_" + characterClass.name().toLowerCase() + ".png")
        );
        portraitTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        iconTexture = new Texture(
            Gdx.files.internal("ui/portrait_" + characterClass.name().toLowerCase() + "_icon.png")
        );
        iconTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        healthBar = new PixelBar(atlas, "bar-red", font, BAR_WIDTH, BAR_HEIGHT);
        staminaBar = new PixelBar(atlas, "bar-yellow", font, BAR_WIDTH, BAR_HEIGHT);
        xpBar = new PixelBar(atlas, "bar-green", font, BAR_WIDTH, BAR_HEIGHT);
        manaBar = characterClass.usesMana()
            ? new PixelBar(atlas, "bar-blue", font, BAR_WIDTH, BAR_HEIGHT)
            : null;

        statsPanel = new StatsPanel(atlas, font, listener::onSpendPoint);
        minimap = new Minimap(atlas, map, iconTexture);
        hotbar = new Hotbar(atlas, font, effects, characterClass.getAbilities());
        bossCompass = new BossCompass(atlas, font, 0f);
        bossBar = new PixelBar(atlas, "bar-red", font, BOSS_BAR_WIDTH, BAR_HEIGHT);
        bossNameLabel = new Label("", new Label.LabelStyle(font, BOSS_NAME_COLOR));

        Label.LabelStyle white = new Label.LabelStyle(font, Color.WHITE);
        levelLabel = new Label("", new Label.LabelStyle(font, LEVEL_COLOR));

        Table heading = new Table();
        heading.add(new Label(playerName, white)).left();
        heading.add(levelLabel).expandX().right();

        Table info = new Table();
        info.add(heading).width(BAR_WIDTH).padBottom(ROW_SPACING).row();

        addBar(info, healthBar);
        addBar(info, manaBar);
        addBar(info, staminaBar);
        addBar(info, xpBar);

        Table status = new Table();
        status.add(new Portrait(atlas, portraitTexture)).size(PORTRAIT_SIZE).top();
        status.add(info).padLeft(INFO_PADDING).top().left();

        Table left = new Table();
        left.add(status).left().row();
        left.add(statsPanel).left().padTop(STATS_TOP_SPACING);

        Table root = new Table();
        root.setFillParent(true);
        root.top().pad(SCREEN_PADDING);
        root.add(left).expandX().top().left();

        Table right = new Table();
        right.add(minimap).right().row();
        right.add(bossCompass).size(minimap.getWidth(), BossCompass.HEIGHT).right().padTop(ROW_SPACING);
        root.add(right).top().right();

        stage.addActor(root);

        Table bottom = new Table();
        bottom.setFillParent(true);
        bottom.bottom().padBottom(SCREEN_PADDING + 2f);
        bottom.add(hotbar).size(hotbar.getWidth(), hotbar.getHeight());
        stage.addActor(bottom);

        bossBarLayer = new Table();
        bossBarLayer.setFillParent(true);
        bossBarLayer.top().padTop(SCREEN_PADDING);
        bossBarLayer.add(bossNameLabel).padBottom(ROW_SPACING).row();
        bossBarLayer.add(bossBar).size(BOSS_BAR_WIDTH, BAR_HEIGHT);
        bossBarLayer.setVisible(false);
        stage.addActor(bossBarLayer);

        if (bossName != null) {
            stage.addActor(createObjectiveBanner(font, bossName));
        }

        gameOver = new GameOverOverlay(
            atlas, font, "GAME OVER", GAME_OVER_COLOR, GAME_OVER_DELAY, listener::onRetry, listener::onMenu
        );
        stage.addActor(gameOver);

        victory = new GameOverOverlay(
            atlas, font, "VICTORY", VICTORY_COLOR, VICTORY_DELAY, listener::onRetry, listener::onMenu
        );
        stage.addActor(victory);

        // The stage always takes mouse input, since the stat buttons are clickable during play.
        Gdx.input.setInputProcessor(stage);
    }

    private void addBar(Table table, PixelBar bar) {
        if (bar == null) return;

        table.add(bar).size(BAR_WIDTH, BAR_HEIGHT).left().padBottom(ROW_SPACING).row();
    }

    /** Mana is optional, only classes that use it have that bar. */
    public void update(
        HealthComponent health,
        ManaComponent mana,
        StaminaComponent stamina,
        PositionComponent position,
        StatsComponent stats,
        LevelComponent level,
        AttackComponent attack
    ) {
        healthBar.setValue(health.current, health.max);
        staminaBar.setValue(stamina.current, stamina.max);
        xpBar.setValue(level.xp, Progression.xpToNextLevel(level.level));

        if (manaBar != null && mana != null) {
            manaBar.setValue(mana.current, mana.max);
        }

        if (level.level != shownLevel) {
            shownLevel = level.level;
            levelLabel.setText("LV " + level.level);
        }

        statsPanel.update(stats, level);
        hotbar.update(attack, mana, stamina);
        minimap.updatePlayer(position.x, position.y);

        // The position is the corner of the body box, close enough to the center for distances.
        playerCenterX = position.x + PLAYER_CENTER_OFFSET;
        playerCenterY = position.y + PLAYER_CENTER_OFFSET;
    }

    /** Updates the boss bar, compass and minimap marker. A null name means there is no boss. */
    public void updateBoss(String name, HealthComponent health, float bossX, float bossY, boolean dead) {
        boolean present = name != null;

        minimap.setBossAlive(present && !dead);
        bossCompass.update(playerCenterX, playerCenterY, bossX, bossY, present && !dead);

        if (!present || dead) {
            bossBarLayer.setVisible(false);

            if (dead && !victory.isShown()) {
                victory.show();
            }

            return;
        }

        if (!name.equals(shownBossName)) {
            shownBossName = name;
            bossNameLabel.setText(name);
        }

        float dx = bossX - playerCenterX;
        float dy = bossY - playerCenterY;

        // The bar only appears once the player is close enough to be in the fight.
        bossBarLayer.setVisible(dx * dx + dy * dy <= BOSS_BAR_RANGE * BOSS_BAR_RANGE);
        bossBar.setValue(health.current, health.max);
    }

    private Table createObjectiveBanner(BitmapFont font, String bossName) {
        Table banner = new Table();
        banner.setFillParent(true);
        banner.top().padTop(BANNER_TOP);
        banner.setTouchable(Touchable.disabled);
        banner.add(new Label("DEFEAT THE " + bossName, new Label.LabelStyle(font, VICTORY_COLOR))).row();
        banner.add(new Label("FOLLOW THE ARROW", new Label.LabelStyle(font, Color.WHITE))).padTop(ROW_SPACING);

        banner.addAction(Actions.sequence(
            Actions.delay(BANNER_SECONDS),
            Actions.fadeOut(BANNER_FADE),
            Actions.visible(false)
        ));

        return banner;
    }

    /** Shows the game over panel while the character is dead. */
    public void setDead(boolean dead) {
        if (dead == gameOver.isShown()) return;

        if (dead) {
            gameOver.show();
        } else {
            gameOver.hide();
        }
    }

    /** True while the mouse is over a clickable HUD element, so the click should not reach the game. */
    public boolean isPointerOverButton() {
        stage.screenToStageCoordinates(pointer.set(Gdx.input.getX(), Gdx.input.getY()));

        Actor hit = stage.hit(pointer.x, pointer.y, true);

        return hit instanceof PixelButton && hit.isVisible();
    }

    public void render(float deltaTime) {
        viewport.apply();

        boolean endPanelOpen = gameOver.isInteractive() || victory.isInteractive();

        if (endPanelOpen && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            listener.onRetry();
        }

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        PixelScale.apply(viewport, width, height);
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
