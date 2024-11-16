package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class BombManager {
    private long nextBombTime = 0;
    private SpriteBatch batch;
    private Actors actors;
    private Map currentMap;

    public BombManager(SpriteBatch batch, Actors actors, Map currentMap) {
        this.batch = batch;
        this.actors = actors;
        this.currentMap = currentMap;
    }

    public void handleBombPlacement(Vector2 playerPosition) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            if (System.nanoTime() > nextBombTime) {
                nextBombTime = System.nanoTime() + 50000000; // Cooldown de 50ms
                placeBomb(playerPosition);
            }
        }
    }

    private void placeBomb(Vector2 playerPosition) {
        // Calcular la posición de la bombwda según la del jugador

        TiledMapTileLayer layer = (TiledMapTileLayer) currentMap.getTiledMap().getLayers().get(0);
        float tileWidth = layer.getTileWidth() * 3;
        float tileHeight = layer.getTileHeight() * 3;

        int tileX = (int) ((playerPosition.x + 48 / 2) / tileWidth);
        int tileY = (int) ((playerPosition.y) / tileHeight);
        float bombX = tileX * tileWidth;
        float bombY = tileY * tileHeight;

        Bomb bomb = new Bomb(batch, bombX, bombY, actors, currentMap);
        System.out.println("Nueva bomba colocada en: " + bombX + ", " + bombY);
    }
}
