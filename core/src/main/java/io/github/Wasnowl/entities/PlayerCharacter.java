package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.GameObject;

/**
 * Personnage jouable du mode combat.
 * Le mouvement est pilote par GameInputController.
 */
public class PlayerCharacter extends GameObject {
    private final Vector2 velocity = new Vector2();
    private final float speed = 180f;
    private final PlayerAnimator animator;
    private TextureRegion currentFrame;
    private final Vector2 inputDirection = new Vector2();

    /**
     * Cree un personnage jouable a la position initiale.
     * @param x position X
     * @param y position Y
     */
    public PlayerCharacter(float x, float y) {
        super(x, y);
        this.size.set(32, 64);
        animator = new PlayerAnimator();
        currentFrame = animator.getFrame(0f, 0f, 0f);
    }

    /**
     * Met a jour la position et l'animation selon l'entree.
     * @param delta temps ecoule (secondes)
     */
    @Override
    public void update(float delta) {
        // Movement driven by inputDirection set by GameInputController
        velocity.set(inputDirection);
        if (velocity.len2() > 0f) {
            velocity.nor().scl(speed);
        }

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        currentFrame = animator.getFrame(delta, velocity.x, velocity.y);
    }

    /**
     * Rend le sprite courant du joueur.
     * @param batch sprite batch actif
     */
    @Override
    public void render(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }
    }

    /**
     * Definit la direction d'entree normalisee (ou nulle).
     * @param dir direction souhaitee
     */
    public void setInputDirection(Vector2 dir) {
        if (dir == null) inputDirection.set(0f, 0f);
        else inputDirection.set(dir);
    }
}
