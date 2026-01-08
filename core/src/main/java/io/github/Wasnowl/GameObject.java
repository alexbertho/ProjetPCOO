package io.github.Wasnowl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Vector2 position;   // position dans le monde
    protected Vector2 size;       // largeur/hauteur (optionnel)

    public GameObject(float x, float y) {
        this.position = new Vector2(x, y);
        this.size = new Vector2(1, 1); // valeur par défaut
    }

    // Getter/Setter
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getSize() {
        return size;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    // Méthodes communes
    public abstract void update(float delta); // logique (déplacement, IA…)

    public abstract void render(SpriteBatch batch); // affichage graphique
}

