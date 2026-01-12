package io.github.Wasnowl.managers;

import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.managers.ProjectilePool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.model.GameState;

/**
 * Gestionnaire des projectiles : update et rendu centralisés.
 * Permet aussi d'appliquer des règles globales (collision, effets).
 */
public class ProjectileManager {
    private Array<Projectile> projectiles;

    /**
     * Cree un gestionnaire base sur une liste de projectiles.
     * @param projectiles liste de projectiles
     */
    public ProjectileManager(Array<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    /**
     * Constructeur compatible MVC: recupere la liste de projectiles depuis GameState.
     * @param state etat du jeu
     */
    public ProjectileManager(GameState state) {
        this(state.getProjectiles());
    }

    /**
     * Met a jour tous les projectiles actifs.
     * @param delta temps ecoule (secondes)
     */
    public void update(float delta) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);

            // Supprimer les projectiles morts
            if (p.isDead()) {
                projectiles.removeIndex(i);
                // release into pool for reuse
                ProjectilePool.getInstance().release(p);
            }
        }
    }

    /**
     * Rend tous les projectiles actifs.
     * @param batch sprite batch actif
     */
    public void render(SpriteBatch batch) {
        for (Projectile p : projectiles) {
            p.render(batch);
        }
    }

    /**
     * Retourne le nombre de projectiles actifs.
     * @return nombre de projectiles
     */
    public int getActiveProjectileCount() {
        return projectiles.size;
    }

    /**
     * Vide la liste de projectiles.
     */
    public void clear() {
        projectiles.clear();
    }
}
