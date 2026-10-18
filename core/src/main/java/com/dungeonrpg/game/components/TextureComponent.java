package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public Texture texture;
    public float width = 32f;
    public float height = 32f;
}
