package com.dungeonrpg.game.tags;

import com.badlogic.ashley.core.Component;

public class DeadComponent implements Component {

    // Tempo desde a morte, usado para remover o corpo dos monstros.
    public float time;
}
