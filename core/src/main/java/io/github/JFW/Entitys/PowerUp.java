package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.JFW.System.Animator;
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
    private Animator animator;

    //State
    private statePlayer.PowerUpType powerUpType;
    //DEBUG
    private ShapeRenderer sr;

    public PowerUp(float x, float y,Actors actors, int type){
        this.position = new Vector2(x,y);
        this.type = type;
        this.actors = actors;

        boundingBox = new Rectangle(x,y,width,height);
        switch (type){ //Sprite
            case 0:
                makeAnimator("cupones/Sol.png");
                break;
            case 1:
                makeAnimator("cupones/BombaDorada.png");
                break;
            case 2:
                makeAnimator("cupones/Detonador.png");
                break;
            case 3:
                makeAnimator("cupones/Patin.png");
                break;
            case 4:
                makeAnimator("cupones/BombaRayada.png");
                break;
            case 5:
                makeAnimator("cupones/MuroRayado.png");
                break;
            case 6:
                makeAnimator("cupones/Pregunta.png");
                break;
            case 7:
                makeAnimator("cupones/HombreEnLlamas.png");
                break;
            default:
                makeAnimator("cupones/Error.png");
                break;
        }


    }
    public void makeAnimator (String path) { //al tener todas las animaciones igual era mejor una funcion que copiar el mismo codigo :p
        animator = new Animator(path, 2, 1, 0, 1, 0.1f, Animation.PlayMode.LOOP);
    }


    public void update(){
        SpriteBatchHandler.getBatch().begin();
        SpriteBatchHandler.getBatch().draw(animator.getFrame(), position.x+12, position.y+24, 48, 48);
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
