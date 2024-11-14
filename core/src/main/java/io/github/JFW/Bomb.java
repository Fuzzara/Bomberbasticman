package io.github.JFW;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

public class Bomb extends Actor{
    private Actors actors; //anti POO, debe de haber una mejor manera

    private Vector2 posicion;
    private float width;
    private float height;

    private Texture bombTexture = new Texture("bomba.png");
    private Sprite bombSprite = new Sprite(bombTexture);
    private SpriteBatch batch;

    private long currentTime;
    private long detonationTime;

    private CollisionSystem collSystem;

    public Bomb(SpriteBatch batch, CollisionSystem collSystem, Vector2 posicion){
        this.batch = batch;
        this.posicion = posicion;
        bombSprite.setSize(48, 48);
        bombSprite.setPosition(posicion.x, posicion.y);
        width = 48;
        height = 48;
        this.collSystem = collSystem;
        this.currentTime = System.nanoTime();
        this.detonationTime = currentTime + 2000000000; //2 segundos
    }

    public void draw(){
        currentTime = System.nanoTime();
        batch.begin();
        bombSprite.setPosition(posicion.x, posicion.y);
        bombSprite.draw(batch);
        if (currentTime >= detonationTime){
            batch.end();
        }
        batch.end();
    }

    public void update(){
        draw();
    }
}
