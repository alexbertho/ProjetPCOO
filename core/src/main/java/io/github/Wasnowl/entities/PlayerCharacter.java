package io.github.Wasnowl.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.GameObject;

public class PlayerCharacter extends GameObject {
    private Vector2 velocity = new Vector2();
    private float speed = 180f;
    private float jumpSpeed = 300f;
    private float gravity = -800f;
    private boolean onGround = true;

    public PlayerCharacter(float x, float y) {
        super(x, y);
        this.size.set(32, 48);
    }

    @Override
    public void update(float delta) {
        float move = 0f;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            move = -1f;
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            move = 1f;
        }

        velocity.x = move * speed;

        if ((Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.W)) && onGround) {
            velocity.y = jumpSpeed;
            onGround = false;
        }

        velocity.y += gravity * delta;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // simple ground collision
        float groundY = 100f;
        if (position.y <= groundY) {
            position.y = groundY;
            velocity.y = 0f;
            onGround = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // placeholder: no sprite yet. You can draw a texture here.
    }
}
