package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Enum pour les types de projectiles.
 * Chaque type encapsule les paramètres immuables (Flyweight intrinsic state).
 * Utilise le Flyweight Pattern: la TextureRegion est partagée par tous les projectiles du même type
 */
public enum ProjectileType {
    /** Projectile simple. */
    SIMPLE(25f, 200f, 5f, false),
    /** Projectile AOE standard. */
    AOE(40f, 180f, 10f, true),
    /** Projectile AOE puissant. */
    AOE_STRONG(75f, 150f, 50f, true),
    /** Projectile a ricochet. */
    RICOCHET(20f, 250f, 5f, false);

    private final float damage;
    private final float speed;
    private final float explosionRadius; // rayon pour AOE
    private final boolean isAOE;
    private TextureRegion texture; // Texture partagée par tous les projectiles de ce type (Flyweight)
    private Animation<TextureRegion> animation; // optional animation

    /**
     * Cree un type de projectile avec ses caracteristiques intrinseques.
     * @param damage degats de base
     * @param speed vitesse
     * @param explosionRadius rayon d'explosion AOE
     * @param isAOE vrai si AOE
     */
    ProjectileType(float damage, float speed, float explosionRadius, boolean isAOE) {
        this.damage = damage;
        this.speed = speed;
        this.explosionRadius = explosionRadius;
        this.isAOE = isAOE;
        this.texture = null; // Sera assignée par ProjectileAssetManager
    }

    /**
     * Retourne les degats de base.
     * @return degats
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Retourne la vitesse de deplacement.
     * @return vitesse
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Retourne le rayon d'explosion AOE.
     * @return rayon
     */
    public float getExplosionRadius() {
        return explosionRadius;
    }

    /**
     * Indique si le projectile est de type AOE.
     * @return true si AOE
     */
    public boolean isAOE() {
        return isAOE;
    }

    /**
     * Retourne la texture shared pour ce type de projectile
     * @return texture partagee
     */
    public TextureRegion getTexture() {
        return texture;
    }

    /**
     * Retourne l'animation associee au type.
     * @return animation ou null
     */
    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    /**
     * Assigne la texture pour ce type (appelée par ProjectileAssetManager)
     * Flyweight: une seule texture pour tous les projectiles de ce type
     * @param tex texture partagee
     */
    public void setTexture(TextureRegion tex) {
        this.texture = tex;
    }

    /**
     * Assigne l'animation partagee pour ce type.
     * @param anim animation partagee
     */
    public void setAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
    }
}
