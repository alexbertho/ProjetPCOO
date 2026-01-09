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
     * @return tableau de TextureRegion ou null
     */
    public TextureRegion[] loadAnimationFromSpritesheet(int id, String animName, int cols, int rows) {
        Map<String, TextureRegion[]> m = cache.get(id);
        if (m != null && m.containsKey(animName)) return m.get(animName);

        // Construire listes de candidates de noms de fichiers, pour gérer vos conventions (S_Walk.png, D_Death.png, etc.)
        String capitalized = animName.length() > 0 ? animName.substring(0,1).toUpperCase() + animName.substring(1) : animName;
        String[] prefixes;
        if (animName.equalsIgnoreCase("walk")) prefixes = new String[]{"S_", "s_", ""};
        else if (animName.equalsIgnoreCase("death") || animName.equalsIgnoreCase("die")) prefixes = new String[]{"D_", "d_", ""};
        else prefixes = new String[]{"", "S_", "s_", "D_", "d_"};

        String[] nameVariants = new String[] { capitalized, animName };

        Exception lastEx = null;
        Texture sheet = null;
        String foundPath = null;

        for (String prefix : prefixes) {
            for (String base : nameVariants) {
                String path = ENEMY_ASSETS_PATH + id + "/" + prefix + base + ".png";
                try {
                    sheet = new Texture(path);
                    foundPath = path;
                    break;
                } catch (Exception e) {
                    lastEx = e;
                    // essayer le suivant
                }
            }
            if (sheet != null) break;
        }

        if (sheet == null) {
            // Aucune candidate trouvée
            System.err.println("Erreur: impossible de trouver de spritesheet pour enemy " + id + " (anim: " + animName + ")");
            if (lastEx != null) {
                System.err.println("Dernière erreur: " + lastEx.getMessage());
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
            System.err.println("Erreur: impossible de découper la spritesheet " + foundPath + " -> " + e.getMessage());
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
