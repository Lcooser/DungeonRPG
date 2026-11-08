package com.dungeonrpg.game.item;

import com.dungeonrpg.game.rpg.CharacterClass;

/** Weapon families. Names follow the 8 icon columns of weapon_icons.png, from worn to one-of-a-kind. */
public enum WeaponType {
    AXE(0, CharacterClass.BARBARIAN, 6, "RUSTED HATCHET", "IRON HATCHET", "STEELBITE AXE", "GLACIER EDGE",
        "VOIDREAVER", "RUNED WARHATCHET", "EMBERFANG CLEAVER", "BLOODMOON REAVER"),
    GREATAXE(1, CharacterClass.BARBARIAN, 9, "RUSTED CHOPPER", "IRON GREATAXE", "STEEL EXECUTIONER",
        "FROSTBITE BARDICHE", "VOID SUNDERER", "RUNIC DOOMAXE", "INFERNO EXECUTOR", "WORLDSPLITTER"),
    SWORD(2, CharacterClass.BARBARIAN, 5, "RUSTY SHORTSWORD", "IRON BLADE", "STEEL LONGSWORD", "FROSTFANG BLADE",
        "NIGHTSHADE EDGE", "RUNEBOUND SABER", "SUNFORGED CLAYMORE", "CRIMSON ECLIPSE"),
    HAMMER(3, CharacterClass.BARBARIAN, 8, "WOODEN MALLET", "IRON MAUL", "STEEL WARHAMMER", "STORMFORGED MAUL",
        "VOIDCRUSHER", "GILDED JUDGEMENT", "MOLTEN BREAKER", "EARTHSHATTERER"),
    SPEAR(4, CharacterClass.BARBARIAN, 6, "RUSTY SPEAR", "IRON PIKE", "STEEL LANCE", "FROSTPIERCER",
        "NIGHT PIKE", "GILDED LANCE", "SUNLANCE", "BLOODTHORN"),
    STAFF(5, CharacterClass.MAGE, 6, "GNARLED STAFF", "APPRENTICE STAFF", "SAPPHIRE STAFF", "FROSTWEAVE STAFF",
        "VOID STAFF", "ARCHMAGE SCEPTER", "SOLARIS STAFF", "CRIMSON ARCANUM"),
    WAND(6, CharacterClass.MAGE, 4, "TWIG WAND", "IRON WAND", "SAPPHIRE WAND", "FROSTSHARD WAND",
        "NIGHT WAND", "RUNIC WAND", "SOLAR WAND", "BLOODSTAR WAND"),
    TOME(7, CharacterClass.MAGE, 5, "TATTERED TOME", "SCHOLAR TOME", "SAPPHIRE GRIMOIRE", "FROSTLORE TOME",
        "VOID GRIMOIRE", "GILDED CODEX", "SOLAR CODEX", "BLOODBOUND GRIMOIRE"),
    ORB(8, CharacterClass.MAGE, 5, "GLASS ORB", "TIDE ORB", "TIDEWEAVE ORB", "FROSTSWIRL ORB",
        "VOID MAELSTROM", "GILDED PRISM", "SUNHEART ORB", "BLOODSTAR CORE");

    private final int iconRow;
    private final CharacterClass owner;
    private final int baseDamage;
    private final String[] names;

    WeaponType(int iconRow, CharacterClass owner, int baseDamage, String... names) {
        this.iconRow = iconRow;
        this.owner = owner;
        this.baseDamage = baseDamage;
        this.names = names;
    }

    public int getIconRow() {
        return iconRow;
    }

    public CharacterClass getOwner() {
        return owner;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    /** Name of the weapon drawn in the given icon column. */
    public String getName(int iconColumn) {
        return names[iconColumn];
    }
}
