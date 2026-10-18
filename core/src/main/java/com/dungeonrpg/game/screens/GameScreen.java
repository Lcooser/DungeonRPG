package com.dungeonrpg.game.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.systems.MovementSystem;
import com.dungeonrpg.game.systems.PlayerInputSystem;
import com.dungeonrpg.game.systems.RenderingSystem;
import com.dungeonrpg.game.tags.PlayerComponent;

public class GameScreen extends ScreenAdapter {

    private final Engine engine;
    private final FitViewport viewport;
    private final RenderingSystem renderingSystem;

    public GameScreen() {
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(640, 360, camera);
        viewport.apply(true);

        engine = new Engine();

        engine.addSystem(new PlayerInputSystem());
        engine.addSystem(new MovementSystem());

        renderingSystem = new RenderingSystem(camera);
        engine.addSystem(renderingSystem);

        createPlayer();
    }

    private void createPlayer() {
        Entity player = new Entity();

        PositionComponent position = new PositionComponent();
        position.x = 304;
        position.y = 164;

        VelocityComponent velocity = new VelocityComponent();

        player.add(position);
        player.add(velocity);
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
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderingSystem.dispose();
    }
}
