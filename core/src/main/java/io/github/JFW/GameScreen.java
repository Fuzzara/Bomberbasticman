package io.github.JFW;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import io.github.JFW.System.*;
import io.github.JFW.MapEnv.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen extends ApplicationAdapter {

    //UI
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture uiBackground;

    //Config y Mapa
    private Config levelconfig;
    public Array<Rectangle> obstacles;
    private ShapeRenderer sr;
    private Map currentMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private InputHandler inputHandler = new InputHandler();

    //Sound
    private MusicPlayer music = new MusicPlayer();
    private SFXPlayer sfx = new SFXPlayer();

    //CUADRITO DEBUG
    //private Rectangle rect = new Rectangle(186,45,2,2);

    //states del juego
    public enum State {
        running,
        paused
    }
    private State state;

    //Constructor bonito
    public GameScreen(int level, int score){
        this.batch = SpriteBatchHandler.getBatch();
        //estado inicial!
        state = State.running;

        sr = new ShapeRenderer();


        stage = new Stage(new ExtendViewport(864, 783)); //usar img de ref
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        uiBackground = new Texture(Gdx.files.internal("uibg.png"));

        Gdx.input.setInputProcessor(stage);

        //Configuración de la cámara y el mapa
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 864, 783);
        camera.position.set((864/2)+24, (783/2)+24, 0);

        levelconfig = new Config(batch);
        currentMap = levelconfig.setuplevel(level);
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap.getTiledMap(),3f);

        //Musica! (WIP, CAMBIAR)
        music.playMusic("sound/lvlmusic/lvl2.mp3");
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

    }

    /*@Override
    public void create() {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
    }*/

    @Override
    public void render() {
        switch (state) {
            case running:
                draw();
                levelconfig.runlevel(state); //actualizar el nivel
                break;
            case paused:
                //no se actualiza! (por alguna razon el jugador tiene la animacion de caminar, pero se ve tierno asi que se queda)
                break;

        }
        //input();//Input del cuadrito
        inputExtra();
        draw();
        levelconfig.runlevel(state); //test
    }
    private void draw() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(uiBackground, 24, 696, 864, 111);
        batch.end();

        // Debug rectangle
        /*
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 1, 0, 1);
        sr.rect(rect.x, rect.y, rect.width, rect.height);
        sr.end();
        */
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
        if (inputHandler.debugReload()) { //debug, not working as intended btw yeahhh
            currentMap.dispose();
            currentMap = levelconfig.setuplevel(0);
            mapRenderer.setMap(currentMap.getTiledMap());
            Gdx.app.log("Debug", "Reloaded");
        }
        if (inputHandler.handlePauseInput()) {
            if (state == State.running) {
                music.pauseMusic();
                sfx.playSFX("sound/pause.mp3");
                state = State.paused;
                music.pauseMusic();
                Gdx.app.log("State", "Pausado");
            } else {
                state = State.running;
                music.resumeMusic();
                Gdx.app.log("State", "Resumido");
            }
        }
    }

    private void renderCollisionLayer() {
        //sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 0, 1, 0.2f); // Blue color with 0.5 opacity

        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                sr.rect(rect.x*3, rect.y*3, rect.width*3, rect.height*3);
            }
        }

        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        uiBackground.dispose();
        sr.dispose();
        currentMap.dispose();
        mapRenderer.dispose();
        music.dispose();

    }

}
