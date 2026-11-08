package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

/** Estado de dano recém-sofrido: toca a animação de hurt, pisca em vermelho e interrompe a IA. */
public class HurtComponent implements Component {

    public static final float DURATION = 0.25f;

    public float timeLeft = DURATION;
}
