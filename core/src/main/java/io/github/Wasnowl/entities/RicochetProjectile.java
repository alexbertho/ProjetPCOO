package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * RicochetProjectile : Projectile spécialisé qui ricoche sur les ennemis
 * Peut faire jusqu'à 3 ricochets vers d'autres ennemis dans la portée
 */
public class RicochetProjectile extends Projectile {
    private float ricochetRange;
    private int bouncesRemaining;
    private Array<Enemy> hitEnemies; // Pour éviter de richocher 2x sur le même ennemi

    public RicochetProjectile(Vector2 start, Enemy target, ProjectileType type, 
                             Array<Enemy> allEnemies, float ricochetRange, int maxBounces) {
        super(start, target, type, allEnemies);
        this.ricochetRange = ricochetRange;
        this.bouncesRemaining = maxBounces;
        this.hitEnemies = new Array<>();
    }

    /**
     * Reset pour la réutilisation depuis le pool
     */
    @Override
    public void reset(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        super.reset(start, target, type, allEnemies);
        this.bouncesRemaining = 3; // Reset les ricochets restants
        this.hitEnemies.clear(); // Réinitialiser la liste des ennemis frappés
    }

    @Override
    protected void handleImpact() {
        if (target != null && !target.isDead()) {
            // Infliger dégâts à la cible actuelle
            target.takeDamage(type.getDamage());
            hitEnemies.add(target);
            
            // Chercher le prochain ennemi à cibler pour le ricochet
            if (bouncesRemaining > 0) {
                Enemy nextTarget = findNextRicochetTarget();
                if (nextTarget != null) {
                    // Continuer avec le ricochet
                    target = nextTarget;
                    bouncesRemaining--;
                    // Réinitialiser pour continuer vers la nouvelle cible
                    return;
                }
            }
            // Sinon, le projectile est mort
        }
    }

    /**
     * Trouve le prochain ennemi à cibler pour le ricochet
     * Cherche un ennemi non touché, dans la portée de ricochet
     */
    private Enemy findNextRicochetTarget() {
        if (allEnemies == null || allEnemies.size == 0) {
            return null;
        }

        Enemy closest = null;
        float closestDistance = ricochetRange;

        for (Enemy e : allEnemies) {
            if (e.isDead() || hitEnemies.contains(e, false)) {
                continue; // Ignorer les morts et déjà frappés
            }

            float distance = position.dst(e.getPosition());
            if (distance <= closestDistance) {
                closestDistance = distance;
                closest = e;
            }
        }

        return closest;
    }

    @Override
    public void update(float delta) {
        if (target == null || target.isDead()) {
            // Si la cible est morte mais qu'on a des ricochets, chercher une nouvelle cible
            if (bouncesRemaining > 0) {
                target = findNextRicochetTarget();
                if (target == null) {
                    // Pas de nouvelle cible, projectile mort
                    dead = true;
                    return;
                }
            } else {
                dead = true;
                return;
            }
        }

        // Recalculer la direction vers la cible pour la suivre en temps réel
        Vector2 direction = target.getPosition().cpy().sub(position);
        float distance = direction.len();

        // Si on est très proche, impact
        if (distance < 5f) {
            handleImpact();
            // Si on a un nouveau target après l'impact, continuer; sinon mourir
            if (target == null || (bouncesRemaining == 0 && hitEnemies.contains(target, false))) {
                dead = true;
            }
            return;
        }

        // Mettre à jour la vélocité pour suivre la cible
        velocity = direction.nor().scl(type.getSpeed());
        
        // Avancer
        position.add(velocity.cpy().scl(delta));
    }
}
