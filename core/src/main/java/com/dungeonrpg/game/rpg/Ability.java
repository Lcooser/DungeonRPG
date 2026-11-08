package com.dungeonrpg.game.rpg;

import com.dungeonrpg.game.effects.EffectType;

/** Attacks and skills of players and monsters. */
public enum Ability {
    AXE_SWING(melee(DamageType.PHYSICAL, 18f).range(40f).cooldown(0.5f).knockback(200f)
        .effect(EffectType.AXE_SLASH).icon(EffectType.AXE_SLASH, 2)),
    WHIRLWIND(area(DamageType.PHYSICAL).multiplier(0.7f).range(56f).cooldown(6f).cost(CostType.STAMINA, 40f)
        .hits(3, 0.12f, 0.22f).duration(0.66f).knockback(170f)
        .effect(EffectType.WHIRLWIND).attachedEffect().icon(EffectType.WHIRLWIND, 3)),
    GROUND_SLAM(area(DamageType.PHYSICAL).multiplier(1.5f).range(64f).forward(28f).cooldown(8f)
        .cost(CostType.STAMINA, 30f).knockback(150f).stun(1f)
        .effect(EffectType.GROUND_SLAM).icon(EffectType.GROUND_SLAM, 4)),
    BERSERKER_RAGE(buff().cooldown(20f).cost(CostType.STAMINA, 35f).buff(6f, 1.3f, 1.15f)
        .effect(EffectType.RAGE_AURA).icon(EffectType.RAGE_AURA, 4)),
    MAGIC_BOLT(projectile(DamageType.MAGICAL, 14f).cooldown(0.55f).knockback(170f).speed(260f)
        .flightRange(300f).hitSize(18f).fizzle(EffectType.MAGIC_IMPACT)
        .projectileEffect(EffectType.MAGIC_BOLT, EffectType.MAGIC_IMPACT).icon(EffectType.MAGIC_BOLT, 1)),
    FIREBALL(projectile(DamageType.MAGICAL, 0f).multiplier(1.2f).range(44f).cooldown(2f)
        .cost(CostType.MANA, 20f).knockback(230f).speed(200f)
        .flightRange(360f).hitSize(22f).fizzle(EffectType.HIT_SPARK)
        .projectileEffect(EffectType.FIREBALL, EffectType.FIREBALL_EXPLOSION).icon(EffectType.FIREBALL, 1)),
    FROST_NOVA(area(DamageType.MAGICAL).multiplier(0.8f).range(72f).cooldown(8f).cost(CostType.MANA, 35f)
        .knockback(140f).slow(0.5f, 3f).effect(EffectType.FROST_NOVA).icon(EffectType.FROST_NOVA, 4)),
    ARCANE_BLINK(blink(150f).cooldown(7f).cost(CostType.MANA, 25f)
        .effect(EffectType.ARCANE_BLINK).icon(EffectType.ARCANE_BLINK, 3)),
    SKELETON_ARROW(projectile(DamageType.PHYSICAL, 12f).cooldown(2.2f).knockback(60f).speed(230f)
        .flightRange(300f).hitSize(12f).projectileEffect(EffectType.ARROW, EffectType.HIT_SPARK)),
    NECRO_BOLT(projectile(DamageType.MAGICAL, 14f).cooldown(2.4f).knockback(70f).speed(190f)
        .flightRange(300f).hitSize(14f).projectileEffect(EffectType.NECROTIC_BOLT, EffectType.MAGIC_IMPACT)),
    CROSSBOW_SHOT(projectile(DamageType.PHYSICAL, 16f).cooldown(2.6f).knockback(90f).speed(260f)
        .flightRange(320f).hitSize(12f).projectileEffect(EffectType.CROSSBOW_BOLT, EffectType.HIT_SPARK)),
    VENOM_GLOB_SHOT(projectile(DamageType.PHYSICAL, 10f).cooldown(2f).knockback(60f).speed(170f)
        .flightRange(230f).hitSize(14f).projectileEffect(EffectType.VENOM_GLOB, EffectType.HIT_SPARK)),
    IMP_FIREBALL_SHOT(projectile(DamageType.MAGICAL, 13f).cooldown(2.2f).knockback(80f).speed(180f)
        .flightRange(300f).hitSize(14f).projectileEffect(EffectType.IMP_FIREBALL, EffectType.HIT_SPARK)),
    EMBER_MACE(melee(DamageType.PHYSICAL, 18f).range(36f).cooldown(1.5f).knockback(160f)
        .effect(EffectType.HIT_SPARK)),
    HOUND_BITE(melee(DamageType.PHYSICAL, 10f).range(28f).cooldown(0.8f).knockback(110f)
        .effect(EffectType.HIT_SPARK)),
    DEATH_KNIGHT_CLEAVE(melee(DamageType.PHYSICAL, 22f).range(40f).cooldown(1.4f).knockback(170f)
        .effect(EffectType.SWORD_SLASH)),
    SOUL_WRAITH_TOUCH(melee(DamageType.MAGICAL, 14f).range(30f).cooldown(1.1f).knockback(60f)
        .effect(EffectType.MAGIC_IMPACT)),
    FORGE_SLAM(melee(DamageType.PHYSICAL, 24f).range(44f).cooldown(1.8f).knockback(200f)
        .effect(EffectType.HIT_SPARK)),
    DRAGON_CLAW(melee(DamageType.PHYSICAL, 28f).range(64f).cooldown(1.2f).knockback(240f)
        .effect(EffectType.HIT_SPARK)),

    LICH_BOLT(projectile(DamageType.MAGICAL, 22f).cooldown(1.8f).knockback(110f).speed(210f)
        .flightRange(340f).hitSize(18f).projectileEffect(EffectType.NECROTIC_BOLT, EffectType.MAGIC_IMPACT)),
    DEATH_NOVA(area(DamageType.MAGICAL).damage(34f).range(110f).cooldown(9f).windup(0.6f).duration(0.9f)
        .knockback(200f).effect(EffectType.DEATH_NOVA)),
    SOUL_BARRAGE(projectile(DamageType.MAGICAL, 12f).cooldown(7f).windup(0.4f).duration(0.8f).knockback(80f)
        .speed(200f).flightRange(300f).hitSize(14f).volley(5, 40f)
        .projectileEffect(EffectType.NECROTIC_BOLT, EffectType.MAGIC_IMPACT)),
    SUMMON_UNDEAD(summon(2).cooldown(14f).windup(0.5f).duration(0.9f).effect(EffectType.SUMMON_UNDEAD)),

    VENOM_SPIT(projectile(DamageType.PHYSICAL, 20f).cooldown(6f).windup(0.4f).duration(0.8f).knockback(100f)
        .speed(180f).flightRange(320f).hitSize(20f)
        .projectileEffect(EffectType.VENOM_GLOB, EffectType.HIT_SPARK)),
    WEB_TRAP(area(DamageType.PHYSICAL).damage(8f).range(64f).cooldown(9f).windup(0.8f).duration(1f)
        .targeted().slow(0.4f, 3f).effect(EffectType.WEB_TRAP)),
    BROOD_SUMMONING(summon(2).cooldown(15f).windup(0.5f).duration(0.9f).effect(EffectType.BROOD_SUMMONING)),

    DRAGON_BREATH(projectile(DamageType.MAGICAL, 14f).cooldown(8f).windup(0.5f).duration(1f).knockback(120f)
        .speed(220f).flightRange(200f).hitSize(16f).volley(7, 70f)
        .projectileEffect(EffectType.MAGMA_BREATH, EffectType.HIT_SPARK)),
    METEOR_RAIN(area(DamageType.MAGICAL).damage(40f).range(72f).cooldown(9f).windup(1f).duration(1.2f)
        .targeted().knockback(220f).effect(EffectType.METEOR_RAIN)),
    LAVA_ERUPTION(area(DamageType.MAGICAL).damage(36f).range(120f).cooldown(12f).windup(0.7f).duration(1f)
        .knockback(240f).effect(EffectType.LAVA_ERUPTION)),

    SKELETON_SLASH(melee(DamageType.PHYSICAL, 14f).range(34f).cooldown(1.3f).knockback(150f)
        .effect(EffectType.SWORD_SLASH)),
    SPIDER_BITE(melee(DamageType.PHYSICAL, 10f).range(30f).cooldown(0.9f).knockback(120f)
        .effect(EffectType.HIT_SPARK)),
    SLIME_LUNGE(melee(DamageType.PHYSICAL, 8f).range(28f).cooldown(1.2f).knockback(100f)),
    BOSS_BITE(melee(DamageType.PHYSICAL, 24f).range(56f).cooldown(1.1f).knockback(260f)
        .effect(EffectType.HIT_SPARK));

    /** How the ability is carried out. */
    public enum Kind {
        MELEE,
        PROJECTILE,
        AREA,
        BUFF,
        TELEPORT,
        SUMMON
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

    // Length of the attack animation (5 frames of 0.08s), also the default ability duration.
    public static final float ANIMATION_LENGTH = 0.4f;

    private final Kind kind;
    private final DamageType damageType;
    private final float baseDamage;
    // When above zero, damage is this fraction of the caster's primary ability damage.
    private final float damageMultiplier;
    // Reach in front (melee), blast radius (area, explosive projectile).
    private final float range;
    // Offset of the blast center in front of the caster.
    private final float forward;
    private final CostType costType;
    private final float cost;
    private final float cooldown;
    // When the first hit lands, aligned with the impact frame of the animation.
    private final float windup;
    private final float duration;
    private final int hits;
    private final float hitInterval;
    private final float knockback;
    private final float stunSeconds;
    private final float slowFactor;
    private final float slowSeconds;
    private final float buffSeconds;
    private final float buffDamage;
    private final float buffSpeed;
    private final float blinkDistance;
    private final float projectileSpeed;
    // Distance a projectile flies before fizzling out, and the size of its hit area.
    private final float flightRange;
    private final float hitSize;
    // Projectiles fired at once, spread over an arc in degrees.
    private final int volleyCount;
    private final float volleySpread;
    // Area hits that land where the target stood when the cast began.
    private final boolean targeted;
    private final int summonCount;
    private final EffectType fizzleEffect;
    private final EffectType effect;
    private final boolean effectAttached;
    private final EffectType projectileEffect;
    private final EffectType impactEffect;
    private final EffectType iconEffect;
    private final int iconFrame;

    Ability(Builder builder) {
        kind = builder.kind;
        damageType = builder.damageType;
        baseDamage = builder.baseDamage;
        damageMultiplier = builder.damageMultiplier;
        range = builder.range;
        forward = builder.forward;
        costType = builder.costType;
        cost = builder.cost;
        cooldown = builder.cooldown;
        windup = builder.windup;
        duration = builder.duration;
        hits = builder.hits;
        hitInterval = builder.hitInterval;
        knockback = builder.knockback;
        stunSeconds = builder.stunSeconds;
        slowFactor = builder.slowFactor;
        slowSeconds = builder.slowSeconds;
        buffSeconds = builder.buffSeconds;
        buffDamage = builder.buffDamage;
        buffSpeed = builder.buffSpeed;
        blinkDistance = builder.blinkDistance;
        projectileSpeed = builder.projectileSpeed;
        flightRange = builder.flightRange;
        hitSize = builder.hitSize;
        volleyCount = builder.volleyCount;
        volleySpread = builder.volleySpread;
        targeted = builder.targeted;
        summonCount = builder.summonCount;
        fizzleEffect = builder.fizzleEffect;
        effect = builder.effect;
        effectAttached = builder.effectAttached;
        projectileEffect = builder.projectileEffect;
        impactEffect = builder.impactEffect;
        iconEffect = builder.iconEffect;
        iconFrame = builder.iconFrame;
    }

    private static Builder melee(DamageType type, float damage) {
        return new Builder(Kind.MELEE, type).damage(damage);
    }

    private static Builder projectile(DamageType type, float damage) {
        return new Builder(Kind.PROJECTILE, type).damage(damage);
    }

    private static Builder area(DamageType type) {
        return new Builder(Kind.AREA, type);
    }

    private static Builder summon(int count) {
        Builder builder = new Builder(Kind.SUMMON, DamageType.MAGICAL);
        builder.summonCount = count;

        return builder;
    }

    private static Builder buff() {
        return new Builder(Kind.BUFF, DamageType.PHYSICAL);
    }

    private static Builder blink(float distance) {
        Builder builder = new Builder(Kind.TELEPORT, DamageType.MAGICAL);
        builder.blinkDistance = distance;

        return builder;
    }

    /** Readable way to describe an ability, since most fields are optional. */
    private static final class Builder {
        private final Kind kind;
        private final DamageType damageType;
        private float baseDamage;
        private float damageMultiplier;
        private float range;
        private float forward;
        private CostType costType = CostType.NONE;
        private float cost;
        private float cooldown;
        private float windup = 0.24f;
        private float duration = ANIMATION_LENGTH;
        private int hits = 1;
        private float hitInterval;
        private float knockback;
        private float stunSeconds;
        private float slowFactor = 1f;
        private float slowSeconds;
        private float buffSeconds;
        private float buffDamage = 1f;
        private float buffSpeed = 1f;
        private float blinkDistance;
        private float projectileSpeed;
        private float flightRange;
        private float hitSize = 8f;
        private int volleyCount = 1;
        private float volleySpread;
        private boolean targeted;
        private int summonCount;
        private EffectType fizzleEffect;
        private EffectType effect;
        private boolean effectAttached;
        private EffectType projectileEffect;
        private EffectType impactEffect;
        private EffectType iconEffect;
        private int iconFrame;

        private Builder(Kind kind, DamageType damageType) {
            this.kind = kind;
            this.damageType = damageType;
        }

        private Builder damage(float value) {
            baseDamage = value;
            return this;
        }

        private Builder multiplier(float value) {
            damageMultiplier = value;
            return this;
        }

        private Builder range(float value) {
            range = value;
            return this;
        }

        private Builder forward(float value) {
            forward = value;
            return this;
        }

        private Builder cooldown(float value) {
            cooldown = value;
            return this;
        }

        private Builder cost(CostType type, float value) {
            costType = type;
            cost = value;
            return this;
        }

        private Builder hits(int count, float interval, float firstHit) {
            hits = count;
            hitInterval = interval;
            windup = firstHit;
            return this;
        }

        private Builder duration(float value) {
            duration = value;
            return this;
        }

        private Builder knockback(float value) {
            knockback = value;
            return this;
        }

        private Builder stun(float seconds) {
            stunSeconds = seconds;
            return this;
        }

        private Builder slow(float factor, float seconds) {
            slowFactor = factor;
            slowSeconds = seconds;
            return this;
        }

        private Builder buff(float seconds, float damage, float speed) {
            buffSeconds = seconds;
            buffDamage = damage;
            buffSpeed = speed;
            return this;
        }

        private Builder speed(float value) {
            projectileSpeed = value;
            return this;
        }

        private Builder windup(float value) {
            windup = value;
            return this;
        }

        private Builder volley(int count, float spreadDegrees) {
            volleyCount = count;
            volleySpread = spreadDegrees;
            return this;
        }

        private Builder targeted() {
            targeted = true;
            return this;
        }

        private Builder flightRange(float value) {
            flightRange = value;
            return this;
        }

        private Builder hitSize(float value) {
            hitSize = value;
            return this;
        }

        private Builder fizzle(EffectType value) {
            fizzleEffect = value;
            return this;
        }

        private Builder effect(EffectType value) {
            effect = value;
            return this;
        }

        private Builder attachedEffect() {
            effectAttached = true;
            return this;
        }

        private Builder projectileEffect(EffectType projectile, EffectType impact) {
            projectileEffect = projectile;
            impactEffect = impact;
            return this;
        }

        private Builder icon(EffectType type, int frame) {
            iconEffect = type;
            iconFrame = frame;
            return this;
        }
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

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getRange() {
        return range;
    }

    public float getForward() {
        return forward;
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

    public float getDuration() {
        return duration;
    }

    public int getHits() {
        return hits;
    }

    public float getHitInterval() {
        return hitInterval;
    }

    public float getKnockback() {
        return knockback;
    }

    public float getStunSeconds() {
        return stunSeconds;
    }

    public float getSlowFactor() {
        return slowFactor;
    }

    public float getSlowSeconds() {
        return slowSeconds;
    }

    public float getBuffSeconds() {
        return buffSeconds;
    }

    public float getBuffDamage() {
        return buffDamage;
    }

    public float getBuffSpeed() {
        return buffSpeed;
    }

    public float getBlinkDistance() {
        return blinkDistance;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public float getFlightRange() {
        return flightRange;
    }

    public int getVolleyCount() {
        return volleyCount;
    }

    public float getVolleySpread() {
        return volleySpread;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public int getSummonCount() {
        return summonCount;
    }

    public float getHitSize() {
        return hitSize;
    }

    public EffectType getFizzleEffect() {
        return fizzleEffect;
    }

    public EffectType getEffect() {
        return effect;
    }

    public boolean isEffectAttached() {
        return effectAttached;
    }

    public EffectType getProjectileEffect() {
        return projectileEffect;
    }

    public EffectType getImpactEffect() {
        return impactEffect;
    }

    public EffectType getIconEffect() {
        return iconEffect;
    }

    public int getIconFrame() {
        return iconFrame;
    }

    /** Name shown in the UI. */
    public String getLabel() {
        return name().replace('_', ' ');
    }
}
