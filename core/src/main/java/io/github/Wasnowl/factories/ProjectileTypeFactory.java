package io.github.Wasnowl.factories;

import io.github.Wasnowl.entities.ProjectileType;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory pour créer et réutiliser les types de projectiles (Flyweight).
 * Assure qu'il n'y a qu'une seule instance de chaque type en mémoire.
 */
public class ProjectileTypeFactory {
    private static final Map<String, ProjectileType> typeCache = new HashMap<>();

    static {
        // Précharger les types prédéfinis
        typeCache.put("SIMPLE", ProjectileType.SIMPLE);
        typeCache.put("AOE", ProjectileType.AOE_STRONG); // map generic AOE to the stronger AOE variant
        typeCache.put("AOE_STRONG", ProjectileType.AOE_STRONG);
        typeCache.put("RICOCHET", ProjectileType.RICOCHET);
    }

    /**
     * Récupère ou crée un type de projectile.
     * Pour les types prédéfinis (SIMPLE, AOE), retourne le singleton.
     * @param typeName le nom du type ("SIMPLE" ou "AOE")
     * @return le ProjectileType (Flyweight)
     */
    public static ProjectileType getType(String typeName) {
        return typeCache.getOrDefault(typeName.toUpperCase(), ProjectileType.SIMPLE);
    }

    /**
     * Récupère un type enum directement.
     * @param type type a retourner
     * @return type de projectile
     */
    public static ProjectileType getType(ProjectileType type) {
        return type;
    }

    /**
     * Affiche les types disponibles (debug).
     */
    public static void printAvailableTypes() {
        System.out.println("Available Projectile Types:");
        typeCache.forEach((name, type) -> 
            System.out.println("  " + name + ": damage=" + type.getDamage() + 
                             ", speed=" + type.getSpeed() + 
                             ", isAOE=" + type.isAOE())
        );
    }
}
