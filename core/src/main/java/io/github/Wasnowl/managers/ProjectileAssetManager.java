package io.github.Wasnowl.managers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import io.github.Wasnowl.entities.ProjectileType;

/**
 * ProjectileAssetManager : Gestionnaire centralisé des textures de projectiles
 * Utilise le pattern Flyweight pour partager les textures
 * Une seule instance de texture par ProjectileType, partagée par tous les projectiles
 */
public class ProjectileAssetManager {
    private static ProjectileAssetManager instance;

    private ProjectileAssetManager() {
        // Les textures seront assignées dynamiquement une fois disponibles
    }

    public static ProjectileAssetManager getInstance() {
        if (instance == null) {
            instance = new ProjectileAssetManager();
        }
        return instance;
    }

    /**
     * Assigne une texture à un type de projectile
     * Tous les projectiles de ce type partageront la même texture (Flyweight)
     */
    public void setProjectileTexture(ProjectileType type, TextureRegion texture) {
        type.setTexture(texture);
    }

    /**
     * Assigne une animation à un type de projectile.
     */
    public void setProjectileAnimation(ProjectileType type, Animation<TextureRegion> animation) {
        type.setAnimation(animation);
    }

    /**
     * Récupère la texture d'un type de projectile
     */
    public TextureRegion getProjectileTexture(ProjectileType type) {
        return type.getTexture();
    }

    public Animation<TextureRegion> getProjectileAnimation(ProjectileType type) {
        return type.getAnimation();
    }
}
