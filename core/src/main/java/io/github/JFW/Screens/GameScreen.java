package io.github.JFW.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import io.github.JFW.Audio.MusicPlayer;
import io.github.JFW.Audio.SFXPlayer;
import io.github.JFW.Entitys.Player;
import io.github.JFW.Graphics.SpriteBatchHandler;
import io.github.JFW.System.Scoreboard;
import io.github.JFW.System.*;
import io.github.JFW.MapEnv.*;

public class GameScreen extends ApplicationAdapter {

    //UI
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture uiBackground;
    private BitmapFont font;

    //Config y Mapa
    private Config levelconfig;
    public Array<Rectangle> obstacles;
    private ShapeRenderer sr;
    private Map currentMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private InputHandler inputHandler = new InputHandler();

    private Player player;

    //Sound
    private MusicPlayer music = new MusicPlayer();
    private float musicUpdateTimer = 0f;
    private SFXPlayer sfx = new SFXPlayer();

    //Scoreboard
    private Scoreboard scoreboard;

    //states del juego
    public enum State {
        running,
        paused,
        levelTransition
    }
    private State state;
    private float transitionTimer;
    private static final float TRANSITION_DURATION = 3.7f; // transicion de nivel (3.7 segundos de espera)

    //Constructor bonito
    public GameScreen(int level, int score) {
        this.batch = SpriteBatchHandler.getBatch();
        state = State.running;

        sr = new ShapeRenderer();

        scoreboard = Scoreboard.getInstance();

        stage = new Stage(new ExtendViewport(864, 783));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        font = new BitmapFont(Gdx.files.internal("fontbomber.fnt"));

        uiBackground = new Texture(Gdx.files.internal("uibg.png"));

        Gdx.input.setInputProcessor(stage);

        //Configuración de la cámara y el mapa
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 864, 783);
        camera.position.set((864/2)+24, (783/2)+24, 0);

        levelconfig = new Config(batch);
        // Pass the Config instance to Scoreboard
        scoreboard.setLevelConfig(levelconfig);

        currentMap = levelconfig.setuplevel(level);
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap.getTiledMap(),3f);

        //Musica!
        updateMusic(level);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    private void updateMusic(int level) {
        music.stopMusic();
        if (level > 0 && (level) % 5 == 0) {
            music.playMusic("sound/lvlmusic/bonus.mp3");
        } else {
            int musicLevel = (level / 5) + 1;
            String musicFile = "sound/lvlmusic/lvl" + musicLevel + ".mp3";
            music.playMusic(musicFile); //:3
        }
    }

    @Override
    public void render() {
        switch (state) {
            case running:
                checkLevelCompletion();
                draw();
                scoreboard.update(Gdx.graphics.getDeltaTime());
                levelconfig.runlevel(state);
                break;
            case paused:
                draw();
                break;
            case levelTransition:
                draw();
                player = Player.getInstance();
                player.win();
                player.draw();
                handleLevelTransition();
                break;
        }
        scoreboard.render();
        inputExtra();
    }

    private void checkLevelCompletion() {
        if (levelconfig.isLevelCompleted()) {
            music.stopMusic();
            sfx.playSFX("sound/win.mp3");
            state = State.levelTransition;
            transitionTimer = TRANSITION_DURATION;
            Gdx.app.log("GameScreen", "Level " + levelconfig.getCurrentLevel()+ " completed!");
        }
    }

    private void handleLevelTransition() {
        transitionTimer -= Gdx.graphics.getDeltaTime();

        if (transitionTimer <= 0) {
            if (levelconfig.switchToNextLevel()) {
                player = Player.getInstance();
                player.respawn();
                scoreboard.setTimeLeft(200);
                // Successfully switched to next level
                currentMap = levelconfig.getCurrentMap();
                mapRenderer.setMap(currentMap.getTiledMap());
                //sfx.playSFX("sound/lvlstart.mp3");
                updateMusic(levelconfig.getCurrentLevel());
                state = State.running;
                Gdx.app.log("GameScreen", "Starting level " + levelconfig.getCurrentLevel());
            } else {
                // No more levels, game complete!
                Gdx.app.log("GameScreen", "Game Complete! Restarting from level 0");
                currentMap = levelconfig.setuplevel(1);
                mapRenderer.setMap(currentMap.getTiledMap());
                updateMusic(0);
                state = State.running;
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(uiBackground, 24, 696, 864, 111);
        batch.end();
    }

    private void inputExtra() {
        if (inputHandler.debugReload()) {
            currentMap.dispose();
            currentMap = levelconfig.setuplevel(1);
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
            } else if (state == State.paused) {
                state = State.running;
                music.resumeMusic();
                Gdx.app.log("State", "Resumido");
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
       // stage.dispose();
       // skin.dispose();
        //batch.dispose();
        //uiBackground.dispose();
        //sr.dispose();
        //currentMap.dispose();
        //mapRenderer.dispose();
        music.dispose();
        //sfx.dispose();
        //font.dispose();
    }
}
