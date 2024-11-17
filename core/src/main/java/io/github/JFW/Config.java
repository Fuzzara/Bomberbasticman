package io.github.JFW;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
            player = new Player(batch, actors, currentMap);
            actors.setPlayer(player);
            setupenemies();
        }
        else{
            actors.clearActors();
        }
        return currentMap;
    }
    //top left (x: 96, y:630) approximately
    //bottom right (x: 775, y:45) approximately

    public void setupenemies(){
        enemy = new Enemy(100.f,96,630,1000,1,currentMap,batch);
        actors.updateEnemies(enemy);
    }

    public void runlevel(GameScreen.State state){
        actors.update(state);
    }



}
