package com.dungeonrpg.game.components;

import com.badlogic.ashley.core.Component;
import com.dungeonrpg.game.rpg.Ability;

public class AttackComponent implements Component {

    // Slot 0 is the basic attack, the following ones are skills.
    public final Ability[] abilities;
    public final float[] cooldowns;

    // Slot requested this frame by input or AI, -1 when none. AttackSystem decides if it can start.
    public int requestedSlot = -1;

    public Ability current;
    public boolean attacking;
    public int hitsDone;
    public float time;

    public AttackComponent(Ability... abilities) {
        this.abilities = abilities;
        this.cooldowns = new float[abilities.length];
    }

    public Ability primary() {
        return abilities[0];
    }
}
