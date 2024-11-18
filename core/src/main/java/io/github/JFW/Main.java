package io.github.JFW;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private MainMenu mainMenu;
    private SpriteBatch batch;
    private GameScreen gameScreen;
    private SFXPlayer sfx = new SFXPlayer();
    public enum State { //states del juego
        mainMenu,
        game,
    }
    private State state;

    @Override
    public void create() {
       this.batch = SpriteBatchHandler.getBatch();
       mainMenu = new MainMenu();
       state = State.mainMenu;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        switch (state) {
            case mainMenu:
                mainMenu.render();
                String action = mainMenu.handleInput();
                if (action!=null) {
                    if (action.equals("start")) {
                        sfx.playSFX("sound/selectMenu.mp3");
                        mainMenu.dispose();
                        startGame();
                    } else if (action.equals("exit")) {
                        dispose();
                        System.exit(0);
                    }
                }
                break;

            case game:
                gameScreen.render();
                break;
        }
    }
    private void startGame() {
        gameScreen = new GameScreen(0, 0); // level 0, score 0
        state = State.game;
    }


    /*private void input(){ //debug!
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            rect.x -= .5;
            Gdx.app.log("COORDS CUADRITO", "X: " + rect.x + " Y: " + rect.y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            rect.y -= .5;
            Gdx.app.log("COORDS CUADRITO", "X: " + rect.x + " Y: " + rect.y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            rect.x += .5;
            Gdx.app.log("COORDS CUADRITO", "X: " + rect.x + " Y: " + rect.y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            rect.y += .5;
            Gdx.app.log("COORDS CUADRITO", "X: " + rect.x + " Y: " + rect.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (state == State.running) {
                music.pauseMusic();
                sfx.playSFX("sound/pause.mp3");
                state = State.paused;
                music.pauseMusic();
                Gdx.app.log("State", "Pausado");
            } else {
                state = State.running;
                music.resumeMusic();
                Gdx.app.log("State", "Running");
            }
        }

    }*/


    @Override
    public void dispose() {
        batch.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (gameScreen != null) gameScreen.dispose();
    }

}
