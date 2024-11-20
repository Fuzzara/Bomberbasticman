package io.github.JFW.Entitys;

import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.Screens.GameScreen;
import io.github.JFW.States.statePlayer;


import java.util.ArrayList;
import java.util.Iterator;

public class Actors extends Actor {
    private ArrayList<Bomb> Bombs;
    private ArrayList<Bomb> BombsTBR; // BombstobeRemoved
    private ArrayList<Enemy> enemies; // sin uso por ahora
    private ArrayList<PowerUp> powerUps;
    private Player player;
    private statePlayer.PowerUpType powerUpType;


    private GameScreen.State state;

    public Actors(){
        this.Bombs = new ArrayList<Bomb>();
        this.enemies = new ArrayList<Enemy>();
        this.BombsTBR = new ArrayList<Bomb>();
        this.powerUps = new ArrayList<PowerUp>();
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
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

            Iterator<PowerUp> powerUpIterator = powerUps.iterator();
            while (powerUpIterator.hasNext()) {
                PowerUp powerUp = powerUpIterator.next();
                powerUp.update();
                if(powerUp.pickUP()){
                    powerUpIterator.remove();
                }
            }
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
        powerUps.clear();
    }

    public void clearEnemies(){
        enemies.clear();
    }

    public ArrayList<PowerUp> getPowerUps(){
        return powerUps;
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

    public void addPowerUp(PowerUp powerUp){
        powerUps.add(powerUp);
    }

    public int getBombCount(){
        return Bombs.size();
    }

    public void useDetonator(){
        if (player.hasPowerUp(statePlayer.PowerUpType.DETONATOR)){
            for (Bomb bomb: Bombs){
                bomb.detonatorExplode();
            }
        }
    }

    public ArrayList<Bomb> getBombs(){
        return Bombs;
    }

}


