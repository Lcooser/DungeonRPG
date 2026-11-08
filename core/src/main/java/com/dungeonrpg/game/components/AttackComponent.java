package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.rpg.Ability;

public class AttackComponent implements Component {

    // Duração da animação de ataque (5 frames de 0.08s), em que o golpe acerta no windup da habilidade.
    public static final float DURATION = 0.4f;

    public Ability primary;
    public Ability secondary;

    // Pedido feito pelo input ou pela IA no frame, o AttackSystem decide se pode começar.
    public Ability requested;

    public Ability current;
    public boolean attacking;
    public boolean hitDone;
    public float time;
    public float cooldownLeft;

    public AttackComponent(Ability primary, Ability secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
}
