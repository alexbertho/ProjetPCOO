package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.screens.GameScreen;
import io.github.Wasnowl.entities.Portal;

public class PlayerTower extends Tower {
    private Array<Portal> portals;

    private GameMain game;

    public PlayerTower(float x, float y, float range, float fireRate,
                       Array<Enemy> enemies,
                       Array<Projectile> projectiles,
                       Array<Portal> portals,
                       GameMain game) {
        super(x, y, range, fireRate, enemies, projectiles);
        this.portals = portals;
        this.game = game;
    }

    public void move(Vector2 newPos) {
        position.set(newPos);

        for (Portal portal : portals) {
            if (portal.getBounds() != null && portal.getBounds().contains(position)) {
                triggerMapChange(portal);
            }
        }
    }

    private void triggerMapChange(Portal portal) {
        System.out.println("Changement de portail déclenché ! type=" + portal.getType());
        if (game == null || portal == null) return;

        if (portal.getType() == Portal.Type.MAP) {
            game.setScreen(new GameScreen(game, portal.getTarget()));
        } else if (portal.getType() == Portal.Type.COMBAT) {
            // passer au CombatScreen (vue de côté)
            game.setScreen(new io.github.Wasnowl.screens.CombatScreen(game, portal.getTarget()));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // batch.draw(playerTexture, position.x, position.y, size.x, size.y);
    }
}
