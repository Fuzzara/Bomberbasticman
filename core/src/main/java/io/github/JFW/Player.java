package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.css.Rect;

public class Player extends Actor {
    //Stats
    private int hp;
    private boolean detonator;

    Main main;

    //Posicion cosas
    private Vector2 posicion;
    private float speed;
    private float width;
    private float height;

    //Sprite cosas
    private Texture bomberTexture = new Texture("bomberTexture.png");
    private Sprite bomberSprite = new Sprite(bomberTexture);
    private SpriteBatch batch;

    //Colision box
    private Rectangle boundingBox;
    private CollisionSystem collSystem;
    private ShapeRenderer shapeRe;

    public Player(SpriteBatch batch, CollisionSystem collSystem){
        this.batch = batch;
        this.hp = 3;
        this.speed = 100f;
        posicion = new Vector2(93, 480);
        bomberSprite.setSize(48, 96);
        bomberSprite.setPosition(posicion.x, posicion.y);
        width = 38;
        height = 38;
        this.collSystem = collSystem;
        boundingBox = new Rectangle(posicion.x, posicion.y, width, height);
        shapeRe = new ShapeRenderer();
    }

    public void draw(){
        batch.begin();
        bomberSprite.setPosition(posicion.x,posicion.y);
        bomberSprite.draw(batch);

        //DEBUG BOUNDING BOX
        shapeRe.begin(ShapeRenderer.ShapeType.Line);
        shapeRe.setColor(1,0,0,1);
        shapeRe.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        shapeRe.end();


        batch.end();
    }

    public void input(){
        if (Gdx.input.isKeyPressed(Input.Keys.A) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            if (!collSystem.willCollide(posicion.x - speed * Gdx.graphics.getDeltaTime(), posicion.y, width, height)) {
                posicion.x -= speed * Gdx.graphics.getDeltaTime();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            if (!collSystem.willCollide(posicion.x + speed * Gdx.graphics.getDeltaTime(), posicion.y, width, height)) {
                posicion.x += speed * Gdx.graphics.getDeltaTime();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
            if (!collSystem.willCollide(posicion.x, posicion.y - speed * Gdx.graphics.getDeltaTime(), width, height)) {
                posicion.y -= speed * Gdx.graphics.getDeltaTime();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || (Gdx.input.isKeyPressed(Input.Keys.UP))) {
            if (!collSystem.willCollide(posicion.x, posicion.y + speed * Gdx.graphics.getDeltaTime(), width, height)) {
                posicion.y += speed * Gdx.graphics.getDeltaTime();
            }
        }
    }

    private void updateBoundingBox(){
        boundingBox.setPosition(posicion.x, posicion.y);
    }

    public boolean collidesWith(Rectangle r){
        return boundingBox.overlaps(r);
    }

    public Rectangle getBoundingBox(){
        return boundingBox;
    }

    public void update(){
        input();
        updateBoundingBox();
        draw();
    }
}
