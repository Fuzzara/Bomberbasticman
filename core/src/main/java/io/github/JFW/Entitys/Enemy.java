package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.System.SpriteBatchHandler;
import io.github.JFW.stateEnemy;

import java.util.Random;

public class Enemy extends Actor {

    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 23;

    //Enemy atributes
    private final boolean noclip; // Atraviesa muros ig
    private final int ai;
    private final int score;
    private final Vector2 position;
    private final float speed;

    //Sprites
    private final Sprite enemySprite;
    private final Texture enemyTexture;
    private final SpriteBatch batch;

    //Colission stuff
    private Map currentMap;
    private final Rectangle boundingBox;
    private final Player player;

    private final stateEnemy state;

    //Tal vez hacer esta clase abstracta y tener una clase por enemigo ?
    public Enemy(float speed, float x, float y, int score, int ai, boolean noclip, String texturepath, Map currentMap) {
        this.speed = speed;
        this.position = new Vector2(x, y);
        this.score = score;
        this.ai = ai;
        this.noclip = noclip;
        this.currentMap = currentMap;
        this.batch = SpriteBatchHandler.getBatch();;
        //por mientras, cambiar!
        this.enemyTexture = new Texture(texturepath);
        this.enemySprite = new Sprite(enemyTexture);
        this.enemySprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        this.enemySprite.setPosition(position.x, position.y);
        this.boundingBox = new Rectangle(position.x - BOUNDING_BOX_OFFSET, position.y - BOUNDING_BOX_OFFSET, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        player = Player.getInstance();

        this.state = new stateEnemy();
    }

    private boolean isCollision(float x, float y) {
        Rectangle enemyRect = new Rectangle(x, y, boundingBox.width, boundingBox.height);

        //solamente se necesita checkear esto si el enemigo puede atravesar muros
        if (noclip) {
            for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
                if (object instanceof RectangleMapObject rectObject) {
                    if (Boolean.TRUE.equals(rectObject.getProperties().get("Bomb"))) {
                        Rectangle rect = rectObject.getRectangle();
                        if (rect.overlaps(enemyRect)) {
                            //Choca con bomba, no puede atavesarlo!
                            return true;
                        }
                    }
                    if (Boolean.TRUE.equals(rectObject.getProperties().get("Indestructible"))) {
                        Rectangle rect = rectObject.getRectangle();
                        if (rect.overlaps(enemyRect)) {
                            //Choca con un muro indestructible
                            return true;
                        }
                    }
                }
            }
        } else {
            //Checkeo normal
            for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    if (rect.overlaps(enemyRect)) {
                        return true;
                    }
                }
            }
        }


        if (enemyRect.overlaps(player.getBoundingBox())) {
            System.out.println("Player hit by enemy!");
        }
        return false;
    }


    private void choosingDirection() {
        Random rand = new Random();
        int randomDirection = rand.nextInt(4);
        switch (randomDirection) {
            case 0:
                if (state.getCurrentState() != stateEnemy.State.DOWN) {
                    state.setCurrentState(stateEnemy.State.DOWN);
                }
                break;
            case 1:
                if (state.getCurrentState() != stateEnemy.State.UP) {
                    state.setCurrentState(stateEnemy.State.UP);
                }
                break;

            case 2:
                if (state.getCurrentState() != stateEnemy.State.RIGHT) {
                    state.setCurrentState(stateEnemy.State.RIGHT);
                }
                break;

            case 3:
                if (state.getCurrentState() != stateEnemy.State.LEFT) {
                    state.setCurrentState(stateEnemy.State.LEFT);
                }
                break;
        }
    }

    private void moveRandomly() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (state.getCurrentState() == stateEnemy.State.STUCK) {
            choosingDirection();
        }
        switch (state.getCurrentState()) {
            case LEFT:
                move(-speed * deltaTime, 0);
                break;
            case RIGHT:
                move(speed * deltaTime, 0);
                break;
            case UP:
                move(0, speed * deltaTime);
                break;
            case DOWN:
                move(0, -speed * deltaTime);
                break;
        }

    }

    private void move(float dx, float dy) {
        float newX = position.x + dx;
        float newY = position.y + dy;
        if (!isCollision(newX, newY)) {
            position.set(newX, newY);
            updateBoundingBox();
        } else {
            state.setCurrentState(stateEnemy.State.STUCK);
            choosingDirection();
        }
    }


    public void draw() {
        batch.begin();
        enemySprite.setPosition(position.x, position.y);
        enemySprite.draw(batch);
        batch.end();
    }

    public void update() {
        moveRandomly();
        updateBoundingBox();
        draw();
    }

    private void updateBoundingBox() {
        boundingBox.setPosition(position.x - BOUNDING_BOX_OFFSET, position.y - 34);
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    public void changeState(stateEnemy.State newState) {
        this.state.setCurrentState(newState);
    }
}

