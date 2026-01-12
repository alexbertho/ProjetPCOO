package io.github.Wasnowl.entities;

/**
 * Types de tour, contenant le coût et le type de projectile associé.
 */
public enum TowerType {
    /** Tour simple. */
    SIMPLE(10, ProjectileType.SIMPLE),
    /** Tour AOE. */
    AOE(20, ProjectileType.AOE_STRONG),
    /** Tour ricochet. */
    RICOCHET(30, ProjectileType.RICOCHET);

    private final int cost;
    private final ProjectileType projectileType;

    TowerType(int cost, ProjectileType projectileType) {
        this.cost = cost;
        this.projectileType = projectileType;
    }

    /**
     * Retourne le cout d'achat de la tour.
     * @return cout de la tour
     */
    public int getCost() {
        return cost;
    }

    /**
     * Retourne le type de projectile associe.
     * @return type de projectile
     */
    public ProjectileType getProjectileType() {
        return projectileType;
    }
}
