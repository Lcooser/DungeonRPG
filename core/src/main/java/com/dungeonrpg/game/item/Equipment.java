package com.dungeonrpg.game.item;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.dungeonrpg.game.components.EquipmentComponent;
import com.dungeonrpg.game.components.StatsComponent;

/** Equipping rules: the weapon bonus is added to the attributes and taken back when it is replaced. */
public final class Equipment {

    private static final ComponentMapper<EquipmentComponent> EQUIPMENTS =
        ComponentMapper.getFor(EquipmentComponent.class);

    private static final ComponentMapper<StatsComponent> STATS = ComponentMapper.getFor(StatsComponent.class);

    private Equipment() {
    }

    public static boolean isUpgrade(Entity entity, Item candidate) {
        EquipmentComponent equipment = EQUIPMENTS.get(entity);

        if (equipment == null) return false;

        return equipment.weapon == null || candidate.getScore() > equipment.weapon.getScore();
    }

    public static void equip(Entity entity, Item item) {
        EquipmentComponent equipment = EQUIPMENTS.get(entity);
        StatsComponent stats = STATS.get(entity);

        if (equipment.weapon != null && equipment.weapon.getBonusStat() != null) {
            equipment.weapon.getBonusStat().add(stats, -equipment.weapon.getBonusAmount());
        }

        equipment.weapon = item;

        if (item.getBonusStat() != null) {
            item.getBonusStat().add(stats, item.getBonusAmount());
        }
    }

    public static int weaponDamage(Entity entity) {
        EquipmentComponent equipment = EQUIPMENTS.get(entity);

        return equipment == null || equipment.weapon == null ? 0 : equipment.weapon.getDamage();
    }
}
