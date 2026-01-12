package io.github.Wasnowl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.managers.MusicManager;

/**
 * Menu principal: lancement du jeu, options et sortie.
 */
public class MainMenuScreen extends ScreenAdapter {
    private final GameMain game;
    private Stage stage;
    private Skin skin;
    private Window optionsWindow;

    /**
     * Cree le menu principal.
     * @param game instance du jeu
     */
    public MainMenuScreen(GameMain game) {
        this.game = game;
    }

    /**
     * Initialise l'UI du menu.
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton playButton = new TextButton("Jouer", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton quitButton = new TextButton("Quitter", skin);

        table.add(playButton).pad(10).row();
        table.add(optionsButton).pad(10).row();
        table.add(quitButton).pad(10);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleOptionsWindow();
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    /**
     * Rend le menu principal.
     * @param delta temps ecoule (secondes)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Libere les ressources du menu.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void toggleOptionsWindow() {
        if (optionsWindow != null) {
            optionsWindow.remove();
            optionsWindow = null;
            return;
        }

        optionsWindow = new Window("Options", skin);
        optionsWindow.defaults().pad(6);

        Label volumeLabel = new Label("Volume", skin);
        Slider volumeSlider = new Slider(
            MusicManager.MIN_VOLUME_DB,
            MusicManager.MAX_VOLUME_DB,
            1f,
            false,
            skin
        );
        if (game.getMusicManager() != null) {
            volumeSlider.setValue(game.getMusicManager().getVolumeDb());
        }

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.getMusicManager() != null) {
                    game.getMusicManager().setVolumeDb(volumeSlider.getValue());
                }
            }
        });

        TextButton closeButton = new TextButton("Fermer", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleOptionsWindow();
            }
        });

        optionsWindow.add(volumeLabel);
        optionsWindow.add(volumeSlider).width(220);
        optionsWindow.row();
        optionsWindow.add(closeButton).colspan(2).padTop(6);

        optionsWindow.pack();
        optionsWindow.setPosition(
            stage.getViewport().getWorldWidth() / 2f - optionsWindow.getWidth() / 2f,
            stage.getViewport().getWorldHeight() / 2f - optionsWindow.getHeight() / 2f
        );
        stage.addActor(optionsWindow);
    }
}
