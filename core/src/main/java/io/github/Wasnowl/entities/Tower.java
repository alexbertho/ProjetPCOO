package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Wasnowl.GameObject;
import io.github.Wasnowl.managers.ProjectilePool;
import com.badlogic.gdx.utils.Array;

/**
 * Tower : crée des projectiles du type spécifié.
 */
public class Tower extends GameObject {
    protected float range;
    protected float fireRate; // tirs par seconde
    protected float fireCooldown = 0;
    protected ProjectileType projectileType = ProjectileType.SIMPLE; // type de projectile par défaut
    protected TowerType towerType;
    protected int cost = 0;

    // Référence vers la liste globale des ennemis et projectiles
    private Array<Enemy> enemies;
    private Array<Projectile> projectiles;

    public Tower(float x, float y, float range, float fireRate,
                 Array<Enemy> enemies, Array<Projectile> projectiles) {
        super(x, y);
        this.range = range;
        this.fireRate = fireRate;
        this.enemies = enemies;
        this.projectiles = projectiles;
    }

    @Override
    public void update(float delta) {
        fireCooldown -= delta;
        Enemy target = findTarget();
        if (target != null && fireCooldown <= 0) {
            shoot(target);
            fireCooldown = 1 / fireRate;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Exemple : dessiner une texture de tour
        // batch.draw(texture, position.x, position.y, size.x, size.y);
    }

    private Enemy findTarget() {
        // sécurité : si la liste est null ou vide, pas de cible
        if (enemies == null || enemies.size == 0) {
            return null;
        }

        for (Enemy e : enemies) {
            if (!e.isDead() && e.getPosition().dst(position) <= range) {
                return e;
            }
        }
        return null;
    }

    protected void shoot(Enemy target) {
        // Utiliser le type de projectile défini pour cette tour
        // Passer la liste des ennemis pour supporter l'AOE
        Projectile p = ProjectilePool.getInstance().acquire(position.cpy(), target, projectileType, enemies);
        projectiles.add(p);
    }

    public void setProjectileType(ProjectileType type) {
        this.projectileType = type;
    }

    public void setTowerType(TowerType type) {
        this.towerType = type;
        if (type != null) {
            this.cost = type.getCost();
            this.projectileType = type.getProjectileType();
        }
    }

    public TowerType getTowerType() {
        return towerType;
    }

    public int getCost() {
        return cost;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }
}

