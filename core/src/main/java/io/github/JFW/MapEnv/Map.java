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

    public void setActors(Actors actors){
        this.actors = actors;
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

    public Array<RectangleMapObject> getObstaclesMO() {
        Array<RectangleMapObject> obstacles = new Array<>();
        //TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles");
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                //Gdx.app.debug("Map", "FOUND OBSTACLE");
                RectangleMapObject tmp =  (RectangleMapObject) object;
                obstacles.add(tmp);
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
    public void PrepareMapCollisions() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0); // Assuming the first layer is the base layer
        int scaledTileWidth = layer.getTileWidth() * 3;
        int scaledTileHeight = layer.getTileHeight() * 3;
        collisionLayer = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), scaledTileWidth, scaledTileHeight);
        //Se encarga de poner los bordes
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
        //esquina izquierda arriba (2,13)
        //esquina derecha abajo (16,1)
        //Se encarga de poner las paredes indestructibles del medio
        for (int x = 2;x<17;x++){
            for(int y = 1;y<14;y++){
                if( (x%2 != 0) && (y%2 == 0)){
                    addSingleCollision(x,y, "Indestructible");
                }
            }
        }
        placerandomwalls(6);
        //Se encarga de poner paredes destruibles



        collisionLayer.setName("collision");
        tiledMap.getLayers().add(collisionLayer);
    }
    //creo que ya esta arreglado
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
        int i = DESTROYABLE_WALL; //modificar por nivel!!!!!!!!!!!!!!!!!

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
                    //addSingleCollision(x, y, "Door");
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

            // Find the matching rectangle to remove
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if (rect.x == x * scaledTileWidth && rect.y == y * scaledTileHeight) {
                        rectToRemove = (RectangleMapObject) object;
                        break;
                    }
                }
            }

            // If found, remove it
            if (rectToRemove != null) {
                if (Boolean.TRUE.equals(rectToRemove.getProperties().get("Indestructible"))) {
                    //Gdx.app.error("MAP", "Cannot remove an indestructible object at " + x + ", " + y);
                    return;
                }
                collisionLayer.getObjects().remove(rectToRemove);
                //Gdx.app.debug("MAP", "Removed collision object at: " + x + ", " + y);
            } else {
                //Gdx.app.debug("MAP", "No collision object found at: " + x + ", " + y);
            }

            // Remove the corresponding tile
            TiledMapTileLayer.Cell cell = layerTile.getCell(x, y);
            if (cell != null) {
                layerTile.setCell(x, y, null);
                //Gdx.app.debug("MAP", "Removed tile at: " + x + ", " + y);
            } else {
                //Gdx.app.debug("MAP", "No tile to remove at: " + x + ", " + y);
            }

            // PowerUP and Door
            if (rectToRemove != null) {
                if (Boolean.TRUE.equals(rectToRemove.getProperties().get("PowerUP"))) {
                    Gdx.app.debug("MAP", "Spawned PowerUP at: " + x + ", " + y);
                    int worldX = x * collisionLayer.getTileWidth()-12;
                    int worldY = y * collisionLayer.getTileHeight()-24;
                    /*
                    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣶⣦⡄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⢀⣀⣀⣀⡀⢀⠀⢹⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⠙⠻⣿⣿⣷⣄⠨⣿⣿⣿⡌⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣷⣿⣿⣿⣿⣿⣶⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⣠⣴⣾⣿⣮⣝⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠈⠉⠙⠻⢿⣿⣿⣿⣿⣿⣿⠟⣹⣿⡿⢿⣿⣿⣬⣶⣶⡶⠦⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⣀⣢⣙⣻⢿⣿⣿⣿⠎⢸⣿⠕⢹⣿⣿⡿⣛⣥⣀⣀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⠈⠉⠛⠿⡏⣿⡏⠿⢄⣜⣡⠞⠛⡽⣸⡿⣟⡋⠉⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠰⠾⠿⣿⠁⠀⡄⠀⠀⠰⠾⠿⠛⠓⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠠⢐⢉⢷⣀⠛⠠⠐⠐⠠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                    ⠀⠀⠀⠀⣀⣠⣴⣶⣿⣧⣾⠡⠼⠎⢎⣋⡄⠆⠀⠱⡄⢉⠃⣦⡤⡀⠀⠀⠀⠀
                    ⠀⠀⠐⠙⠻⢿⣿⣿⣿⣿⣿⣿⣄⡀⠀⢩⠀⢀⠠⠂⢀⡌⠀⣿⡇⠟⠀⠀⢄⠀
                    ⠀⣴⣇⠀⡇⠀⠸⣿⣿⣿⣿⣽⣟⣲⡤⠀⣀⣠⣴⡾⠟⠀⠀⠟⠀⠀⠀⠀⡰⡀
                    ⣼⣿⠋⢀⣇⢸⡄⢻⣟⠻⣿⣿⣿⣿⣿⣿⠿⡿⠟⢁⠀⠀⠀⠀⠀⢰⠀⣠⠀⠰
                    ⢸⣿⡣⣜⣿⣼⣿⣄⠻⡄⡀⠉⠛⠿⠿⠛⣉⡤⠖⣡⣶⠁⠀⠀⠀⣾⣶⣿⠐⡀
                    ⣾⡇⠈⠛⠛⠿⣿⣿⣦⠁⠘⢷⣶⣶⡶⠟⢋⣠⣾⡿⠃⠀⠀⠀⠰⠛⠉⠉⠀⠀
                     */
                    actors.addPowerUp(new PowerUp(worldX, worldY, actors, LEVEL_POWERUP));
                }
                if (rectToRemove.getProperties().containsKey("Door")) { //CUANDO QUITA PUERTA OCULTA
                    addSingleCollision(x,y,"ActualDoor");
                    Gdx.app.debug("MAP", "Spawned Door at: " + x + ", " + y);
                }
            }
        } else {
            Gdx.app.error("MAP", "Coordinates out of bounds: " + x + ", " + y);
        }
    }

    public void addSingleCollision(int x, int y, String type) {
        TiledMapTileLayer layerTile = (TiledMapTileLayer) tiledMap.getLayers().get("obstacles"); // Assuming the first layer is the base layer
        int scaledTileWidth = layerTile.getTileWidth() * 3;
        int scaledTileHeight = layerTile.getTileHeight() * 3;

        if (x >= 0 && x < layerTile.getWidth() && y >= 0 && y < layerTile.getHeight()) {
            // Add the collision rectangle to the collision layer
            RectangleMapObject rectObject = new RectangleMapObject((x * scaledTileWidth), (y * scaledTileHeight), scaledTileWidth, scaledTileHeight);
            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
            /*
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣷⣶⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣿⣿⣿⣿⣷⡒⢄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⣿⣿⣿⣿⣿⣆⠙⡄⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣤⣤⣤⣤⣤⣤⣤⣤⣤⠤⢄⡀⠀⠀⣿⣿⣿⣿⣿⣿⡆⠘⡄⠀⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⣿⣿⣿⣿⣿⣿⣿⣦⡈⠒⢄⢸⣿⣿⣿⣿⣿⣿⡀⠱⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⣿⣿⣿⣿⣿⣿⣿⣦⠀⠱⣿⣿⣿⣿⣿⣿⣇⠀⢃⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⢿⣿⣿⣿⣿⣿⣿⣷⡄⣹⣿⣿⣿⣿⣿⣿⣶⣾⣿⣶⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣀⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣶⣿⣭⣍⡉⠙⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⢀⣠⣶⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠉⠉⠛⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡷⢂⣓⣶⣶⣶⣶⣤⣤⣄⣀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⠟⢀⣴⢿⣿⣿⣿⠟⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠛⠋⠉⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠤⠤⠤⠤⠙⣻⣿⣿⣿⣿⣿⣿⣾⣿⣿⡏⣠⠟⡉⣾⣿⣿⠋⡠⠊⣿⡟⣹⣿⢿⣿⣿⣿⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⣶⣤⣭⣤⣼⣿⢛⣿⣿⣿⣿⣻⣿⣿⠇⠐⢀⣿⣿⡷⠋⠀⢠⣿⣺⣿⣿⢺⣿⣋⣉⣉⣩⣴⣶⣤⣤⣄⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠛⠻⠿⣿⣿⣿⣇⢻⣿⣿⡿⠿⣿⣯⡀⠀⢸⣿⠋⢀⣠⣶⠿⠿⢿⡿⠈⣾⣿⣿⣿⣿⡿⠿⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠻⢧⡸⣿⣿⣿⠀⠃⠻⠟⢦⢾⢣⠶⠿⠏⠀⠰⠀⣼⡇⣸⣿⣿⠟⠉⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣾⣶⣽⣿⡟⠓⠒⠀⠀⡀⠀⠠⠤⠬⠉⠁⣰⣥⣾⣿⣿⣶⣶⣷⡶⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠉⠹⠟⣿⣿⡄⠀⠀⠠⡇⠀⠀⠀⠀⠀⢠⡟⠛⠛⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⠋⠹⣷⣄⠀⠐⣊⣀⠀⠀⢀⡴⠁⠣⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣤⣀⠤⠊⢁⡸⠀⣆⠹⣿⣧⣀⠀⠀⡠⠖⡑⠁⠀⠀⠀⠑⢄⣀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⣦⣶⣿⣿⣟⣁⣤⣾⠟⠁⢀⣿⣆⠹⡆⠻⣿⠉⢀⠜⡰⠀⠀⠈⠑⢦⡀⠈⢾⠑⡾⠲⣄⠀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠖⠒⠚⠛⠛⠢⠽⢄⣘⣤⡎⠠⠿⠂⠀⠠⠴⠶⢉⡭⠃⢸⠃⠀⣿⣿⣿⠡⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀
            ⠀⠀⠀⠀⠀⡤⠶⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣋⠁⠀⠀⠀⠀⠀⢹⡇⠀⠀⠀⠀⠒⠢⣤⠔⠁⠀⢀⡏⠀⠀⢸⣿⣿⠀⢻⡟⠑⠢⢄⡀⠀⠀⠀⠀
            ⠀⠀⠀⠀⢸⠀⠀⠀⡀⠉⠛⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣄⣀⣀⡀⠀⢸⣷⡀⣀⣀⡠⠔⠊⠀⠀⢀⣠⡞⠀⠀⠀⢸⣿⡿⠀⠘⠀⠀⠀⠀⠈⠑⢤⠀⠀
            ⠀⠀⢀⣴⣿⡀⠀⠀⡇⠀⠀⠀⠈⣿⣿⣿⣿⣿⣿⣿⣿⣝⡛⠿⢿⣷⣦⣄⡀⠈⠉⠉⠁⠀⠀⠀⢀⣠⣴⣾⣿⡿⠁⠀⠀⠀⢸⡿⠁⠀⠀⠀⠀⠀⠀⠀⠀⡜⠀⠀
            ⠀⢀⣾⣿⣿⡇⠀⢰⣷⠀⢀⠀⠀⢹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣦⣭⣍⣉⣉⠀⢀⣀⣤⣶⣾⣿⣿⣿⢿⠿⠁⠀⠀⠀⠀⠘⠀⠀⠀⠀⠀⠀⠀⠀⠀⡰⠉⢦⠀
            ⢀⣼⣿⣿⡿⢱⠀⢸⣿⡀⢸⣧⡀⠀⢿⣿⣿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡭⠖⠁⠀⡠⠂⠀⠀⠀⠀⠀⠀⠀⠀⢠⠀⠀⠀⢠⠃⠀⠈⣀
            ⢸⣿⣿⣿⡇⠀⢧⢸⣿⣇⢸⣿⣷⡀⠈⣿⣿⣇⠈⠛⢿⣿⣿⣿⣿⣿⣿⠿⠿⠿⠿⠿⠿⠟⡻⠟⠉⠀⠀⡠⠊⠀⢠⠀⠀⠀⠀⠀⠀⠀⠀⣾⡄⠀⢠⣿⠔⠁⠀⢸
            ⠈⣿⣿⣿⣷⡀⠀⢻⣿⣿⡜⣿⣿⣷⡀⠈⢿⣿⡄⠀⠀⠈⠛⠿⣿⣿⣿⣷⣶⣶⣶⡶⠖⠉⠀⣀⣤⡶⠋⠀⣠⣶⡏⠀⠀⠀⠀⠀⠀⠀⢰⣿⣧⣶⣿⣿⠖⡠⠖⠁
            ⠀⣿⣿⣷⣌⡛⠶⣼⣿⣿⣷⣿⣿⣿⣿⡄⠈⢻⣷⠀⣄⡀⠀⠀⠀⠈⠉⠛⠛⠛⠁⣀⣤⣶⣾⠟⠋⠀⣠⣾⣿⡟⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⣿⣿⠷⠊⠀⢰⠀
            ⢰⣿⣿⠀⠈⢉⡶⢿⣿⣿⣿⣿⣿⣿⣿⣿⣆⠀⠙⢇⠈⢿⣶⣦⣤⣀⣀⣠⣤⣶⣿⣿⡿⠛⠁⢀⣤⣾⣿⣿⡿⠁⠀⠀⠀⠀⠀⠀⠀⣸⣿⡿⠿⠋⠙⠒⠄⠀⠉⡄
            ⣿⣿⡏⠀⠀⠁⠀⠀⠀⠉⠉⠙⢻⣿⣿⣿⣿⣷⡀⠀⠀⠀⠻⣿⣿⣿⣿⣿⠿⠿⠛⠁⠀⣀⣴⣿⣿⣿⣿⠟⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠰
             */
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
                rectObject.getProperties().put("Door", true); //PUERTA OCULTA
                rectObject.getProperties().put("Indestructible", false);

            } else if (type.equals("ActualDoor")) {
                cell.setTile(tiledMap.getTileSets().getTile(9));
                rectObject.getProperties().put("Door", true);
                rectObject.getProperties().put("KYS",true);
            } else {
                rectObject.getProperties().put("Indestructible", false);
                rectObject.getProperties().put("Pass-Through", true);
                cell.setTile(tiledMap.getTileSets().getTile(5));
            }
            collisionLayer.getObjects().add(rectObject);

            layerTile.setCell(x, y, cell);
        }
    }

    private boolean isCollisionTile(TiledMapTile tile) {
        // Define your logic to determine if a tile should be a collision tile
        // For example, checking a property of the tile
        return tile.getProperties().containsKey("collidable");
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
