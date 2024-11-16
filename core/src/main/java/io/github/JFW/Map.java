package io.github.JFW;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

import java.util.Random;

public class Map {
    private TiledMap tiledMap;
    private TiledMapTileLayer collisionLayer;

    public Map(String mapPath) {
        tiledMap = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0); // Assuming the first layer is the base layer
        TiledMapTileLayer obstacleLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), layer.getTileWidth(), layer.getTileHeight());
        obstacleLayer.setName("obstacles");
        collisionLayer = obstacleLayer;
        tiledMap.getLayers().add(obstacleLayer);
        createCollisionLayer();
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Array<Rectangle> getObstacles() {
        Array<Rectangle> obstacles = new Array<>();
        //TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                //Gdx.app.debug("Map", "FOUND OBSTACLE");
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                obstacles.add(rect);
            }
        }
        return obstacles;
    }

    /*private void createCollisionLayer() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0); // Assuming the first layer is the base layer
        collisionLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), layer.getTileWidth(), layer.getTileHeight());

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null && isCollisionTile(cell.getTile())) {
                    RectangleMapObject rectObject = new RectangleMapObject(x * layer.getTileWidth(), y * layer.getTileHeight(), layer.getTileWidth(), layer.getTileHeight());
                    collisionLayer.getObjects().add(rectObject);
                }
            }
        }
    }*/

    /*Esta vara crea la minga de colisiones y de una le poner bordes al mapa brother*/
    public void createCollisionLayer() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0); // Assuming the first layer is the base layer
        int scaledTileWidth = layer.getTileWidth() * 3;
        int scaledTileHeight = layer.getTileHeight() * 3;
        collisionLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), scaledTileWidth, scaledTileHeight);

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (x == 0 || y == 0 || x == layer.getWidth() - 1 || y == layer.getHeight() - 1 || x == 1 || x == layer.getWidth() - 2) {
                    // Add collision rectangle for border tiles and extra columns on the left and right
                    RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
                    collisionLayer.getObjects().add(rectObject);
                } else {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null && isCollisionTile(cell.getTile())) {
                        RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
                        collisionLayer.getObjects().add(rectObject);
                    }
                }
            }
        }

        for (int x = 2;x<17;x++){
            for(int y = 1;y<14;y++){
                if( (x%2 != 0) && (y%2 == 0)){
                    addSingleCollision(x,y);
                }
            }
        }
        placerandomwalls(6);

        collisionLayer.setName("collision");
        tiledMap.getLayers().add(collisionLayer);
    }
    //creo que ya esta arreglado
    public void placerandomwalls(int n){
        Random rand = new Random();
        while (n != 0){
            int x = rand.nextInt((16-2)+1)+2;
            int y = rand.nextInt((13-1)+1)+1;
            if ((x%2 == 0) && (y%2 != 0) && (x!=2)){
                addSingleCollision(x,y);
                n -=1;
            }
        }
    }

    public void removeSingleCollision(int x, int y) {
        TiledMapTileLayer layerTile = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        int scaledTileWidth = layerTile.getTileWidth() * 3;
        int scaledTileHeight = layerTile.getTileHeight() * 3;

        if (x >= 0 && x < layerTile.getWidth() && y >= 0 && y < layerTile.getHeight()) {
            // Remove the collision rectangle from the collision layer
            RectangleMapObject rectToRemove = null;
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if (rect.x == x * scaledTileWidth && rect.y == y * scaledTileHeight) {
                        rectToRemove = (RectangleMapObject) object;
                        break;
                    }
                }
            }
            if (rectToRemove != null) {
                collisionLayer.getObjects().remove(rectToRemove);
            }

            // Remove the tile from the obstacles layer
            layerTile.setCell(x, y, null);
        }
    }

    private boolean isCollisionTile(TiledMapTile tile) {
        // Define your logic to determine if a tile should be a collision tile
        // For example, checking a property of the tile
        return tile.getProperties().containsKey("collidable");
    }

    public void addSingleCollision(int x, int y) {
        TiledMapTileLayer layerTile = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles"); // Assuming the first layer is the base layer
        int scaledTileWidth = layerTile.getTileWidth() * 3;
        int scaledTileHeight = layerTile.getTileHeight()* 3;

        if (x >= 0 && x < layerTile.getWidth() && y >= 0 && y < layerTile.getHeight()) {
            // Add the collision rectangle to the collision layer
            RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
            collisionLayer.getObjects().add(rectObject);

            // Set the tile in the base tile layer
            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
            cell.setTile(tiledMap.getTileSets().getTile(13));
            layerTile.setCell(x, y, cell);
        }
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    //Repensar colisiones

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
