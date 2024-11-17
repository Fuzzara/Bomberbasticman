package io.github.JFW;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private MainMenu mainMenu;
    private GameScreen gameScreen;
    public enum State { //states del juego
        mainMenu,
        game,
    }
    private State state;

    @Override
    public void create() {
       batch = new SpriteBatch();
       mainMenu = new MainMenu(batch);
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
        gameScreen = new GameScreen(batch, 0, 0); // level 0, score 0
        state = State.game;
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (gameScreen != null) gameScreen.dispose();
    }

}
