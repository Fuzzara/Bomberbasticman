package io.github.JFW;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.Gdx;

public class Map {
    private TiledMap tiledMap;

    public Map(String mapPath) {
        tiledMap = new TmxMapLoader().load(mapPath);
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Array<Rectangle> getObstacles() {
        Array<Rectangle> obstacles = new Array<>();
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                obstacles.add(rect);
            }
        }
        return obstacles;
    }

    //Repensar colisiones

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
