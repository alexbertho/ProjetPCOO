package io.github.Wasnowl.entities;

import com.badlogic.gdx.math.Vector2;

/**
 * EnemyMovement : gère le déplacement et le suivi du chemin
 * Responsabilité unique : mouvement et pathfinding
 */
public class EnemyMovement {
    private Vector2 position;
    private Vector2[] path;
    private int currentPoint = 0;
    private float speed;
    private boolean isMoving = true;

    public EnemyMovement(Vector2 startPos, Vector2[] path, float speed) {
        this.position = startPos.cpy();
        this.path = path;
        this.speed = speed;
    }

    public void update(float delta) {
        if (!isMoving || currentPoint >= path.length) return;

        Vector2 target = path[currentPoint];
        Vector2 direction = target.cpy().sub(position);
        float distance = direction.len();

        // Si on est très proche du point cible, passer au suivant directement
        if (distance < 1.0f) {
            position.set(target);
            currentPoint++;
            return;
        }

        // Normaliser et avancer
        direction.nor();
        position.add(direction.scl(speed * delta));
    }

    /**
     * Arrête le mouvement (utilisé lors de la mort)
     */
    public void stop() {
        isMoving = false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 pos) {
        this.position = pos.cpy();
    }

    public boolean hasReachedEnd() {
        return currentPoint >= path.length;
    }

    public boolean isMoving() {
        return isMoving;
    }
}
