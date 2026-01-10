package io.github.Wasnowl.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.screens.GameScreen;
import io.github.Wasnowl.entities.Portal;

public class PlayerTower extends Tower {
    private static final float MOVE_SPEED = 180f;
    private Array<Portal> portals;
    private final Vector2 velocity = new Vector2();
    private PlayerAnimator animator;
    private TextureRegion currentFrame;

    private GameMain game;

    public PlayerTower(float x, float y, float range, float fireRate,
                       Array<Enemy> enemies,
                       Array<Projectile> projectiles,
                       Array<Portal> portals,
                       GameMain game) {
        super(x, y, range, fireRate, enemies, projectiles);
        this.portals = portals;
        this.game = game;
        this.size.set(32, 64);
        this.animator = new PlayerAnimator();
        this.currentFrame = animator.getFrame(0f, 0f, 0f);
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
    public void update(float delta) {
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Z)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            moveY -= 1f;
        }

        velocity.set(moveX, moveY);
        if (velocity.len2() > 0f) {
            velocity.nor().scl(MOVE_SPEED);
        }

        if (velocity.len2() > 0f) {
            Vector2 next = position.cpy().add(velocity.x * delta, velocity.y * delta);
            move(next);
        }

        currentFrame = animator.getFrame(delta, velocity.x, velocity.y);
        super.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        } else {
            super.render(batch);
        }
    }
}
