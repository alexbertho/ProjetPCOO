package io.github.Wasnowl.strategies;

import com.badlogic.gdx.math.Vector2;

/**
 * MainPathStrategy : trajectoire principale des ennemis
 * (0,0) → (0,5) → (500,5) → (500,450) → (150,450)
 */
public class MainPathStrategy implements PathStrategy {

    @Override
    public Vector2[] getPath() {
        return new Vector2[] {
            new Vector2(0, 0),
            new Vector2(0, 5),
            new Vector2(500, 5),
            new Vector2(500, 450),
            new Vector2(150, 450)
        };
    }

    @Override
    public String getName() {
        return "Main Path";
    }
}
