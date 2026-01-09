package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Enum pour les types de projectiles.
 * Chaque type encapsule les paramètres immuables (Flyweight intrinsic state).
 * Utilise le Flyweight Pattern: la TextureRegion est partagée par tous les projectiles du même type
 */
public enum ProjectileType {
    SIMPLE(25f, 200f, 5f, false),
    AOE(40f, 180f, 10f, true),
    AOE_STRONG(75f, 150f, 50f, true),
    RICOCHET(20f, 250f, 5f, false);

    private final float damage;
    private final float speed;
    private final float explosionRadius; // rayon pour AOE
    private final boolean isAOE;
    private TextureRegion texture; // Texture partagée par tous les projectiles de ce type (Flyweight)
    private Animation<TextureRegion> animation; // optional animation

    ProjectileType(float damage, float speed, float explosionRadius, boolean isAOE) {
        this.damage = damage;
        this.speed = speed;
        this.explosionRadius = explosionRadius;
        this.isAOE = isAOE;
        this.texture = null; // Sera assignée par ProjectileAssetManager
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

    /**
     * Retourne la texture shared pour ce type de projectile
     */
    public TextureRegion getTexture() {
        return texture;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    /**
     * Assigne la texture pour ce type (appelée par ProjectileAssetManager)
     * Flyweight: une seule texture pour tous les projectiles de ce type
     */
    public void setTexture(TextureRegion tex) {
        this.texture = tex;
    }

    public void setAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
    }
}

