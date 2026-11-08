package com.dungeonrpg.game.item;

import com.dungeonrpg.game.rpg.CharacterClass;
import com.dungeonrpg.game.rpg.StatType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Rolls weapons: which family, which icon, its damage and the attribute bonus with its RPG-style name. */
public final class ItemGenerator {

    // Attributes that make sense on a weapon for each class.
    private static final StatType[] BARBARIAN_STATS = {
        StatType.STRENGTH, StatType.STRENGTH, StatType.VITALITY, StatType.DEFENSE, StatType.AGILITY
    };
    private static final StatType[] MAGE_STATS = {
        StatType.INTELLIGENCE, StatType.INTELLIGENCE, StatType.VITALITY, StatType.AGILITY, StatType.DEFENSE
    };

    private ItemGenerator() {
    }

    public static Item generate(Rarity rarity, CharacterClass characterClass, Random random) {
        List<WeaponType> types = new ArrayList<>();

        for (WeaponType type : WeaponType.values()) {
            if (type.getOwner() == characterClass) {
                types.add(type);
            }
        }

        WeaponType type = types.get(random.nextInt(types.size()));
        int column = rarity.getFirstIconColumn()
            + random.nextInt(rarity.getLastIconColumn() - rarity.getFirstIconColumn() + 1);

        int damage = Math.round(type.getBaseDamage() * rarity.getDamageMultiplier() * (0.9f + random.nextFloat() * 0.2f));

        StatType bonusStat = null;
        int bonusAmount = 0;

        if (rarity.getMaxBonus() > 0) {
            StatType[] pool = characterClass == CharacterClass.MAGE ? MAGE_STATS : BARBARIAN_STATS;
            bonusStat = pool[random.nextInt(pool.length)];
            bonusAmount = Math.max(1, rarity.getMaxBonus() - random.nextInt(2));
        }

        return new Item(type, rarity, column, nameOf(type, rarity, column, bonusStat), damage, bonusStat, bonusAmount);
    }

    // Common and unique items keep the plain name, everything in between gets a "of the ..." epithet.
    private static String nameOf(WeaponType type, Rarity rarity, int column, StatType bonusStat) {
        String base = type.getName(column);

        if (rarity == Rarity.COMMON || rarity == Rarity.UNIQUE || bonusStat == null) return base;

        return base + " " + epithetOf(bonusStat);
    }

    private static String epithetOf(StatType stat) {
        switch (stat) {
            case STRENGTH:
                return "OF THE BEAR";
            case AGILITY:
                return "OF THE FOX";
            case DEFENSE:
                return "OF THE BULWARK";
            case INTELLIGENCE:
                return "OF THE SAGE";
            default:
                return "OF VIGOR";
        }
    }
}
