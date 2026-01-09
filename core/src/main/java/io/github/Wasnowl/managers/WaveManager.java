package io.github.Wasnowl.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.List;
import java.util.ArrayList;
import io.github.Wasnowl.entities.Enemy;
import java.util.Iterator;

/**
 * WaveManager: gère le spawn des vagues. Pour la première vague on spawn 3 de chaque ennemi 1..4
 */
public class WaveManager {
    private int currentWave = 0;
    private Array<Enemy> enemies;
    private float spawnTimer = 0f;
    private float timeBetweenSpawns = 1.5f; // délai entre chaque ennemi
    private int enemiesToSpawn = 0;
    private int enemiesSpawned = 0;
    private List<Integer> spawnQueue = new ArrayList<>();

    // Configuration des vagues (exemple simple)
    private int[] waveSizes = {5, 10, 15}; // nombre d’ennemis par vague

    public WaveManager(Array<Enemy> enemies) {
        this.enemies = enemies;
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
        // Ici tu peux définir des chemins différents selon la vague
        return new Vector2[] {
            new Vector2(0, 0),
            new Vector2(5, 0),
            new Vector2(5, 5),
            new Vector2(10, 5)
        };
    }

    public boolean isWaveFinished() {
        return enemies.size == 0 && enemiesSpawned == enemiesToSpawn;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}

