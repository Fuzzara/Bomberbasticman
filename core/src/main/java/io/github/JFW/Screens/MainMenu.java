package io.github.JFW.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.JFW.System.InputHandler;
import io.github.JFW.Audio.MusicPlayer;
import io.github.JFW.Graphics.SpriteBatchHandler;

public class MainMenu extends ApplicationAdapter {
    private Texture background;
    private Texture logo;
    private Stage stage;
    private MusicPlayer music = new MusicPlayer();
    private InputHandler inputHandler = new InputHandler();
    private SpriteBatch batch;
    float delta;
    private float y = -800;
    private float speed = 500;

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
        if (y < 0) {
            y += speed * Gdx.graphics.getDeltaTime();
            if (y > 0) y = 0;
        }
        batch.draw(logo, 0, y, 864, 783);
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
