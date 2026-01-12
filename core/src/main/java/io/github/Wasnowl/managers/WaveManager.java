package io.github.Wasnowl.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.List;
import java.util.ArrayList;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.strategies.PathStrategyFactory;
import java.util.function.IntConsumer;
import io.github.Wasnowl.model.GameState;

/**
 * WaveManager: gère le spawn des vagues. Pour la première vague on spawn 3 de chaque ennemi 1..4
 */
public class WaveManager {
    private int currentWave = 0;
    private final Array<Enemy> enemies;
    private float spawnTimer = 0f;
    private final float timeBetweenSpawns = 1.5f; // délai entre chaque ennemi
    private int enemiesToSpawn = 0;
    private int enemiesSpawned = 0;
    private final List<Integer> spawnQueue = new ArrayList<>();
    private final CurrencyManager currencyManager;
    private Runnable onMoneyChanged; // Callback pour mettre à jour l'UI
    private IntConsumer onLifeLost; // Callback pour notifier une perte de vie (int amount)

    // Configuration des vagues (exemple simple)
    private int[] waveSizes = {5, 10, 15}; // nombre d'ennemis par vague
    private java.util.Map<Integer, Integer> damageByEnemyType = new java.util.HashMap<>();

    public WaveManager(Array<Enemy> enemies, CurrencyManager currencyManager) {
        this.enemies = enemies;
        this.currencyManager = currencyManager;
        // valeurs par défaut des dégâts égal au type (1->1, 2->2, etc.)
        for (int i = 1; i <= 4; i++) {
            damageByEnemyType.put(i, i);
        }
    }

    /**
     * Constructeur compatible MVC: utilise un GameState central
     */
    public WaveManager(GameState state) {
        this(state.getEnemies(), state.getCurrencyManager());
    }

    /**
     * Définit une callback appelée quand l'argent change
     */
    public void setOnMoneyChanged(Runnable callback) {
        this.onMoneyChanged = callback;
    }

    /**
     * Définit une callback appelée quand le joueur perd des vies (amount)
     */
    public void setOnLifeLost(IntConsumer callback) {
        this.onLifeLost = callback;
    }

    /**
     * Remplace ou complète la table de dégâts par type d'ennemi.
     * Exemple: map.put(1,1); map.put(2,2); map.put(3,5);
     */
    public void setDamageMapping(java.util.Map<Integer, Integer> mapping) {
        if (mapping == null) return;
        this.damageByEnemyType.clear();
        this.damageByEnemyType.putAll(mapping);
    }

    public void startNextWave() {
        if (currentWave < waveSizes.length) {
            enemies.clear();
            // Pour la 1ère vague, on veut 3 de chaque ennemi 1..4
            if (currentWave == 0) {
                spawnQueue.clear();
                for (int id = 1; id <= 4; id++) {
                    for (int k = 0; k < 3; k++) spawnQueue.add(id);
                }
                enemiesToSpawn = spawnQueue.size();
            } else {
                enemiesToSpawn = waveSizes[currentWave];
            }
            enemiesSpawned = 0;
            spawnTimer = 0f;
            currentWave++;
        } else {
            System.out.println("Toutes les vagues terminées !");
        }
    }

    public void update(float delta) {
        // Spawn progressif des ennemis
        if (enemiesSpawned < enemiesToSpawn) {
            spawnTimer += delta;
            if (spawnTimer >= timeBetweenSpawns) {
                spawnTimer = 0f;
                spawnEnemy();
                enemiesSpawned++;
            }
        }

        // Mise à jour des ennemis existants
        // Mise à jour des ennemis existants (itération arrière pour suppression sûre)
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            e.update(delta);
            if (e.isDead()) {
                // Si l'ennemi a atteint la fin du chemin, le joueur perd des vies
                if (e.hasReachedEndOfPath()) {
                    if (onLifeLost != null) {
                        int damage = getDamageForEnemyType(e.getEnemyType());
                        onLifeLost.accept(damage);
                    }
                } else {
                    // Donner de l'argent si l'ennemi n'a pas atteint la fin du chemin
                    int reward = getRewardForEnemyType(e.getEnemyType());
                    currencyManager.add(reward);
                    // Notifier que l'argent a changé
                    if (onMoneyChanged != null) {
                        onMoneyChanged.run();
                    }
                }
                enemies.removeIndex(i);
            }
        }
    }

    private void spawnEnemy() {
        // Si spawnQueue remplie, utiliser les IDs demandés
        int enemyId = -1;
        if (!spawnQueue.isEmpty()) {
            enemyId = spawnQueue.remove(0);
        }

        Enemy enemy = new Enemy(0, 0, getPathForWave(currentWave), 50f);
        // Assigner taille raisonnable (ex: 32x32)
        enemy.setSize(new com.badlogic.gdx.math.Vector2(32f, 32f));

        if (enemyId != -1) {
            // Charger seulement walk et death au spawn
            // walk2 sera chargé de manière lazy quand <50% HP
            TextureRegion[] walk = EnemyAssetManager.getInstance().loadAnimationFromSpritesheet(enemyId, "walk", 6, 1);
            if (walk != null) enemy.setWalkFrames(walk, 0.1f);

            TextureRegion[] death = EnemyAssetManager.getInstance().loadAnimationFromSpritesheet(enemyId, "death", 6, 1);
            if (death != null) enemy.setDeathFrames(death, 0.1f);

            // Passer l'ID pour lazy loading de walk2
            enemy.setEnemyId(enemyId);
        }

        enemies.add(enemy);
    }

    private Vector2[] getPathForWave(int wave) {
        // Utilise la stratégie de chemin via la Factory
        return PathStrategyFactory.getStrategyForWave(wave).getPath();
    }

    /**
     * Retourne la récompense pour un type d'ennemi
     * Type 1 = 1 or, Type 2 = 2 or, Type 3 = 3 or, Type 4 = 4 or
     */
    private int getRewardForEnemyType(int enemyType) {
        if (enemyType >= 1 && enemyType <= 4) {
            return enemyType;
        }
        return 0;
    }

    private int getDamageForEnemyType(int enemyType) {
        Integer d = damageByEnemyType.get(enemyType);
        if (d != null) return d;
        // fallback : si non défini, 1 point
        return 1;
    }

    public boolean isWaveFinished() {
        return enemies.size == 0 && enemiesSpawned == enemiesToSpawn;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}
