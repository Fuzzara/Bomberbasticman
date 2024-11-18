package io.github.JFW.MapEnv;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import io.github.JFW.Entitys.Actors;
import io.github.JFW.Entitys.PowerUp;

import java.util.Random;

public class Map {
    private TiledMap tiledMap;
    private TiledMapTileLayer collisionLayer;

    private final int LEVEL_POWERUP;

    Actors actors;

    private int DESTROYABLE_WALL = 50;

    public Map(String mapPath, int powerUP) {
        tiledMap = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0); // Assuming the first layer is the base layer
        TiledMapTileLayer obstacleLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), layer.getTileWidth(), layer.getTileHeight());
        obstacleLayer.setName("obstacles");
        collisionLayer = obstacleLayer;
        tiledMap.getLayers().add(obstacleLayer);
        PrepareMapCollisions();
        this.LEVEL_POWERUP = powerUP;
    }

    public void clearCollisionLayer() {
        if (collisionLayer != null) {
            int i = collisionLayer.getObjects().getCount();
            for(int j = i - 1; j >= 0; j--) {
                collisionLayer.getObjects().remove(j);
            }
            // Also clear the obstacle tiles
            for (int x = 0; x < collisionLayer.getWidth(); x++) {
                for (int y = 0; y < collisionLayer.getHeight(); y++) {
                    collisionLayer.setCell(x, y, null);
                }
            }
        }
    }

    public void setActors(Actors actors){
        this.actors = actors;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Array<Rectangle> getObstacles() {
        Array<Rectangle> obstacles = new Array<>();
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                obstacles.add(rect);
            }
        }
        return obstacles;
    }

    public Array<RectangleMapObject> getObstaclesMO() {
        Array<RectangleMapObject> obstacles = new Array<>();
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject tmp =  (RectangleMapObject) object;
                obstacles.add(tmp);
            }
        }
        return obstacles;
    }

    public void PrepareMapCollisions() {
        // Clear existing collisions before preparing new ones
        clearCollisionLayer();

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int scaledTileWidth = layer.getTileWidth() * 3;
        int scaledTileHeight = layer.getTileHeight() * 3;
        collisionLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), scaledTileWidth, scaledTileHeight);

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (x == 0 || y == 0 || x == layer.getWidth() - 1 || y == layer.getHeight() - 1 || x == 1 || x == layer.getWidth() - 2) {
                    RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
                    rectObject.getProperties().put("Indestructible", true);
                    collisionLayer.getObjects().add(rectObject);
                } else {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null && isCollisionTile(cell.getTile())) {
                        RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
                        rectObject.getProperties().put("Indestructible", true);
                        collisionLayer.getObjects().add(rectObject);
                    }
                }
            }
        }

        for (int x = 2;x<17;x++){
            for(int y = 1;y<14;y++){
                if( (x%2 != 0) && (y%2 == 0)){
                    addSingleCollision(x,y, "Indestructible");
                }
            }
        }
        placerandomwalls(6);

        collisionLayer.setName("collision");
        tiledMap.getLayers().add(collisionLayer);
    }

    public void placerandomwalls(int n){
        Random rand = new Random();
        while (n != 0){
            int x = rand.nextInt((16-2)+1)+2;
            int y = rand.nextInt((13-1)+1)+1;
            if ((x%2 == 0) && (y%2 != 0) && !((x==2||x==4||x==6) && y==13) && !( x==2 && (y==11||y==9) )){
                addSingleCollision(x,y, "Indestructible");
                n -=1;
            }
        }
        int i = DESTROYABLE_WALL;

        while (i>=0){
            int x = rand.nextInt((16-2)+1)+2;
            int y = rand.nextInt((13-1)+1)+1;

            TiledMapTileLayer obstacleLayer = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
            if (obstacleLayer.getCell(x, y) == null && !(y == 13 && x == 2) && !(y == 12 && x == 2) && !(y == 13 && x == 3)) {
                if (i == DESTROYABLE_WALL) {
                    addSingleCollision(x, y, "PowerUP");
                } else if (i == DESTROYABLE_WALL - 1) {
                    addSingleCollision(x, y, "Door");
                } else {
                    addSingleCollision(x, y, "Destroyable");
                }
                i--;
            }
        }
    }

    public void removeSingleCollision(int x, int y) {
        TiledMapTileLayer layerTile = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        int scaledTileWidth = layerTile.getTileWidth() * 3;
        int scaledTileHeight = layerTile.getTileHeight() * 3;

        if (x >= 0 && x < layerTile.getWidth() && y >= 0 && y < layerTile.getHeight()) {
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
                if (Boolean.TRUE.equals(rectToRemove.getProperties().get("Indestructible"))) {
                    return;
                }
                collisionLayer.getObjects().remove(rectToRemove);
            }

            TiledMapTileLayer.Cell cell = layerTile.getCell(x, y);
            if (cell != null) {
                layerTile.setCell(x, y, null);
            }

            if (rectToRemove != null) {
                if (Boolean.TRUE.equals(rectToRemove.getProperties().get("PowerUP"))) {
                    Gdx.app.debug("MAP", "Spawned PowerUP at: " + x + ", " + y);
                    int worldX = x * collisionLayer.getTileWidth()-12;
                    int worldY = y * collisionLayer.getTileHeight()-24;
                    actors.addPowerUp(new PowerUp(worldX, worldY, actors, LEVEL_POWERUP));
                }
                if (rectToRemove.getProperties().containsKey("Door")) {
                    addSingleCollision(x,y,"ActualDoor");
                    Gdx.app.debug("MAP", "Spawned Door at: " + x + ", " + y);
                }
            }
        } else {
            Gdx.app.error("MAP", "Coordinates out of bounds: " + x + ", " + y);
        }
    }

    public void addSingleCollision(int x, int y, String type) {
        TiledMapTileLayer layerTile = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        int scaledTileWidth = layerTile.getTileWidth() * 3;
        int scaledTileHeight = layerTile.getTileHeight() * 3;

        if (x >= 0 && x < layerTile.getWidth() && y >= 0 && y < layerTile.getHeight()) {
            RectangleMapObject rectObject = new RectangleMapObject((x * scaledTileWidth), (y * scaledTileHeight), scaledTileWidth, scaledTileHeight);
            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

            if(type.equals("Indestructible")) {
                cell.setTile(tiledMap.getTileSets().getTile(13));
                rectObject.getProperties().put("Indestructible", true);
            } else if (type.equals("Bomb")) {
                cell.setTile(tiledMap.getTileSets().getTile(100));
                rectObject.getProperties().put("Bomb", true);
                rectObject.getProperties().put("Indestructible", false);
            } else if (type.equals("PowerUP")) {
                cell.setTile(tiledMap.getTileSets().getTile(4));
                rectObject.getProperties().put("PowerUP", true);
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
            } else if (type.equals("Door")) {
                cell.setTile(tiledMap.getTileSets().getTile(90));
                rectObject.getProperties().put("Door", true);
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
            } else if (type.equals("ActualDoor")) {
                cell.setTile(tiledMap.getTileSets().getTile(9));
                rectObject.getProperties().put("Door", true);
                rectObject.getProperties().put("KYS",true);
            } else {
                cell.setTile(tiledMap.getTileSets().getTile(5));
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
            }
            collisionLayer.getObjects().add(rectObject);

            layerTile.setCell(x, y, cell);
        }
    }

    private boolean isCollisionTile(TiledMapTile tile) {
        return tile.getProperties().containsKey("collidable");
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void dispose() {
        clearCollisionLayer();
        if (tiledMap != null) {
            tiledMap.getLayers().remove(collisionLayer);
            tiledMap.dispose();
        }
    }
}
