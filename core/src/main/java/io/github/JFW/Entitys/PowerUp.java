package io.github.JFW.Entitys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class PowerUp {
    //Pos and bounding box
    private Vector2 position;
    private float width = 28;
    private float height = 28;
    private Rectangle boundingBox;
    private Actors actors;

    //Type of powerup
    private int type;

    //DEBUG
    private ShapeRenderer sr;

    public PowerUp(float x, float y,Actors actors, int type){
        this.position = new Vector2(x,y);
        this.type = type;
        this.actors = actors;

        switch (type){
            //Sprite dependiendo del tipo de powerup
        }

        sr = new ShapeRenderer();
        boundingBox = new Rectangle(x,y,width,height);
    }

    public void update(){
        //pickUP();
        draw();
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
            Gdx.app.debug("PowerUP", "Player picked up PowerUP: " + Player.PowerUpType.values()[type]);
            player.applyPowerUp(Player.PowerUpType.values()[type]);
            return true;
        }
        return false;
    }

    public void dispose(){
        sr.dispose();
    }

    //Talvez en Bomb hacer que el powerUP explote;
}
