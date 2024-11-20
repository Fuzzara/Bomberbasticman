package io.github.JFW.MapEnv;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import io.github.JFW.Entities.Actors;
import io.github.JFW.Entities.Items.PowerUp;
import io.github.JFW.System.GlobalAccess;

import java.util.Random;

public class Map {
    private TiledMap tiledMap;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer obstacleLayer;
    private boolean BONUS = false;
    private int LEVEL_POWERUP;
    private Actors actors;
    private int DESTROYABLE_WALL = 40;

    public Map(String mapPath, int powerUP) {
        loadMap(mapPath);
        this.LEVEL_POWERUP = powerUP;
        prepareMapCollisions();
    }

    // Carga el mapa y configura las capas
    private void loadMap(String mapPath) {
        tiledMap = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        if (mapPath.equals("maps/lvl6.tmx")) {
            DESTROYABLE_WALL = -1;
            BONUS = true;
            Gdx.app.debug("Map", "Bonus level loaded");
        }

        obstacleLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
            baseLayer.getTileWidth(), baseLayer.getTileHeight());
        obstacleLayer.setName("obstacles");
        tiledMap.getLayers().add(obstacleLayer);

        collisionLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),
            baseLayer.getTileWidth() * 3, baseLayer.getTileHeight() * 3);
        collisionLayer.setName("collision");
        tiledMap.getLayers().add(collisionLayer);
    }

    // Devuelve si el mapa es de bonus
    public boolean isBonus() {
        return BONUS;
    }

    // Limpia la capa de colisiones
    public void clearCollisionLayer() {
        if (collisionLayer != null) {
            tiledMap.getLayers().remove(collisionLayer);
            TiledMapTileLayer baseLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
            collisionLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(),baseLayer.getTileWidth() * 3, baseLayer.getTileHeight() * 3);
            collisionLayer.setName("collision");
            tiledMap.getLayers().add(collisionLayer);
            clearObstacleLayer();
        }
    }

    // Limpia la capa de obstáculos
    private void clearObstacleLayer() {
        if (obstacleLayer != null) {
            for (int x = 0; x < obstacleLayer.getWidth(); x++) {
                for (int y = 0; y < obstacleLayer.getHeight(); y++) {
                    obstacleLayer.setCell(x, y, null);
                }
            }
        }
    }

    // Funcion de utilidad para que el mapa sepa sus actores
    public void setActors(Actors actors) {
        this.actors = actors;
    }

    // Devuelve el mapa en formato TiledMap
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    // Devuelve los obstáculos como un array de RectangleMapObject
    public Array<RectangleMapObject> getObstaclesMO() {
        Array<RectangleMapObject> obstacles = new Array<>();
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                obstacles.add((RectangleMapObject) object);
            }
        }
        return obstacles;
    }

    // Prepara las colisiones del mapa
    public void prepareMapCollisions() {
        clearCollisionLayer();
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        int scaledTileWidth = layer.getTileWidth() * 3;
        int scaledTileHeight = layer.getTileHeight() * 3;

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (isBorderTile(x, y, layer)) {
                    addBasicCollision(scaledTileWidth, scaledTileHeight, x, y);
                } else {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null && isCollisionTile(cell.getTile())) {
                        addBasicCollision(scaledTileWidth, scaledTileHeight, x, y);
                    }
                }
            }
        }

        addIndestructibleWalls();
        placeRandomWalls(6);
    }

    // Verifica si una celda es un borde del mapa
    private boolean isBorderTile(int x, int y, TiledMapTileLayer layer) {
        return x == 0 || y == 0 || x == layer.getWidth() - 1 || y == layer.getHeight() - 1 || x == 1 || x == layer.getWidth() - 2;
    }

    // Añade una colisión básica
    private void addBasicCollision(int scaledTileWidth, int scaledTileHeight, int x, int y) {
        RectangleMapObject rectObject = new RectangleMapObject(x * scaledTileWidth, y * scaledTileHeight, scaledTileWidth, scaledTileHeight);
        rectObject.getProperties().put("Indestructible", true);
        collisionLayer.getObjects().add(rectObject);
        TiledMapTileLayer.Cell cellX = new TiledMapTileLayer.Cell();
        collisionLayer.setCell(x, y, cellX);
    }

    // Añade paredes indestructibles del medio
    private void addIndestructibleWalls() {
        for (int x = 2; x < 17; x++) {
            for (int y = 1; y < 14; y++) {
                if ((x % 2 != 0) && (y % 2 == 0)) {
                    addSingleCollision(x, y, "Indestructible");
                }
            }
        }
    }

    // Coloca paredes aleatorias
    public void placeRandomWalls(int n) {
        Random rand = new Random();
        while (n != 0) {
            int x = rand.nextInt((16 - 2) + 1) + 2;
            int y = rand.nextInt((13 - 1) + 1) + 1;
            if (isValidRandomWallPosition(x, y)) {
                addSingleCollision(x, y, "Indestructible");
                n--;
            }
        }
        placeDestroyableWalls();
    }

    // Verifica si la posición es válida para una pared aleatoria
    private boolean isValidRandomWallPosition(int x, int y) {
        return (x % 2 == 0) && (y % 2 != 0) && !((x == 2 || x == 4 || x == 6) && y == 13) && !(x == 2 && (y == 11 || y == 9));
    }

    // Coloca paredes destruibles
    private void placeDestroyableWalls() {
        Random rand = new Random();
        int i = DESTROYABLE_WALL;
        while (i >= 0) {
            int x = rand.nextInt((16 - 2) + 1) + 2;
            int y = rand.nextInt((13 - 1) + 1) + 1;
            if (obstacleLayer.getCell(x, y) == null && isValidDestroyableWallPosition(x, y)) {
                if (i == DESTROYABLE_WALL) {
                    //todo: debug - borrar
                    Gdx.app.debug("Map", "se puso un powerup en " +x + " " +y);
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

    // Verifica si la posición es válida para una pared destruible
    private boolean isValidDestroyableWallPosition(int x, int y) {
        return !(y == 13 && x == 2) && !(y == 12 && x == 2) && !(y == 13 && x == 3);
    }

    // Elimina una colisión en una posición específica
    public void removeSingleCollision(int x, int y) {
        int scaledTileWidth = obstacleLayer.getTileWidth() * 3;
        int scaledTileHeight = obstacleLayer.getTileHeight() * 3;

        if (x >= 0 && x < obstacleLayer.getWidth() && y >= 0 && y < obstacleLayer.getHeight()) {
            RectangleMapObject rectToRemove = findCollisionObject(x, y, scaledTileWidth, scaledTileHeight);
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

            handleSpecialCollisions(rectToRemove, x, y);
        } else {
            Gdx.app.error("MAP", "Coordinates out of bounds: " + x + ", " + y);
        }
    }

    // Encuentra una colision en una posición específica
    private RectangleMapObject findCollisionObject(int x, int y, int scaledTileWidth, int scaledTileHeight) {
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.x == x * scaledTileWidth && rect.y == y * scaledTileHeight) {
                    return (RectangleMapObject) object;
                }
            }
        }
        return null;
    }

    // Maneja colisiones especiales como PowerUP y Door
    private void handleSpecialCollisions(RectangleMapObject rectToRemove, int x, int y) {
        if (rectToRemove != null) {
            if (Boolean.TRUE.equals(rectToRemove.getProperties().get("PowerUP"))) {
                Gdx.app.debug("MAP", "Spawned PowerUP at: " + x + ", " + y);
                int worldX = x * collisionLayer.getTileWidth() - 12;
                int worldY = y * collisionLayer.getTileHeight() - 24;
                if (LEVEL_POWERUP == 100) LEVEL_POWERUP = GlobalAccess.getInstance().getConfig().randomPowerUp();
                actors.addPowerUp(new PowerUp(worldX, worldY, actors, LEVEL_POWERUP));
            }
            if (rectToRemove.getProperties().containsKey("Door")) {
                addSingleCollision(x, y, "ActualDoor");
                Gdx.app.debug("MAP", "Spawned Door at: " + x + ", " + y);
            }
        }
    }

    // Añade una colisión en una posición específica
    public void addSingleCollision(int x, int y, String type) {
        int scaledTileWidth = obstacleLayer.getTileWidth() * 3;
        int scaledTileHeight = obstacleLayer.getTileHeight() * 3;

        if (x >= 0 && x < obstacleLayer.getWidth() && y >= 0 && y < obstacleLayer.getHeight()) {
            RectangleMapObject rectObject = new RectangleMapObject((x * scaledTileWidth), (y * scaledTileHeight), scaledTileWidth, scaledTileHeight);
            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

            configureCollisionType(type, rectObject, cell);
            collisionLayer.getObjects().add(rectObject);
            obstacleLayer.setCell(x, y, cell);
        }
    }

    // Configura el tipo de colisión
    private void configureCollisionType(String type, RectangleMapObject rectObject, TiledMapTileLayer.Cell cell) {
        switch (type) {
            case "Indestructible":
                cell.setTile(tiledMap.getTileSets().getTile(13));
                rectObject.getProperties().put("Indestructible", true);
                break;
            case "Bomb":
                cell.setTile(tiledMap.getTileSets().getTile(200));
                rectObject.getProperties().put("Bomb", true);
                rectObject.getProperties().put("Indestructible", false);
                break;
            case "PowerUP":
                cell.setTile(tiledMap.getTileSets().getTile(46));
                rectObject.getProperties().put("PowerUP", true);
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
                break;
            case "Door":
                cell.setTile(tiledMap.getTileSets().getTile(46));
                rectObject.getProperties().put("Door", true);
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
                break;
            case "ActualDoor":
                cell.setTile(tiledMap.getTileSets().getTile(9));
                rectObject.getProperties().put("Door", false);
                rectObject.getProperties().put("KYS", true);
                break;
            default:
                cell.setTile(tiledMap.getTileSets().getTile(8));
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
                break;
        }
    }

    // Verifica si una celda es de colisión
    private boolean isCollisionTile(TiledMapTile tile) {
        return tile.getProperties().containsKey("collidable");
    }

    // Devuelve la capa de colisiones
    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    // Libera los recursos del mapa
    public void dispose() {
        clearCollisionLayer();
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
