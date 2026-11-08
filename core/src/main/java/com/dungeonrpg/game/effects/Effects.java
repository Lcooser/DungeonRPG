package com.dungeonrpg.game.effects;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dungeonrpg.game.animation.Direction;
import com.dungeonrpg.game.components.AttachedEffectComponent;
import com.dungeonrpg.game.components.EffectComponent;
import com.dungeonrpg.game.components.FloatingTextComponent;
import com.dungeonrpg.game.components.PositionComponent;
import com.dungeonrpg.game.components.ProjectileComponent;
import com.dungeonrpg.game.components.TextureComponent;
import com.dungeonrpg.game.rpg.Ability;
import com.dungeonrpg.game.rpg.Combat;
import com.dungeonrpg.game.tags.PlayerComponent;

import java.util.EnumMap;
import java.util.Map;

/** Creates visual effects, projectiles and floating text from the effect sheets. */
public class Effects implements Disposable {

    private static final int CELL_SIZE = 192;
    // Effects draw on top of characters.
    private static final int EFFECT_LAYER = 1;
    private static final Color PLAYER_DAMAGE_COLOR = new Color(1f, 0.35f, 0.3f, 1f);
    private static final Color MONSTER_DAMAGE_COLOR = new Color(1f, 1f, 1f, 1f);
    // Height of a hit on the body, so the effect does not spawn at the feet.
    private static final float HIT_HEIGHT = 10f;
    private static final float HEAD_RATIO = 0.8f;

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<PlayerComponent> players =
        ComponentMapper.getFor(PlayerComponent.class);

    private final Engine engine;
    private final BitmapFont font;
    private final Map<EffectType.Sheet, Texture> sheets = new EnumMap<>(EffectType.Sheet.class);
    private final Map<EffectType, Animation<TextureRegion>> animations = new EnumMap<>(EffectType.class);
    private final Map<EffectType, Array<TextureRegion>> frames = new EnumMap<>(EffectType.class);

    public Effects(Engine engine) {
        this.engine = engine;

        font = new BitmapFont(Gdx.files.internal("ui/pixel-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Map<EffectType.Sheet, TextureRegion[][]> cells = new EnumMap<>(EffectType.Sheet.class);

        for (EffectType.Sheet sheet : EffectType.Sheet.values()) {
            // Effects are shrunk on screen, so they use mipmaps to avoid shimmering.
            Texture texture = new Texture(Gdx.files.internal(sheet.getPath()), true);
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

            sheets.put(sheet, texture);
            cells.put(sheet, TextureRegion.split(texture, CELL_SIZE, CELL_SIZE));
        }

        for (EffectType type : EffectType.values()) {
            Array<TextureRegion> typeFrames = new Array<>(type.getFrames());

            for (int i = 0; i < type.getFrames(); i++) {
                typeFrames.add(cells.get(type.getSheet())[type.getRow()][i]);
            }

            frames.put(type, typeFrames);
            animations.put(type, new Animation<>(
                type.getFrameDuration(),
                typeFrames,
                type.isLooping() ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL
            ));
        }
    }

    public BitmapFont getFont() {
        return font;
    }

    /** One frame of an effect, used for skill icons. */
    public TextureRegion getFrame(EffectType type, int frame) {
        return frames.get(type).get(frame);
    }

    /** Creates an effect centered at (x, y), rotated in degrees (effects are drawn facing right). */
    public Entity spawn(EffectType type, float x, float y, float rotation) {
        Entity effect = new Entity();

        PositionComponent position = new PositionComponent();
        position.x = x;
        position.y = y;

        float size = CELL_SIZE * type.getScale();

        TextureComponent sprite = new TextureComponent();
        sprite.width = size;
        sprite.height = size;
        sprite.offsetX = -size / 2f;
        sprite.offsetY = -size / 2f;
        sprite.rotation = rotation;
        sprite.layer = EFFECT_LAYER;

        EffectComponent animation = new EffectComponent();
        animation.animation = animations.get(type);
        animation.looping = type.isLooping();

        effect.add(position);
        effect.add(sprite);
        effect.add(animation);

        engine.addEntity(effect);

        return effect;
    }

    /** Creates an effect that follows its owner's center, raised by offsetY. */
    public Entity spawnAttached(EffectType type, Entity owner, float offsetY) {
        Entity effect = spawn(type, Combat.centerX(owner), Combat.centerY(owner) + offsetY, 0f);

        effect.add(new AttachedEffectComponent(owner, offsetY));

        return effect;
    }

    public Entity spawnProjectile(Ability ability, boolean fromPlayer, float damage, float x, float y, Direction direction) {
        Entity projectile = spawn(ability.getProjectileEffect(), x, y, direction.getAngle());

        ProjectileComponent data = new ProjectileComponent();
        data.ability = ability;
        data.fromPlayer = fromPlayer;
        data.damage = damage;
        data.directionX = direction.getX();
        data.directionY = direction.getY();

        projectile.add(data);

        return projectile;
    }

    /** Sparks or blood at the hit point and the damage number above the target's head. */
    public void spawnHit(Entity target, float damage) {
        boolean isPlayer = players.has(target);
        float x = Combat.centerX(target);
        float y = Combat.centerY(target) + HIT_HEIGHT;

        spawn(isPlayer ? EffectType.BLOOD : EffectType.HIT_SPARK, x, y, 0f);

        TextureComponent sprite = textures.get(target);
        float headY = positions.get(target).y + sprite.offsetY + sprite.height * HEAD_RATIO;

        spawnDamageNumber(x, headY, damage, isPlayer ? PLAYER_DAMAGE_COLOR : MONSTER_DAMAGE_COLOR);
    }

    public void spawnDamageNumber(float x, float y, float amount, Color color) {
        spawnText(x, y, String.valueOf(Math.round(amount)), color);
    }

    public void spawnText(float x, float y, String value, Color color) {
        Entity number = new Entity();

        PositionComponent position = new PositionComponent();
        position.x = x;
        position.y = y;

        FloatingTextComponent text = new FloatingTextComponent();
        text.text = value;
        text.color.set(color);

        number.add(position);
        number.add(text);

        engine.addEntity(number);
    }

    @Override
    public void dispose() {
        for (Texture texture : sheets.values()) {
            texture.dispose();
        }

        font.dispose();
    }
}
