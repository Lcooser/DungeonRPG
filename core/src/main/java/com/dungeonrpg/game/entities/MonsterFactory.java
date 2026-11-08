package com.dungeonrpg.game.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Creates monsters. Each sheet is loaded once, the first time a monster that uses it appears, and the
 * visible figure is measured so any sheet (any cell size or padding) ends up at the size the type asks for.
 */
public class MonsterFactory implements Disposable {

    private static final int SHEET_COLUMNS = 8;
    private static final int MIN_ALPHA = 40;

    /** A loaded sheet with the measurements of its first idle frame. */
    private static final class Sheet {
        private Texture texture;
        private CharacterAnimations animations;
        private int cell;
        // Larger side of the figure in the first idle frame, and the gaps around it, in sheet pixels.
        private int figureSpan;
        private int bottomPadding;
        private float centerOffset;
    }

    private final Engine engine;
    private final float speedMultiplier;
    private final Map<String, Sheet> sheets = new HashMap<>();

    public MonsterFactory(Engine engine, float speedMultiplier) {
        this.engine = engine;
        this.speedMultiplier = speedMultiplier;
    }

    /** Creates a monster with the body centered at (x, y) in world coordinates. */
    public Entity spawn(MonsterType type, float x, float y) {
        Entity monster = new Entity();
        Sheet sheet = sheetFor(type);

        float body = type.getBodySize();
        float drawSize = type.getVisualSize() * sheet.cell / sheet.figureSpan;

        PositionComponent position = new PositionComponent();
        position.x = x - body / 2f;
        position.y = y - body / 2f;

        VelocityComponent velocity = new VelocityComponent();
        velocity.speed = type.getSpeed() * speedMultiplier;

        // The figure sits on the bottom of the hitbox and is centered on it, whatever the cell padding is.
        TextureComponent sprite = new TextureComponent();
        sprite.width = drawSize;
        sprite.height = drawSize;
        sprite.offsetX = (body - drawSize) / 2f + sheet.centerOffset * drawSize / sheet.cell;
        sprite.offsetY = -sheet.bottomPadding * drawSize / sheet.cell;
        sprite.tint = type.getTint();

        ColliderComponent collider = new ColliderComponent();
        collider.width = type.getColliderWidth();
        collider.height = type.getColliderHeight();
        collider.offsetX = (body - collider.width) / 2f;
        collider.offsetY = 0f;

        AnimationComponent animation = new AnimationComponent();
        animation.animations = sheet.animations;

        monster.add(position);
        monster.add(velocity);
        monster.add(sprite);
        monster.add(collider);
        monster.add(animation);
        monster.add(new FacingComponent());
        monster.add(type.createStats());
        monster.add(new HealthComponent(type.getHealth()));
        monster.add(new AttackComponent(type.getAbilities()));
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

    private Sheet sheetFor(MonsterType type) {
        Sheet cached = sheets.get(type.getSheetPath());

        if (cached != null) return cached;

        Sheet sheet = new Sheet();

        sheet.texture = new Texture(Gdx.files.internal(type.getSheetPath()), true);
        sheet.texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        sheet.cell = sheet.texture.getWidth() / SHEET_COLUMNS;

        sheet.animations = type.isDirectional()
            ? new CharacterAnimations(sheet.texture, sheet.cell)
            : CharacterAnimations.withoutDirections(sheet.texture, sheet.cell);

        measure(sheet, type.getSheetPath());
        sheets.put(type.getSheetPath(), sheet);

        return sheet;
    }

    // Finds the box around the figure of the first frame (front idle) to know its size and where it stands.
    private void measure(Sheet sheet, String path) {
        Pixmap pixmap = new Pixmap(Gdx.files.internal(path));

        int minX = sheet.cell;
        int maxX = -1;
        int minY = sheet.cell;
        int maxY = -1;

        for (int y = 0; y < sheet.cell; y++) {
            for (int x = 0; x < sheet.cell; x++) {
                if ((pixmap.getPixel(x, y) & 0xFF) > MIN_ALPHA) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        pixmap.dispose();

        if (maxX < 0) {
            // Empty first frame, fall back to treating the whole cell as the figure.
            sheet.figureSpan = sheet.cell;
            return;
        }

        sheet.figureSpan = Math.max(maxX - minX + 1, maxY - minY + 1);
        sheet.bottomPadding = sheet.cell - 1 - maxY;
        sheet.centerOffset = sheet.cell / 2f - (minX + maxX + 1) / 2f;
    }

    @Override
    public void dispose() {
        for (Sheet sheet : sheets.values()) {
            sheet.texture.dispose();
        }
    }
}
