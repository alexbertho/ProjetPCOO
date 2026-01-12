package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.Wasnowl.GameObject;
import io.github.Wasnowl.managers.ProjectilePool;
import com.badlogic.gdx.utils.Array;

/**
 * Tower : crée des projectiles du type spécifié.
 */
public class Tower extends GameObject {
    /** Portee de tir. */
    protected float range;
    /** Cadence de tir (tirs par seconde). */
    protected float fireRate;
    /** Temps avant prochain tir. */
    protected float fireCooldown = 0;
    /** Type de projectile par defaut. */
    protected ProjectileType projectileType = ProjectileType.SIMPLE;
    /** Type de tour (si defini). */
    protected TowerType towerType;
    /** Cout d'achat. */
    protected int cost = 0;

    // Référence vers la liste globale des ennemis et projectiles
    /** Liste d'ennemis partagee. */
    protected Array<Enemy> enemies;
    /** Liste de projectiles partagee. */
    protected Array<Projectile> projectiles;
    
    // Gestion du rendu (texture, animation)
    /** Renderer dedie aux sprites/animations. */
    protected TowerRenderer renderer;

    /**
     * Cree une tour avec ses dependances de jeu.
     * @param x position X
     * @param y position Y
     * @param range portee
     * @param fireRate cadence de tir
     * @param enemies liste d'ennemis
     * @param projectiles liste de projectiles
     */
    public Tower(float x, float y, float range, float fireRate,
                 Array<Enemy> enemies, Array<Projectile> projectiles) {
        super(x, y);
        this.range = range;
        this.fireRate = fireRate;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.renderer = new TowerRenderer();
    }
    
    /**
     * Definit une texture statique pour la tour.
     * @param texture texture a utiliser
     */
    public void setTexture(com.badlogic.gdx.graphics.g2d.TextureRegion texture) {
        renderer.setTexture(texture);
    }
    
    /**
     * Definit une animation pour la tour.
     * @param frames frames d'animation
     * @param frameDuration duree d'une frame
     */
    public void setAnimation(com.badlogic.gdx.graphics.g2d.TextureRegion[] frames, float frameDuration) {
        renderer.setAnimation(frames, frameDuration);
    }
    
    /**
     * Active ou desactive l'animation.
     * @param animating true pour animer
     */
    public void setAnimating(boolean animating) {
        renderer.setAnimating(animating);
    }

    /**
     * Met a jour l'animation et gere les tirs.
     * @param delta temps ecoule (secondes)
     */
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

    /**
     * Rend la tour a l'ecran.
     * @param batch sprite batch actif
     */
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

    /**
     * Tire un projectile vers la cible.
     * @param target cible visee
     */
    protected void shoot(Enemy target) {
        // Utiliser le type de projectile défini pour cette tour
        // Passer la liste des ennemis pour supporter l'AOE
        Projectile p = ProjectilePool.getInstance().acquire(position.cpy(), target, projectileType, enemies);
        projectiles.add(p);
    }

    /**
     * Definit le type de projectile tire par la tour.
     * @param type type de projectile
     */
    public void setProjectileType(ProjectileType type) {
        this.projectileType = type;
    }

    /**
     * Definit le type de tour et applique cout/projectile associes.
     * @param type type de tour
     */
    public void setTowerType(TowerType type) {
        this.towerType = type;
        if (type != null) {
            this.cost = type.getCost();
            this.projectileType = type.getProjectileType();
        }
    }

    /**
     * Retourne le type de tour.
     * @return type de tour
     */
    public TowerType getTowerType() {
        return towerType;
    }

    /**
     * Retourne le cout d'achat.
     * @return cout
     */
    public int getCost() {
        return cost;
    }

    /**
     * Retourne le type de projectile associe.
     * @return type de projectile
     */
    public ProjectileType getProjectileType() {
        return projectileType;
    }
}
