package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.GameObject;

/**
 * Enemy : orchestration des 3 composants (Movement, Health, Animator)
 * Responsabilité : coordonner les sous-systèmes
 */
public class Enemy extends GameObject {
    private EnemyMovement movement;
    private EnemyHealth health;
    private EnemyAnimator animator;
    private int enemyType = -1;
    private boolean reachedEndOfPath = false;

    /**
     * Cree un ennemi et initialise ses composants (mouvement, vie, animation).
     * @param x position X
     * @param y position Y
     * @param path chemin a suivre
     * @param speed vitesse de deplacement
     */
    public Enemy(float x, float y, Vector2[] path, float speed) {
        super(x, y);
        animator = new EnemyAnimator();
        movement = new EnemyMovement(new Vector2(x, y), path, speed);
        health = new EnemyHealth(100f, animator);
        this.size = new Vector2(32f, 32f);
    }

    /**
     * Doit etre appele apres la creation pour permettre le lazy loading de walk2.
     * @param id identifiant d'ennemi
     */
    public void setEnemyId(int id) {
        health.setEnemyId(id);
        this.enemyType = id;
    }

    /**
     * Met a jour le mouvement, l'animation et les PV.
     * @param delta temps ecoule (secondes)
     */
    @Override
    public void update(float delta) {
        movement.update(delta);
        animator.update(delta);
        health.update();

        // Synchroniser la position avec le rendu
        position = movement.getPosition();

        // Si l'ennemi atteint la fin du chemin, le marquer comme mort
        if (movement.hasReachedEnd() && !health.isDead()) {
            reachedEndOfPath = true;
            takeDamage(health.getMaxHealth());
        }
    }

    /**
     * Rend l'ennemi a sa position courante.
     * @param batch sprite batch actif
     */
    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch, position, size);
    }

    // API publique pour les dégâts
    /**
     * Applique des degats et arrete le mouvement si mort.
     * @param amount degats a appliquer
     */
    public void takeDamage(float amount) {
        health.takeDamage(amount);
        // Arrêter le mouvement si mort
        if (health.isDead()) {
            movement.stop();
        }
    }

    /**
     * Indique si l'ennemi est mort.
     * @return true si mort
     */
    public boolean isDead() {
        return health.isDead();
    }

    /**
     * Retourne la vie actuelle.
     * @return vie courante
     */
    public float getHealth() {
        return health.getHealth();
    }

    /**
     * Retourne la vie maximale.
     * @return vie max
     */
    public float getMaxHealth() {
        return health.getMaxHealth();
    }

    // Delegation pour les animations
    /**
     * Charge les frames de marche (etat normal).
     * @param frames frames de marche
     * @param frameDuration duree d'une frame
     */
    public void setWalkFrames(TextureRegion[] frames, float frameDuration) {
        animator.setWalkFrames(frames, frameDuration);
    }

    /**
     * Charge les frames de marche blessee.
     * @param frames frames de marche
     * @param frameDuration duree d'une frame
     */
    public void setWalk2Frames(TextureRegion[] frames, float frameDuration) {
        animator.setWalk2Frames(frames, frameDuration);
    }

    /**
     * Charge les frames de mort.
     * @param frames frames de mort
     * @param frameDuration duree d'une frame
     */
    public void setDeathFrames(TextureRegion[] frames, float frameDuration) {
        animator.setDeathFrames(frames, frameDuration);
    }

    /**
     * Retourne l'identifiant du type d'ennemi.
     * @return id du type
     */
    public int getEnemyType() {
        return enemyType;
    }

    /**
     * Indique si l'ennemi a atteint la fin du chemin.
     * @return true si fin atteinte
     */
    public boolean hasReachedEndOfPath() {
        return reachedEndOfPath;
    }
}
