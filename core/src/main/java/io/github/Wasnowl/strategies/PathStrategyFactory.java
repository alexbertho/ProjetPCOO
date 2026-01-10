package io.github.Wasnowl.strategies;

/**
 * PathStrategyFactory : Factory Pattern pour créer les stratégies de chemin (volant / terrestre etc..)
 * Centralise la création et la sélection des chemins possibles
 */
public class PathStrategyFactory {

    public enum PathType {
        MAIN,
        // Ajoute d'autres types ici au besoin (ALTERNATE, SHORTCUT, etc.)
    }

    /**
     * Crée une stratégie de chemin selon le type spécifié
     * @param type le type de chemin à créer
     * @return une implémentation de PathStrategy
     */
    public static PathStrategy createPathStrategy(PathType type) {
        switch (type) {
            case MAIN:
            default:
                return new MainPathStrategy();
        }
    }

    /**
     * Sélectionne une stratégie pour une vague donnée
     * Permet de varier les chemins selon la progression
     * @param waveNumber le numéro de la vague
     * @return une implémentation de PathStrategy
     */
    public static PathStrategy getStrategyForWave(int waveNumber) {
        // Pour l'instant, on utilise le chemin principal pour toutes les vagues
        // Modifier cette logique pour varier les chemins
        return createPathStrategy(PathType.MAIN);
    }
}
