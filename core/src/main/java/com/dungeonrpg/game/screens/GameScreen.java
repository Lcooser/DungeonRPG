package com.dungeonrpg.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends ScreenAdapter {

    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final ShapeRenderer shapeRenderer;

    private float playerX = 304;
    private float playerY = 164;

    public GameScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(640, 360, camera);
        shapeRenderer = new ShapeRenderer();

        viewport.apply(true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        camera.update();
        viewport.apply();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.9f, 0.6f, 0.2f, 1f);
        shapeRenderer.rect(playerX, playerY, 32, 32);

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
