package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public TextureRegion region;
    public float width = 32f;
    public float height = 32f;
    // Deslocamento do desenho em relação à posição da entidade.
    public float offsetX;
    public float offsetY;
    // Graus em torno do centro do desenho.
    public float rotation;
    // Camadas maiores são desenhadas por cima, e dentro de cada camada quem está mais abaixo na tela fica na frente.
    public int layer;
    // Multiplied over the sprite colors, null for none.
    public Color tint;
}
