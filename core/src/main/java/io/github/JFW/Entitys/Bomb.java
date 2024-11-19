package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

import io.github.JFW.GlobalAccess;
import io.github.JFW.Scoreboard;
import io.github.JFW.statePlayer.PowerUpType;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.System.Animator;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class Bomb extends Actor{

    private boolean enemiesSetup;
    private boolean tileDestroyed;
    private float tileDestroyedTimer;
    private boolean isDoor;
    private boolean destroyedThisFrame;

    private final SpriteBatch batch;
    private int EXPLOSION_RANGE = 1;

    private final Actors actors;

    private final Vector2 position;
    private float width = 48;
    private float height = 48;

    private Animator idleAnimator;
    private Animator explosionAnimator;
    private Animator currentAnimator;

    private float detonationTime;
    private long currentTime;
    private boolean exploded;
    private float explosionTimer;

    private SFXPlayer sfx;

    private Map tiledMap;

    //Animation!
    private Animator explosionHorizontal;
    private Animator explosionVertical;
    private Animator explosionUp;
    private Animator explosionDown;
    private Animator explosionLeft;
    private Animator explosionRight;

    //DEBUG
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Rectangle horizHB;
    private Rectangle vertHB;
    private Rectangle destroyLocation;

    private Rectangle explosionHitbox;

    private Rectangle bombTileHitbox;
    private boolean playerInBomb = true;
    private boolean placedCollision = false;

    private Player player;
    private Scoreboard scoreboard;

    private boolean doorToggle = false;

    public Bomb(float x ,float y, Actors actors, Map map){
        this.batch = SpriteBatchHandler.getBatch();
        this.position = new Vector2(x-24,y-24); //YA TIENE OFFSET
        this.exploded = false;
        this.explosionTimer = 0f;
        this.tileDestroyed = false;
        this.tileDestroyedTimer = 0f;
        this.isDoor = false;
        this.destroyedThisFrame = false;

        this.tiledMap = map;
        this.currentTime = System.nanoTime();
        this.detonationTime = currentTime + 2000000000; //2 segundos
        this.actors = actors;
        this.scoreboard = Scoreboard.getInstance();

        //Animations!
        makeAnimation();

        this.currentAnimator = idleAnimator;

        bombTileHitbox = new Rectangle(
            position.x,
            position.y,
            width,
            height
        );

        this.sfx = new SFXPlayer();

        actors.updateBombs(this);

        player = Player.getInstance();

        if (player.hasPowerUp(PowerUpType.SUN)){
            EXPLOSION_RANGE++;
        }
    }

    public void makeAnimation(){
        this.idleAnimator = new Animator("bombSpriteSheet.png", 8, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionAnimator = new Animator("bombSpriteSheet.png", 8, 1, 3, 7, 0.15f, Animation.PlayMode.LOOP_PINGPONG);

        this.explosionUp = new Animator("bombExplosion.png", 30, 1, 0, 4, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionLeft = new Animator("bombExplosion.png", 30, 1, 5, 9, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionDown = new Animator("bombExplosion.png", 30, 1, 10, 14, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionRight = new Animator("bombExplosion.png", 30, 1, 15, 19, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionHorizontal = new Animator("bombExplosion.png", 30, 1, 20, 24, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionVertical = new Animator("bombExplosion.png", 30, 1, 25, 29, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
    }

    public void detonatorExplode(){
        exploded = true;
    }

    public void draw(){
        batch.begin();
        batch.draw(currentAnimator.getFrame(), position.x+24, position.y+24, width, height);
        if (exploded) {
            debugDraw();
        }
        batch.end();
    }

    public void logic() {
        this.currentTime = System.nanoTime();
        if (!bombTileHitbox.overlaps(player.getBoundingBox())) {
            playerInBomb = false;
        }
        if (!playerInBomb && !placedCollision) {
            int tileXX = (int) ((position.x + 48) / tiledMap.getCollisionLayer().getTileWidth());
            int tileYY = (int) ((position.y + 48) / tiledMap.getCollisionLayer().getTileHeight());
            placedCollision = true;
            tiledMap.addSingleCollision(tileXX, tileYY, "Bomb");
        }
        if (currentTime >= detonationTime && !exploded) {
            exploded = true;
            sfx.playSFX("sound/explodeBomb.mp3");
            currentAnimator = explosionAnimator;
        }

        if (exploded) {
            processExplosion();
        }

    }

    private void processExplosionDirection(int centerTileX, int centerTileY, int deltaX, int deltaY) {
        for (int i = 0; i <= EXPLOSION_RANGE; i++) {
            int tileX = centerTileX + i * deltaX;
            int tileY = centerTileY + i * deltaY;

            Rectangle tileRect = new Rectangle(
                tileX * tiledMap.getCollisionLayer().getTileWidth(),
                tileY * tiledMap.getCollisionLayer().getTileHeight(),
                tiledMap.getCollisionLayer().getTileWidth(),
                tiledMap.getCollisionLayer().getTileHeight()
            );

            // Verifica colision con obstaculos
            for (RectangleMapObject obstacle : tiledMap.getObstaclesMO()) {
                if (obstacle.getRectangle().overlaps(tileRect)) {
                    if (Boolean.TRUE.equals(obstacle.getProperties().get("Indestructible"))&& !obstacle.getProperties().containsKey("Door")) return;

                    // Mark tile as destroyed and start timer
                    tileDestroyed = true;
                    tileDestroyedTimer = 0f;
                    destroyedThisFrame = true;

                    // Remove collision immediately
                    tiledMap.removeSingleCollision(centerTileX, centerTileY);
                    tiledMap.removeSingleCollision(tileX, tileY);

                    // Check if it's a door
                    if (Boolean.FALSE.equals(obstacle.getProperties().get("Door")) && !doorToggle && !isDoor) {
                        GlobalAccess ga = GlobalAccess.getInstance();
                        ga.getConfig().setupenemies(ga.getConfig().getCurrentLevel());
                        doorToggle = true;
                    }
                    return;
                }
            }
        }
    }

    public void processExplosion() {
        explosionTimer += Gdx.graphics.getDeltaTime();
        float tileWidth = tiledMap.getCollisionLayer().getTileWidth();
        float tileHeight = tiledMap.getCollisionLayer().getTileHeight();

        // Reset destroyedThisFrame at start of frame
        destroyedThisFrame = false;

        // bomb center position
        int centerTileX = (int) ((position.x + width / 2) / tileWidth);
        int centerTileY = (int) ((position.y + height / 2) / tileHeight);

        // Calculate effective explosion ranges in each direction
        int rangeLeft = calculateEffectiveRange(centerTileX, centerTileY, -1, 0);
        int rangeRight = calculateEffectiveRange(centerTileX, centerTileY, 1, 0);
        int rangeUp = calculateEffectiveRange(centerTileX, centerTileY, 0, 1);
        int rangeDown = calculateEffectiveRange(centerTileX, centerTileY, 0, -1);

        // Adjust hitboxes based on effective ranges
        horizHB = new Rectangle(
            position.x + width / 2 - tileWidth * rangeLeft,
            (position.y + height / 2 - tileHeight / 2) + 10,
            tileWidth * (rangeLeft + rangeRight),
            tileHeight - 20
        );

        vertHB = new Rectangle(
            (position.x + width / 2 - tileWidth / 2) + 10,
            position.y + height / 2 - tileHeight * rangeDown,
            tileWidth - 20,
            tileHeight * (rangeUp + rangeDown)
        );

        //Detecta si el jugador esta en la explosion
        if (!player.hasPowerUp(PowerUpType.QUESTION_MARK)) {
            if (!player.hasPowerUp(PowerUpType.FIRE_MAN)) {
                if (horizHB.overlaps(player.getBoundingBox()) || vertHB.overlaps(player.getBoundingBox())) {
                    scoreboard.removeLife();
                }
            }
        }
       /* if ((( horizHB.overlaps(player.getBoundingBox()) && (!player.hasPowerUp(PowerUpType.FIRE_MAN)) || !player.hasPowerUp(PowerUpType.QUESTION_MARK))
            || (vertHB.overlaps(player.getBoundingBox()) && (!player.hasPowerUp(PowerUpType.FIRE_MAN)) || !player.hasPowerUp(PowerUpType.QUESTION_MARK)))) {
            scoreboard.removeLife();
        }*/

        // Check for enemy collisions with explosion
        ArrayList<Enemy> enemies = actors.getEnemies();
        Iterator<Enemy> enemyIterator = enemies.iterator();
        int multiplier = 1;
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            Rectangle enemyBounds = enemy.getBoundingBox();
            if (horizHB.overlaps(enemyBounds) || vertHB.overlaps(enemyBounds)) {
                int score = (enemy.getScore()*multiplier);
                scoreboard.addScore(score); //implementar multiplier
                multiplier++;
                enemyIterator.remove(); // Remove enemy if hit by explosion
            }
        }

        ArrayList<PowerUp> powerups = actors.getPowerUps();
        Iterator<PowerUp> powerupIterator = powerups.iterator();
        while (powerupIterator.hasNext()) {
            PowerUp powerUP = powerupIterator.next();
            Rectangle powerUPColl = powerUP.getBoundingBox();
            if (horizHB.overlaps(powerUPColl) || vertHB.overlaps(powerUPColl)) {
                if(!powerUP.getInvincibility()){
                    powerupIterator.remove(); // Remove enemy if hit by explosion
                }
            }
        }

        // Process explosion in each direction first
        processExplosionDirection(centerTileX, centerTileY, 0, 0); // Center
        processExplosionDirection(centerTileX, centerTileY, -1, 0); // Left
        processExplosionDirection(centerTileX, centerTileY, 1, 0);  // Right
        processExplosionDirection(centerTileX, centerTileY, 0, -1); // Down
        processExplosionDirection(centerTileX, centerTileY, 0, 1);  // Up

        // Chain reaction with other bombs
        ArrayList<Bomb> bombs = actors.getBombs();
        for (Bomb bomb : bombs) {
            if (bomb != this) {
                if (horizHB.overlaps(bomb.bombTileHitbox) || vertHB.overlaps(bomb.bombTileHitbox)) {
                    bomb.detonatorExplode();
                }
            }
        }

        if (explosionTimer >= 1.36f) { // Wait for animation to finish
            actors.removeBombs(this);
        }
    }

    private int calculateEffectiveRange(int centerTileX, int centerTileY, int deltaX, int deltaY) {
        for (int i = 1; i <= EXPLOSION_RANGE; i++) {
            int tileX = centerTileX + i * deltaX;
            int tileY = centerTileY + i * deltaY;

            Rectangle tileRect = new Rectangle(
                tileX * tiledMap.getCollisionLayer().getTileWidth(),
                tileY * tiledMap.getCollisionLayer().getTileHeight(),
                tiledMap.getCollisionLayer().getTileWidth(),
                tiledMap.getCollisionLayer().getTileHeight()
            );

            // Check for obstacles
            for (RectangleMapObject obstacle : tiledMap.getObstaclesMO()) {
                if (Boolean.TRUE.equals(obstacle.getProperties().get("Door"))){
                    isDoor = true;
                }
                if (obstacle.getRectangle().overlaps(tileRect)) {
                    // If it's indestructible (and not a door), stop before it
                    if (Boolean.TRUE.equals(obstacle.getProperties().get("Indestructible"))
                        && !obstacle.getProperties().containsKey("Door")) {
                        return i - 1;
                    }
                    // For destructible objects, include this tile but stop here
                    tiledMap.removeSingleCollision(tileX, tileY);
                    return i;
                }
            }
        }
        return EXPLOSION_RANGE; // No obstacles in range
    }

    public void debugDraw(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (horizHB != null && vertHB != null) {

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(horizHB.x, horizHB.y, horizHB.width, horizHB.height);
            shapeRenderer.rect(vertHB.x, vertHB.y, vertHB.width, vertHB.height);
            if (destroyLocation != null) {
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(destroyLocation.x, destroyLocation.y, destroyLocation.width, destroyLocation.height);
            }

        }

        if(explosionHitbox != null){
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(explosionHitbox.x, explosionHitbox.y, explosionHitbox.width, explosionHitbox.height);
        }

        shapeRenderer.end();
    }

    public void update(){
        logic();
        draw();
    }
}
