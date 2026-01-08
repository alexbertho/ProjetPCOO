package io.github.Wasnowl.entities;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.Wasnowl.GameObject;

public class Enemy extends GameObject {
    private Vector2[] path;
    private int currentPoint = 0;
    private float speed;

    // Système de vie
    private float maxHealth = 100f;
    private float health = maxHealth;

    public Enemy(float x, float y, Vector2[] path, float speed) {
        super(x, y);
        this.path = path;
        this.speed = speed;
    }

    @Override
    public void update(float delta) {
        if (currentPoint < path.length) {
            Vector2 target = path[currentPoint];
            Vector2 direction = target.cpy().sub(position).nor();
            position.add(direction.scl(speed * delta));

            if (position.dst(target) < 0.1f) {
                currentPoint++;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // draw...
    }

    // API combat
    public void takeDamage(float amount) {
        health = Math.max(0f, health - amount);
    }

    public boolean isDead() {
        return health <= 0f;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}



