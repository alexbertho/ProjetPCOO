package io.github.Wasnowl.entities;

/**
 * EnemyHealth : gère la vie et les transitions d'état basées sur les HP
 * Responsabilité unique : système de santé et logique de transition
 */
public class EnemyHealth {
    private float maxHealth;
    private float health;
    private EnemyAnimator animator;

    public EnemyHealth(float maxHealth, EnemyAnimator animator) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.animator = animator;
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
            // Passer à walk2 (ennemi endommagé)
            animator.setState(EnemyAnimator.State.WALK2);
        }
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
