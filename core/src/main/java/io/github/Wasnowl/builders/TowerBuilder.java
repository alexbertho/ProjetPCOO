package io.github.Wasnowl.builders;

import io.github.Wasnowl.entities.Tower;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.entities.ProjectileType;
import io.github.Wasnowl.entities.TowerType;
import com.badlogic.gdx.utils.Array;

/**
 * Builder pattern pour créer des tours avec des configurations flexibles.
 * Permet de définir le type de projectile, la portée, etc.
 */
public class TowerBuilder {
    private float x;
    private float y;
    private float range = 150f;
    private float fireRate = 1f;
    private ProjectileType projectileType = ProjectileType.SIMPLE;
    private TowerType towerType = null;
    private Array<Enemy> enemies;
    private Array<Projectile> projectiles;

    public TowerBuilder(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public TowerBuilder withRange(float range) {
        this.range = range;
        return this;
    }

    public TowerBuilder withFireRate(float fireRate) {
        this.fireRate = fireRate;
        return this;
    }

    public TowerBuilder withProjectileType(ProjectileType type) {
        this.projectileType = type;
        return this;
    }

    public TowerBuilder withTowerType(TowerType towerType) {
        this.towerType = towerType;
        if (towerType != null) this.projectileType = towerType.getProjectileType();
        return this;
    }

    public TowerBuilder withEnemies(Array<Enemy> enemies) {
        this.enemies = enemies;
        return this;
    }

    public TowerBuilder withProjectiles(Array<Projectile> projectiles) {
        this.projectiles = projectiles;
        return this;
    }

    public Tower build() {
        if (enemies == null || projectiles == null) {
            throw new IllegalStateException("enemies et projectiles doivent être définis");
        }
        Tower tower = new Tower(x, y, range, fireRate, enemies, projectiles);
        tower.setProjectileType(projectileType);
        if (towerType != null) tower.setTowerType(towerType);
        return tower;
    }
}
