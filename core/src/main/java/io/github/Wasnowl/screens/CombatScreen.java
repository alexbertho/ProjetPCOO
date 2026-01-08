package io.github.Wasnowl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.entities.PlayerCharacter;

public class CombatScreen extends ScreenAdapter {
    private final GameMain game;
    private String combatId;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    private PlayerCharacter player;

    public CombatScreen(GameMain game, String combatId) {
        this.game = game;
        this.combatId = combatId;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        camera.position.set(400, 300, 0);
        camera.update();

        // create player near left side
        player = new PlayerCharacter(100, 120);

        // capture input if needed
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update
        player.update(delta);

        // Camera follow horizontally
        camera.position.x = Math.max(400, player.getPosition().x);
        camera.update();

        // Render
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();

        // simple back to map debug: press ESC to return to the top-down map
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
