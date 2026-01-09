package io.github.Wasnowl.strategies;

import com.badlogic.gdx.math.Vector2;

/**
 * PathStrategy : pattern Strategy pour les différentes trajectoires possibles
 * Permet de définir et sélectionner facilement différents chemins
 */
public interface PathStrategy {
    /**
     * @return le chemin à suivre
     */
    Vector2[] getPath();
    
    /**
     * @return le nom descriptif du chemin
     */
    String getName();
}
