package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import io.github.JFW.*;
import io.github.JFW.Entitys.Actors;
import io.github.JFW.Entitys.Enemy;
import io.github.JFW.Entitys.Player;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.MapEnv.MapSystem;

import java.util.ArrayList;
import java.util.Random;

public class Config {
    private Map currentMap;
    private MapSystem mapSystem;
    private Player player;
    private Actors actors;
    private final SpriteBatch batch;
    private int currentLevel;

    private float bonusLevelTimer = 30f;
    private float enemyRespawnTimer = 5f;
    private boolean isBonusLevel = false;
    private boolean soundPlayed = false;
    private SFXPlayer sfx = new SFXPlayer();
    GlobalAccess globalaccess;

    public Config(SpriteBatch batch) {
        this.batch = batch;
        this.currentLevel = 1;
        this.mapSystem = new MapSystem();
        globalaccess = GlobalAccess.getInstance();
        globalaccess.setConfig(this);
    }

    public void timerOutEnemies() {
        ArrayList<Enemy> enemies = actors.getEnemies();
        // Store old enemy positions before clearing
        ArrayList<Rectangle> oldPositions = new ArrayList<>();
        for (Enemy enemy : enemies) {
            oldPositions.add(new Rectangle(enemy.getBoundingBox()));
        }

        // Clear existing enemies
        enemies.clear();

        // Create new type 6 enemies at old positions
        for (Rectangle pos : oldPositions) {
            Enemy enemy = EnemyFactory.createEnemy(6, (int) pos.x + 24, (int) pos.y +24, currentMap, player.getSpeed());
            if (enemy != null) {
                actors.updateEnemies(enemy);
            }
        }

        // Create additional type 6 enemies in random positions
        int[] enemyTypes = getEnemyTypesForLevel(currentLevel);
        for (int i = 0; i < enemyTypes.length; i++) {
            int maxAttempts = 100; // Prevent infinite loops
            int attempts = 0;
            boolean StuckinEnvironment = true;
            Random rand = new Random();

            while (StuckinEnvironment && attempts < maxAttempts) {
                attempts++;
                int x = rand.nextInt((775 - 335) + 1) + 355;
                int y = rand.nextInt((385 - 50) + 1) + 50;
                Rectangle rect = new Rectangle(x, y, 34, 34);
                StuckinEnvironment = stuck(rect);

                if (!StuckinEnvironment) {
                    Enemy enemy = EnemyFactory.createEnemy(6, x, y, currentMap, player.getSpeed());
                    if (enemy != null) {
                        actors.updateEnemies(enemy);
                    }
                    break;
                }
            }
        }
    }

    // Rest of the class remains unchanged
    public int randomPowerUp() {
        Random rand = new Random();
        int x = rand.nextInt(7);
        while (Player.getInstance().hasPowerUp(statePlayer.PowerUpType.values()[x])) {
            x = rand.nextInt(7);
        }
        return x;
    }

    public Map setuplevel(int n) {
        currentLevel = n;
        soundPlayed = false;
        if (mapSystem == null) {
            mapSystem = new MapSystem();
        }

        currentMap = mapSystem.getMap(n);

        isBonusLevel = (n) % 5 == 0;
        if (isBonusLevel) {
            bonusLevelTimer = 30f;
            enemyRespawnTimer = 5f;
        }

        if (actors == null) {
            actors = new Actors();
            globalaccess.setActors(actors);
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
        return currentMap;
    }

    public boolean switchToNextLevel() {
        int nextLevel = currentLevel + 1;
        if (nextLevel < mapSystem.getMapCount()) {
            currentMap = setuplevel(nextLevel);
            if (player != null) {
                player.setMap(currentMap);
            }
            return true;
        }
        return false;
    }

    public boolean restartLevel() {
        int nextLevel = currentLevel;
        if (nextLevel < mapSystem.getMapCount()) {
            currentMap = setuplevel(nextLevel);
            if (player != null) {
                player.setMap(currentMap);
            }
            return true;
        }
        return false;
    }

    public void clearSound() {
        if (currentMap != null && player != null) {
            ArrayList<Enemy> enemies = actors.getEnemies();
            if (enemies.isEmpty() && !soundPlayed) {
                sfx.playSFX("sound/clear.mp3");
                soundPlayed = true;
            }
        }
    }

    public boolean isLevelCompleted() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            return true;
        }

        clearSound();

        if (isBonusLevel && bonusLevelTimer <= 0) {
            return true;
        }

        if (currentMap != null && player != null) {
            for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    if (rectObject.getProperties().get("Door") != null &&
                        rectObject.getProperties().get("KYS") != null) {
                        Rectangle doorRect = rectObject.getRectangle();
                        Rectangle playerRect = player.getBoundingBox();
                        ArrayList<Enemy> enemies = actors.getEnemies();
                        if (doorRect.overlaps(playerRect) && enemies.isEmpty()) {
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
        if (baseEnemies > 10) baseEnemies = 10;

        if (level <= 1) {
            enemyTypes.add(0);
            enemyTypes.add(0);
        } else if (level <= 2) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(1));
            }
            enemyTypes.add(1);
        } else if (level <= 5) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(2));
            }
            enemyTypes.add(2);
        } else if (level <= 7) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(3));
            }
            enemyTypes.add(3);
        } else if (level <= 10) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(4));
            }
            enemyTypes.add(4);
        } else if (level <= 13) {
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(5));
            }
            enemyTypes.add(5);
        } else if (level >= 14) {
            baseEnemies = level;
            for (int i = 0; i < baseEnemies; i++) {
                enemyTypes.add(rand.nextInt(6));
            }
            enemyTypes.add(6);
        }

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

            if (isBonusLevel) {
                bonusLevelTimer -= deltaTime;
                enemyRespawnTimer -= deltaTime;

                if (enemyRespawnTimer <= 0) {
                    setupenemies(currentLevel);
                    enemyRespawnTimer = 5f;
                }
            }
        }

        actors.update(state);
    }

    public Actors getActors() {
        return actors;
    }

    public void dispose() {
        if (mapSystem != null) {
            mapSystem.dispose();
        }
    }
}
