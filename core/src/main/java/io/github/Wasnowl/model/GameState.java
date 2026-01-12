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

    public GameState() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.currencyManager = new CurrencyManager(0);
        this.playerHealth = 0f;
        this.playerMaxHealth = 0f;
        this.player = null;
    }

    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }

    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public void setCurrencyManager(CurrencyManager m) { this.currencyManager = m; }

    public float getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(float h) { this.playerHealth = h; }

    public float getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(float h) { this.playerMaxHealth = h; }

    public PlayerTower getPlayer() { return player; }
    public void setPlayer(PlayerTower p) { this.player = p; }
}

