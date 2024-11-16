package io.github.JFW;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Renderer {
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;

    private SpriteBatch batch;
    private Texture uiBackground;

    public Renderer(){

    }
}
