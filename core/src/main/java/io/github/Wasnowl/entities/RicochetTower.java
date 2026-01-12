package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.managers.ProjectilePool;

/**
 * RicochetTower : Tour spécialisée qui tire des projectiles qui ricochent
 * Les projectiles ricochent jusqu'à 3 fois sur des ennemis à portée de 100 pixels
 */
public class RicochetTower extends Tower {

    /**
     * Cree une tour ricochet avec projectile specifique.
     * @param x position X
     * @param y position Y
     * @param range portee
     * @param fireRate cadence de tir
     * @param enemies liste d'ennemis
     * @param projectiles liste de projectiles
     */
    public RicochetTower(float x, float y, float range, float fireRate,
                         Array<Enemy> enemies, Array<Projectile> projectiles) {
        super(x, y, range, fireRate, enemies, projectiles);
        this.projectileType = ProjectileType.RICOCHET;
    }

    /**
     * Tire un projectile ricochet via le pool.
     * @param target cible visee
     */
    @Override
    protected void shoot(Enemy target) {
        // Utiliser le ProjectilePool pour créer les projectiles ricochets
        Projectile p = ProjectilePool.getInstance().acquire(position.cpy(), target, projectileType, enemies);
        projectiles.add(p);
    }
}
