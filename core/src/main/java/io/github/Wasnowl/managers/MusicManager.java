package io.github.Wasnowl.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Gere la playlist musicale: chargement, lecture en boucle et volume.
 */
public class MusicManager {
    private static final String MUSIC_DIR = "musique";
    private static final String MUSIC_EXT = "mp3";
    /** Volume minimum en dB. */
    public static final float MIN_VOLUME_DB = -60f;
    /** Volume maximum en dB. */
    public static final float MAX_VOLUME_DB = 0f;
    private static final float DEFAULT_VOLUME_DB = -18f;
    private final Array<FileHandle> tracks = new Array<>();
    private Music current;
    private int currentIndex = -1;
    private float volume = dbToLinear(DEFAULT_VOLUME_DB);

    /**
     * Lance la lecture en parcourant les pistes du dossier musique/.
     */
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

    /**
     * Libere les ressources audio en cours.
     */
    public void dispose() {
        stopCurrent();
    }

    /**
     * Definit le volume en valeur lineaire (0..1).
     * @param volume volume lineaire
     */
    public void setVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        this.volume = clamped;
        if (current != null) {
            current.setVolume(clamped);
        }
    }

    /**
     * Retourne le volume lineaire (0..1).
     * @return volume lineaire
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Definit le volume en dB (clamp min/max).
     * @param volumeDb volume en dB
     */
    public void setVolumeDb(float volumeDb) {
        float clampedDb = Math.max(MIN_VOLUME_DB, Math.min(MAX_VOLUME_DB, volumeDb));
        float linear = dbToLinear(clampedDb);
        this.volume = linear;
        if (current != null) {
            current.setVolume(linear);
        }
    }

    /**
     * Retourne le volume en dB.
     * @return volume en dB
     */
    public float getVolumeDb() {
        return linearToDb(volume);
    }

    private static float dbToLinear(float db) {
        return (float) Math.pow(10f, db / 20f);
    }

    private static float linearToDb(float linear) {
        if (linear <= 0f) {
            return MIN_VOLUME_DB;
        }
        float db = 20f * (float) Math.log10(linear);
        return Math.max(MIN_VOLUME_DB, Math.min(MAX_VOLUME_DB, db));
    }
}
