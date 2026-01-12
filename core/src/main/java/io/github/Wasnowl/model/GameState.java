package io.github.Wasnowl.model;

import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.entities.*;
import io.github.Wasnowl.managers.CurrencyManager;

/**
 * GameState : conteneur central de l'état du jeu (Model pour MVC)
 * Regroupe collections partagées et managers.
 */
public class GameState {
    private final Array<Enemy> enemies;
    private final Array<Tower> towers;
    private final Array<Projectile> projectiles;
    private CurrencyManager currencyManager;
    private float playerHealth;
    private float playerMaxHealth;
    private PlayerTower player;

    /**
     * Cree un etat de jeu vide avec collections par defaut.
     */
    public GameState() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.currencyManager = new CurrencyManager(0);
        this.playerHealth = 0f;
        this.playerMaxHealth = 0f;
        this.player = null;
    }

    /**
     * Retourne la liste des ennemis actifs.
     * @return ennemis actifs
     */
    public Array<Enemy> getEnemies() { return enemies; }
    /**
     * Retourne la liste des tours placees.
     * @return tours
     */
    public Array<Tower> getTowers() { return towers; }
    /**
     * Retourne la liste des projectiles actifs.
     * @return projectiles actifs
     */
    public Array<Projectile> getProjectiles() { return projectiles; }

    /**
     * Retourne le gestionnaire d'argent.
     * @return currency manager
     */
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    /**
     * Definit le gestionnaire d'argent.
     * @param m nouveau manager
     */
    public void setCurrencyManager(CurrencyManager m) { this.currencyManager = m; }

    /**
     * Retourne la vie actuelle du joueur.
     * @return vie courante
     */
    public float getPlayerHealth() { return playerHealth; }
    /**
     * Definit la vie actuelle du joueur.
     * @param h vie courante
     */
    public void setPlayerHealth(float h) { this.playerHealth = h; }

    /**
     * Retourne la vie maximale du joueur.
     * @return vie max
     */
    public float getPlayerMaxHealth() { return playerMaxHealth; }
    /**
     * Definit la vie maximale du joueur.
     * @param h vie max
     */
    public void setPlayerMaxHealth(float h) { this.playerMaxHealth = h; }

    /**
     * Retourne l'instance du joueur sur la map.
     * @return joueur
     */
    public PlayerTower getPlayer() { return player; }
    /**
     * Definit l'instance du joueur sur la map.
     * @param p joueur
     */
    public void setPlayer(PlayerTower p) { this.player = p; }
}
