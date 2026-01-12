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

    /**
     * Construit le controleur principal qui orchestre les updates du modele.
     * @param waveManager gestionnaire des vagues
     * @param projectileManager gestionnaire des projectiles
     * @param player joueur courant
     * @param towers liste des tours
     * @param enemies liste des ennemis
     * @param projectiles liste des projectiles
     */
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
     * Met a jour l'etat du jeu (modele) : vagues, joueur, tours, projectiles.
     * @param delta temps ecoule (secondes)
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
