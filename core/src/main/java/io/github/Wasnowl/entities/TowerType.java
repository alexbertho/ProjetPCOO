package io.github.Wasnowl.entities;

/**
 * Types de tour, contenant le coût et le type de projectile associé.
 */
public enum TowerType {
    SIMPLE(10, ProjectileType.SIMPLE),
    AOE(20, ProjectileType.AOE_STRONG),
    RICOCHET(30, ProjectileType.RICOCHET);

    private final int cost;
    private final ProjectileType projectileType;

    TowerType(int cost, ProjectileType projectileType) {
        this.cost = cost;
        this.projectileType = projectileType;
    }

    public int getCost() {
        return cost;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }
}
