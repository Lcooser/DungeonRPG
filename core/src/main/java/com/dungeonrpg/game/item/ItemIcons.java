package com.dungeonrpg.game.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/** Weapon icons from weapon_icons.png (row = weapon family, column = tier) and a soft glow for drops. */
public class ItemIcons implements Disposable {

    private static final String SHEET_PATH = "sprites/weapon_icons.png";
    private static final int CELL_SIZE = 160;
    private static final int GLOW_SIZE = 64;

    private final Texture sheet;
    private final Texture glow;
    private final TextureRegion[][] cells;
    private final TextureRegion glowRegion;

    public ItemIcons() {
        // Icons are shrunk a lot on screen, mipmaps keep them smooth.
        sheet = new Texture(Gdx.files.internal(SHEET_PATH), true);
        sheet.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        cells = TextureRegion.split(sheet, CELL_SIZE, CELL_SIZE);

        glow = createGlow();
        glowRegion = new TextureRegion(glow);
    }

    public TextureRegion get(Item item) {
        return cells[item.getType().getIconRow()][item.getIconColumn()];
    }

    public TextureRegion getGlow() {
        return glowRegion;
    }

    // White radial gradient, tinted with the rarity color when drawn.
    private Texture createGlow() {
        Pixmap pixmap = new Pixmap(GLOW_SIZE, GLOW_SIZE, Pixmap.Format.RGBA8888);
        float center = (GLOW_SIZE - 1) / 2f;

        for (int x = 0; x < GLOW_SIZE; x++) {
            for (int y = 0; y < GLOW_SIZE; y++) {
                float distance = (float) Math.sqrt((x - center) * (x - center) + (y - center) * (y - center)) / center;
                float alpha = Math.max(0f, 1f - distance);

                pixmap.drawPixel(x, y, 0xFFFFFF00 | Math.round(alpha * alpha * 200f));
            }
        }

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        return texture;
    }

    @Override
    public void dispose() {
        sheet.dispose();
        glow.dispose();
    }
}
