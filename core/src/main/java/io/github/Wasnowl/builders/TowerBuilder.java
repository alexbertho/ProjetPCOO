package io.github.Wasnowl.builders;

import io.github.Wasnowl.entities.Tower;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.entities.ProjectileType;
import io.github.Wasnowl.entities.TowerType;
import io.github.Wasnowl.managers.TowerAssetManager;
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
    private int towerId = 7; // ID du sprite de la tour (défaut: 7)
    private float size = 32f; // taille du sprite en pixels

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
    
    public TowerBuilder withTowerId(int towerId) {
        this.towerId = towerId;
        return this;
    }
    
    public TowerBuilder withSize(float size) {
        this.size = size;
        return this;
    }

    public Tower build() {
        if (enemies == null || projectiles == null) {
            throw new IllegalStateException("enemies et projectiles doivent être définis");
        }
        Tower tower = new Tower(x, y, range, fireRate, enemies, projectiles);
        tower.setProjectileType(projectileType);
        if (towerType != null) tower.setTowerType(towerType);
        
        // Charger et assigner l'animation spritesheet
        tower.setSize(new com.badlogic.gdx.math.Vector2(size, size));
        com.badlogic.gdx.graphics.g2d.TextureRegion[] frames = TowerAssetManager.getInstance().loadTowerAnimationFromSpritesheet(towerId, 6, 1);
        if (frames != null) {
            tower.setAnimation(frames, 0.1f); // 0.1s par frame = 10 frames/sec
            tower.setAnimating(true);
        } else {
            System.err.println("Warning: animation non trouvée pour la tour " + towerId);
        }
        
        return tower;
    }
}
