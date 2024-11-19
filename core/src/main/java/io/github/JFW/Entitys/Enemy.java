package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.Scoreboard;
import io.github.JFW.System.Animator;
import io.github.JFW.System.SpriteBatchHandler;
import io.github.JFW.stateEnemy;

import java.util.ArrayList;
import java.util.Random;

public class Enemy extends Actor {

    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 23;

    //Enemy atributes
    private final boolean noclip; // Atraviesa muros ig
    private final int ai;
    private final Vector2 position;
    private final float speed;
    private final int score;

    //Sprites
    private final SpriteBatch batch;
    private Animator animation;

    //Colission stuff
    private Map currentMap;
    private final Rectangle boundingBox;
    private final Player player;
    //Ai stuff
    private EnemyAlgorithm algo;
    private stateEnemy.State nextDirection;

    private Scoreboard scoreboard;

    private final stateEnemy state;
    private ShapeRenderer shapeRenderer;

    private float directionChangeTimer = 0;
    private static final float DIRECTION_CHANGE_INTERVAL = 2.0f; // Change direction every 2 seconds

    //Tal vez hacer esta clase abstracta y tener una clase por enemigo ?
    public Enemy(float speed, float x, float y, int score, int ai, boolean noclip, Map currentMap, Animator animation) {
        this.speed = speed;
        this.position = new Vector2(x, y);
        this.score = score;
        this.ai = ai;
        this.noclip = noclip;
        this.currentMap = currentMap;
        this.animation = animation;
        this.batch = SpriteBatchHandler.getBatch();

        shapeRenderer = new ShapeRenderer();

        //por mientras, cambiar!
        this.boundingBox = new Rectangle(position.x, position.y, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        player = Player.getInstance();

        this.state = new stateEnemy();
        this.scoreboard = Scoreboard.getInstance();
    }

    // Add getBoundingBox method to expose the enemy's collision box
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public boolean isCollision(float x, float y) {
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
            //System.out.println("Player hit by enemy!");
            scoreboard.removeLife();

        }
        return false;
    }

    private void choosingDirection() {
        Random rand = new Random();
        int randomDirection = rand.nextInt(4);
        switch (randomDirection) {
            case 0:
                state.setCurrentState(stateEnemy.State.DOWN);
                break;
            case 1:
                state.setCurrentState(stateEnemy.State.UP);
                break;
            case 2:
                state.setCurrentState(stateEnemy.State.RIGHT);
                break;
            case 3:
                state.setCurrentState(stateEnemy.State.LEFT);
                break;
        }
    }

    private void moveRandomly() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        directionChangeTimer += deltaTime;

        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL) {
            choosingDirection();
            directionChangeTimer = 0;
        }

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

    private void chasing(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        Vector2 PlayerPosition = player.getPosition();
        float x = PlayerPosition.x - position.x;
        float y = PlayerPosition.y - position.y;
        stateEnemy.State nextDirection = closestDirectiontoPlayer(x,y,PlayerPosition);
        boolean valid = ValidDirection(nextDirection,deltaTime,state.getCurrentState());
        if (!valid){
            nextDirection = OtherDirections(nextDirection,deltaTime);
        }
        if (nextDirection != stateEnemy.State.STUCK){movetoNextDirection(nextDirection,deltaTime);}
    }

    int getScore(){
        return score;
    }

    private stateEnemy.State closestDirectiontoPlayer(float x, float y, Vector2 PlayerPosition){
        if (Math.abs(x) > Math.abs(y)){ // si la distancia es mas grande horizontalmente
            if (PlayerPosition.x < position.x ){
                return stateEnemy.State.LEFT;
            }
            else{
                return stateEnemy.State.RIGHT;
            }
        }
        else{
            if (PlayerPosition.y < position.y){
                return stateEnemy.State.DOWN;
            }
            else{
                return stateEnemy.State.UP;
            }
        }
    }

    private boolean ValidDirection(stateEnemy.State nextDirection, float deltaTime, stateEnemy.State lastDirection){
        switch(nextDirection){
            case LEFT:
                return !isCollision(position.x + -speed * deltaTime, position.y);
            case RIGHT:
                return !isCollision(position.x + speed * deltaTime, position.y);
            case DOWN:
                return !isCollision(position.x, position.y + -speed * deltaTime);
            case UP:
                return !isCollision(position.x, position.y + speed * deltaTime);
        }
        return false;
    }

    private stateEnemy.State OtherDirections(stateEnemy.State nextDirection, float deltaTime){
        ArrayList<stateEnemy.State> Directions = new ArrayList<stateEnemy.State>();
        switch(nextDirection){
            case LEFT:
                Directions.add(stateEnemy.State.DOWN);
                Directions.add(stateEnemy.State.UP);
                break;
            case RIGHT:
                Directions.add(stateEnemy.State.DOWN);
                Directions.add(stateEnemy.State.UP);
                break;
            case DOWN:
                Directions.add(stateEnemy.State.LEFT);
                Directions.add(stateEnemy.State.RIGHT);
                break;
            case UP:
                Directions.add(stateEnemy.State.LEFT);
                Directions.add(stateEnemy.State.RIGHT);
                break;
        }
        for (stateEnemy.State direction: Directions){
            if (ValidDirection(direction,deltaTime,state.getCurrentState())){
                return direction;
            }
        }
        return stateEnemy.State.STUCK;
    }

    private void movetoNextDirection(stateEnemy.State nextDirection, float deltaTime){
        switch(nextDirection){
            case LEFT:
                move(-speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.LEFT);
                break;
            case RIGHT:
                move(speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.RIGHT);
                break;
            case DOWN:
                move(0, -speed * deltaTime);
                state.setCurrentState(stateEnemy.State.DOWN);
                break;
            case UP:
                move(0, speed * deltaTime);
                state.setCurrentState(stateEnemy.State.UP);
                break;
        }
    }
    private void movetoNextDirection(){
        float deltaTime = Gdx.graphics.getDeltaTime();
        switch(nextDirection){
            case LEFT:
                move(-speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.LEFT);
                break;
            case RIGHT:
                move(speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.RIGHT);
                break;
            case DOWN:
                move(0, -speed * deltaTime);
                state.setCurrentState(stateEnemy.State.DOWN);
                break;
            case UP:
                move(0, speed * deltaTime);
                state.setCurrentState(stateEnemy.State.UP);
                break;
            case STUCK:
                moveRandomly();
        }
    }

    //la posicion del enemigo y del jugador en la matriz se vuelven menos precisas mientras se mueven
    //si se arregla eso el algoritmo ya queda

    private void algorithm(){
        algo = new EnemyAlgorithm(this,currentMap);
        this.nextDirection = algo.optimalDirection();
        movetoNextDirection();

    }

    public int[] getCenterPositions(){
        // enemy center position

        int centerTileX = (int) ((position.x + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        int centerTileY = (int) ((position.y + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());//cambiar esto a sprite height maybe

        //player center position

        Vector2 playerposition = player.getPosition();
        int centerTileXP = (int) ((playerposition.x + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        int centerTileYP = (int) ((playerposition.y + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        return new int[]{centerTileX,centerTileY,centerTileXP,centerTileYP};
    }

    public void draw() {
        batch.begin();
        batch.draw(animation.getFrame(), position.x, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);
        batch.end();
    }

    public void update() {
        chasing();
        //moveRandomly();
        updateBoundingBox();
        draw();
    }

    private void updateBoundingBox() {
        System.out.println(position.x + " " + position.y);
        boundingBox.setPosition(position.x, position.y-24);
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    public void changeState(stateEnemy.State newState) {
        this.state.setCurrentState(newState);
    }
    public Vector2 getPosition() {
        return position;
    }

    public float getSpeed(){
        return speed;
    }
}
