package io.github.JFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.JFW.Audio.SFXPlayer;
import io.github.JFW.Graphics.SpriteBatchHandler;
import io.github.JFW.Screens.GameOverScreen;
import io.github.JFW.Screens.GameScreen;
import io.github.JFW.Screens.MainMenu;
import io.github.JFW.System.Scoreboard;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private MainMenu mainMenu;
    private static Main instance;
    private SpriteBatch batch;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private SFXPlayer sfx = new SFXPlayer();

    // Estados del juego
    public enum State {
        mainMenu,
        game,
        gameover
    }
    private State state;

    public Main() {}

    // Singleton para obtener la instancia de Main
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    // Inicializa el juego
    @Override
    public void create() {
        this.batch = SpriteBatchHandler.getBatch();
        mainMenu = new MainMenu();
        state = State.mainMenu;
    }

    // Renderiza el juego según el estado actual
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        switch (state) {
            case mainMenu:
                mainMenu.render();
                String action = mainMenu.handleInput();
                if (action != null) {
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
                if (Scoreboard.getInstance().getLives() < 0) {
                    this.state = State.gameover;
                } else {
                    gameScreen.render();
                }
                break;

            case gameover:
                if (gameOverScreen == null) {
                    gameScreen.dispose();
                    gameScreen = null;
                    gameOver();
                } else {
                    gameOverScreen.render();
                    boolean action2 = gameOverScreen.handleInput();
                    if (action2) {
                        dispose();
                        System.exit(0);
                    }
                }
                break;
        }
    }

    // Inicia el juego
    private void startGame() {
        gameScreen = new GameScreen(1, 0); // level 1, score 0
        this.state = State.game;
    }

    // Maneja el estado de Game Over
    public void gameOver() {
        gameOverScreen = new GameOverScreen();
        this.state = State.gameover;
    }

    // Cambia el estado del juego
    public void setState(State newState) {
        this.state = newState;
    }

    // Libera recursos
    @Override
    public void dispose() {
        batch.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (gameScreen != null) gameScreen.dispose();
    }
}
