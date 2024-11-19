package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import io.github.JFW.EnemyFactory;
import io.github.JFW.Entitys.Actors;
import io.github.JFW.Entitys.Enemy;
import io.github.JFW.Entitys.Player;
import io.github.JFW.GameScreen;
import io.github.JFW.Main;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.MapEnv.MapSystem;

import java.util.Random;
import java.util.ArrayList;

public class Config {

    private Map currentMap;
    private MapSystem mapSystem;
    private Player player;
    private Actors actors;
    private final SpriteBatch batch;
    private int currentLevel;

    public Config(SpriteBatch batch) {
        this.batch = batch;
        this.currentLevel = 0;
        this.mapSystem = new MapSystem();
    }

    public Map setuplevel(int n) {
        currentLevel = n;
        if (mapSystem == null) {
            mapSystem = new MapSystem();
        }

        // Get the new map (this will handle disposal of the old map)
        currentMap = mapSystem.getMap(n);

        if (actors == null) {
            actors = new Actors();
            player = Player.getInstance(actors, currentMap);
            actors.setPlayer(player);
            setupenemies(1);
        } else {
            actors.clearActors();
            // Update player's map reference and reset for new level
            player = Player.getInstance(actors, currentMap);
            player.setMap(currentMap); // Explicitly update player's map reference
            actors.setPlayer(player);
            setupenemies(1);
        }
        currentMap.setActors(actors);
        Gdx.app.debug("Config", "Level " + n + " setup complete");
        return currentMap;
    }

    public boolean switchToNextLevel() {
        int nextLevel = currentLevel + 1;
        if (nextLevel < mapSystem.getMapCount()) {
            Gdx.app.debug("Config", "Switching to level " + nextLevel);

            // Setup next level and ensure player's map reference is updated
            currentMap = setuplevel(nextLevel);
            if (player != null) {
                player.setMap(currentMap); // Ensure player has the correct map reference
            }

            return true;
        }
        Gdx.app.debug("Config", "No more levels available, restarting from level 0");
        return false;
    }

    public boolean isLevelCompleted() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
            return true;
        }
        return false;
        /*if (currentMap != null && player != null) {
            // Check if player has reached the door and all enemies are defeated
            for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    if (rectObject.getProperties().get("Door") != null &&
                        rectObject.getProperties().get("KYS") != null) {
                        Rectangle doorRect = rectObject.getRectangle();
                        Rectangle playerRect = player.getBoundingBox();
                        // Check if player is at the door and there are no enemies left
                        ArrayList<Enemy> enemies = actors.getEnemies();
                        if (doorRect.overlaps(playerRect) && enemies.isEmpty()) {
                            Gdx.app.debug("Config", "Level " + currentLevel + " completed!");
                            return true;
                        }
                    }
                }
            }
        }
        return false;*/
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setupenemies(int n) {
        for (int i = 0; i < n; i++) {
            boolean StuckinEnvironment = true;
            Random rand = new Random();
            while (StuckinEnvironment) {
                int x = rand.nextInt((775 - 335) + 1) + 355;
                int y = rand.nextInt((385 - 50) + 1) + 50;
                Rectangle rect = new Rectangle(x, y, 34, 34);
                StuckinEnvironment = stuck(rect);
                if (!StuckinEnvironment) {
                    Enemy test = EnemyFactory.createEnemy(1, x, y, currentMap, player.getSpeed());
                    //Enemy test2 = EnemyFactory.createEnemy(6, x, y, currentMap, player.getSpeed());
                    //Enemy test3 = EnemyFactory.createEnemy(5, x, y, currentMap, player.getSpeed());
                    //Enemy test4 = EnemyFactory.createEnemy(4, x, y, currentMap, player.getSpeed());
                    //Enemy test5 = EnemyFactory.createEnemy(3, x, y, currentMap, player.getSpeed());
                    //Enemy test6 = EnemyFactory.createEnemy(2, x, y, currentMap, player.getSpeed());
                    //Enemy test7 = EnemyFactory.createEnemy(1, x, y, currentMap, player.getSpeed());
                   actors.updateEnemies(test);
                    //actors.updateEnemies(test2);
                    //actors.updateEnemies(test3);
                    //actors.updateEnemies(test4);
                    //actors.updateEnemies(test5);
                    //actors.updateEnemies(test6);
                    //actors.updateEnemies(test7);
                }
            }
        }
        Gdx.app.debug("Config", "Enemies setup complete for level " + currentLevel);
    }

    public boolean stuck(Rectangle enemyRect) {
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(enemyRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void runlevel(GameScreen.State state) {
        actors.update(state);
    }

    public void dispose() {
        if (mapSystem != null) {
            mapSystem.dispose();
        }
    }
}
