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
    /**
     * Callback pour le mouvement du joueur (direction normalisee).
     */
    public interface MoveHandler {
        /**
         * Notifie un changement de direction du joueur.
         * @param direction direction souhaitee (non normalisee possible)
         */
        void onMove(com.badlogic.gdx.math.Vector2 direction);
    }
    /**
     * Callback pour le mouvement de la camera (direction + delta).
     */
    public interface CameraMoveHandler {
        /**
         * Notifie un changement de direction de la camera.
         * @param direction direction souhaitee
         * @param delta temps ecoule (secondes)
         */
        void onMove(com.badlogic.gdx.math.Vector2 direction, float delta);
    }
    /**
     * Callback pour les clics/touches ecran.
     */
    public interface TouchDownHandler {
        /**
         * Notifie un clic/tap.
         * @param screenX position X ecran
         * @param screenY position Y ecran
         * @param pointer index du pointeur
         * @param button bouton presse
         * @return true si l'event est consomme
         */
        boolean touchDown(int screenX, int screenY, int pointer, int button);
    }
    /**
     * Callback pour les touches ponctuelles (ex: ESC).
     */
    public interface KeyDownHandler {
        /**
         * Notifie l'appui sur une touche.
         * @param keycode code de la touche
         * @return true si l'event est consomme
         */
        boolean keyDown(int keycode);
    }
    /**
     * Callback pour la molette (zoom).
     */
    public interface ScrollHandler {
        /**
         * Notifie un scroll de souris.
         * @param amountX scroll horizontal
         * @param amountY scroll vertical
         * @return true si l'event est consomme
         */
        boolean scrolled(float amountX, float amountY);
    }

    private final Stage uiStage;
    private final java.util.function.BooleanSupplier isPaused;
    private final MoveHandler moveHandler;
    private final CameraMoveHandler cameraMoveHandler;
    private final TouchDownHandler touchHandler;
    private final KeyDownHandler keyHandler;
    private final ScrollHandler scrollHandler;
    private final com.badlogic.gdx.math.Vector2 reusableDir = new com.badlogic.gdx.math.Vector2();
    private final com.badlogic.gdx.math.Vector2 reusableCameraDir = new com.badlogic.gdx.math.Vector2();

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

    /**
     * Construit un controleur d'entree avec callbacks (vue/controller).
     * @param uiStage stage UI a brancher
     * @param isPaused fournisseur d'etat pause
     * @param moveHandler callback de mouvement joueur
     * @param touchHandler callback de clic
     * @param keyHandler callback clavier
     * @param scrollHandler callback molette
     * @param cameraMoveHandler callback de mouvement camera
     */
    public GameInputController(Stage uiStage,
                               java.util.function.BooleanSupplier isPaused,
                               MoveHandler moveHandler,
                               TouchDownHandler touchHandler,
                               KeyDownHandler keyHandler,
                               ScrollHandler scrollHandler,
                               CameraMoveHandler cameraMoveHandler) {
        this.uiStage = uiStage;
        this.isPaused = isPaused;
        this.moveHandler = moveHandler;
        this.touchHandler = touchHandler;
        this.keyHandler = keyHandler;
        this.scrollHandler = scrollHandler;
        this.cameraMoveHandler = cameraMoveHandler;
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
     * Doit etre appele chaque frame pour traiter les touches continues.
     * @param delta temps ecoule (secondes)
     */
    public void update(float delta) {
        if (isPaused != null && isPaused.getAsBoolean()) {
            // envoyer zéro
            if (moveHandler != null) moveHandler.onMove(reusableDir.set(0f, 0f));
            if (cameraMoveHandler != null) cameraMoveHandler.onMove(reusableCameraDir.set(0f, 0f), delta);
            return;
        }

        reusableDir.set(0f, 0f);
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Q)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            reusableDir.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            reusableDir.x += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.Z)
                || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            reusableDir.y += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            reusableDir.y -= 1f;
        }
        if (moveHandler != null) {
            moveHandler.onMove(reusableDir);
        }

        reusableCameraDir.set(0f, 0f);
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            reusableCameraDir.x -= 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            reusableCameraDir.x += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            reusableCameraDir.y += 1f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            reusableCameraDir.y -= 1f;
        }
        if (cameraMoveHandler != null) {
            cameraMoveHandler.onMove(reusableCameraDir, delta);
        }
    }
}
