package io.github.Wasnowl.controllers;

import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.entities.*;
import io.github.Wasnowl.managers.*;

/**
 * GameController: orchestre les mises à jour du modèle (managers, entités) séparément de la vue.
 * Ceci aide à clarifier la séparation MVC en regroupant la logique d'update côté controller.
 */
public class GameController {
    private final WaveManager waveManager;
    private final ProjectileManager projectileManager;
    private final PlayerTower player;
    private final Array<Tower> towers;
    private final Array<Enemy> enemies;
    private final Array<Projectile> projectiles;

    public GameController(WaveManager waveManager,
                          ProjectileManager projectileManager,
                          PlayerTower player,
                          Array<Tower> towers,
                          Array<Enemy> enemies,
                          Array<Projectile> projectiles) {
        this.waveManager = waveManager;
        this.projectileManager = projectileManager;
        this.player = player;
        this.towers = towers;
        this.enemies = enemies;
        this.projectiles = projectiles;
    }

    /**
     * Met à jour l'état du jeu (modèle) : vagues, joueur, tours, projectiles.
     */
    public void update(float delta) {
        if (waveManager != null) waveManager.update(delta);
        if (player != null) player.update(delta);
        if (towers != null) {
            for (Tower t : towers) {
                if (t != null) t.update(delta);
            }
        }
        if (projectileManager != null) projectileManager.update(delta);
    }
}

