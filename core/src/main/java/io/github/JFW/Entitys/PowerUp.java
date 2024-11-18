package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;
import io.github.JFW.statePlayer;
import io.github.JFW.statePlayer.PowerUpType;


public class PowerUp {
    //Pos and bounding box
    private Vector2 position;
    private float width = 28;
    private float height = 28;
    private Rectangle boundingBox;
    private Actors actors;

    //Type of powerup
    private int type;

    //SFX!
    private SFXPlayer sfx;

    //Texture sprite
    private Texture texture;
    private Sprite sprite;
    private final SpriteBatch batch;

    //State
    private statePlayer.PowerUpType powerUpType;
    //DEBUG
    private ShapeRenderer sr;

    public PowerUp(float x, float y,Actors actors, int type){
        this.position = new Vector2(x,y);
        this.type = type;
        this.actors = actors;
        this.batch = SpriteBatchHandler.getBatch();;
        switch (type){ //Sprite
            case 0:
                texture = new Texture("cupones/Sol.png");
                break;
            case 1:
                texture = new Texture("cupones/BombaDorada.png");
                break;
            case 2:
                texture = new Texture("cupones/Detonador.png");
                break;
            case 3:
                texture = new Texture("cupones/Patin.png");
                break;
            case 4:
                texture = new Texture("cupones/BombaRayada.png");
                break;
            case 5:
                texture = new Texture("cupones/MuroRayado.png");
                break;
            case 6:
                texture = new Texture("cupones/Pregunta.png");
                break;
            case 7:
                texture = new Texture("cupones/HombreEnLlamas.png");
                break;
            default:
                texture = new Texture("cupones/Error.png");
                break;
        }
        sprite = new Sprite(texture);
        sprite.setSize(48, 48);
        sprite.setPosition(position.x+12, position.y+24);

        boundingBox = new Rectangle(x,y,width,height);
    }

    public void update(){
        SpriteBatchHandler.getBatch().begin();
        sprite.draw(SpriteBatchHandler.getBatch());
        SpriteBatchHandler.getBatch().end();
    }

    public void draw(){
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(1, 0, 0, 1);
        sr.rect(position.x, position.y, width, height);
        sr.end();
    }

    public boolean pickUP(){
        Player player = Player.getInstance();
        if(boundingBox.overlaps(player.getBoundingBox())){
            Gdx.app.debug("PowerUP", "Player picked up PowerUP: " + statePlayer.PowerUpType.values()[type]);
            player.applyPowerUp(statePlayer.PowerUpType.values()[type]);
            sfx = new SFXPlayer();
            sfx.playSFX("sound/item.mp3");
            return true;
        }
        return false;
    }

    public void dispose(){
        sr.dispose();
    }

    //Talvez en Bomb hacer que el powerUP explote;
}
