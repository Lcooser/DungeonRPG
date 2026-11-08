package com.dungeonrpg.game.screens;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dungeonrpg.game.DungeonGame;
import com.dungeonrpg.game.GameSession;
import com.dungeonrpg.game.animation.CharacterAnimations;
import com.dungeonrpg.game.components.AnimationComponent;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.EquipmentComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.LevelComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.entities.MonsterFactory;
import com.dungeonrpg.game.item.ItemDrops;
import com.dungeonrpg.game.item.ItemIcons;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.Progression;
import com.dungeonrpg.game.rpg.StatType;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.systems.AnimationSystem;
import com.dungeonrpg.game.systems.AttackSystem;
import com.dungeonrpg.game.systems.CameraSystem;
import com.dungeonrpg.game.systems.CorpseSystem;
import com.dungeonrpg.game.systems.DebugInputSystem;
import com.dungeonrpg.game.systems.EffectSystem;
import com.dungeonrpg.game.systems.ExperienceSystem;
import com.dungeonrpg.game.systems.FloatingTextSystem;
import com.dungeonrpg.game.systems.HealthSystem;
import com.dungeonrpg.game.systems.HudSystem;
import com.dungeonrpg.game.systems.HurtSystem;
import com.dungeonrpg.game.systems.ItemLabelSystem;
import com.dungeonrpg.game.systems.LootSystem;
import com.dungeonrpg.game.systems.MapRenderingSystem;
import com.dungeonrpg.game.systems.MonsterAiSystem;
import com.dungeonrpg.game.systems.MonsterHealthBarSystem;
import com.dungeonrpg.game.systems.MovementSystem;
import com.dungeonrpg.game.systems.PickupSystem;
import com.dungeonrpg.game.systems.PlayerInputSystem;
import com.dungeonrpg.game.systems.ProgressionSystem;
import com.dungeonrpg.game.systems.ProjectileSystem;
import com.dungeonrpg.game.systems.RenderingSystem;
import com.dungeonrpg.game.systems.ResourceSystem;
import com.dungeonrpg.game.systems.StatusEffectSystem;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.ui.Hud;
import com.dungeonrpg.game.world.DungeonMap;
import com.dungeonrpg.game.world.MonsterSpawner;

public class GameScreen extends ScreenAdapter implements Hud.Listener {

    private static final int VIEWPORT_WIDTH = 640;
    private static final int VIEWPORT_HEIGHT = 360;
    // Em telas mais largas o mundo se estende até este limite, em vez de ficar com faixas pretas.
    private static final int MAX_VIEWPORT_WIDTH = 960;
    private static final int MAX_VIEWPORT_HEIGHT = 540;

    // Test keys (1 damage, 2 spend resource, 3 restore, F1 switch class), to remove before the final game.
    private static final boolean DEBUG_CONTROLS = true;

    private static final int MAP_WIDTH = 80;
    private static final int MAP_HEIGHT = 60;

    private static final int PLAYER_SHEET_CELL_SIZE = 160;
    private static final float PLAYER_BODY_SIZE = 32f;
    private static final float PLAYER_SPRITE_SIZE = 72f;

    private final DungeonGame game;
    private final GameSession session;
    private final CharacterClass characterClass;
    private final Engine engine;
    private final ExtendViewport viewport;
    private final DungeonMap map;
    private final Effects effects;
    private final MonsterFactory monsterFactory;
    private final ItemIcons itemIcons;
    private final ItemLabelSystem itemLabelSystem;
    private final MapRenderingSystem mapRenderingSystem;
    private final RenderingSystem renderingSystem;
    private final FloatingTextSystem floatingTextSystem;
    private final MonsterHealthBarSystem healthBarSystem;
    private final HudSystem hudSystem;
    private final Texture playerTexture;
    private final Texture playerTiredTexture;
    private final Hud hud;

    private Entity player;
    private boolean restarting;

    public GameScreen(DungeonGame game) {
        this.game = game;
        this.session = game.getSession();
        this.characterClass = session.getCharacterClass();

        OrthographicCamera camera = new OrthographicCamera();

        viewport = new ExtendViewport(
            VIEWPORT_WIDTH,
            VIEWPORT_HEIGHT,
            MAX_VIEWPORT_WIDTH,
            MAX_VIEWPORT_HEIGHT,
            camera
        );
        viewport.apply(true);

        map = new DungeonMap(MAP_WIDTH, MAP_HEIGHT, System.currentTimeMillis());

        // The sprite is shrunk on screen, so it uses mipmaps to avoid shimmering.
        playerTexture = loadSheet(characterClass.getSheetPath());
        // The tired sheet is optional, without it the character uses idle.
        playerTiredTexture = Gdx.files.internal(characterClass.getTiredSheetPath()).exists()
            ? loadSheet(characterClass.getTiredSheetPath())
            : null;

        engine = new Engine();
        effects = new Effects(engine);
        monsterFactory = new MonsterFactory(engine, session.getDifficulty().getMonsterSpeedMultiplier());
        itemIcons = new ItemIcons();

        hud = new Hud(characterClass, session.getPlayerName(), map, bossName(), effects, itemIcons, this);

        engine.addSystem(new PlayerInputSystem(hud::isPointerOverButton));

        if (DEBUG_CONTROLS) {
            engine.addSystem(new DebugInputSystem());
        }

        engine.addSystem(new MonsterAiSystem());
        engine.addSystem(new AttackSystem(effects, map, monsterFactory));
        engine.addSystem(new ProjectileSystem(map, effects));
        engine.addSystem(new ProgressionSystem(session.getDifficulty().getPlayerSpeedMultiplier()));
        engine.addSystem(new ExperienceSystem(effects));
        engine.addSystem(new ResourceSystem());
        engine.addSystem(new HealthSystem(effects));
        engine.addSystem(new CorpseSystem());
        engine.addSystem(new LootSystem(new ItemDrops(engine, itemIcons), characterClass));
        engine.addSystem(new PickupSystem(effects));
        engine.addSystem(new MovementSystem(map));
        engine.addSystem(new HurtSystem());
        engine.addSystem(new StatusEffectSystem());
        engine.addSystem(new EffectSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new CameraSystem(camera, map));

        mapRenderingSystem = new MapRenderingSystem(camera, map, session.getTheme());
        engine.addSystem(mapRenderingSystem);

        renderingSystem = new RenderingSystem(camera);
        engine.addSystem(renderingSystem);

        floatingTextSystem = new FloatingTextSystem(camera, effects.getFont());
        engine.addSystem(floatingTextSystem);

        itemLabelSystem = new ItemLabelSystem(camera, effects.getFont());
        engine.addSystem(itemLabelSystem);

        healthBarSystem = new MonsterHealthBarSystem(camera);
        engine.addSystem(healthBarSystem);

        hudSystem = new HudSystem(hud);
        engine.addSystem(hudSystem);

        createPlayer();
        new MonsterSpawner(monsterFactory, session.getTheme()).populate(map);
    }

    private String bossName() {
        return map.getBossRoom() == null ? null : session.getTheme().getBoss().getDisplayName();
    }

    private Texture loadSheet(String path) {
        Texture texture = new Texture(Gdx.files.internal(path), true);

        texture.setFilter(
            Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear
        );

        return texture;
    }

    private void createPlayer() {
        player = new Entity();

        // A posição é o canto da caixa de 32x32 do personagem, o sprite maior é centralizado nela.
        TextureComponent sprite = new TextureComponent();
        sprite.width = PLAYER_SPRITE_SIZE;
        sprite.height = PLAYER_SPRITE_SIZE;
        sprite.offsetX = (PLAYER_BODY_SIZE - PLAYER_SPRITE_SIZE) / 2f;
        sprite.offsetY = -2f;

        PositionComponent position = new PositionComponent();
        position.x = map.getSpawnX() - PLAYER_BODY_SIZE / 2f;
        position.y = map.getSpawnY() - PLAYER_BODY_SIZE / 2f;

        StatsComponent stats = characterClass.createStats();

        VelocityComponent velocity = new VelocityComponent();
        velocity.speed = StatsCalculator.moveSpeed(stats) * session.getDifficulty().getPlayerSpeedMultiplier();

        // Hitbox nos pés do personagem, menor que o sprite.
        ColliderComponent collider = new ColliderComponent();
        collider.offsetX = 6f;
        collider.offsetY = 0f;
        collider.width = 20f;
        collider.height = 14f;

        AnimationComponent animation = new AnimationComponent();
        animation.animations = new CharacterAnimations(playerTexture, playerTiredTexture, PLAYER_SHEET_CELL_SIZE);

        player.add(position);
        player.add(velocity);
        player.add(sprite);
        player.add(animation);
        player.add(new FacingComponent());
        player.add(collider);
        player.add(stats);
        player.add(new HealthComponent(StatsCalculator.maxHealth(stats)));
        player.add(new AttackComponent(characterClass.getAbilities()));

        if (characterClass.usesMana()) {
            player.add(new ManaComponent(StatsCalculator.maxMana(stats)));
        }

        player.add(new StaminaComponent(StatsCalculator.maxStamina(stats)));
        player.add(new LevelComponent());
        player.add(new EquipmentComponent());
        player.add(new PlayerComponent());

        engine.addEntity(player);
    }

    @Override
    public void onRetry() {
        restart();
    }

    @Override
    public void onMenu() {
        returnToMenu();
    }

    @Override
    public void onSpendPoint(StatType stat) {
        Progression.spendPoint(player, stat);
    }

    private void restart() {
        if (restarting) return;

        restarting = true;

        // Swap after the frame, otherwise resources would be freed while still drawing.
        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameScreen(game));
            dispose();
        });
    }

    private void returnToMenu() {
        if (restarting) return;

        restarting = true;

        Gdx.app.postRunnable(() -> {
            game.setScreen(new TitleScreen(game));
            dispose();
        });
    }

    private void switchClass() {
        CharacterClass[] classes = CharacterClass.values();

        session.setCharacterClass(classes[(characterClass.ordinal() + 1) % classes.length]);
        restart();
    }

    @Override
    public void render(float delta) {
        if (DEBUG_CONTROLS && Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            switchClass();
        }

        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        viewport.apply();
        engine.update(Math.min(delta, 1f / 30f));
    }

    @Override
    public void resize(int width, int height) {
        // A câmera é controlada pelo CameraSystem, então não recentraliza aqui.
        viewport.update(width, height, false);
        hudSystem.resize(width, height);
    }

    @Override
    public void dispose() {
        mapRenderingSystem.dispose();
        renderingSystem.dispose();
        floatingTextSystem.dispose();
        itemLabelSystem.dispose();
        itemIcons.dispose();
        healthBarSystem.dispose();
        hudSystem.dispose();
        effects.dispose();
        monsterFactory.dispose();
        playerTexture.dispose();

        if (playerTiredTexture != null) {
            playerTiredTexture.dispose();
        }
    }
}
