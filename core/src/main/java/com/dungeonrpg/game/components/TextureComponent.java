package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public TextureRegion region;
    public float width = 32f;
    public float height = 32f;
    // Deslocamento do desenho em relação à posição da entidade.
    public float offsetX;
    public float offsetY;
}
