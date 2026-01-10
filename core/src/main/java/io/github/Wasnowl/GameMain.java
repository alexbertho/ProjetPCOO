package io.github.Wasnowl;

import com.badlogic.gdx.Game;
import io.github.Wasnowl.managers.MusicManager;
import io.github.Wasnowl.screens.MainMenuScreen;

public class GameMain extends Game {
    private MusicManager musicManager;

    @Override
    public void create() {
        musicManager = new MusicManager();
        musicManager.start();
        setScreen(new MainMenuScreen(this));
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    @Override
    public void dispose() {
        if (musicManager != null) {
            musicManager.dispose();
        }
        super.dispose();
    }
}
