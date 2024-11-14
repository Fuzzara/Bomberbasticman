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

import java.awt.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture background;
    private Texture uiBackground;
    private CollisionSystem collSystem;
    //TESTEO
    private Player player;
    public Array<Rectangle> obstacles;
    private ShapeRenderer sr;

    //CUADRITO DEBUG
    private Rectangle rect = new Rectangle(90,45,2,2);

    @Override
    public void create() {
        stage = new Stage(new ExtendViewport(768, 672)); //
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("bg.png"));
        uiBackground = new Texture(Gdx.files.internal("uibg.png"));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        draw();
        input();
        player.update();
    }
    private void draw() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        batch.begin();
        batch.draw(background, 0, 0, 768, 576);
        batch.draw(uiBackground, 0, 576, 768, 96);
        batch.end();

        //DEBUG
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0,1,0,1);
        sr.rect(rect.x, rect.y, rect.width, rect.height);
        sr.end();

    }

    private void input(){
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
