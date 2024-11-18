package io.github.JFW.System;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Random;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import io.github.JFW.*;
import io.github.JFW.Entitys.*;
import io.github.JFW.MapEnv.*;

public class Config {

    private Map currentMap;
    private MapSystem mapSystem;
    private Player player;
    private Actors actors;
    private SpriteBatch batch;
    private Enemy enemy;
    private Main.State state;

    public Config(SpriteBatch batch){
        this.batch = batch;
        //this.mapSystem = mapSystem;

    }

    public Map setuplevel(int n){
        mapSystem = new MapSystem();
        currentMap = mapSystem.getMap(n);
        if (actors == null){
            actors = new Actors();
            player = Player.getInstance(batch, actors, currentMap);
            actors.setPlayer(player);
            //setupenemies();
            setupenemies(4);
        }
        else{
            actors.clearActors();
        }
        currentMap.setActors(actors);
        return currentMap;
    }

    //top left (x: 96, y:630) approximately
    //bottom right (x: 775, y:50) approximately

    public void setupenemies(){ // OHHH THE MISERY
        enemy = new Enemy(46.6666666667f,96,630,1000,1,currentMap,batch);
        actors.updateEnemies(enemy);
    }

    public void setupenemies(int n){
        for(int i=0;i<n;i++){
            boolean StuckinEnvironment = true;
            Random rand = new Random();
            while(StuckinEnvironment){
                int x = rand.nextInt((775-335)+1)+355;
                int y = rand.nextInt((385-50)+1)+50;
                Rectangle rect = new Rectangle(x ,y ,34,34);
                StuckinEnvironment = stuck(rect);
                if (StuckinEnvironment == false){
                    enemy = new Enemy(46.6666666667f,x,y,1000,1,currentMap,batch);
                    actors.updateEnemies(enemy);
                }
            }

        }
    }

    public boolean stuck(Rectangle enemyRect){
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(enemyRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void runlevel(GameScreen.State state){
        actors.update(state);
    }



}
