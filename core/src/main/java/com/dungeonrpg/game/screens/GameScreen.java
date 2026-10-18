package com.dungeonrpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends ScreenAdapter {


    private final SpriteBatch batch;
    private final Texture playerTexture;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    public GameScreen() {
        batch = new SpriteBatch();
        playerTexture = new Texture(
            Gdx.files.internal("player.png")
        );
        camera = new OrthographicCamera();
        viewport = new FitViewport(640, 360, camera);
        viewport.apply(true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(playerTexture, 304, 164, 32, 32);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
            viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
    }

}
