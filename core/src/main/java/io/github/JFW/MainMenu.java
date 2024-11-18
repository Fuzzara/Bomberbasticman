package io.github.JFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.JFW.System.InputHandler;
import io.github.JFW.System.MusicPlayer;
import io.github.JFW.System.SpriteBatchHandler;

public class MainMenu extends ApplicationAdapter {
    private Texture background;
    private Texture logo;
    private Stage stage;
    private MusicPlayer music = new MusicPlayer();
    private InputHandler inputHandler = new InputHandler();
    private SpriteBatch batch;
    float delta;
    private float y = -1000;

    public MainMenu(){
        this.batch = SpriteBatchHandler.getBatch(); //Singleton oh yeahh
        background = new Texture("logobg.png");
        stage = new Stage(new ExtendViewport(864, 783));
        logo = new Texture("logo.png");
        music.playMusic("sound/title.mp3");
        delta = Gdx.graphics.getDeltaTime();

    }
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, 864, 783);
        delta += Gdx.graphics.getDeltaTime();
        logoAnimation(SpriteBatchHandler.getBatch());

        batch.end();
    }
    public void logoAnimation(SpriteBatch batch) {
        boolean isPlaying = false;
        if (!isPlaying){
            isPlaying = true;
            delta += Gdx.graphics.getDeltaTime();
            if (delta > 1.5f) {
                for (int i = 0; i < 100; i++) {
                    y += 0.1f;
                    System.out.println(i);
                    batch.draw(logo, 0, y, 864, 783);
                break;
                }
            }
        }
    }
    public String handleInput() {
        return inputHandler.handleMainMenuInput();
    }
    public void dispose() {
        background.dispose();
        logo.dispose();
        music.dispose();
    }

}
