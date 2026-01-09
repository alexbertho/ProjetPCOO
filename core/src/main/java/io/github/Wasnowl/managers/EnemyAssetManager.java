package io.github.Wasnowl.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * EnemyAssetManager : charge les spritesheets et découpe en frames pour les ennemis
 * Structure attendue : assets/ennemies/<id>/<animationFile.png>
 * Ex: assets/ennemies/1/S_Walk.png ou assets/ennemies/1/S_Walk.png
 */
public class EnemyAssetManager {
    private static final String ENEMY_ASSETS_PATH = "ennemies/";
    private static EnemyAssetManager instance;

    // cache[id][animName] -> frames
    private Map<Integer, Map<String, TextureRegion[]>> cache = new HashMap<>();

    private EnemyAssetManager() {}

    public static EnemyAssetManager getInstance() {
        if (instance == null) instance = new EnemyAssetManager();
        return instance;
    }

    /**
     * Charge et découpe un spritesheet en frames
     * @param id id du monstre (dossier)
     * @param animName nom de l'animation (ex: "walk", "death")
     * @param cols colonnes dans le spritesheet
     * @param rows lignes dans le spritesheet
     * @param optional si true, ne pas afficher d'erreur si l'animation n'existe pas
     * @return tableau de TextureRegion ou null
     */
    public TextureRegion[] loadAnimationFromSpritesheet(int id, String animName, int cols, int rows) {
        return loadAnimationFromSpritesheet(id, animName, cols, rows, false);
    }

    public TextureRegion[] loadAnimationFromSpritesheet(int id, String animName, int cols, int rows, boolean optional) {
        Map<String, TextureRegion[]> m = cache.get(id);
        if (m != null && m.containsKey(animName)) return m.get(animName);

        // Générer variantes avec majuscules correctes (ex: D_Walk, D_Walk2, S_Walk, S_Walk2)
        String[] nameVariants = new String[]{};
        
        if (animName.equalsIgnoreCase("walk")) {
            nameVariants = new String[]{"S_Walk", "s_walk", "Walk", "walk"};
        } else if (animName.equalsIgnoreCase("walk2")) {
            nameVariants = new String[]{"S_Walk2", "s_walk2", "Walk2", "walk2"};
        } else if (animName.equalsIgnoreCase("death") || animName.equalsIgnoreCase("die")) {
            nameVariants = new String[]{"S_Death", "D_Death", "s_death", "d_death", "Death", "death"};
        }

        Exception lastEx = null;
        Texture sheet = null;
        String foundPath = null;

        for (String variant : nameVariants) {
            String path = ENEMY_ASSETS_PATH + id + "/" + variant + ".png";
            try {
                sheet = new Texture(path);
                foundPath = path;
                break;
            } catch (Exception e) {
                lastEx = e;
            }
        }

        if (sheet == null) {
            if (!optional) {
                System.err.println("Erreur: impossible de trouver spritesheet pour enemy " + id + " (anim: " + animName + ")");
                if (lastEx != null) System.err.println("Dernier essai: " + lastEx.getMessage());
            }
            return null;
        }

        try {
            int frameWidth = sheet.getWidth() / cols;
            int frameHeight = sheet.getHeight() / rows;

            TextureRegion[][] temp = TextureRegion.split(sheet, frameWidth, frameHeight);
            TextureRegion[] frames = new TextureRegion[cols * rows];
            int index = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    frames[index++] = temp[r][c];
                }
            }

            if (m == null) {
                m = new HashMap<>();
                cache.put(id, m);
            }
            m.put(animName, frames);
            return frames;
        } catch (Exception e) {
            System.err.println("Erreur: impossible de découper " + foundPath + " -> " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Dispose: libère les textures du cache
     * Note: ici on ne conserve pas les Texture instances séparément, donc pas simple à disposer.
     * Pour un vrai AssetManager, on utiliserait com.badlogic.gdx.assets.AssetManager.
     */
    public void dispose() {
        // Pas implémenté (textures non trackées séparément)
    }
}
