package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.JFW.Entitys.Player;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;

import java.util.BitSet;

public class Scoreboard {
    private static Scoreboard instance; //Singleton YEAHHHHHHHHHHHHHHHHHHHHHH

    private int score;
    private int lives;
    private int timeLeft;
    private float timeAcc;
    private Player player;
    private BitmapFont font;
    private SpriteBatch batch;

    private Scoreboard(){
        this.score = 0; //999999999 max
        this.timeLeft = 200;
        this.lives = 3;
        batch = SpriteBatchHandler.getBatch();
        font = new BitmapFont(Gdx.files.internal("fontBomber.fnt"),Gdx.files.internal("fontBomber.png"),false);
        font.getData().setScale(1.1f);
    }
    public static Scoreboard getInstance(){
        if (instance == null){
            instance = new Scoreboard();
        }
        return instance;
    }

    public void render(){
        batch.begin();
        font.draw(batch, "" + score, 650, Gdx.graphics.getHeight() - 2);
        font.draw(batch, "" + timeLeft, 300, Gdx.graphics.getHeight()-2);
        font.draw(batch, "" + lives, 113, Gdx.graphics.getHeight()-2);
        batch.end();
    }
    public void update(float delta){
        timeAcc += delta;
        if (timeAcc >= 1.0f) {
            timeAcc -= 1.0f;
            countDown();
        }
    }
    public void countDown(){
        if (timeLeft > 0) {
            timeLeft--;
        } else {
            timeLeft = 0;
            // spawn moneda Giratoria!!!!
        }
    }

    public void addScore(int score){
        this.score += score;
    }

    public void removeLife(){
        player = Player.getInstance();
        if (player.getHP() > 0) {
            if (!player.getInvincible()) {
                this.lives--;
                player.setHP(player.getHP() - 1);
                player.die(Gdx.graphics.getDeltaTime());
            }
        } else {
            //GAMEOVER!!!!!!!!!!!!!
            //RESET!
            player.setHP(3);
            this.lives = 3;
            this.score = 0;
            this.timeLeft = 200;
            //temporal tho
        }

    }

    public int getScore(){
        return this.score;
    }

    public int getLives(){
        return this.lives;
    }
    public void setTimeLeft(int time){
        this.timeLeft = time;
    }

    public void reset(){
        this.score = 0;
        this.lives = 3;
        this.timeLeft = 200;
    }
}
