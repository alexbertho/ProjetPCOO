package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Rectangle;

public class Portal {
    public enum Type { MAP, COMBAT }

    private Rectangle bounds;
    private String target;
    private Type type;

    public Portal(Rectangle bounds, String target, Type type) {
        this.bounds = bounds;
        this.target = target;
        this.type = type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getTarget() {
        return target;
    }

    public Type getType() {
        return type;
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
