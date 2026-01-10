package io.github.Wasnowl.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerAnimator {
    private static final int TILE_SIZE = 32;
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 64;
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

    public PlayerAnimator() {
        this(WALK_PATH, IDLE_PATH);
    }

    public PlayerAnimator(String walkPath, String idlePath) {
        walkTexture = new Texture(Gdx.files.internal(walkPath));
        idleTexture = new Texture(Gdx.files.internal(idlePath));

        TextureRegion[] walkDownRow = buildRowFrames(walkTexture, 0);
        TextureRegion[] walkLeftRow = buildRowFrames(walkTexture, 2);
        TextureRegion[] walkUpRow = buildRowFrames(walkTexture, 4);
        TextureRegion[] idleDownRow = buildRowFrames(idleTexture, 0);
        TextureRegion[] idleLeftRow = buildRowFrames(idleTexture, 2);
        TextureRegion[] idleUpRow = buildRowFrames(idleTexture, 4);

        boolean walkCol1Empty = isRegionEmpty(walkTexture, walkDownRow[1]);
        boolean idleCol1Empty = isRegionEmpty(idleTexture, idleDownRow[1]);

        idleDown = buildIdleDown(idleDownRow, idleCol1Empty);
        idleLeft = buildIdleLeft(idleLeftRow);
        idleUp = buildIdleUp(idleUpRow);
        idleRight = buildFlipped(idleLeft);

        walkDown = buildWalkDown(walkDownRow, walkCol1Empty);
        walkLeft = buildWalkLeft(walkLeftRow);
        walkUp = buildWalkUp(walkUpRow);
        walkRight = buildFlipped(walkLeft);
    }

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

    public void dispose() {
        walkTexture.dispose();
        idleTexture.dispose();
    }

    private Animation<TextureRegion> buildIdleDown(TextureRegion[] row, boolean col1Empty) {
        if (col1Empty) {
            return buildAnimation(IDLE_FRAME_DURATION, row[0], row[2]);
        }
        return buildAnimation(IDLE_FRAME_DURATION, row[0], row[1], row[2]);
    }

    private Animation<TextureRegion> buildIdleLeft(TextureRegion[] row) {
        return buildAnimation(IDLE_FRAME_DURATION, row[0], row[2]);
    }

    private Animation<TextureRegion> buildIdleUp(TextureRegion[] row) {
        return buildAnimation(IDLE_FRAME_DURATION, row[0], row[2]);
    }

    private Animation<TextureRegion> buildWalkDown(TextureRegion[] row, boolean col1Empty) {
        if (col1Empty) {
            return buildAnimation(WALK_FRAME_DURATION, row[0], row[2], row[0], row[2]);
        }
        return buildAnimation(WALK_FRAME_DURATION, row[0], row[1], row[2]);
    }

    private Animation<TextureRegion> buildWalkLeft(TextureRegion[] row) {
        return buildAnimation(WALK_FRAME_DURATION, row[0], row[2], row[0], row[2]);
    }

    private Animation<TextureRegion> buildWalkUp(TextureRegion[] row) {
        return buildAnimation(WALK_FRAME_DURATION, row[0], row[2], row[0], row[2]);
    }

    private TextureRegion[] buildRowFrames(Texture texture, int rowFromTop) {
        TextureRegion[] row = new TextureRegion[3];
        for (int col = 0; col < 3; col++) {
            row[col] = buildFrame(texture, col, rowFromTop);
        }
        return row;
    }

    private TextureRegion buildFrame(Texture texture, int col, int rowFromTop) {
        int x = col * FRAME_WIDTH;
        int yTop = rowFromTop * TILE_SIZE;
        int y = texture.getHeight() - yTop - FRAME_HEIGHT;
        return new TextureRegion(texture, x, y, FRAME_WIDTH, FRAME_HEIGHT);
    }

    private Animation<TextureRegion> buildFlipped(Animation<TextureRegion> source) {
        TextureRegion[] frames = source.getKeyFrames();
        TextureRegion[] flipped = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            TextureRegion region = new TextureRegion(frames[i]);
            region.flip(true, false);
            flipped[i] = region;
        }
        return buildAnimation(source.getFrameDuration(), flipped);
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

    private boolean isRegionEmpty(Texture texture, TextureRegion region) {
        TextureData data = texture.getTextureData();
        if (!data.isPrepared()) {
            data.prepare();
        }
        Pixmap pixmap = data.consumePixmap();
        boolean dispose = data.disposePixmap();

        int startX = region.getRegionX();
        int startY = region.getRegionY();
        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
        boolean empty = true;

        int step = 2;
        for (int y = 0; y < height && empty; y += step) {
            for (int x = 0; x < width; x += step) {
                int pixel = pixmap.getPixel(startX + x, startY + y);
                int alpha = (pixel >>> 24) & 0xff;
                if (alpha != 0) {
                    empty = false;
                    break;
                }
            }
        }

        if (dispose) {
            pixmap.dispose();
        }
        return empty;
    }
}
