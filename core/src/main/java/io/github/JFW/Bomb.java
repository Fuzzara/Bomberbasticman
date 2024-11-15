package io.github.JFW;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

public class Bomb extends Actor{
    private Actors actors; //anti POO, debe de haber una mejor manera

    private Vector2 position;
    private float width;
    private float height;

    private Texture bombTexture;
    private Sprite bombSprite;
    private SpriteBatch batch;

    private long currentTime;
    private long detonationTime;

    private CollisionSystem collSystem;

    public Bomb(SpriteBatch batch, float positionx ,float positiony, Actors actors){
        this.batch = batch;
        this.position = new Vector2(positionx,positiony);

        this.width = 48;
        this.height = 48;
        this.currentTime = System.nanoTime();
        this.detonationTime = currentTime + 2000000000; //2 segundos
        this.actors = actors;

        this.bombTexture = new Texture("bomba.png");
        this.bombSprite = new Sprite(bombTexture);
        this.bombSprite.setSize(this.width, this.height);
        this.bombSprite.setPosition(this.position.x, this.position.y);
        actors.updateBombs(this);
    }

    public void draw(){
        this.currentTime = System.nanoTime();
        batch.begin();
        bombSprite.setPosition(position.x, position.y);
        bombSprite.draw(batch);
        if (currentTime >= detonationTime){
            actors.removeBombs(this);
        }
        batch.end();
    }

    public void update(){
        draw();
    }
}
