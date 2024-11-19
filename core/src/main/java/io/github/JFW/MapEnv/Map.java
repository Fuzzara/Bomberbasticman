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
    private TiledMapTileLayer obstacleLayer;

    private final int LEVEL_POWERUP;

    Actors actors;

    private int DESTROYABLE_WALL = 50;

    public Map(String mapPath, int powerUP) {
        tiledMap = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        // Initialize obstacle layer
        obstacleLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                                            baseLayer.getTileWidth(), baseLayer.getTileHeight());
        obstacleLayer.setName("obstacles");
        tiledMap.getLayers().add(obstacleLayer);

        // Initialize collision layer
        collisionLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                                             baseLayer.getTileWidth() * 3, baseLayer.getTileHeight() * 3);
        collisionLayer.setName("collision");
        tiledMap.getLayers().add(collisionLayer);

        this.LEVEL_POWERUP = powerUP;
        PrepareMapCollisions();
    }

    public void clearCollisionLayer() {
        if (collisionLayer != null) {
            // Remove the collision layer from tiledMap first
            tiledMap.getLayers().remove(collisionLayer);

            // Create a new collision layer with the same dimensions
            TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
            collisionLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
                                                 baseLayer.getTileWidth() * 3, baseLayer.getTileHeight() * 3);
            collisionLayer.setName("collision");

            // Add the fresh collision layer back to tiledMap
            tiledMap.getLayers().add(collisionLayer);

            // Also clear the obstacle layer
            if (obstacleLayer != null) {
                for (int x = 0; x < obstacleLayer.getWidth(); x++) {
                    for (int y = 0; y < obstacleLayer.getHeight(); y++) {
                        obstacleLayer.setCell(x, y, null);
                    }
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
        int scaledTileWidth = obstacleLayer.getTileWidth() * 3;
        int scaledTileHeight = obstacleLayer.getTileHeight() * 3;

        if (x >= 0 && x < obstacleLayer.getWidth() && y >= 0 && y < obstacleLayer.getHeight()) {
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

            TiledMapTileLayer.Cell cell = obstacleLayer.getCell(x, y);
            if (cell != null) {
                obstacleLayer.setCell(x, y, null);
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
        int scaledTileWidth = obstacleLayer.getTileWidth() * 3;
        int scaledTileHeight = obstacleLayer.getTileHeight() * 3;

        if (x >= 0 && x < obstacleLayer.getWidth() && y >= 0 && y < obstacleLayer.getHeight()) {
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
                cell.setTile(tiledMap.getTileSets().getTile(8));
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
            }
            collisionLayer.getObjects().add(rectObject);
            obstacleLayer.setCell(x, y, cell);
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
            tiledMap.dispose();
        }
    }
}
