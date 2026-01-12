package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Rectangle;

/**
 * Zone de transition vers une autre map ou un combat.
 */
public class Portal {
    /**
     * Types de portail disponibles.
     */
    public enum Type {
        /** Transition de map a map. */
        MAP,
        /** Transition vers un combat. */
        COMBAT
    }

    private Rectangle bounds;
    private String target;
    private Type type;

    /**
     * Cree un portail avec sa zone, sa cible et son type.
     * @param bounds zone de collision
     * @param target identifiant de destination
     * @param type type de portail
     */
    public Portal(Rectangle bounds, String target, Type type) {
        this.bounds = bounds;
        this.target = target;
        this.type = type;
    }

    /**
     * Retourne la zone du portail.
     * @return rectangle de collision
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Retourne l'identifiant de la cible (map ou combat).
     * @return destination
     */
    public String getTarget() {
        return target;
    }

    /**
     * Retourne le type de portail.
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Indique si un point est dans la zone du portail.
     * @param x position X
     * @param y position Y
     * @return true si inclus
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
