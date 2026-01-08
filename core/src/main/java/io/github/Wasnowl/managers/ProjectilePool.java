package io.github.Wasnowl.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.entities.ProjectileType;

/**
 * Simple singleton object pool for Projectile instances.
 */
public class ProjectilePool {
    private static ProjectilePool instance;
    private final Array<Projectile> pool = new Array<>();

    private ProjectilePool() {}

    public static ProjectilePool getInstance() {
        if (instance == null) instance = new ProjectilePool();
        return instance;
    }

    public Projectile acquire(Vector2 start, Enemy target, ProjectileType type, Array<Enemy> allEnemies) {
        if (pool.size > 0) {
            Projectile p = pool.pop();
            p.reset(start, target, type, allEnemies);
            return p;
        }
        return new Projectile(start, target, type, allEnemies);
    }

    public void release(Projectile p) {
        if (p == null) return;
        // Optionally clear heavy references
        pool.add(p);
    }
}
