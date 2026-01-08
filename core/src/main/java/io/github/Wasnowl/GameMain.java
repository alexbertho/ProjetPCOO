package io.github.Wasnowl;

import com.badlogic.gdx.Game;
import io.github.Wasnowl.screens.MainMenuScreen;

public class GameMain extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
