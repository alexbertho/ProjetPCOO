package io.github.Wasnowl.strategies;

import com.badlogic.gdx.math.Vector2;

/**
 * MainPathStrategy : trajectoire principale des ennemis
 *
 */
public class MainPathStrategy implements PathStrategy {

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

    @Override
    public String getName() {
        return "Main Path";
    }
}
