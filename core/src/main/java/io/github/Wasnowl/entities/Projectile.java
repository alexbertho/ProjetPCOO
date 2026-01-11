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
    protected ProjectileType type;  // Flyweight intrinsic state
    protected Vector2 velocity;
    protected Enemy target;
    protected Array<Enemy> allEnemies;  // pour dégâts AOE
    protected boolean dead = false;
    protected com.badlogic.gdx.graphics.g2d.TextureRegion texture; // Texture du projectile pour rendu
    protected float stateTime = 0f; // pour l'animation

    public Projectile(Vector2 start, Enemy target, ProjectileType type) {
        this(start, target, type, null);
    }

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
     */
    public void setTexture(com.badlogic.gdx.graphics.g2d.TextureRegion tex) {
        this.texture = tex;
    }

    public boolean isDead() {
        return dead;
    }

    public ProjectileType getType() {
        return type;
    }

    public Enemy getTarget() {
        return target;
    }
}



