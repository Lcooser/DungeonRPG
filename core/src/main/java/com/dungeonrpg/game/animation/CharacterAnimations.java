package com.dungeonrpg.game.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;
import java.util.Map;

/**
 * Monta as animações de uma spritesheet em grade (gerada por tools/normalize_spritesheet.py).
 *
 * Folha com direções: linhas 0-2 de frente (idle, walk, attack), 3-5 de costas, 6-8 de lado (direita) e
 * 9 com hurt (2 frames) seguido de death (6 frames). A esquerda é a direita espelhada.
 *
 * Folha sem direções (slime): linha 0 idle, 1 walk, 2 attack e 3 com hurt (2 frames) e death (6 frames).
 *
 * A folha de cansaço é opcional: 3 linhas (frente, costas, lado) de 4 frames. Sem ela, o
 * personagem usa a animação de idle quando está cansado.
 */
public class CharacterAnimations {

    private static final int DOWN_ROW = 0;
    private static final int UP_ROW = 3;
    private static final int RIGHT_ROW = 6;
    private static final int EFFECTS_ROW = 9;
    private static final int SINGLE_EFFECTS_ROW = 3;

    private static final int IDLE_FRAMES = 4;
    private static final int WALK_FRAMES = 6;
    private static final int ATTACK_FRAMES = 5;
    private static final int TIRED_FRAMES = 4;
    private static final int HURT_FRAMES = 2;
    private static final int DEATH_FRAMES = 6;

    private final Map<AnimationType, Map<Direction, Animation<TextureRegion>>> animations =
        new EnumMap<>(AnimationType.class);

    public CharacterAnimations(Texture texture, int cellSize) {
        this(texture, null, cellSize);
    }

    public CharacterAnimations(Texture texture, Texture tiredTexture, int cellSize) {
        TextureRegion[][] cells = TextureRegion.split(texture, cellSize, cellSize);

        addDirectional(AnimationType.IDLE, cells, 0, IDLE_FRAMES);
        addDirectional(AnimationType.WALK, cells, 1, WALK_FRAMES);
        addDirectional(AnimationType.ATTACK, cells, 2, ATTACK_FRAMES);

        if (tiredTexture != null) {
            addTired(TextureRegion.split(tiredTexture, cellSize, cellSize));
        } else {
            animations.put(AnimationType.TIRED, animations.get(AnimationType.IDLE));
        }

        addUniform(AnimationType.HURT, cells[EFFECTS_ROW], 0, HURT_FRAMES);
        addUniform(AnimationType.DEATH, cells[EFFECTS_ROW], HURT_FRAMES, DEATH_FRAMES);
    }

    private CharacterAnimations(TextureRegion[][] cells) {
        addUniform(AnimationType.IDLE, cells[0], 0, IDLE_FRAMES);
        addUniform(AnimationType.WALK, cells[1], 0, WALK_FRAMES);
        addUniform(AnimationType.ATTACK, cells[2], 0, ATTACK_FRAMES);
        animations.put(AnimationType.TIRED, animations.get(AnimationType.IDLE));

        addUniform(AnimationType.HURT, cells[SINGLE_EFFECTS_ROW], 0, HURT_FRAMES);
        addUniform(AnimationType.DEATH, cells[SINGLE_EFFECTS_ROW], HURT_FRAMES, DEATH_FRAMES);
    }

    /** Para criaturas que não têm lado, como o slime: o mesmo desenho vale para qualquer direção. */
    public static CharacterAnimations withoutDirections(Texture texture, int cellSize) {
        return new CharacterAnimations(TextureRegion.split(texture, cellSize, cellSize));
    }

    private void addDirectional(AnimationType type, TextureRegion[][] cells, int rowOffset, int frameCount) {
        Map<Direction, Animation<TextureRegion>> byDirection = new EnumMap<>(Direction.class);

        byDirection.put(Direction.DOWN, create(type, cells[DOWN_ROW + rowOffset], 0, frameCount, false));
        byDirection.put(Direction.UP, create(type, cells[UP_ROW + rowOffset], 0, frameCount, false));
        byDirection.put(Direction.RIGHT, create(type, cells[RIGHT_ROW + rowOffset], 0, frameCount, false));
        byDirection.put(Direction.LEFT, create(type, cells[RIGHT_ROW + rowOffset], 0, frameCount, true));

        animations.put(type, byDirection);
    }

    private void addTired(TextureRegion[][] cells) {
        Map<Direction, Animation<TextureRegion>> byDirection = new EnumMap<>(Direction.class);

        byDirection.put(Direction.DOWN, create(AnimationType.TIRED, cells[0], 0, TIRED_FRAMES, false));
        byDirection.put(Direction.UP, create(AnimationType.TIRED, cells[1], 0, TIRED_FRAMES, false));
        byDirection.put(Direction.RIGHT, create(AnimationType.TIRED, cells[2], 0, TIRED_FRAMES, false));
        byDirection.put(Direction.LEFT, create(AnimationType.TIRED, cells[2], 0, TIRED_FRAMES, true));

        animations.put(AnimationType.TIRED, byDirection);
    }

    /** Animações sem variação de direção usam o mesmo frame para qualquer lado. */
    private void addUniform(AnimationType type, TextureRegion[] row, int firstFrame, int frameCount) {
        Animation<TextureRegion> animation = create(type, row, firstFrame, frameCount, false);
        Map<Direction, Animation<TextureRegion>> byDirection = new EnumMap<>(Direction.class);

        for (Direction direction : Direction.values()) {
            byDirection.put(direction, animation);
        }

        animations.put(type, byDirection);
    }

    private Animation<TextureRegion> create(
        AnimationType type,
        TextureRegion[] row,
        int firstFrame,
        int frameCount,
        boolean flipHorizontally
    ) {
        Array<TextureRegion> frames = new Array<>(frameCount);

        for (int i = firstFrame; i < firstFrame + frameCount; i++) {
            TextureRegion frame = new TextureRegion(row[i]);

            if (flipHorizontally) {
                frame.flip(true, false);
            }

            frames.add(frame);
        }

        return new Animation<>(
            type.getFrameDuration(),
            frames,
            type.isLooping() ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL
        );
    }

    public Animation<TextureRegion> get(AnimationType type, Direction direction) {
        return animations.get(type).get(direction);
    }
}
