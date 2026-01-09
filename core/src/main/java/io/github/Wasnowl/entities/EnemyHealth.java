package io.github.Wasnowl.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.Wasnowl.managers.EnemyAssetManager;

/**
 * EnemyHealth : gère la vie et les transitions d'état basées sur les HP
 * Responsabilité unique : système de santé et logique de transition
 * Lazy loading: walk2 est chargé seulement à la première transition <50%HP
 */
public class EnemyHealth {
    private float maxHealth;
    private float health;
    private EnemyAnimator animator;
    private int enemyId = -1; // Pour lazy loading de walk2
    private boolean walk2Loaded = false;

    public EnemyHealth(float maxHealth, EnemyAnimator animator) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.animator = animator;
    }

    public void setEnemyId(int id) {
        this.enemyId = id;
    }

    /**
     * Applique des dégâts et mets à jour l'état d'animation si nécessaire
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
     * Appelé chaque frame pour vérifier si la mort est complète
     */
    public void update() {
        if (animator.getState() == EnemyAnimator.State.DEATH && animator.isDeathFinished()) {
            animator.setState(EnemyAnimator.State.DEAD);
        }
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public boolean isDead() {
        return animator.getState() == EnemyAnimator.State.DEAD;
    }
}
