package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.MapObject;

public class Player extends Actor {
    private static Player instance; // Singleton instance
    private Actors actors;

    // Constants
    private static final int INITIAL_HP = 3;
    private static final float INITIAL_SPEED = 70f;
    private static final Vector2 INITIAL_POSITION = new Vector2(96, 630);
    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 23;

    // Stats
    private int hp;
    private boolean detonator;
    private long Timeuntilnextbomb;

    // Position and movement
    private Vector2 position;
    private float speed;
    private InputHandler inputHandler;

    private Sprite bomberSprite;
    private SpriteBatch batch;

    //Bomb stuff
    private BombManager bombManager;

    //Animations
    private final Animator upAnimator;
    private final Animator downAnimator;
    private final Animator leftAnimator;
    private final Animator rightAnimator;
    private Animator currentAnimator;
    private Animator deathAnimator;
    private Animator winAnimator;

    // Collision and bounding box
    private Rectangle boundingBox;
    private ShapeRenderer shapeRenderer;
    private Map currentMap;

    //Sound
    private Sound walkSound;
    private float walkSoundTime;

    //States
    statePlayer state;

    private Player(SpriteBatch batch, Actors actors, Map currentMap) { // Make constructor private
        this.batch = batch;
        this.actors = actors;
        this.currentMap = currentMap;
        this.hp = INITIAL_HP;
        this.speed = INITIAL_SPEED;
        this.position = new Vector2(INITIAL_POSITION);
        this.state = new statePlayer();
        this.inputHandler = new InputHandler();

        this.actors = actors;

        this.walkSound = Gdx.audio.newSound(Gdx.files.internal("sound/Walking-1.mp3"));

        // Sprite and rendering
        Texture bomberTexture = new Texture("bomberTexture.png");
        this.bomberSprite = new Sprite(bomberTexture);
        this.bomberSprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        this.bomberSprite.setPosition(position.x, position.y);

        this.bombManager = new BombManager(batch, actors, currentMap);

        this.boundingBox = new Rectangle(position.x - BOUNDING_BOX_OFFSET, position.y - BOUNDING_BOX_OFFSET, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        this.shapeRenderer = new ShapeRenderer();

        this.downAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.rightAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 3, 5, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.upAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 6, 8, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.leftAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 9, 11, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.deathAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 12, 18, 0.3f, Animation.PlayMode.NORMAL);
        this.winAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 19, 27, 0.2f, Animation.PlayMode.NORMAL);

        this.currentAnimator = downAnimator; //default
    }

    public static Player getInstance(SpriteBatch batch, Actors actors, Map currentMap) {
        if (instance == null) {
            instance = new Player(batch, actors, currentMap);
        }
        return instance;
    }

    public static Player getInstance() {
        return instance;
    }

    public void draw() {
        batch.begin();
        //batch.draw(currentAnimator.getFrame(), position.x, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);

        //DEBUG BOUNDING BOX
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1,0,0,1);
        shapeRenderer.rect(boundingBox.x , boundingBox.y, boundingBox.width, boundingBox.height);
        shapeRenderer.end();


        batch.end();
    }

    private void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        // -- Estados movimiento --
        statePlayer.State currentState = inputHandler.handlePlayerMovement();
        inputHandler.canPlaceBomb();
        if (currentState != null) {
            state.setCurrentState(currentState);
            switch (currentState) {
                case LEFT:
                    move(-speed * deltaTime, 0);
                    currentAnimator = leftAnimator;
                    break;
                case RIGHT:
                    move(speed * deltaTime, 0);
                    currentAnimator = rightAnimator;
                    break;
                case UP:
                    move(0, speed * deltaTime);
                    currentAnimator = upAnimator;
                    break;
                case DOWN:
                    move(0, -speed * deltaTime);
                    currentAnimator = downAnimator;
                    break;
            }
        }

        if (currentState != null) {
            currentAnimator.getFrame();
            playSound();
        } else {
            currentAnimator.reset(0.5f);
            walkSoundTime = 0f;
        }

        bombManager.handleBombPlacement(position);
    }

    private void move(float dx, float dy) {
        float newX = position.x + dx;
        float newY = position.y + dy;
        if (!isCollision(newX, newY)) {
            position.set(newX, newY);
            updateBoundingBox();
        }
    }

    private boolean isCollision(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, boundingBox.width, boundingBox.height);
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(playerRect)) {
                    //Gdx.app.debug("Player", "Collision detected at (" + x + ", " + y + ")");
                    return true;
                }
            }
        }
        return false;
    }

    private void playSound(){
        walkSoundTime += Gdx.graphics.getDeltaTime();
        if (walkSoundTime >= 0.5f){
            walkSound.play();
            walkSoundTime = 0f;
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    private void updateBoundingBox(){
        boundingBox.setPosition(position.x - BOUNDING_BOX_OFFSET, position.y -34);
    }

    public boolean collidesWith(Rectangle r){
        return boundingBox.overlaps(r);
    }

    public Rectangle getBoundingBox(){
        return boundingBox;
    }

    public void update(){
        handleInput();
        updateBoundingBox();
        draw();
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

}
