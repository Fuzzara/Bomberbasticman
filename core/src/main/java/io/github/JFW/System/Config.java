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
    private float bonusLevelTimer = 30f; // 30 seconds for bonus levels
    private float enemyRespawnTimer = 5f; // 5 seconds between enemy respawns
    private boolean isBonusLevel = false;

    public Config(SpriteBatch batch) {
        this.batch = batch;
        this.currentLevel = 1;
        this.mapSystem = new MapSystem();
    }

    public Map setuplevel(int n) {
        currentLevel = n;
        if (mapSystem == null) {
            mapSystem = new MapSystem();
        }

        currentMap = mapSystem.getMap(n);

        // Check if this is a bonus level
        isBonusLevel = (n) % 5 == 0;
        if (isBonusLevel) {
            bonusLevelTimer = 30f; // Reset bonus level timer
            enemyRespawnTimer = 5f; // Reset enemy respawn timer
            Gdx.app.debug("Config", "Bonus level started! Timer: " + bonusLevelTimer);
        }

        if (actors == null) {
            actors = new Actors();
            player = Player.getInstance(actors, currentMap);
            actors.setPlayer(player);
            setupenemies(currentLevel);
        } else {
            actors.clearActors();
            player = Player.getInstance(actors, currentMap);
            player.setMap(currentMap);
            actors.setPlayer(player);
            setupenemies(currentLevel);
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
                player.setMap(currentMap);
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

        // For bonus levels, check if time is up
        if (isBonusLevel && bonusLevelTimer <= 0) {
            Gdx.app.debug("Config", "Bonus level time up!");
            return true;
        }

        if (currentMap != null && player != null) {
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
        return false;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isBonusLevel() {
        return isBonusLevel;
    }

    public float getBonusLevelTimer() {
        return bonusLevelTimer;
    }

    private int[] getEnemyTypesForLevel(int level) {
        Random rand = new Random();
        ArrayList<Integer> enemyTypes = new ArrayList<>();

        int baseEnemies = 1 + level;
        if(baseEnemies>10) baseEnemies=10;

        // Nivel 1
        if (level <= 1) {
            enemyTypes.add(0);
            enemyTypes.add(0);
        }
        // Nivel 2
        else if (level <= 2) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(1));
            }
            enemyTypes.add(1);
        }
        // Nivel 3 - 5
        else if (level <= 5) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(2));
            }
            enemyTypes.add(2);
        }
        // Nivel 6-7
        else if (level <= 7) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(3));
            }
            enemyTypes.add(3);
        }
        // Nivel 8-10
        else if (level <= 10) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(4));
            }
            enemyTypes.add(4);
        }
        // Nivel 11-13
        else if (level <= 13) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(5));
            }
            enemyTypes.add(5);
        }
        // Nivel 14
        else if (level >= 14) {
            baseEnemies = level;
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(6));
            }
            enemyTypes.add(6);
        }

        // Convert ArrayList to array
        int[] result = new int[enemyTypes.size()];
        for (int i = 0; i < enemyTypes.size(); i++) {
            result[i] = enemyTypes.get(i);
        }

        return result;
    }

    public void setupenemies(int level) {
        int[] enemyTypes = getEnemyTypesForLevel(level);

        for (int enemyType : enemyTypes) {
            boolean StuckinEnvironment = true;
            Random rand = new Random();
            while (StuckinEnvironment) {
                int x = rand.nextInt((775 - 335) + 1) + 355;
                int y = rand.nextInt((385 - 50) + 1) + 50;
                Rectangle rect = new Rectangle(x, y, 34, 34);
                StuckinEnvironment = stuck(rect);
                if (!StuckinEnvironment) {
                    Enemy enemy = EnemyFactory.createEnemy(enemyType, x, y, currentMap, player.getSpeed());
                    if (enemy != null) {
                        actors.updateEnemies(enemy);
                    }
                }
            }
        }
        Gdx.app.debug("Config", "Enemies setup complete for level " + currentLevel + " with " + enemyTypes.length + " enemies");
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
        if (state == GameScreen.State.running) {
            float deltaTime = Gdx.graphics.getDeltaTime();

            // Handle bonus level timers
            if (isBonusLevel) {
                bonusLevelTimer -= deltaTime;
                enemyRespawnTimer -= deltaTime;

                // Respawn enemies every 5 seconds
                if (enemyRespawnTimer <= 0) {
                    setupenemies(currentLevel);
                    enemyRespawnTimer = 5f; // Reset timer
                    Gdx.app.debug("Config", "Respawning enemies in bonus level!");
                }
            }
        }

        actors.update(state);
    }

    public void dispose() {
        if (mapSystem != null) {
            mapSystem.dispose();
        }
    }
}
