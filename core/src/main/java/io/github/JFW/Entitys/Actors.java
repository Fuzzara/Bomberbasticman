package io.github.JFW.Entitys;

import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.GameScreen;


import java.util.ArrayList;

public class Actors extends Actor {
    private ArrayList<Bomb> Bombs;
    private ArrayList<Bomb> BombsTBR; // BombstobeRemoved
    private ArrayList<Enemy> enemies; // sin uso por ahora
    private Player player;


    private GameScreen.State state;

    public Actors(){
        this.Bombs = new ArrayList<Bomb>();
        this.enemies = new ArrayList<Enemy>();
        this.BombsTBR = new ArrayList<Bomb>();
    }

    public void update(GameScreen.State state){
        if (state == GameScreen.State.running) {
            for (Bomb bomb : Bombs) {
                bomb.update();
                if (Bombs.isEmpty()) {
                    return;
                }

            }
            actuallyremovingBombs();
            for (Enemy enemy: enemies){
                enemy.update();
            }
            this.player.update();
        }
        else{
            this.player.draw();
            for (Bomb bomb : Bombs) {
                bomb.draw();
            }
            for (Enemy enemy: enemies){
                enemy.draw();
            }
        }
    }
    public void clearActors(){
        Bombs.clear();
        BombsTBR.clear();
        enemies.clear();
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public void updateBombs(Bomb bomb){
        Bombs.add(bomb);
    }

    public void actuallyremovingBombs(){
        //int counter = 0;
        for (Bomb bomb: BombsTBR){
            Bombs.removeFirst();
            //System.out.println("Removing bomb");
            //counter += 1;
        }
        BombsTBR.clear();
        /*for (int i = 0; i<counter;i++){
            BombsTBR.removeFirst();
            //System.out.println("Removing bomb from counter");
        }*/
    }

    public void removeBombs(Bomb bomb){
       BombsTBR.add(bomb);
    }

    public void updateEnemies(Enemy enemy){
        enemies.add(enemy);
    }
}


