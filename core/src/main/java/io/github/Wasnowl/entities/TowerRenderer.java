package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

/**
 * TowerRenderer : gère TOUT ce qui concerne le rendu visuel d'une tour
 * Sépare la logique de jeu (Tower) du rendu (TowerRenderer)
 * Suit le pattern Composition + Strategy
 */
public class TowerRenderer {
    private TextureRegion staticTexture;
    private Animation<TextureRegion> animation;
    private float animationStateTime = 0;
    private boolean isAnimating = false;
    
    public TowerRenderer() {
    }
    
    /**
     * Définit une texture statique (pas d'animation)
     */
    public void setTexture(TextureRegion texture) {
        this.staticTexture = texture;
        this.animation = null;
        this.animationStateTime = 0;
    }
    
    /**
     * Définit une animation à partir de frames
     */
    public void setAnimation(TextureRegion[] frames, float frameDuration) {
        if (frames != null && frames.length > 0) {
            this.animation = new Animation<>(frameDuration, frames);
            this.staticTexture = null;
            this.animationStateTime = 0;
            this.isAnimating = true;
        }
    }
    
    /**
     * Met à jour l'état de l'animation
     */
    public void update(float delta) {
        if (isAnimating && animation != null) {
            animationStateTime += delta;
        }
    }
    
    /**
     * Affiche la tour (texture ou animation)
     */
    public void render(SpriteBatch batch, Vector2 position, Vector2 size) {
        TextureRegion frame;
        
        if (animation != null && isAnimating) {
            // Afficher la frame actuelle (true = loop)
            frame = animation.getKeyFrame(animationStateTime, true);
        } else if (staticTexture != null) {
            frame = staticTexture;
        } else {
            return;
        }
        
        batch.draw(frame, position.x, position.y, size.x, size.y);
    }
    
    public void setAnimating(boolean animating) {
        this.isAnimating = animating;
    }
    
    public boolean isAnimating() {
        return isAnimating;
    }
    
    public TextureRegion getStaticTexture() {
        return staticTexture;
    }
    
    public Animation<TextureRegion> getAnimation() {
        return animation;
    }
}
