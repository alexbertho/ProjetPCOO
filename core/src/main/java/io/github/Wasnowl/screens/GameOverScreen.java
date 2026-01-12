package io.github.Wasnowl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.Wasnowl.GameMain;

/**
 * Écran affiché quand le joueur perd toutes ses vies.
 */
public class GameOverScreen extends ScreenAdapter {
    private final GameMain game;
    private Stage uiStage;
    private Skin uiSkin;

    public GameOverScreen(GameMain game) {
        this.game = game;
    }

    @Override
    public void show() {
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        uiStage = new Stage(new FitViewport(800, 480));

        BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"));
        Label title = new Label("Game Over", new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE));

        Window window = new Window("", uiSkin);
        window.defaults().pad(8);
        window.add(title).row();

        TextButton retry = new TextButton("Retry", uiSkin);
        TextButton mainMenu = new TextButton("Main Menu", uiSkin);

        retry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        mainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        window.add(retry).row();
        window.add(mainMenu).row();
        window.pack();
        window.setPosition(uiStage.getViewport().getWorldWidth() / 2f - window.getWidth() / 2f,
            uiStage.getViewport().getWorldHeight() / 2f - window.getHeight() / 2f);

        uiStage.addActor(window);
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (uiStage != null) uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (uiStage != null) uiStage.dispose();
        if (uiSkin != null) uiSkin.dispose();
    }
}
