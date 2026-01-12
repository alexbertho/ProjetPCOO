package io.github.Wasnowl.controllers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.Wasnowl.entities.Tower;
import io.github.Wasnowl.entities.Enemy;
import io.github.Wasnowl.entities.Projectile;
import io.github.Wasnowl.builders.TowerBuilder;
import io.github.Wasnowl.managers.CurrencyManager;
import io.github.Wasnowl.entities.TowerType;
import io.github.Wasnowl.ui.UIManager;

/**
 * PlacementController gère l'état de prévisualisation et le placement des tours.
 */
public class PlacementController {
    private final Viewport viewport;
    private final Array<Tower> towers;
    private final Array<Enemy> enemies;
    private final Array<Projectile> projectiles;
    private final CurrencyManager currencyManager;
    private final UIManager uiManager;
    private final InputAdapter inputAdapter;

    private boolean placing = false;
    private TowerType previewType = null;
    private final Vector2 previewPos = new Vector2();

    /**
     * Construit le controleur de placement des tours.
     * @param viewport viewport actif
     * @param towers liste des tours
     * @param enemies liste des ennemis
     * @param projectiles liste des projectiles
     * @param currencyManager gestionnaire d'argent
     * @param uiManager gestionnaire UI
     */
    public PlacementController(Viewport viewport,
                               Array<Tower> towers,
                               Array<Enemy> enemies,
                               Array<Projectile> projectiles,
                               CurrencyManager currencyManager,
                               UIManager uiManager) {
        this.viewport = viewport;
        this.towers = towers;
        this.enemies = enemies;
        this.projectiles = projectiles;
        this.currencyManager = currencyManager;
        this.uiManager = uiManager;

        this.inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (placing && button == com.badlogic.gdx.Input.Buttons.LEFT) {
                    Vector3 tmp = new Vector3(screenX, screenY, 0);
                    viewport.unproject(tmp);
                    float wx = tmp.x;
                    float wy = tmp.y;
                    boolean overlap = false;
                    for (Tower t : towers) {
                        if (t.getPosition().dst(wx, wy) < 32f) { overlap = true; break; }
                    }
                    if (!overlap && previewType != null && currencyManager.canAfford(previewType.getCost())) {
                        currencyManager.spend(previewType.getCost());
                        uiManager.setBalance(currencyManager.getBalance());
                        Tower placed = new TowerBuilder(wx, wy)
                            .withRange(previewType.getProjectileType().isAOE() ? 180f : 200f)
                            .withFireRate(previewType.getProjectileType().isAOE() ? 1f : 1.5f)
                            .withTowerType(previewType)
                            .withEnemies(enemies)
                            .withProjectiles(projectiles)
                            .withTowerId(7) // Utiliser le sprite tower 7
                            .withSize(32f) // Taille 32x32 pixels
                            .build();
                        towers.add(placed);
                    }
                    placing = false;
                    previewType = null;
                    uiManager.setCostText("");
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (placing && keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    placing = false;
                    previewType = null;
                    uiManager.setCostText("");
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Retourne l'InputProcessor gere par ce controleur.
     * @return input processor
     */
    public InputProcessor getInputProcessor() {
        return inputAdapter;
    }

    /**
     * Active le mode previsualisation pour un type de tour.
     * @param type type de tour a placer
     */
    public void startPreview(TowerType type) {
        this.previewType = type;
        this.placing = true;
        uiManager.setCostText("Cost: " + type.getCost());
    }

    /**
     * Annule la previsualisation en cours.
     */
    public void cancelPreview() {
        this.placing = false;
        this.previewType = null;
        uiManager.setCostText("");
    }

    /**
     * Indique si une previsualisation est active.
     * @return true si placement actif
     */
    public boolean isPlacing() {
        return placing;
    }

    /**
     * Affiche la previsualisation de placement (rectangle colore).
     * @param shapeRenderer renderer de formes
     * @param camera camera de la scene
     */
    public void renderPreview(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        if (!placing || previewType == null) return;
        Vector3 mouse = new Vector3(com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY(), 0);
        viewport.unproject(mouse);
        previewPos.set(mouse.x, mouse.y);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        com.badlogic.gdx.graphics.Color c = previewType == TowerType.AOE ? new com.badlogic.gdx.graphics.Color(1f, 0.5f, 0.2f, 0.5f) : new com.badlogic.gdx.graphics.Color(0.2f, 0.8f, 0.2f, 0.5f);
        shapeRenderer.setColor(c);
        float w = 32f, h = 32f;
        shapeRenderer.rect(previewPos.x - w/2f, previewPos.y - h/2f, w, h);
        shapeRenderer.end();
    }
}
