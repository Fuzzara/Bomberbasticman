package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;

public class Player extends Actor {
    // Constants
    private static final int INITIAL_HP = 3;
    private static final float INITIAL_SPEED = 100f;
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

    // Sprite and rendering
    private Texture bomberTexture;
    private Sprite bomberSprite;
    private SpriteBatch batch;
    TextureRegion[] animationFrames;
    Animation animation;

    // Collision and bounding box
    private Rectangle boundingBox;
    private CollisionSystem collisionSystem;
    private ShapeRenderer shapeRenderer;

    public Player(SpriteBatch batch, CollisionSystem collisionSystem) {
        this.batch = batch;
        this.collisionSystem = collisionSystem;
        this.hp = INITIAL_HP;
        this.speed = INITIAL_SPEED;
        this.position = new Vector2(INITIAL_POSITION);

        this.bomberTexture = new Texture("bomberTexture.png");
        this.bomberSprite = new Sprite(bomberTexture);
        this.bomberSprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        this.bomberSprite.setPosition(position.x, position.y);

        this.boundingBox = new Rectangle(position.x, position.y, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        this.shapeRenderer = new ShapeRenderer();
    }

    public void draw(){
        batch.begin();
        bomberSprite.setPosition(position.x,position.y);
        bomberSprite.draw(batch);

        //DEBUG BOUNDING BOX
        /*shapeRe.begin(ShapeRenderer.ShapeType.Line);
        shapeRe.setColor(1,0,0,1);
        shapeRe.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        shapeRe.end();*/


        batch.end();
    }

    private void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            move(-speed * deltaTime, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            move(speed * deltaTime, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            move(0, -speed * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            move(0, speed * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            //le pone la bomba mhmm~~~~
        }
    }

    private void move(float dx, float dy) {
        if (!collisionSystem.willCollide(position.x + dx, position.y + dy, boundingBox.width, boundingBox.height)) {
            position.add(dx, dy);
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
