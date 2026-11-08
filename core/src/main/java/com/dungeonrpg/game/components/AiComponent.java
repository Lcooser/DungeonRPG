package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;

public class AiComponent implements Component {

    // Distância a partir da qual o monstro percebe o jogador e passa a persegui-lo.
    public float aggroRange;

    public AiComponent(float aggroRange) {
        this.aggroRange = aggroRange;
    }
}
