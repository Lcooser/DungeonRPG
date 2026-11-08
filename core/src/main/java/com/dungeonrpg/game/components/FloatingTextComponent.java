package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/** Texto que sobe e some, usado nos números de dano. */
public class FloatingTextComponent implements Component {

    public static final float DURATION = 0.8f;
    public static final float RISE_SPEED = 26f;

    public String text;
    public final Color color = new Color(Color.WHITE);
    public final GlyphLayout layout = new GlyphLayout();
    public float timeLeft = DURATION;
}
