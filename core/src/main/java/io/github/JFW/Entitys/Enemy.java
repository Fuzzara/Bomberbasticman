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

import java.util.Random;

public class Enemy extends Actor{

    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 23;

    //Enemy atributes
    private boolean noclip; // Atraviesa muros ig
    private int ai;
    private int score;
    private Vector2 position;
    private float speed;
    private State currentState;
    private State lastState;

    //Sprites
    private Sprite enemySprite;
    private Texture enemyTexture;
    private SpriteBatch batch;

    //Colission stuff
    private Map currentMap;
    private Rectangle boundingBox;
    private Player player;

    //Tal vez hacer esta clase abstracta y tener una clase por enemigo ?
    public Enemy(float speed, float x, float y, int score, int ai, Map currentMap, SpriteBatch batch){
        this.speed = speed;
        this.position = new Vector2(x,y);
        this.score = score;
        this.ai = ai;
        this.currentState = State.STUCK;
        this.lastState = State.STUCK;
        this.currentMap = currentMap;
        this.batch = batch;
        //por mientras, cambiar!
        this.enemyTexture = new Texture("bombwip.png");
        this.enemySprite = new Sprite(enemyTexture);
        this.enemySprite.setSize(SPRITE_WIDTH,SPRITE_HEIGHT);
        this.enemySprite.setPosition(position.x,position.y);
        this.boundingBox = new Rectangle(position.x - BOUNDING_BOX_OFFSET,position.y - BOUNDING_BOX_OFFSET,BOUNDING_BOX_SIZE,BOUNDING_BOX_SIZE);
        player = Player.getInstance();

    }

    private enum State {
        UP, DOWN, LEFT, RIGHT, STUCK, CHASING, DEAD
    }
    private void  choosingDirection(){
        Random rand = new Random();
        int x = rand.nextInt((4-1)+1)+1;
        switch(x){
            case 1:
                if (lastState != State.DOWN) {
                    currentState = State.DOWN;
                    break;
                }
            case 2:
                if (lastState != State.UP){
                    currentState = State.UP;
                    break;
                }

            case 3:
                if (lastState != State.RIGHT){
                    currentState = State.RIGHT;
                    break;
                }

            case 4:
                if (lastState != State.LEFT){
                    currentState = State.LEFT;
                    break;
                }

        }
    }

    private void moveRandomly(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (currentState == State.STUCK){
            choosingDirection();
        }
        switch (currentState) {
            case LEFT:
                move(-speed * deltaTime, 0,State.LEFT);
                break;
            case RIGHT:
                move(speed * deltaTime, 0,State.RIGHT);
                break;
            case UP:
                move(0, speed * deltaTime,State.UP);
                break;
            case DOWN:
                move(0, -speed * deltaTime,State.DOWN);
                break;
        }

    }
    private void move(float dx, float dy,State LS) {
        float newX = position.x + dx;
        float newY = position.y + dy;
        if (!isCollision(newX, newY)) {
            position.set(newX, newY);
            updateBoundingBox();
        }
        else{
            currentState = State.STUCK;
            lastState = LS;
        }
    }

    private boolean isCollision(float x, float y) {
        Rectangle enemyRect = new Rectangle(x, y, boundingBox.width, boundingBox.height);
        if(enemyRect.overlaps(player.getBoundingBox())){
            Gdx.app.debug("Enemy hit ","player");
        }
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(enemyRect)) {
                    //Gdx.app.debug("Enemy", "Collision detected at (" + x + ", " + y + ")");
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(){
        batch.begin();
        enemySprite.setPosition(position.x,position.y);
        enemySprite.draw(batch);
        batch.end();
    }

    public void update(){
        moveRandomly();
        updateBoundingBox();
        draw();
    }
    private void updateBoundingBox(){
        boundingBox.setPosition(position.x-BOUNDING_BOX_OFFSET, position.y-34);
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

}

