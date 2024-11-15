package io.github.JFW;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.awt.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture background;
    private Texture uiBackground;
    // Remove collSystem reference
    private Player player;
    private Actors actors;
    public Array<Rectangle> obstacles;
    private ShapeRenderer sr;

    private MusicPlayer music = new MusicPlayer();

    //CUADRITO DEBUG
    private Rectangle rect = new Rectangle(186,45,2,2);

    private MapSystem mapSystem;
    private Map currentMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    public enum State { //states del juego
        running,
        paused
    }
    private State state;

    @Override
    public void create() {
        stage = new Stage(new FitViewport(864, 768)); //
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("bg.png"));
        uiBackground = new Texture(Gdx.files.internal("uibg.png"));

        Gdx.input.setInputProcessor(stage);
        // Remove collSystem initialization

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 864, 768);

        mapSystem = new MapSystem();
        currentMap = mapSystem.getMap(0);
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap.getTiledMap(),2.74f);



        actors = new Actors();
        player = new Player(batch, actors, currentMap);
        actors.setPlayer(player);
        sr = new ShapeRenderer();

        state = State.running;
        stage.addActor(player);

        music.playMusic("sound/w1.mp3");

    }


    @Override
    public void render() {
        switch (state) {
            case running:
                draw();
                actors.update(state);
                break;
            case paused:
                //no se actualiza!
                //mostar pantalla de pausa
                break;

        }
        input();//Input del cuadrito
        draw();
        actors.update(state);
    }
    private void draw() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(uiBackground, 0, 657, 864, 111);
        batch.end();

        // Debug rectangle
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 1, 0, 1);
        sr.rect(rect.x, rect.y, rect.width, rect.height);
        sr.end();
    }

    private void input(){ //debug!
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
                state = State.paused;
                Gdx.app.log("State", "Pausado");
            } else {
                state = State.running;
                Gdx.app.log("State", "Running");
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
