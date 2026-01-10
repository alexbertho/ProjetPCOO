package io.github.Wasnowl.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.GameObject;

public class PlayerCharacter extends GameObject {
    private Vector2 velocity = new Vector2();
    private float speed = 180f;
    private PlayerAnimator animator;
    private TextureRegion currentFrame;

    public PlayerCharacter(float x, float y) {
        super(x, y);
        this.size.set(32, 64);
        animator = new PlayerAnimator();
        currentFrame = animator.getFrame(0f, 0f, 0f);
    }

    @Override
    public void update(float delta) {
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Z) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            moveY += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            moveY -= 1f;
        }

        velocity.set(moveX, moveY);
        if (velocity.len2() > 0f) {
            velocity.nor().scl(speed);
        }

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        currentFrame = animator.getFrame(delta, velocity.x, velocity.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }
    }
}
