package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;

public class Player extends Actor {
    // Constants
    private static final int INITIAL_HP = 3;
    private static final float INITIAL_SPEED = 120f;
    private static final Vector2 INITIAL_POSITION = new Vector2(93, 480);
    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 38;

    // Stats
    private int hp;
    private boolean detonator;

    // Position and movement
    private Vector2 position;
    private float speed;

    private Sprite bomberSprite;
    private SpriteBatch batch;

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
    private CollisionSystem collisionSystem;
    private ShapeRenderer shapeRenderer;

    //Sound
    private Sound walkSound;
    private float walkSoundTime;

    public Player(SpriteBatch batch, CollisionSystem collisionSystem) {
        this.batch = batch;
        this.collisionSystem = collisionSystem;
        this.hp = INITIAL_HP;
        this.speed = INITIAL_SPEED;
        this.position = new Vector2(INITIAL_POSITION);

        // Sprite and rendering
        Texture bomberTexture = new Texture("bomberTexture.png");
        this.bomberSprite = new Sprite(bomberTexture);
        this.bomberSprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        this.bomberSprite.setPosition(position.x, position.y);

        this.boundingBox = new Rectangle(position.x, position.y, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        this.shapeRenderer = new ShapeRenderer();

        this.downAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.rightAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 3, 5, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.upAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 6, 8, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.leftAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 9, 11, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.deathAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 12, 18, 0.3f, Animation.PlayMode.NORMAL);
        this.winAnimator = new Animator("bomberSpriteSheet.png", 28, 1, 19, 27, 0.2f, Animation.PlayMode.NORMAL);

        this.currentAnimator = downAnimator; //default
    }

    public void draw(){
        batch.begin();
        batch.draw(currentAnimator.getFrame(), position.x, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);

        //DEBUG BOUNDING BOX
        /*shapeRe.begin(ShapeRenderer.ShapeType.Line);
        shapeRe.setColor(1,0,0,1);
        shapeRe.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        shapeRe.end();*/


        batch.end();
    }

    private void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        boolean moving = false; //Booleano que indica si el jugador se esta moviendo
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            move(-speed * deltaTime, 0);
            currentAnimator = leftAnimator;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            move(speed * deltaTime, 0);
            currentAnimator = rightAnimator;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            move(0, -speed * deltaTime);
            currentAnimator = downAnimator;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            move(0, speed * deltaTime);
            currentAnimator = upAnimator;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            //le pone la bomba mhmm~~~~
        }
        if (moving) {
            currentAnimator.getFrame(); //Se actualiza solamente si se esta moviendo
            playSound(); //Se reproduce el sonido de caminar
        }
        else {
           currentAnimator.reset(0.5f); //Se resetea la animacion si no se esta moviendo ("idle")
            walkSoundTime = 0f; //Se resetea el tiempo del sonido
        }

    }

    private void move(float dx, float dy) {
        if (!collisionSystem.willCollide(position.x + dx, position.y + dy, boundingBox.width, boundingBox.height)) {
            position.add(dx, dy);
        }
    }
    private void playSound(){
        walkSoundTime += Gdx.graphics.getDeltaTime();
        if (walkSoundTime >= 0.3f){
            walkSound.play();
            walkSoundTime = 0f;
        }
    }

    private void updateBoundingBox(){
        boundingBox.setPosition(position.x, position.y);
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

}
