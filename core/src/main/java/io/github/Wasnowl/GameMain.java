package io.github.Wasnowl;

import com.badlogic.gdx.Game;
import io.github.Wasnowl.managers.MusicManager;
import io.github.Wasnowl.screens.MainMenuScreen;

/**
 * Point d'entree LibGDX du jeu.
 * Initialise les managers globaux et charge le menu principal.
 */
public class GameMain extends Game {
    private MusicManager musicManager;

    /**
     * Cree les ressources globales et affiche le menu principal.
     */
    @Override
    public void create() {
        musicManager = new MusicManager();
        musicManager.start();
        setScreen(new MainMenuScreen(this));
    }

    /**
     * Retourne le gestionnaire de musique global.
     * @return music manager
     */
    public MusicManager getMusicManager() {
        return musicManager;
    }

    /**
     * Libere les ressources globales.
     */
    @Override
    public void dispose() {
        if (musicManager != null) {
            musicManager.dispose();
        }
        super.dispose();
    }
}
