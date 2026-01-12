package io.github.Wasnowl.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * GameInputController : encapsule la logique de gestion des entrées (touch, keys, scroll)
 * Fournit des callbacks simples que la View (GameScreen) peut implémenter.
 */
public class GameInputController {
    public interface MoveHandler { void onMove(com.badlogic.gdx.math.Vector2 direction); }
    public interface TouchDownHandler { boolean touchDown(int screenX, int screenY, int pointer, int button); }
    public interface KeyDownHandler { boolean keyDown(int keycode); }
    public interface ScrollHandler { boolean scrolled(float amountX, float amountY); }

    private final Stage uiStage;
    private final java.util.function.BooleanSupplier isPaused;
    private final MoveHandler moveHandler;
    private final TouchDownHandler touchHandler;
    private final KeyDownHandler keyHandler;
    private final ScrollHandler scrollHandler;
    private final com.badlogic.gdx.math.Vector2 reusableDir = new com.badlogic.gdx.math.Vector2();

    private InputMultiplexer multiplexer;
    private final InputAdapter adapter = new InputAdapter() {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (isPaused != null && isPaused.getAsBoolean()) return false;
            if (touchHandler != null) return touchHandler.touchDown(screenX, screenY, pointer, button);
            return false;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (isPaused != null && isPaused.getAsBoolean()) {
                // allow ESC to unpause
                if (keyHandler != null) return keyHandler.keyDown(keycode);
                return false;
            }
            if (keyHandler != null) return keyHandler.keyDown(keycode);
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (isPaused != null && isPaused.getAsBoolean()) return false;
            if (scrollHandler != null) return scrollHandler.scrolled(amountX, amountY);
            return false;
        }
    };

    public GameInputController(Stage uiStage,
                               java.util.function.BooleanSupplier isPaused,
                               MoveHandler moveHandler,
                               TouchDownHandler touchHandler,
                               KeyDownHandler keyHandler,
                               ScrollHandler scrollHandler) {
        this.uiStage = uiStage;
        this.isPaused = isPaused;
        this.moveHandler = moveHandler;
        this.touchHandler = touchHandler;
        this.keyHandler = keyHandler;
        this.scrollHandler = scrollHandler;
    }

    /** Attache le controller (installe l'InputProcessor) */
    public void attach() {
        multiplexer = new InputMultiplexer();
        if (uiStage != null) multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(adapter);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /** Détache le controller (restaure éventuellement un autre InputProcessor) */
    public void detach() {
        // Restaurer le processeur à null (ou garder uiStage) - on remet null pour simplicité
        if (Gdx.input.getInputProcessor() == multiplexer) {
            Gdx.input.setInputProcessor(null);
        }
        multiplexer = null;
    }

    /**
     * Doit être appelé chaque frame par le contrôleur pour traiter les touches continues (WASD / flèches).
     */
    public void update(float delta) {
        // use delta to avoid 'parameter not used' warnings (no-op purpose)
        float _d = delta; if (_d < 0f) _d = -_d;
        if (isPaused != null && isPaused.getAsBoolean()) {
            // envoyer zéro
            if (moveHandler != null) moveHandler.onMove(reusableDir.set(0f, 0f));
            return;
        }
        reusableDir.set(0f, 0f);
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q)) {
            reusableDir.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            reusableDir.x += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Z)) {
            reusableDir.y += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN) || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            reusableDir.y -= 1f;
        }
        if (moveHandler != null) {
            moveHandler.onMove(reusableDir);
        }
    }
}
