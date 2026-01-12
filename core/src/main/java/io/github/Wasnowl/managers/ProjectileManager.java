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

    public ProjectileManager(Array<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    /**
     * Constructeur compatible MVC: récupère la liste de projectiles depuis GameState
     */
    public ProjectileManager(GameState state) {
        this(state.getProjectiles());
    }

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

    public void render(SpriteBatch batch) {
        for (Projectile p : projectiles) {
            p.render(batch);
        }
    }

    public int getActiveProjectileCount() {
        return projectiles.size;
    }

    public void clear() {
        projectiles.clear();
    }
}
