package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.Wasnowl.managers.EnemyAssetManager;

/**
 * EnemyHealth : gère la vie et les transitions d'état basées sur les HP
 * Responsabilité unique : système de santé et logique de transition
 * Lazy loading: walk2 est charge seulement a la premiere transition &lt;50%HP
 */
public class EnemyHealth {
    private float maxHealth;
    private float health;
    private EnemyAnimator animator;
    private int enemyId = -1; // Pour lazy loading de walk2
    private boolean walk2Loaded = false;

    /**
     * Cree un composant de vie avec un maximum et un animateur associe.
     * @param maxHealth vie maximale
     * @param animator animateur lie
     */
    public EnemyHealth(float maxHealth, EnemyAnimator animator) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.animator = animator;
    }

    /**
     * Definit l'identifiant d'ennemi pour le chargement lazy.
     * @param id identifiant d'ennemi
     */
    public void setEnemyId(int id) {
        this.enemyId = id;
    }

    /**
     * Applique des dégâts et mets à jour l'état d'animation si nécessaire
     * @param amount degats a appliquer
     */
    public void takeDamage(float amount) {
        health = Math.max(0f, health - amount);
        updateAnimationState();
    }

    /**
     * Vérifie le ratio de santé et change l'état de l'animation si besoin
     */
    private void updateAnimationState() {
        float healthRatio = health / maxHealth;

        if (animator.getState() == EnemyAnimator.State.DEAD) {
            return; // Déjà mort, pas de changement
        }

        if (health <= 0) {
            // Déclencher l'animation de mort
            animator.setState(EnemyAnimator.State.DEATH);
        } else if (animator.getState() == EnemyAnimator.State.WALK && healthRatio < 0.5f) {
            // Passer à walk2 (ennemi endommagé) - seulement si walk2 existe
            if (loadWalk2IfNeeded()) {
                animator.setState(EnemyAnimator.State.WALK2);
            }
            // Sinon rester en WALK (pas de spritesheet walk2)
        }
    }

    /**
     * Charge walk2 de manière lazy (seulement quand nécessaire)
     * @return true si walk2 a été chargé avec succès
     */
    private boolean loadWalk2IfNeeded() {
        if (!walk2Loaded && enemyId != -1) {
            // walk2 est optionnel - ne pas afficher d'erreur s'il n'existe pas
            TextureRegion[] walk2 = EnemyAssetManager.getInstance().loadAnimationFromSpritesheet(enemyId, "walk2", 6, 1, true);
            walk2Loaded = true; // marquer comme chargé (ou tenté)
            return walk2 != null; // retourner si le chargement a réussi
        }
        return false; // walk2 n'existe pas ou déjà chargé
    }

    /**
     * Appele chaque frame pour verifier si la mort est complete.
     */
    public void update() {
        if (animator.getState() == EnemyAnimator.State.DEATH && animator.isDeathFinished()) {
            animator.setState(EnemyAnimator.State.DEAD);
        }
    }

    /**
     * Retourne la vie courante.
     * @return vie courante
     */
    public float getHealth() {
        return health;
    }

    /**
     * Retourne la vie maximale.
     * @return vie max
     */
    public float getMaxHealth() {
        return maxHealth;
    }

    /**
     * Indique si l'ennemi est marque comme mort.
     * @return true si mort
     */
    public boolean isDead() {
        return animator.getState() == EnemyAnimator.State.DEAD;
    }
}
