package com.dungeonrpg.game.screens;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dungeonrpg.game.DungeonGame;
import com.dungeonrpg.game.animation.CharacterAnimations;
import com.dungeonrpg.game.components.AnimationComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.StatsComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.systems.AnimationSystem;
import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.StatsCalculator;
import com.dungeonrpg.game.systems.CameraSystem;
import com.dungeonrpg.game.systems.DebugInputSystem;
import com.dungeonrpg.game.systems.HealthSystem;
import com.dungeonrpg.game.systems.HudSystem;
import com.dungeonrpg.game.systems.MapRenderingSystem;
import com.dungeonrpg.game.systems.MovementSystem;
import com.dungeonrpg.game.systems.PlayerInputSystem;
import com.dungeonrpg.game.systems.RenderingSystem;
import com.dungeonrpg.game.systems.ResourceSystem;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.ui.Hud;
import com.dungeonrpg.game.world.DungeonMap;

public class GameScreen extends ScreenAdapter {

    private static final int VIEWPORT_WIDTH = 640;
    private static final int VIEWPORT_HEIGHT = 360;
    // Em telas mais largas o mundo se estende até este limite, em vez de ficar com faixas pretas.
    private static final int MAX_VIEWPORT_WIDTH = 960;
    private static final int MAX_VIEWPORT_HEIGHT = 540;

    // Teclas de teste de vida e mana, devem sair quando o combate existir.
    private static final boolean DEBUG_CONTROLS = true;

    private static final CharacterClass PLAYER_CLASS = CharacterClass.BARBARIAN;

    private static final int MAP_WIDTH = 80;
    private static final int MAP_HEIGHT = 60;

    private static final String PLAYER_SHEET_PATH = "sprites/barbarian_sheet.png";
    // Folha opcional com a animação de cansaço, sem ela o personagem usa o idle.
    private static final String PLAYER_TIRED_SHEET_PATH = "sprites/barbarian_tired.png";
    private static final int PLAYER_SHEET_CELL_SIZE = 160;
    private static final float PLAYER_BODY_SIZE = 32f;
    private static final float PLAYER_SPRITE_SIZE = 72f;

    private final DungeonGame game;
    private final Engine engine;
    private final ExtendViewport viewport;
    private final DungeonMap map;
    private final MapRenderingSystem mapRenderingSystem;
    private final RenderingSystem renderingSystem;
    private final HudSystem hudSystem;
    private final Texture playerTexture;
    private final Texture playerTiredTexture;

    private boolean restarting;

    public GameScreen(DungeonGame game) {
        this.game = game;

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

        // O sprite é reduzido em tela, então usa mipmaps para não serrilhar.
        playerTexture = loadSheet(PLAYER_SHEET_PATH);
        playerTiredTexture = Gdx.files.internal(PLAYER_TIRED_SHEET_PATH).exists()
            ? loadSheet(PLAYER_TIRED_SHEET_PATH)
            : null;

        engine = new Engine();

        engine.addSystem(new PlayerInputSystem());

        if (DEBUG_CONTROLS) {
            engine.addSystem(new DebugInputSystem());
        }

        engine.addSystem(new ResourceSystem());
        engine.addSystem(new HealthSystem());
        engine.addSystem(new MovementSystem(map));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new CameraSystem(camera, map));

        mapRenderingSystem = new MapRenderingSystem(camera, map);
        engine.addSystem(mapRenderingSystem);

        renderingSystem = new RenderingSystem(camera);
        engine.addSystem(renderingSystem);

        hudSystem = new HudSystem(new Hud(PLAYER_CLASS, map, this::restart));
        engine.addSystem(hudSystem);

        createPlayer();
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
        Entity player = new Entity();

        // A posição é o canto da caixa de 32x32 do personagem, o sprite maior é centralizado nela.
        TextureComponent sprite = new TextureComponent();
        sprite.width = PLAYER_SPRITE_SIZE;
        sprite.height = PLAYER_SPRITE_SIZE;
        sprite.offsetX = (PLAYER_BODY_SIZE - PLAYER_SPRITE_SIZE) / 2f;
        sprite.offsetY = -2f;

        PositionComponent position = new PositionComponent();
        position.x = map.getSpawnX() - PLAYER_BODY_SIZE / 2f;
        position.y = map.getSpawnY() - PLAYER_BODY_SIZE / 2f;

        StatsComponent stats = PLAYER_CLASS.createStats();

        VelocityComponent velocity = new VelocityComponent();
        velocity.speed = StatsCalculator.moveSpeed(stats);

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

        if (PLAYER_CLASS.usesMana()) {
            player.add(new ManaComponent(StatsCalculator.maxMana(stats)));
        }

        player.add(new StaminaComponent(StatsCalculator.maxStamina(stats)));
        player.add(new PlayerComponent());

        engine.addEntity(player);
    }

    private void restart() {
        if (restarting) return;

        restarting = true;

        // A troca de tela fica para depois do frame, senão os recursos seriam liberados durante o desenho.
        Gdx.app.postRunnable(() -> {
            game.setScreen(new GameScreen(game));
            dispose();
        });
    }

    @Override
    public void render(float delta) {
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
        hudSystem.dispose();
        playerTexture.dispose();

        if (playerTiredTexture != null) {
            playerTiredTexture.dispose();
        }
    }
}
