package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

/**
 * EnemyAnimator : gère l'état d'animation (WALK, WALK2, DEATH, DEAD) et le rendu
 * Responsabilité unique : animations et rendu visuel
 */
public class EnemyAnimator {
    public enum State { WALK, WALK2, DEATH, DEAD }

    private State currentState = State.WALK;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> walk2Animation;
    private Animation<TextureRegion> deathAnimation;
    private float animationStateTime = 0f;

    public void update(float delta) {
        animationStateTime += delta;
    }

    public void render(SpriteBatch batch, Vector2 position, Vector2 size) {
        TextureRegion frame = null;

        switch (currentState) {
            case WALK:
                if (walkAnimation != null) frame = walkAnimation.getKeyFrame(animationStateTime, true);
                break;
            case WALK2:
                if (walk2Animation != null) frame = walk2Animation.getKeyFrame(animationStateTime, true);
                break;
            case DEATH:
                if (deathAnimation != null) frame = deathAnimation.getKeyFrame(animationStateTime, false);
                break;
            case DEAD:
                return; // Ne rien afficher
        }

        if (frame != null) {
            batch.draw(frame, position.x, position.y, size.x, size.y);
        }
    }

    /**
     * Vérifie si l'animation de mort est terminée
     */
    public boolean isDeathFinished() {
        return deathAnimation != null && deathAnimation.isAnimationFinished(animationStateTime);
    }

    /**
     * Transition vers un nouvel état
     */
    public void setState(State newState) {
        if (newState != currentState) {
            currentState = newState;
            animationStateTime = 0f;
        }
    }

    public State getState() {
        return currentState;
    }

    // Setters pour les animations
    public void setWalkFrames(TextureRegion[] frames, float frameDuration) {
        if (frames != null && frames.length > 0) {
            this.walkAnimation = new Animation<>(frameDuration, frames);
        }
    }

    public void setWalk2Frames(TextureRegion[] frames, float frameDuration) {
        if (frames != null && frames.length > 0) {
            this.walk2Animation = new Animation<>(frameDuration, frames);
        }
    }

    public void setDeathFrames(TextureRegion[] frames, float frameDuration) {
        if (frames != null && frames.length > 0) {
            this.deathAnimation = new Animation<>(frameDuration, frames);
        }
    }
}
