package com.dungeonrpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dungeonrpg.game.components.AttackComponent;
import com.dungeonrpg.game.components.ManaComponent;
import com.dungeonrpg.game.components.StaminaComponent;
import com.dungeonrpg.game.effects.Effects;
import com.dungeonrpg.game.rpg.Ability;

/** Row of skill slots with key, icon, cooldown and a dimmed look when the resource is missing. */
public class Hotbar extends Actor {

    private static final String[] KEYS = {"J", "K", "L", "I"};
    private static final float SLOT_SIZE = 28f;
    private static final float SLOT_GAP = 4f;
    private static final float ICON_SIZE = 22f;
    private static final Color COOLDOWN_COLOR = new Color(0f, 0f, 0f, 0.65f);
    private static final Color UNAFFORDABLE_TINT = new Color(0.45f, 0.45f, 0.55f, 1f);
    private static final Color KEY_COLOR = new Color(0.98f, 0.84f, 0.35f, 1f);
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.9f);

    private final Ability[] abilities;
    private final TextureRegion[] icons;
    private final NinePatch slotFrame;
    private final TextureRegion pixel;
    private final BitmapFont font;
    private final GlyphLayout[] keyLayouts;
    private final GlyphLayout[] cooldownLayouts;
    private final GlyphLayout shadowLayout = new GlyphLayout();
    private final float[] cooldowns;
    private final boolean[] affordable;
    private final int[] shownSeconds;

    public Hotbar(TextureAtlas atlas, BitmapFont font, Effects effects, Ability[] abilities) {
        this.abilities = abilities;
        this.font = font;
        this.slotFrame = atlas.createPatch("bar-frame");
        this.pixel = atlas.findRegion("pixel");

        int count = abilities.length;
        icons = new TextureRegion[count];
        keyLayouts = new GlyphLayout[count];
        cooldownLayouts = new GlyphLayout[count];
        cooldowns = new float[count];
        affordable = new boolean[count];
        shownSeconds = new int[count];

        for (int i = 0; i < count; i++) {
            if (abilities[i].getIconEffect() != null) {
                icons[i] = effects.getFrame(abilities[i].getIconEffect(), abilities[i].getIconFrame());
            }

            keyLayouts[i] = new GlyphLayout(font, KEYS[i], KEY_COLOR, 0f, 0, false);
            cooldownLayouts[i] = new GlyphLayout();
            affordable[i] = true;
            shownSeconds[i] = -1;
        }

        setSize(count * SLOT_SIZE + (count - 1) * SLOT_GAP, SLOT_SIZE);
    }

    public void update(AttackComponent attack, ManaComponent mana, StaminaComponent stamina) {
        for (int i = 0; i < abilities.length; i++) {
            cooldowns[i] = attack.cooldowns[i];

            Ability ability = abilities[i];

            switch (ability.getCostType()) {
                case STAMINA:
                    affordable[i] = stamina != null && stamina.current >= ability.getCost();
                    break;
                case MANA:
                    affordable[i] = mana != null && mana.current >= ability.getCost();
                    break;
                default:
                    affordable[i] = true;
            }

            int seconds = MathUtils.ceil(cooldowns[i]);

            // Text is rebuilt only when the displayed second changes.
            if (seconds != shownSeconds[i] && seconds > 0) {
                shownSeconds[i] = seconds;
                cooldownLayouts[i].setText(font, String.valueOf(seconds), Color.WHITE, 0f, 0, false);
            } else if (seconds == 0) {
                shownSeconds[i] = 0;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < abilities.length; i++) {
            float x = getX() + i * (SLOT_SIZE + SLOT_GAP);
            float y = getY();

            batch.setColor(1f, 1f, 1f, parentAlpha);
            slotFrame.draw(batch, x, y, SLOT_SIZE, SLOT_SIZE);

            if (icons[i] != null) {
                Color tint = affordable[i] ? Color.WHITE : UNAFFORDABLE_TINT;
                batch.setColor(tint.r, tint.g, tint.b, parentAlpha);
                batch.draw(icons[i], x + (SLOT_SIZE - ICON_SIZE) / 2f, y + (SLOT_SIZE - ICON_SIZE) / 2f, ICON_SIZE, ICON_SIZE);
            }

            drawCooldown(batch, parentAlpha, i, x, y);

            font.draw(batch, keyLayouts[i], x + 3f, y + SLOT_SIZE - 3f);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    // Dark overlay that drains upward as the cooldown runs out, with the seconds left on top.
    private void drawCooldown(Batch batch, float parentAlpha, int slot, float x, float y) {
        if (cooldowns[slot] <= 0f) return;

        float total = Math.max(abilities[slot].getCooldown(), 0.01f);
        float ratio = MathUtils.clamp(cooldowns[slot] / total, 0f, 1f);
        float inner = SLOT_SIZE - 4f;

        batch.setColor(COOLDOWN_COLOR.r, COOLDOWN_COLOR.g, COOLDOWN_COLOR.b, COOLDOWN_COLOR.a * parentAlpha);
        batch.draw(pixel, x + 2f, y + 2f, inner, MathUtils.ceil(inner * ratio));

        // Short cooldowns (basic attacks) skip the number, it would only flicker.
        if (abilities[slot].getCooldown() < 1.5f) return;

        GlyphLayout layout = cooldownLayouts[slot];
        float textX = MathUtils.floor(x + (SLOT_SIZE - layout.width) / 2f);
        float textY = MathUtils.floor(y + (SLOT_SIZE + 7f) / 2f + 1f);

        shadowLayout.setText(font, layout.runs.size > 0 ? String.valueOf(shownSeconds[slot]) : "", SHADOW_COLOR, 0f, 0, false);
        font.draw(batch, shadowLayout, textX + 1f, textY - 1f);
        font.draw(batch, layout, textX, textY);
    }
}
