package com.dungeonrpg.game.screens;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.systems.CameraSystem;
import com.dungeonrpg.game.systems.MapRenderingSystem;
import com.dungeonrpg.game.systems.MovementSystem;
import com.dungeonrpg.game.systems.PlayerInputSystem;
import com.dungeonrpg.game.systems.RenderingSystem;
import com.dungeonrpg.game.tags.PlayerComponent;
import com.dungeonrpg.game.world.DungeonMap;

public class GameScreen extends ScreenAdapter {

    private static final int MAP_WIDTH = 80;
    private static final int MAP_HEIGHT = 60;

    private final Engine engine;
    private final FitViewport viewport;
    private final DungeonMap map;
    private final MapRenderingSystem mapRenderingSystem;
    private final RenderingSystem renderingSystem;
    private final Texture playerTexture;

    public GameScreen() {
        OrthographicCamera camera = new OrthographicCamera();

        viewport = new FitViewport(640, 360, camera);
        viewport.apply(true);

        map = new DungeonMap(MAP_WIDTH, MAP_HEIGHT, System.currentTimeMillis());

        playerTexture = new Texture(
            Gdx.files.internal("sprites/barbarian.png")
        );

        // Preserva os pixels definidos ao ampliar a imagem.
        playerTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        engine = new Engine();

        engine.addSystem(new PlayerInputSystem());
        engine.addSystem(new MovementSystem(map));
        engine.addSystem(new CameraSystem(camera, map));

        mapRenderingSystem = new MapRenderingSystem(camera, map);
        engine.addSystem(mapRenderingSystem);

        renderingSystem = new RenderingSystem(camera);
        engine.addSystem(renderingSystem);

        createPlayer();
    }

    private void createPlayer() {
        Entity player = new Entity();

        TextureComponent sprite = new TextureComponent();
        sprite.texture = playerTexture;
        sprite.width = 32f;
        sprite.height = 32f;

        PositionComponent position = new PositionComponent();
        position.x = map.getSpawnX() - sprite.width / 2f;
        position.y = map.getSpawnY() - sprite.height / 2f;

        VelocityComponent velocity = new VelocityComponent();

        // Hitbox nos pés do personagem, menor que o sprite.
        ColliderComponent collider = new ColliderComponent();
        collider.offsetX = 6f;
        collider.offsetY = 0f;
        collider.width = 20f;
        collider.height = 14f;

        player.add(position);
        player.add(velocity);
        player.add(sprite);
        player.add(collider);
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
