package com.dungeonrpg.game.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.dungeonrpg.game.animation.CharacterAnimations;
import com.dungeonrpg.game.components.AiComponent;
import com.dungeonrpg.game.components.AnimationComponent;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.ColliderComponent;
import com.dungeonrpg.game.components.FacingComponent;
import com.dungeonrpg.game.components.HealthComponent;
import com.dungeonrpg.game.components.LootComponent;
import com.dungeonrpg.game.components.MonsterComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.components.VelocityComponent;
import com.dungeonrpg.game.components.XpRewardComponent;
import com.dungeonrpg.game.tags.BossComponent;

import java.util.EnumMap;
import java.util.Map;

/** Creates monsters and caches each type's sheet, loaded the first time the type appears. */
public class MonsterFactory implements Disposable {

    private static final int SHEET_CELL_SIZE = 160;
    // Feet sit slightly above the cell edge, same ratio as the player (2px in 72px).
    private static final float FEET_OFFSET_RATIO = 2f / 72f;

    private final Engine engine;
    private final float speedMultiplier;
    private final Map<MonsterType, Texture> textures = new EnumMap<>(MonsterType.class);
    private final Map<MonsterType, CharacterAnimations> animations = new EnumMap<>(MonsterType.class);

    public MonsterFactory(Engine engine, float speedMultiplier) {
        this.engine = engine;
        this.speedMultiplier = speedMultiplier;
    }

    /** Creates a monster with the body centered at (x, y) in world coordinates. */
    public Entity spawn(MonsterType type, float x, float y) {
        Entity monster = new Entity();

        float body = type.getBodySize();

        PositionComponent position = new PositionComponent();
        position.x = x - body / 2f;
        position.y = y - body / 2f;

        VelocityComponent velocity = new VelocityComponent();
        velocity.speed = type.getSpeed() * speedMultiplier;

        TextureComponent sprite = new TextureComponent();
        sprite.width = type.getSpriteSize();
        sprite.height = type.getSpriteSize();
        sprite.offsetX = (body - type.getSpriteSize()) / 2f;
        sprite.offsetY = -type.getSpriteSize() * FEET_OFFSET_RATIO;
        sprite.tint = type.getTint();

        ColliderComponent collider = new ColliderComponent();
        collider.width = type.getColliderWidth();
        collider.height = type.getColliderHeight();
        collider.offsetX = (body - type.getColliderWidth()) / 2f;
        collider.offsetY = 0f;

        AnimationComponent animation = new AnimationComponent();
        animation.animations = animationsFor(type);

        monster.add(position);
        monster.add(velocity);
        monster.add(sprite);
        monster.add(collider);
        monster.add(animation);
        monster.add(new FacingComponent());
        monster.add(type.createStats());
        monster.add(new HealthComponent(type.getHealth()));
        monster.add(new AttackComponent(type.getAttack()));
        monster.add(new AiComponent(type.getAggroRange()));
        monster.add(new MonsterComponent(type));
        monster.add(new XpRewardComponent(type.getXpReward()));
        monster.add(new LootComponent(type.getLootTier()));

        if (type.isBoss()) {
            monster.add(new BossComponent());
        }

        engine.addEntity(monster);

        return monster;
    }

    private CharacterAnimations animationsFor(MonsterType type) {
        CharacterAnimations cached = animations.get(type);

        if (cached != null) return cached;

        Texture texture = new Texture(Gdx.files.internal(type.getSheetPath()), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        textures.put(type, texture);

        CharacterAnimations created = type.isDirectional()
            ? new CharacterAnimations(texture, SHEET_CELL_SIZE)
            : CharacterAnimations.withoutDirections(texture, SHEET_CELL_SIZE);
        animations.put(type, created);

        return created;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
    }
}
