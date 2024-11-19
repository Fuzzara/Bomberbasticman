package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.System.InputHandler;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;

public class BombManager {
    private final SpriteBatch batch;
    private float nextBombTime = 0f;
    private Actors actors;
    private Map currentMap;
    private SFXPlayer sfx;
    private InputHandler inputHandler;

    public BombManager(Actors actors, Map currentMap) {
        this.batch = SpriteBatchHandler.getBatch();;
        this.actors = actors;
        this.currentMap = currentMap;
        this.sfx = new SFXPlayer();
        this.inputHandler = new InputHandler();

    }

    public void setMap(Map map) {
        this.currentMap = map;
    }

    public void handleBombPlacement(Vector2 playerPosition, float deltaTime) {
        if(actors.getBombCount() > Player.getInstance().getBombLimit()){
            //Gdx.app.debug("BombManager", "Player has reached bomb limit");
            return;
        }
        nextBombTime += deltaTime;
            if (inputHandler.canPlaceBomb()) {
                if (nextBombTime >= 0.3f) {
                    sfx.playSFX("sound/placeBomb.mp3");
                    nextBombTime = 0f;
                    placeBomb(playerPosition);
                }
        }
    }

    private void placeBomb(Vector2 playerPosition) {

        // Calcular la posición de la bomba según la del jugador
        TiledMapTileLayer layer = (TiledMapTileLayer) currentMap.getTiledMap().getLayers().get(0);
        float tileWidth = layer.getTileWidth() * 3;
        float tileHeight = layer.getTileHeight() * 3;

        int tileX = (int) ((playerPosition.x + 48 / 2) / tileWidth);
        int tileY = (int) ((playerPosition.y) / tileHeight);
        float bombX = tileX * tileWidth;
        float bombY = tileY * tileHeight;

        //Creacion de la bomba!
        Bomb bomb = new Bomb(bombX, bombY, actors, currentMap);
        //System.out.println("Nueva bomba colocada en: " + bombX + ", " + bombY);
    }
}
