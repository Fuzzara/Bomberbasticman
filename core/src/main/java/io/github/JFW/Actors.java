package io.github.JFW;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

public class Actors extends Actor {
    private ArrayList<Bomb> Bombs;
    private ArrayList<Actor> Monster; // sin uso por ahora
    private Player player;

    public Actors(){
        this.Bombs = new ArrayList<Bomb>();
        this.Monster = new ArrayList<Actor>();
    }
    public void update(){
        this.player.update();
        for (Bomb bomb:Bombs){
            bomb.update();
            if(Bombs.isEmpty()){break;}
        }
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public void updateBombs(Bomb bomb){
        Bombs.add(bomb);
    }
    public void removeBombs(Bomb bomb){
       Bombs.remove(bomb);
    }
    public void updateMonsters(){

    }
}


