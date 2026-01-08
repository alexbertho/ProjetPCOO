package io.github.Wasnowl.entities;

/**
 * Types de tour, contenant le coût et le type de projectile associé.
 */
public enum TowerType {
    SIMPLE(100, ProjectileType.SIMPLE),
    AOE(200, ProjectileType.AOE);

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
