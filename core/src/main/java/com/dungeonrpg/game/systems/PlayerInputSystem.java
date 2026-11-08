package com.dungeonrpg.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.tags.DeadComponent;
import com.dungeonrpg.game.tags.PlayerComponent;

import java.util.function.BooleanSupplier;

public class PlayerInputSystem extends IteratingSystem {

    private static final float SPRINT_MULTIPLIER = 1.6f;
    private static final float SPRINT_STAMINA_PER_SECOND = 30f;

    private final ComponentMapper<VelocityComponent> velocities =
        ComponentMapper.getFor(VelocityComponent.class);

    private final ComponentMapper<StaminaComponent> staminas =
        ComponentMapper.getFor(StaminaComponent.class);

    private final ComponentMapper<FacingComponent> facings =
        ComponentMapper.getFor(FacingComponent.class);

    private final ComponentMapper<AttackComponent> attacks =
        ComponentMapper.getFor(AttackComponent.class);

    private final Vector2 direction = new Vector2();
    // True while the pointer is over a HUD button, so clicking it does not also attack.
    private final BooleanSupplier pointerOverUi;

    public PlayerInputSystem(BooleanSupplier pointerOverUi) {
        super(Family.all(
            PlayerComponent.class,
            VelocityComponent.class
        ).exclude(DeadComponent.class).get(), 0);

        this.pointerOverUi = pointerOverUi;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = velocities.get(entity);

        direction.set(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1;

        direction.nor();

        float speed = velocity.speed;

        if (isSprinting(entity, deltaTime)) {
            speed *= SPRINT_MULTIPLIER;
        }

        velocity.x = direction.x * speed;
        velocity.y = direction.y * speed;

        // Face the pressed direction right away so an attack in the same frame goes that way.
        FacingComponent facing = facings.get(entity);

        if (facing != null && !direction.isZero()) {
            facing.direction = Direction.fromVector(direction.x, direction.y);
        }

        requestAttack(entity);
    }

    private void requestAttack(Entity entity) {
        AttackComponent attack = attacks.get(entity);

        if (attack == null) return;

        boolean pointerFree = !pointerOverUi.getAsBoolean();

        // Holding the basic attack keeps swinging, skills need a fresh press.
        if (Gdx.input.isKeyPressed(Input.Keys.J) || pointerFree && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            attack.requestedSlot = 0;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K) || pointerFree && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            attack.requestedSlot = 1;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L) || pointerFree && Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
            attack.requestedSlot = 2;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            attack.requestedSlot = 3;
        }
    }

    private boolean isSprinting(Entity entity, float deltaTime) {
        StaminaComponent stamina = staminas.get(entity);

        boolean wantsToSprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && !direction.isZero()
            && stamina != null
            && !stamina.exhausted;

        if (!wantsToSprint) return false;

        if (!stamina.spend(SPRINT_STAMINA_PER_SECOND * deltaTime)) {
            stamina.exhausted = true;
            return false;
        }

        return true;
    }
}
