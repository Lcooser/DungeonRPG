package com.dungeonrpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dungeonrpg.game.DungeonGame;
import com.dungeonrpg.game.GameSession;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Difficulty;
import com.dungeonrpg.game.ui.ClassCard;
import com.dungeonrpg.game.ui.PixelButton;
import com.dungeonrpg.game.ui.PixelScale;

import java.util.EnumMap;
import java.util.Map;

/** Start screen: pick a class, type a name and choose the difficulty. */
public class TitleScreen extends ScreenAdapter {

    private static final float TITLE_SCALE = 4f;
    private static final float SECTION_SPACING = 12f;
    private static final float CARD_GAP = 12f;
    private static final float NAME_WIDTH = 132f;
    private static final float NAME_HEIGHT = 16f;
    private static final float DIFFICULTY_WIDTH = 64f;
    private static final float BUTTON_HEIGHT = 18f;
    private static final float START_WIDTH = 120f;
    private static final float TEXT_PADDING = 5f;
    private static final Color TITLE_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);
    private static final Color HINT_COLOR = new Color(0.65f, 0.65f, 0.7f, 1f);

    private final DungeonGame game;
    private final GameSession session;
    private final ScreenViewport viewport = new ScreenViewport();
    private final Stage stage;
    private final TextureAtlas atlas;
    private final BitmapFont font;
    private final Map<CharacterClass, Texture> portraits = new EnumMap<>(CharacterClass.class);
    private final Map<CharacterClass, ClassCard> cards = new EnumMap<>(CharacterClass.class);
    private final Map<Difficulty, PixelButton> difficultyButtons = new EnumMap<>(Difficulty.class);
    private final TextField nameField;

    private boolean starting;

    public TitleScreen(DungeonGame game) {
        this.game = game;
        this.session = game.getSession();

        PixelScale.apply(viewport, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport);
        atlas = new TextureAtlas(Gdx.files.internal("ui/hud.atlas"));
        font = new BitmapFont(Gdx.files.internal("ui/pixel-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        nameField = createNameField();

        Table root = new Table();
        root.setFillParent(true);

        Label title = new Label("DUNGEON RPG", new Label.LabelStyle(font, TITLE_COLOR));
        title.setFontScale(TITLE_SCALE);
        root.add(title).padBottom(SECTION_SPACING).row();

        root.add(createCards()).padBottom(SECTION_SPACING).row();
        root.add(createNameRow()).padBottom(SECTION_SPACING).row();
        root.add(createDifficultyRow()).padBottom(SECTION_SPACING).row();

        root.add(new PixelButton(atlas, font, "START", START_WIDTH, BUTTON_HEIGHT + 4f, this::start))
            .size(START_WIDTH, BUTTON_HEIGHT + 4f)
            .padBottom(SECTION_SPACING / 2f)
            .row();
        root.add(new Label("PRESS ENTER TO START", new Label.LabelStyle(font, HINT_COLOR)));

        stage.addActor(root);
        stage.setKeyboardFocus(nameField);

        select(session.getCharacterClass());
        selectDifficulty(session.getDifficulty());
    }

    private Table createCards() {
        Table row = new Table();

        for (CharacterClass characterClass : CharacterClass.values()) {
            Texture face = new Texture(
                Gdx.files.internal("ui/portrait_" + characterClass.name().toLowerCase() + ".png")
            );
            face.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            portraits.put(characterClass, face);

            ClassCard card = new ClassCard(atlas, font, characterClass, face, () -> select(characterClass));
            cards.put(characterClass, card);

            row.add(card).top().pad(0f, CARD_GAP / 2f, 0f, CARD_GAP / 2f);
        }

        return row;
    }

    private Table createNameRow() {
        Table row = new Table();

        row.add(new Label("NAME", new Label.LabelStyle(font, Color.WHITE))).padRight(TEXT_PADDING * 2f);
        row.add(nameField).size(NAME_WIDTH, NAME_HEIGHT);

        return row;
    }

    private TextField createNameField() {
        TextureRegionDrawable pixel = new TextureRegionDrawable(atlas.findRegion("pixel"));

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.messageFont = font;
        style.messageFontColor = HINT_COLOR;
        style.cursor = pixel.tint(TITLE_COLOR);
        style.cursor.setMinWidth(1f);
        style.selection = pixel.tint(new Color(0.3f, 0.5f, 0.9f, 0.6f));

        NinePatchDrawable background = new NinePatchDrawable(atlas.createPatch("bar-frame"));
        background.setLeftWidth(TEXT_PADDING);
        background.setRightWidth(TEXT_PADDING);
        background.setTopHeight(3f);
        background.setBottomHeight(3f);
        style.background = background;

        TextField field = new TextField(session.getPlayerName(), style);
        field.setMaxLength(GameSession.MAX_NAME_LENGTH);
        field.setMessageText("HERO");

        // The pixel font only has uppercase letters, digits and a few symbols.
        field.setTextFieldFilter((textField, character) ->
            Character.isLetterOrDigit(character) && character < 128 || character == ' ' || character == '-');

        field.setTextFieldListener((textField, character) -> {
            String upper = textField.getText().toUpperCase();

            if (!upper.equals(textField.getText())) {
                int cursor = textField.getCursorPosition();
                textField.setText(upper);
                textField.setCursorPosition(cursor);
            }
        });

        return field;
    }

    private Table createDifficultyRow() {
        Table row = new Table();

        for (Difficulty difficulty : Difficulty.values()) {
            PixelButton button = new PixelButton(
                atlas, font, difficulty.getLabel(), DIFFICULTY_WIDTH, BUTTON_HEIGHT, () -> selectDifficulty(difficulty)
            );
            difficultyButtons.put(difficulty, button);

            row.add(button).size(DIFFICULTY_WIDTH, BUTTON_HEIGHT).pad(0f, 3f, 0f, 3f);
        }

        return row;
    }

    private void select(CharacterClass characterClass) {
        session.setCharacterClass(characterClass);

        for (Map.Entry<CharacterClass, ClassCard> entry : cards.entrySet()) {
            entry.getValue().setSelected(entry.getKey() == characterClass);
        }
    }

    private void selectDifficulty(Difficulty difficulty) {
        session.setDifficulty(difficulty);

        for (Map.Entry<Difficulty, PixelButton> entry : difficultyButtons.entrySet()) {
            entry.getValue().setSelected(entry.getKey() == difficulty);
        }
    }

    private void start() {
        if (starting) return;

        starting = true;
        session.setPlayerName(nameField.getText());

        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameScreen(game));
            dispose();
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            start();
        }

        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        viewport.apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        PixelScale.apply(viewport, width, height);
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
        font.dispose();

        for (Texture texture : portraits.values()) {
            texture.dispose();
        }
    }
}
