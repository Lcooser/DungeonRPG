package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.effects.EffectType;

/** Ataques e habilidades de jogadores e monstros. */
public enum Ability {
    AXE_SWING(Kind.MELEE, DamageType.PHYSICAL, 18f, 40f, CostType.NONE, 0f, 0.5f, 0.24f, 110f,
        EffectType.AXE_SLASH, null, null, 0f),
    HEAVY_SWING(Kind.MELEE, DamageType.PHYSICAL, 36f, 54f, CostType.STAMINA, 25f, 1f, 0.24f, 200f,
        EffectType.AXE_SLASH, null, null, 0f),
    MAGIC_BOLT(Kind.PROJECTILE, DamageType.MAGICAL, 14f, 0f, CostType.NONE, 0f, 0.55f, 0.24f, 90f,
        null, EffectType.MAGIC_BOLT, EffectType.MAGIC_IMPACT, 260f),
    FIREBALL(Kind.PROJECTILE, DamageType.MAGICAL, 30f, 44f, CostType.MANA, 20f, 1.1f, 0.24f, 160f,
        null, EffectType.FIREBALL, EffectType.FIREBALL_EXPLOSION, 200f),
    SKELETON_SLASH(Kind.MELEE, DamageType.PHYSICAL, 14f, 34f, CostType.NONE, 0f, 1.3f, 0.24f, 120f,
        EffectType.SWORD_SLASH, null, null, 0f),
    SPIDER_BITE(Kind.MELEE, DamageType.PHYSICAL, 10f, 30f, CostType.NONE, 0f, 0.9f, 0.24f, 80f,
        EffectType.HIT_SPARK, null, null, 0f),
    SLIME_LUNGE(Kind.MELEE, DamageType.PHYSICAL, 8f, 28f, CostType.NONE, 0f, 1.2f, 0.24f, 60f,
        null, null, null, 0f);

    public enum Kind {
        MELEE,
        PROJECTILE
    }

    public enum DamageType {
        PHYSICAL,
        MAGICAL
    }

    public enum CostType {
        NONE,
        STAMINA,
        MANA
    }

    private final Kind kind;
    private final DamageType damageType;
    private final float baseDamage;
    // Alcance do golpe à frente (corpo a corpo) ou raio da explosão (projétil, 0 se não explode).
    private final float range;
    private final CostType costType;
    private final float cost;
    private final float cooldown;
    // Momento do ataque em que o golpe acerta, alinhado ao frame de impacto da animação.
    private final float windup;
    private final float knockback;
    private final EffectType effect;
    private final EffectType projectileEffect;
    private final EffectType impactEffect;
    private final float projectileSpeed;

    Ability(
        Kind kind,
        DamageType damageType,
        float baseDamage,
        float range,
        CostType costType,
        float cost,
        float cooldown,
        float windup,
        float knockback,
        EffectType effect,
        EffectType projectileEffect,
        EffectType impactEffect,
        float projectileSpeed
    ) {
        this.kind = kind;
        this.damageType = damageType;
        this.baseDamage = baseDamage;
        this.range = range;
        this.costType = costType;
        this.cost = cost;
        this.cooldown = cooldown;
        this.windup = windup;
        this.knockback = knockback;
        this.effect = effect;
        this.projectileEffect = projectileEffect;
        this.impactEffect = impactEffect;
        this.projectileSpeed = projectileSpeed;
    }

    public Kind getKind() {
        return kind;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getRange() {
        return range;
    }

    public CostType getCostType() {
        return costType;
    }

    public float getCost() {
        return cost;
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getWindup() {
        return windup;
    }

    public float getKnockback() {
        return knockback;
    }

    public EffectType getEffect() {
        return effect;
    }

    public EffectType getProjectileEffect() {
        return projectileEffect;
    }

    public EffectType getImpactEffect() {
        return impactEffect;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }
}
