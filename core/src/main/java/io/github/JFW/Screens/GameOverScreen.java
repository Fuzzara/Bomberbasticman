package io.github.JFW.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.JFW.Main;
import io.github.JFW.System.InputHandler;
import io.github.JFW.Audio.MusicPlayer;
import io.github.JFW.Graphics.SpriteBatchHandler;

public class GameOverScreen extends ApplicationAdapter {
    private static GameOverScreen instance;
    private Texture background;
    private Texture logo;
    private Stage stage;
    private MusicPlayer music = new MusicPlayer();
    private InputHandler inputHandler = new InputHandler();
    private SpriteBatch batch;
    float delta;
    private float y = 900;
    private float speed = 350;

    public GameOverScreen() {
        this.batch = SpriteBatchHandler.getBatch(); // Singleton oh yeahh
        background = new Texture("logobg.png");
        stage = new Stage(new ExtendViewport(864, 783));
        logo = new Texture("gameover.png");
        music.playMusic("sound/gameover.mp3");
        delta = Gdx.graphics.getDeltaTime();
    }

    // Renderiza la pantalla de Game Over
    public void render() {
        batch.begin();
        batch.draw(background, 0 + 24, 0 + 24, 864, 783);
        delta += Gdx.graphics.getDeltaTime();
        logoAnimation(SpriteBatchHandler.getBatch());
        if (handleInput()) {
            music.stopMusic();
            Main.getInstance().setState(Main.State.mainMenu);
        }
        batch.end();
    }

    // Anima el logo de Game Over
    public void logoAnimation(SpriteBatch batch) {
        if (y > 0) {
            y -= speed * Gdx.graphics.getDeltaTime();
            if (y < 0) y = 0;
        }
        batch.draw(logo, 24, y + 24, 864, 783);
    }

    // Input en la pantalla
    public boolean handleInput() {
        return inputHandler.handleGameOverInput();
    }

    // Libera los recursos de la pantalla de Game Over
    public void dispose() {
        background.dispose();
        logo.dispose();
        music.dispose();
    }
}
