package io.github.Wasnowl.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

/**
 * TowerAssetManager : charge les assets des tours (textures, animations)
 * Évite de surcharger la classe Tower avec les logiques de chargement
 */
public class TowerAssetManager {
    private static final String TOWER_ASSETS_PATH = "towers/Idle/";
    private static TowerAssetManager instance;
    private Map<Integer, TextureRegion> cachedTextures = new HashMap<>();
    
    private TowerAssetManager() {
    }
    
    /**
     * Retourne l'instance singleton du manager.
     * @return instance unique
     */
    public static TowerAssetManager getInstance() {
        if (instance == null) {
            instance = new TowerAssetManager();
        }
        return instance;
    }
    
    /**
     * Charge la texture d'une tour par son ID
     * Ex: loadTowerTexture(7) charge "assets/towersidle/7.png"
     * @param towerId id de la tour
     * @return texture chargee ou null
     */
    public TextureRegion loadTowerTexture(int towerId) {
        if (cachedTextures.containsKey(towerId)) {
            return cachedTextures.get(towerId);
        }
        
        try {
            String path = TOWER_ASSETS_PATH + towerId + ".png";
            Texture texture = new Texture(path);
            TextureRegion region = new TextureRegion(texture);
            cachedTextures.put(towerId, region);
            return region;
        } catch (Exception e) {
            System.err.println("Erreur : impossible de charger la texture de la tour " + towerId);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Charge une animation à partir d'un spritesheet
     * Ex: loadTowerAnimationFromSpritesheet(7, 6, 1) découpe l'image 7.png en 6 frames (6 colonnes, 1 ligne)
     * @param towerId ID du fichier (exemple: 7 -> towers/Idle/7.png)
     * @param cols Nombre de colonnes
     * @param rows Nombre de lignes
     * @return frames d'animation ou null
     */
    public TextureRegion[] loadTowerAnimationFromSpritesheet(int towerId, int cols, int rows) {
        try {
            String path = TOWER_ASSETS_PATH + towerId + ".png";
            Texture spritesheet = new Texture(path);
            
            // Découper le spritesheet en frames
            int frameWidth = spritesheet.getWidth() / cols;
            int frameHeight = spritesheet.getHeight() / rows;
            
            TextureRegion[][] temp = TextureRegion.split(spritesheet, frameWidth, frameHeight);
            
            // Aplatir en tableau 1D
            TextureRegion[] frames = new TextureRegion[cols * rows];
            int index = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    frames[index++] = temp[row][col];
                }
            }
            
            return frames;
        } catch (Exception e) {
            System.err.println("Erreur : impossible de charger l'animation spritesheet de la tour " + towerId);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Dispose les assets chargés (quand le jeu se termine)
     */
    public void dispose() {
        for (TextureRegion region : cachedTextures.values()) {
            if (region.getTexture() != null) {
                region.getTexture().dispose();
            }
        }
        cachedTextures.clear();
    }
}
