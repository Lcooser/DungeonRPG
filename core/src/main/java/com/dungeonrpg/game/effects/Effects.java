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

/** Cria efeitos visuais, projéteis e números de dano a partir da folha effects_sheet.png. */
public class Effects implements Disposable {

    private static final String SHEET_PATH = "sprites/effects_sheet.png";
    private static final int CELL_SIZE = 192;
    // Efeitos ficam por cima dos personagens.
    private static final int EFFECT_LAYER = 1;
    private static final Color PLAYER_DAMAGE_COLOR = new Color(1f, 0.35f, 0.3f, 1f);
    private static final Color MONSTER_DAMAGE_COLOR = new Color(1f, 1f, 1f, 1f);
    // Altura do golpe no corpo, para o efeito não nascer nos pés.
    private static final float HIT_HEIGHT = 10f;
    private static final float HEAD_RATIO = 0.8f;

    private final ComponentMapper<TextureComponent> textures =
        ComponentMapper.getFor(TextureComponent.class);

    private final ComponentMapper<PositionComponent> positions =
        ComponentMapper.getFor(PositionComponent.class);

    private final ComponentMapper<PlayerComponent> players =
        ComponentMapper.getFor(PlayerComponent.class);

    private final Engine engine;
    private final Texture texture;
    private final BitmapFont font;
    private final Map<EffectType, Animation<TextureRegion>> animations = new EnumMap<>(EffectType.class);

    public Effects(Engine engine) {
        this.engine = engine;

        // Os efeitos são reduzidos em tela, então usam mipmaps para não serrilhar.
        texture = new Texture(Gdx.files.internal(SHEET_PATH), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        font = new BitmapFont(Gdx.files.internal("ui/pixel-font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] cells = TextureRegion.split(texture, CELL_SIZE, CELL_SIZE);

        for (EffectType type : EffectType.values()) {
            Array<TextureRegion> frames = new Array<>(type.getFrames());

            for (int i = 0; i < type.getFrames(); i++) {
                frames.add(cells[type.getRow()][i]);
            }

            animations.put(type, new Animation<>(
                type.getFrameDuration(),
                frames,
                type.isLooping() ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL
            ));
        }
    }

    public BitmapFont getFont() {
        return font;
    }

    /** Cria um efeito centrado em (x, y), girado em graus (os efeitos são desenhados virados para a direita). */
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

    /** Faíscas ou sangue no ponto do golpe e o número de dano acima da cabeça do alvo. */
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
        Entity number = new Entity();

        PositionComponent position = new PositionComponent();
        position.x = x;
        position.y = y;

        FloatingTextComponent text = new FloatingTextComponent();
        text.text = String.valueOf(Math.round(amount));
        text.color.set(color);

        number.add(position);
        number.add(text);

        engine.addEntity(number);
    }

    @Override
    public void dispose() {
        texture.dispose();
        font.dispose();
    }
}
