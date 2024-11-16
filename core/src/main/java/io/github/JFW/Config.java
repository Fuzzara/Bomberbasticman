package io.github.JFW;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Config {

    private Map currentMap;
    private MapSystem mapSystem;
    private Player player;
    private Actors actors;
    private SpriteBatch batch;
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
            player = new Player(batch, actors, currentMap);
            actors.setPlayer(player);
        }
        else{
            actors.clearActors();
        }
        return currentMap;
    }

    public void runlevel(Main.State state){
        actors.update(state);
    }



}
