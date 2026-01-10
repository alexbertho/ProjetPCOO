package io.github.Wasnowl.controllers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input;

/**
 * InputController handles global game input such as camera movement.
 * It exposes an InputProcessor to be added into the InputMultiplexer and an
 * update(delta) method that should be called every frame to move the camera.
 */
public class InputController extends InputAdapter {
    private final OrthographicCamera camera;
    private boolean left, right, up, down;
    private float speed = 300f;

    public InputController(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A || keycode == Input.Keys.Q) left = true;
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) right = true;
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W || keycode == Input.Keys.Z) up = true;
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) down = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A || keycode == Input.Keys.Q) left = false;
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) right = false;
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W || keycode == Input.Keys.Z) up = false;
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) down = false;
        return false;
    }

    public InputProcessor getInputProcessor() {
        return this;
    }

    public void update(float delta) {
        float move = speed * delta;
        boolean moved = false;
        if (left) { camera.position.x -= move; moved = true; }
        if (right) { camera.position.x += move; moved = true; }
        if (up) { camera.position.y += move; moved = true; }
        if (down) { camera.position.y -= move; moved = true; }
        if (moved) camera.update();
    }
}
