package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.GameObject;

/**
 * Projectile utilisant le pattern Flyweight.
 * - ProjectileType (intrinsic state) : données immuables partagées (damage, speed, etc.)
 * - État extrinsèque : position, target, velocity (uniques pour chaque instance)
 */
public class Projectile extends GameObject {
    /** Type de projectile (flyweight). */
    protected ProjectileType type;
    /** Vitesse courante. */
    protected Vector2 velocity;
    /** Cible actuelle. */
    protected Enemy target;
    /** Liste d'ennemis (AOE). */
    protected Array<Enemy> allEnemies;
    /** Indique si le projectile est termine. */
    protected boolean dead = false;
    /** Texture du projectile pour rendu. */
    protected com.badlogic.gdx.graphics.g2d.TextureRegion texture;
    /** Temps d'animation cumule. */
    protected float stateTime = 0f;

    /**
     * Cree un projectile vers une cible.
     * @param start position de depart
     * @param target cible visee
     * @param type type de projectile
     */
    public Projectile(Vector2 start, Enemy target, ProjectileType type) {
        this(start, target, type, null);
    }

    /**
     * Cree un projectile avec reference a la liste d'ennemis (AOE).
     * @param start position de depart
     * @param target cible visee
     * @param type type de projectile
     * @param allEnemies liste d'ennemis (AOE)
     */
    public Projectile(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        super(start.x, start.y);
        this.type = type;
        this.target = target;
        this.allEnemies = allEnemies;
        setSizeFromType(type);
        // Calcul de la vélocité basée sur le speed du type
        this.velocity = target.getPosition().cpy().sub(start).nor().scl(type.getSpeed());
    }

    /**
     * Reset the projectile to reuse from a pool.
     * @param start position de depart
     * @param target cible visee
     * @param type type de projectile
     * @param allEnemies liste d'ennemis (AOE)
     */
    public void reset(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        this.position.set(start.x, start.y);
        this.type = type;
        this.target = target;
        this.allEnemies = allEnemies;
        this.dead = false;
        setSizeFromType(type);
        if (target != null) {
            this.velocity = target.getPosition().cpy().sub(start).nor().scl(type.getSpeed());
        } else {
            this.velocity = new Vector2(0,0);
        }
    }

    private void setSizeFromType(ProjectileType type) {
        if (type == null) {
            this.size = new Vector2(12f, 12f);
            return;
        }
        switch (type) {
            case AOE_STRONG:
                this.size = new Vector2(32f, 32f);
                break;
            case RICOCHET:
                this.size = new Vector2(10f, 10f);
                break;
            case SIMPLE:
            default:
                this.size = new Vector2(14f, 14f);
                break;
        }
    }

    /**
     * Met a jour la trajectoire et gere l'impact.
     * @param delta temps ecoule (secondes)
     */
    @Override
    public void update(float delta) {
        stateTime += delta;
        if (target == null || target.isDead()) {
            dead = true;
            return;
        }

        // Recalculer la direction vers la cible pour la suivre en temps réel
        Vector2 direction = target.getPosition().cpy().sub(position);
        float distance = direction.len();

        // Si on est très proche, impact
        if (distance < 5f) {
            handleImpact();
            dead = true;
            return;
        }

        // Mettre à jour la vélocité pour suivre la cible
        velocity = direction.nor().scl(type.getSpeed());
        
        // Avancer
        position.add(velocity.cpy().scl(delta));
    }

    /**
     * Applique les degats sur la cible ou en AOE.
     */
    protected void handleImpact() {
        if (type.isAOE()) {
            // AOE : infliger dégâts à tous les ennemis dans le rayon
            if (allEnemies != null) {
                for (Enemy e : allEnemies) {
                    if (!e.isDead() && e.getPosition().dst(position) <= type.getExplosionRadius()) {
                        e.takeDamage(type.getDamage());
                    }
                }
            } else {
                // Fallback : au moins le target
                target.takeDamage(type.getDamage());
            }
        } else {
            // Simple : infliger dégâts au target uniquement
            target.takeDamage(type.getDamage());
        }
    }

    /**
     * Rend le projectile (texture ou animation).
     * @param batch sprite batch actif
     */
    @Override
    public void render(SpriteBatch batch) {
        if (type != null) {
            com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> anim = type.getAnimation();
            if (anim != null) {
                com.badlogic.gdx.graphics.g2d.TextureRegion frame = anim.getKeyFrame(stateTime, true);
                batch.draw(frame,
                        position.x - size.x/2,
                        position.y - size.y/2,
                        size.x,
                        size.y);
                return;
            }
            if (type.getTexture() != null) {
                batch.draw(type.getTexture(),
                        position.x - size.x/2,
                        position.y - size.y/2,
                        size.x,
                        size.y);
            }
        }
    }

    /**
     * Définit la texture du projectile (deprecated - utiliser ProjectileAssetManager)
     * @param tex texture a utiliser
     */
    public void setTexture(com.badlogic.gdx.graphics.g2d.TextureRegion tex) {
        this.texture = tex;
    }

    /**
     * Indique si le projectile est a detruire.
     * @return true si mort
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Retourne le type de projectile (flyweight).
     * @return type
     */
    public ProjectileType getType() {
        return type;
    }

    /**
     * Retourne la cible courante.
     * @return cible ou null
     */
    public Enemy getTarget() {
        return target;
    }
}
