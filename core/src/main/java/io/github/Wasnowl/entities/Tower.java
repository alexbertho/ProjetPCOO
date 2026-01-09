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
    
    // Gestion du rendu (texture, animation)
    protected TowerRenderer renderer;

    public Tower(float x, float y, float range, float fireRate,
                 Array<Enemy> enemies, Array<Projectile> projectiles) {
        super(x, y);
        this.range = range;
        this.fireRate = fireRate;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.renderer = new TowerRenderer();
    }
    
    public void setTexture(com.badlogic.gdx.graphics.g2d.TextureRegion texture) {
        renderer.setTexture(texture);
    }
    
    public void setAnimation(com.badlogic.gdx.graphics.g2d.TextureRegion[] frames, float frameDuration) {
        renderer.setAnimation(frames, frameDuration);
    }
    
    public void setAnimating(boolean animating) {
        renderer.setAnimating(animating);
    }

    @Override
    public void update(float delta) {
        // Mettre à jour l'animation
        renderer.update(delta);
        
        fireCooldown -= delta;
        Enemy target = findTarget();
        if (target != null && fireCooldown <= 0) {
            shoot(target);
            fireCooldown = 1 / fireRate;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch, position, size);
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

