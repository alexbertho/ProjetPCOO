package io.github.Wasnowl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Base abstraite pour toutes les entites du moteur.
 * Centralise la position/taille et impose update/render.
 */
public abstract class GameObject {
    /** Position dans le monde. */
    protected Vector2 position;
    /** Largeur/hauteur (optionnel). */
    protected Vector2 size;

    /**
     * Construit un objet de jeu a la position initiale.
     * @param x position en X
     * @param y position en Y
     */
    public GameObject(float x, float y) {
        this.position = new Vector2(x, y);
        this.size = new Vector2(1, 1); // valeur par défaut
    }

    // Getter/Setter
    /**
     * Retourne la position courante dans le monde.
     * @return position courante
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Definit la position courante dans le monde.
     * @param position nouvelle position
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Retourne la taille (largeur/hauteur) associee.
     * @return taille courante
     */
    public Vector2 getSize() {
        return size;
    }

    /**
     * Definit la taille (largeur/hauteur) associee.
     * @param size nouvelle taille
     */
    public void setSize(Vector2 size) {
        this.size = size;
    }

    // Méthodes communes
    /**
     * Met a jour la logique (deplacement, IA, timers).
     * @param delta temps ecoule (secondes)
     */
    public abstract void update(float delta); // logique (déplacement, IA…)

    /**
     * Rend l'objet dans le SpriteBatch courant.
     * @param batch sprite batch actif
     */
    public abstract void render(SpriteBatch batch); // affichage graphique
}
