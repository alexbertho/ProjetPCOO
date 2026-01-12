package io.github.Wasnowl.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.entities.RicochetProjectile;
import io.github.Wasnowl.entities.ProjectileType;

/**
 * Simple singleton object pool for Projectile instances.
 * Gère automatiquement la création du bon type (Projectile ou RicochetProjectile)
 */
public class ProjectilePool {
    private static ProjectilePool instance;
    private final Array<Projectile> pool = new Array<>();
    private static final float RICOCHET_RANGE = 100f;
    private static final int MAX_BOUNCES = 3;

    private ProjectilePool() {}

    /**
     * Retourne l'instance singleton du pool.
     * @return instance unique
     */
    public static ProjectilePool getInstance() {
        if (instance == null) instance = new ProjectilePool();
        return instance;
    }

    /**
     * Recupere un projectile du pool ou en cree un nouveau.
     * @param start position de depart
     * @param target cible visee
     * @param type type de projectile
     * @param allEnemies liste d'ennemis (AOE)
     * @return projectile reutilise ou cree
     */
    public Projectile acquire(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        // Récupérer du pool si disponible
        if (pool.size > 0) {
            Projectile p = pool.pop();
            p.reset(start, target, type, allEnemies);
            return p;
        }
        
        // Créer un nouveau projectile du bon type
        if (type == ProjectileType.RICOCHET) {
            return new RicochetProjectile(start, target, type, allEnemies, RICOCHET_RANGE, MAX_BOUNCES);
        } else {
            return new Projectile(start, target, type, allEnemies);
        }
    }

    /**
     * Remet un projectile dans le pool pour reutilisation.
     * @param p projectile a recycler
     */
    public void release(Projectile p) {
        if (p == null) return;
        pool.add(p);
    }
}
