package io.github.Wasnowl.managers;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.entities.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WaveManager {
    private int currentWave = 0;
    private List<Enemy> enemies = new ArrayList<>();
    private float spawnTimer = 0f;
    private float timeBetweenSpawns = 1.5f; // délai entre chaque ennemi
    private int enemiesToSpawn = 0;
    private int enemiesSpawned = 0;

    // Configuration des vagues (exemple simple)
    private int[] waveSizes = {5, 10, 15}; // nombre d’ennemis par vague

    public void startNextWave() {
        if (currentWave < waveSizes.length) {
            enemies.clear();
            enemiesToSpawn = waveSizes[currentWave];
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
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            e.update(delta);

            // Exemple : si l’ennemi est "mort", on le retire
            if (e.isDead()) {
                it.remove();
            }
        }
    }

    private void spawnEnemy() {
        // Exemple de création d’ennemi avec un chemin prédéfini
        Enemy enemy = new Enemy(0, 0, getPathForWave(currentWave), 50f);
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
        return enemies.isEmpty() && enemiesSpawned == enemiesToSpawn;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}

