package io.github.JFW;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
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

    private void renderCollisionLayer() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 1, 0.2f); // Blue color with 0.5 opacity

        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        shapeRenderer.end();
    }

    private Vector3 getMouseWorldPosition() {
        Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        return camera.unproject(screenCoords);
    }

    private void logMouseWorldPosition() {
        Vector3 worldCoords = getMouseWorldPosition();
        Gdx.app.log("Mouse World Position", "X: " + worldCoords.x + " Y: " + worldCoords.y);
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
    private void inputExtra(){
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
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (gameScreen != null) gameScreen.dispose();
    }

}
