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
    private ProjectileType type;  // Flyweight intrinsic state
    private Vector2 velocity;
    private Enemy target;
    private Array<Enemy> allEnemies;  // pour dégâts AOE
    private boolean dead = false;

    public Projectile(Vector2 start, Enemy target, ProjectileType type) {
        this(start, target, type, null);
    }

    public Projectile(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        super(start.x, start.y);
        this.type = type;
        this.target = target;
        this.allEnemies = allEnemies;
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
        if (target != null) {
            this.velocity = target.getPosition().cpy().sub(start).nor().scl(type.getSpeed());
        } else {
            this.velocity = new Vector2(0,0);
        }
    }

    @Override
    public void update(float delta) {
        if (target == null || target.isDead()) {
            dead = true;
            return;
        }

        position.add(velocity.cpy().scl(delta));

        if (position.dst(target.getPosition()) < 5f) {
            handleImpact();
            dead = true;
        }
    }

    private void handleImpact() {
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
        // batch.draw(texture, position.x, position.y, size.x, size.y);
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



