package io.github.JFW;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Enemies extends Actor{

    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;

    private boolean noclip; // Atraviesa muros ig
    private int ai;
    private int score;
    private Vector2 position;
    private float speed;
    private Sprite enemySprite;
    private Texture enemyTexture;

    private SpriteBatch batch;
    private Map currentMap;

    private Rectangle boundingBox;

    public Enemies(float speed,float x,float y,int score,int ai){
        this.speed = speed;
        this.position = new Vector2(x,y);
        this.score = score;
        this.ai = ai;

        //por mientras, cambiar!
        this.enemyTexture = new Texture("bombwip.png");
        this.enemySprite = new Sprite(enemyTexture);
        this.enemySprite.setSize(SPRITE_WIDTH,SPRITE_HEIGHT);
        this.enemySprite.setPosition(position.x,position.y);
        this.boundingBox = new Rectangle(position.x,position.y,BOUNDING_BOX_SIZE,BOUNDING_BOX_SIZE);

    }

    public void draw(){
        batch.begin();
        enemySprite.draw(batch);
        batch.end();
    }

    public void update(){
        draw();
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

}

