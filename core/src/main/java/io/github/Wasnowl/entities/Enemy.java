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

    public Enemy(float x, float y, Vector2[] path, float speed) {
        super(x, y);
        animator = new EnemyAnimator();
        movement = new EnemyMovement(new Vector2(x, y), path, speed);
        health = new EnemyHealth(100f, animator);
        this.size = new Vector2(32f, 32f);
    }

    @Override
    public void update(float delta) {
        movement.update(delta);
        animator.update(delta);
        health.update();

        // Synchroniser la position avec le rendu
        position = movement.getPosition();
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch, position, size);
    }

    // API publique pour les dégâts
    public void takeDamage(float amount) {
        health.takeDamage(amount);
        // Arrêter le mouvement si mort
        if (health.isDead()) {
            movement.stop();
        }
    }

    public boolean isDead() {
        return health.isDead();
    }

    public float getHealth() {
        return health.getHealth();
    }

    public float getMaxHealth() {
        return health.getMaxHealth();
    }

    // Delegation pour les animations
    public void setWalkFrames(TextureRegion[] frames, float frameDuration) {
        animator.setWalkFrames(frames, frameDuration);
    }

    public void setWalk2Frames(TextureRegion[] frames, float frameDuration) {
        animator.setWalk2Frames(frames, frameDuration);
    }

    public void setDeathFrames(TextureRegion[] frames, float frameDuration) {
        animator.setDeathFrames(frames, frameDuration);
    }
}



