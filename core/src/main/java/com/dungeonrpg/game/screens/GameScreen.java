package com.dungeonrpg.game.screens;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
import com.dungeonrpg.game.systems.MapRenderingSystem;
import com.dungeonrpg.game.systems.MovementSystem;
import com.dungeonrpg.game.systems.PlayerInputSystem;
import com.dungeonrpg.game.systems.RenderingSystem;
import com.dungeonrpg.game.systems.ResourceSystem;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.world.DungeonMap;

public class GameScreen extends ScreenAdapter {

    private static final int VIEWPORT_WIDTH = 640;
    private static final int VIEWPORT_HEIGHT = 360;

    // Teclas de teste de vida e mana, devem sair quando o combate existir.
    private static final boolean DEBUG_CONTROLS = true;

    private static final int MAP_WIDTH = 80;
    private static final int MAP_HEIGHT = 60;

    private static final int PLAYER_SHEET_CELL_SIZE = 160;
    private static final float PLAYER_BODY_SIZE = 32f;
    private static final float PLAYER_SPRITE_SIZE = 72f;

    private final Engine engine;
    private final FitViewport viewport;
    private final DungeonMap map;
    private final MapRenderingSystem mapRenderingSystem;
    private final RenderingSystem renderingSystem;
    private final Texture playerTexture;

    public GameScreen() {
        OrthographicCamera camera = new OrthographicCamera();

        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.apply(true);

        map = new DungeonMap(MAP_WIDTH, MAP_HEIGHT, System.currentTimeMillis());

        // O sprite é reduzido em tela, então usa mipmaps para não serrilhar.
        playerTexture = new Texture(
            Gdx.files.internal("sprites/barbarian_sheet.png"),
            true
        );

        playerTexture.setFilter(
            Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear
        );

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

        createPlayer();
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

        StatsComponent stats = CharacterClass.WARRIOR.createStats();

        VelocityComponent velocity = new VelocityComponent();
        velocity.speed = StatsCalculator.moveSpeed(stats);

        // Hitbox nos pés do personagem, menor que o sprite.
        ColliderComponent collider = new ColliderComponent();
        collider.offsetX = 6f;
        collider.offsetY = 0f;
        collider.width = 20f;
        collider.height = 14f;

        AnimationComponent animation = new AnimationComponent();
        animation.animations = new CharacterAnimations(playerTexture, PLAYER_SHEET_CELL_SIZE);

        player.add(position);
        player.add(velocity);
        player.add(sprite);
        player.add(animation);
        player.add(new FacingComponent());
        player.add(collider);
        player.add(stats);
        player.add(new HealthComponent(StatsCalculator.maxHealth(stats)));
        player.add(new ManaComponent(StatsCalculator.maxMana(stats)));
        player.add(new StaminaComponent(StatsCalculator.maxStamina(stats)));
        player.add(new PlayerComponent());

        engine.addEntity(player);
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
    }

    @Override
    public void dispose() {
        mapRenderingSystem.dispose();
        renderingSystem.dispose();
        playerTexture.dispose();
    }
}
