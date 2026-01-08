package io.github.Wasnowl.entities;

/**
 * Enum pour les types de projectiles.
 * Chaque type encapsule les paramètres immuables (Flyweight intrinsic state).
 */
public enum ProjectileType {
    SIMPLE(25f, 200f, 5f, false),
    AOE(40f, 180f, 10f, true);

    private final float damage;
    private final float speed;
    private final float explosionRadius; // rayon pour AOE
    private final boolean isAOE;

    ProjectileType(float damage, float speed, float explosionRadius, boolean isAOE) {
        this.damage = damage;
        this.speed = speed;
        this.explosionRadius = explosionRadius;
        this.isAOE = isAOE;
    }

    public float getDamage() {
        return damage;
    }

    public float getSpeed() {
        return speed;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public boolean isAOE() {
        return isAOE;
    }
}
