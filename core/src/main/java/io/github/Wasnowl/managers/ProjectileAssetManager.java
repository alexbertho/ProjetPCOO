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
        // Tableau des 27 frames de flèches orientées (index 0 = 1.png, index 26 = 27.png)
        private TextureRegion[] arrowFrames = null;

        /**
         * Charge les 27 sprites de flèches orientées depuis le dossier assets/towers/Units/Arrow
         * Doit être appelé au démarrage du jeu (ex: dans GameScreen.show)
         */
        public void loadArrowFrames() {
            arrowFrames = new TextureRegion[27];
            for (int i = 0; i < 27; i++) {
                String path = "towers/Units/Arrow/" + (i+1) + ".png";
                if (com.badlogic.gdx.Gdx.files.internal(path).exists()) {
                    com.badlogic.gdx.graphics.Texture tex = new com.badlogic.gdx.graphics.Texture(com.badlogic.gdx.Gdx.files.internal(path));
                    arrowFrames[i] = new TextureRegion(tex);
                } else {
                    arrowFrames[i] = null;
                }
            }
        }

        /**
         * Retourne la frame de flèche la plus proche de l'angle donné (en radians, 0 = droite, PI/2 = haut)
         * angle en radians, sens trigo, 0 = droite, PI/2 = haut, PI = gauche, -PI/2 = bas
         */
        public TextureRegion getArrowFrameForAngle(float angleRad) {
            if (arrowFrames == null) return null;
            // Convertir angle en degrés [0, 360)
            float deg = (float)Math.toDegrees(angleRad);
            if (deg < 0) deg += 360f;
            // 1.png = 90° (haut), 2.png = un peu moins, ... 27.png = juste avant revenir à 90°
            // Donc 0° = droite, 90° = haut, 180° = gauche, 270° = bas
            // On veut mapper [0, 360) en 27 secteurs centrés sur 90°
            float sector = ((deg - 90f + 360f) % 360f) / 360f * 27f;
            int idx = Math.round(sector) % 27;
            return arrowFrames[idx];
        }
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
