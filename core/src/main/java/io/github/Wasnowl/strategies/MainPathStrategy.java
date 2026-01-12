package io.github.Wasnowl.strategies;

import com.badlogic.gdx.math.Vector2;

/**
 * MainPathStrategy : trajectoire principale des ennemis
 *
 */
public class MainPathStrategy implements PathStrategy {

    /**
     * Retourne la liste de points du chemin principal.
     * @return points du chemin
     */
    @Override
    public Vector2[] getPath() {
        return new Vector2[] {
            new Vector2(0, 0),
            new Vector2(0, 5),
            new Vector2(530, 5),
            new Vector2(530, 410),
            new Vector2(180, 410)
        };
    }

    /**
     * Retourne le nom du chemin.
     * @return nom du chemin
     */
    @Override
    public String getName() {
        return "Main Path";
    }
}
