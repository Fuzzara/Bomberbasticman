package io.github.JFW.Entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.Entities.Enemy.Enemy;
import io.github.JFW.Entities.Items.Bomb;
import io.github.JFW.Entities.Items.PowerUp;
import io.github.JFW.Entities.Player.Player;
import io.github.JFW.Screens.GameScreen;
import io.github.JFW.States.stateGameScreen;
import io.github.JFW.States.statePlayer;

import java.util.ArrayList;
import java.util.Iterator;

public class Actors extends Actor {
    private ArrayList<Bomb> Bombs;
    private ArrayList<Bomb> BombsTBR;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUp> powerUps;
    private Player player;
    private statePlayer.PowerUpType powerUpType;

    private stateGameScreen.State state;
    public Actors() {
        this.Bombs = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.BombsTBR = new ArrayList<>();
        this.powerUps = new ArrayList<>();
    }

    // Devuelve la lista de enemigos
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    // Actualiza el estado de todos los actores
    public void update(stateGameScreen state) {
        if (state.getCurrentState() == stateGameScreen.State.running) {
            updateBombs();
            actuallyRemovingBombs();
            updateEnemies();
            player.update();
            updatePowerUps();
        } else {
            drawActors();
        }
    }

    // Limpia todas las listas de actores, esto se usa principalmente cuando se cambia de nivel
    public void clearActors() {
        Bombs.clear();
        BombsTBR.clear();
        enemies.clear();
        powerUps.clear();
    }

    // Limpia la lista de enemigos
    public void clearEnemies() {
        enemies.clear();
    }

    // Devuelve la lista de powerups
    public ArrayList<PowerUp> getPowerUps() {
        return powerUps;
    }

    // Establece el jugador
    public void setPlayer(Player player) {
        this.player = player;
    }

    // Añade una bomba a la lista
    public void updateBombs(Bomb bomb) {
        Bombs.add(bomb);
    }

    // Elimina las bombas marcadas para ser eliminadas
    public void actuallyRemovingBombs() {
        Bombs.removeAll(BombsTBR);
        BombsTBR.clear();
    }

    // Quita una bomba de la lista
    public void removeBombs(Bomb bomb) {
        BombsTBR.add(bomb);
    }

    // Añade un enemigo a la lista
    public void updateEnemies(Enemy enemy) {
        enemies.add(enemy);
    }

    // Añade un powerup a la lista
    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    // número de bombas
    public int getBombCount() {
        return Bombs.size();
    }

    // Explota todas las bombas si el jugador tiene el detonador
    public void useDetonator() {
        if (player.hasPowerUp(statePlayer.PowerUpType.DETONATOR)) {
            for (Bomb bomb : Bombs) {
                bomb.detonatorExplode();
            }
        }
    }

    // Lista de bombas
    public ArrayList<Bomb> getBombs() {
        return Bombs;
    }

    // Actualiza las bombas
    private void updateBombs() {
        for (Bomb bomb : Bombs) {
            bomb.update();
        }
    }

    // Actualiza los enemigos
    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.update();
        }
    }

    // Actualiza los power-ups
    private void updatePowerUps() {
        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            powerUp.update();
            if (powerUp.pickUP()) {
                powerUpIterator.remove();
            }
        }
    }

    // Renderiza todos los actores
    private void drawActors() {
        player.draw();
        for (Bomb bomb : Bombs) {
            bomb.draw();
        }
        for (Enemy enemy : enemies) {
            enemy.draw();
        }
    }
}
