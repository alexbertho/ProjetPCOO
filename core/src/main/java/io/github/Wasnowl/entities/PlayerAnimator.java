package io.github.Wasnowl.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Construit les animations du joueur (idle/walk) selon la direction.
 */
public class PlayerAnimator {
    private static final int FRAME_COLS = 4;
    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final float IDLE_FRAME_DURATION = 0.2f;
    private static final float WALK_FRAME_DURATION = 0.12f;
    private static final float IDLE_EPSILON = 0.01f;
    private static final String WALK_PATH = "player/Char.png";
    private static final String IDLE_PATH = "player/Char_Idle.png";

    private final Texture walkTexture;
    private final Texture idleTexture;
    private final Animation<TextureRegion> idleDown;
    private final Animation<TextureRegion> idleLeft;
    private final Animation<TextureRegion> idleUp;
    private final Animation<TextureRegion> idleRight;
    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkUp;
    private final Animation<TextureRegion> walkRight;

    private float stateTime = 0f;
    private Direction lastDirection = Direction.DOWN;
    private AnimState state = AnimState.IDLE;

    /**
     * Cree un animateur avec les assets par defaut.
     */
    public PlayerAnimator() {
        this(WALK_PATH, IDLE_PATH);
    }

    /**
     * Cree un animateur a partir des chemins de spritesheets.
     * @param walkPath chemin du spritesheet de marche
     * @param idlePath chemin du spritesheet idle
     */
    public PlayerAnimator(String walkPath, String idlePath) {
        walkTexture = new Texture(Gdx.files.internal(walkPath));
        idleTexture = new Texture(Gdx.files.internal(idlePath));

        TextureRegion[] walkDownRow = buildRowFrames(walkTexture, 3);
        TextureRegion[] walkLeftRow = buildRowFrames(walkTexture, 2);
        TextureRegion[] walkRightRow = buildRowFrames(walkTexture, 1);
        TextureRegion[] walkUpRow = buildRowFrames(walkTexture, 0);
        TextureRegion[] idleDownRow = buildRowFrames(idleTexture, 3);
        TextureRegion[] idleLeftRow = buildRowFrames(idleTexture, 2);
        TextureRegion[] idleRightRow = buildRowFrames(idleTexture, 1);
        TextureRegion[] idleUpRow = buildRowFrames(idleTexture, 0);

        idleDown = buildAnimation(IDLE_FRAME_DURATION, idleDownRow);
        idleLeft = buildAnimation(IDLE_FRAME_DURATION, idleLeftRow);
        idleUp = buildAnimation(IDLE_FRAME_DURATION, idleUpRow);
        idleRight = buildAnimation(IDLE_FRAME_DURATION, idleRightRow);

        walkDown = buildAnimation(WALK_FRAME_DURATION, walkDownRow);
        walkLeft = buildAnimation(WALK_FRAME_DURATION, walkLeftRow);
        walkUp = buildAnimation(WALK_FRAME_DURATION, walkUpRow);
        walkRight = buildAnimation(WALK_FRAME_DURATION, walkRightRow);
    }

    /**
     * Retourne la frame courante selon la vitesse et le temps.
     * @param delta temps ecoule (secondes)
     * @param vx vitesse en X
     * @param vy vitesse en Y
     * @return frame courante
     */
    public TextureRegion getFrame(float delta, float vx, float vy) {
        stateTime += delta;

        if (Math.abs(vx) < IDLE_EPSILON && Math.abs(vy) < IDLE_EPSILON) {
            state = AnimState.IDLE;
        } else {
            state = AnimState.WALK;
            if (Math.abs(vx) > Math.abs(vy)) {
                lastDirection = vx >= 0f ? Direction.RIGHT : Direction.LEFT;
            } else {
                lastDirection = vy >= 0f ? Direction.UP : Direction.DOWN;
            }
        }

        Animation<TextureRegion> current = getAnimation(state, lastDirection);
        return current.getKeyFrame(stateTime, true);
    }

    /**
     * Libere les textures chargees.
     */
    public void dispose() {
        walkTexture.dispose();
        idleTexture.dispose();
    }

    private TextureRegion[] buildRowFrames(Texture texture, int rowFromTop) {
        TextureRegion[] row = new TextureRegion[FRAME_COLS];
        for (int col = 0; col < FRAME_COLS; col++) {
            row[col] = buildFrame(texture, col, rowFromTop);
        }
        return row;
    }

    private TextureRegion buildFrame(Texture texture, int col, int rowFromTop) {
        int x = col * FRAME_WIDTH;
        int y = texture.getHeight() - ((rowFromTop + 1) * FRAME_HEIGHT);
        return new TextureRegion(texture, x, y, FRAME_WIDTH, FRAME_HEIGHT);
    }

    private Animation<TextureRegion> getAnimation(AnimState state, Direction direction) {
        if (state == AnimState.IDLE) {
            switch (direction) {
                case LEFT:
                    return idleLeft;
                case UP:
                    return idleUp;
                case RIGHT:
                    return idleRight;
                case DOWN:
                default:
                    return idleDown;
            }
        }

        switch (direction) {
            case LEFT:
                return walkLeft;
            case UP:
                return walkUp;
            case RIGHT:
                return walkRight;
            case DOWN:
            default:
                return walkDown;
        }
    }

    private Animation<TextureRegion> buildAnimation(float frameDuration, TextureRegion... frames) {
        Array<TextureRegion> array = new Array<>(true, frames.length, TextureRegion.class);
        for (TextureRegion frame : frames) {
            array.add(frame);
        }
        return new Animation<>(frameDuration, array, Animation.PlayMode.LOOP);
    }
}
