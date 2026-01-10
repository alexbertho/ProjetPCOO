package io.github.Wasnowl.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class MusicManager {
    private static final String MUSIC_DIR = "musique";
    private static final String MUSIC_EXT = "mp3";
    private final Array<FileHandle> tracks = new Array<>();
    private Music current;
    private int currentIndex = -1;
    private float volume = 0.5f;

    public void start() {
        FileHandle dir = Gdx.files.internal(MUSIC_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            Gdx.app.log("MUSIC", "No musique/ directory found.");
            return;
        }

        for (FileHandle file : dir.list()) {
            if (MUSIC_EXT.equalsIgnoreCase(file.extension())) {
                tracks.add(file);
            }
        }

        if (tracks.size == 0) {
            Gdx.app.log("MUSIC", "No mp3 tracks found in musique/.");
            return;
        }

        playNext();
    }

    private void playNext() {
        stopCurrent();

        if (tracks.size == 0) {
            return;
        }

        int attempts = tracks.size;
        while (attempts-- > 0) {
            currentIndex = (currentIndex + 1) % tracks.size;
            FileHandle track = tracks.get(currentIndex);
            try {
                current = Gdx.audio.newMusic(track);
                current.setOnCompletionListener(music -> playNext());
                current.setVolume(volume);
                current.play();
                return;
            } catch (Exception e) {
                Gdx.app.log("MUSIC", "Failed to play " + track.path() + ": " + e.getMessage());
                current = null;
            }
        }

        Gdx.app.log("MUSIC", "No playable mp3 tracks found in musique/.");
    }

    private void stopCurrent() {
        if (current == null) {
            return;
        }
        current.stop();
        current.dispose();
        current = null;
    }

    public void dispose() {
        stopCurrent();
    }

    public void setVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        this.volume = clamped;
        if (current != null) {
            current.setVolume(clamped);
        }
    }

    public float getVolume() {
        return volume;
    }
}
