package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.JFW.Entitys.Player;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;

import java.util.BitSet;

public class Scoreboard {
    private int score;
    private int lives;
    private int timeLeft;
    private float timeAcc;
    private Player player;
    private BitmapFont font;
    private SpriteBatch batch;

    public Scoreboard(){
        this.score = 0; //999999999 max
        this.timeLeft = 200;
        //player = Player.getInstance();
        this.lives = 3;
        batch = SpriteBatchHandler.getBatch();
        font = new BitmapFont(Gdx.files.internal("fontBomber.fnt"),Gdx.files.internal("fontBomber.png"),false);
        font.getData().setScale(1.1f);
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
        if (timeLeft <= 0) {
            //player.die();
            this.timeLeft = 0;
            //MonG Spawn!
        }else {
            this.timeLeft--;
        }
    }

    public void addScore(int score){
        this.score += score;
    }

    public void removeLife(){
        this.lives--;
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
