package io.github.JFW;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator {
    private Animation<TextureRegion> animation;
    private float time;
    private Texture texture;
    private int frameWidth;
    private int frameHeight;

    public Animator(String path, int frameCols, int frameRows, int startFrame, int endFrame, float frameDuration, Animation.PlayMode playMode) {
        texture = new Texture(path);

        this.frameWidth = texture.getWidth() / frameCols;
        this.frameHeight = texture.getHeight() / frameRows;

        TextureRegion[][] tempFrames = TextureRegion.split(texture, frameWidth, frameHeight);

        TextureRegion[] frames = new TextureRegion[(endFrame - startFrame) + 1];
        int index = 0;
        for (int i = startFrame; i <= endFrame; i++) {
            frames[index++] = tempFrames[i / frameCols][i % frameCols];
        }

        animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
    }
    public TextureRegion getFrame() {
        time += Gdx.graphics.getDeltaTime();
        return animation.getKeyFrame(time);
    }
    public void reset(float frame) {
        time = frame;
    }
    public void dispose() {
        texture.dispose();
    }
}
