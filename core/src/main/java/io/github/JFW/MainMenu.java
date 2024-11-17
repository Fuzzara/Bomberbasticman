package io.github.JFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MainMenu extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture logo;
    private Stage stage;
    private MusicPlayer music = new MusicPlayer();
    private SFXPlayer sfx = new SFXPlayer();
    private InputHandler inputHandler = new InputHandler();

    public MainMenu(SpriteBatch batch){
        this.batch = batch;
        background = new Texture("logobg.png");
        stage = new Stage(new ExtendViewport(864, 783));
        logo = new Texture("logo.png");
        music.playMusic("sound/title.mp3");
    }
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(logo, 300, 300);
        batch.end();
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
