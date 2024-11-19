package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.JFW.Entitys.Player;
import io.github.JFW.System.SFXPlayer;
import io.github.JFW.System.SpriteBatchHandler;
import io.github.JFW.System.Config;

public class Scoreboard {
    private static Scoreboard instance; //Singleton YEAHHHHHHHHHHHHHHHHHHHHHH

    private int score;
    private int lives;
    private int timeLeft;
    private float timeAcc;
    private Player player;
    private BitmapFont font;
    private SpriteBatch batch;
    private Config levelConfig;
    private SFXPlayer sfx;

    private Scoreboard(){
        this.score = 0; //999999999 max
        this.timeLeft = 200;
        this.lives = 3;
        batch = SpriteBatchHandler.getBatch();
        font = new BitmapFont(Gdx.files.internal("fontBomber.fnt"),Gdx.files.internal("fontBomber.png"),false);
        font.getData().setScale(1.1f);
        sfx = new SFXPlayer();
    }

    public static Scoreboard getInstance(){
        if (instance == null){
            instance = new Scoreboard();
        }
        return instance;
    }

    public void setLevelConfig(Config config) {
        this.levelConfig = config;
    }

    public void render(){
        batch.begin();
        font.draw(batch, "" + score, 650, Gdx.graphics.getHeight() - 2);

        // If in bonus level, show bonus timer instead of regular time
        if (levelConfig != null && levelConfig.isBonusLevel()) {
            font.draw(batch, String.format("%.0f", levelConfig.getBonusLevelTimer()), 300, Gdx.graphics.getHeight()-2);
        } else {
            font.draw(batch, "" + timeLeft, 300, Gdx.graphics.getHeight()-2);
        }

        font.draw(batch, "" + lives, 113, Gdx.graphics.getHeight()-2);
        batch.end();
    }

    public void update(float delta){
        // Only update regular timer if not in bonus level
        if (levelConfig == null || !levelConfig.isBonusLevel()) {
            timeAcc += delta;
            if (timeAcc >= 1.0f) {
                timeAcc -= 1.0f;
                countDown();
            }
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

    public void addScore(int newScore){
        int oldScoreFirstDigit = this.score;
        this.score += newScore;
        int scoreNewFirstDigit = this.score;
        while (oldScoreFirstDigit > 9) {
            oldScoreFirstDigit /= 10;
        }
        while (scoreNewFirstDigit > 9) {
            scoreNewFirstDigit /=10;
        }
        if (oldScoreFirstDigit != scoreNewFirstDigit && this.score > 99999) {
            sfx.playSFX("sound/1up.mp3");
            this.lives++;
            player = Player.getInstance();
            player.setHP(player.getHP() + 1);
        }
    }

    public void removeLife(){
        player = Player.getInstance();
        if (player.getHP() >= 0) {
            if (!player.getInvincible()) {
                player.die(Gdx.graphics.getDeltaTime());
                this.lives = player.getHP();
                return;
            }
        } else {
            //GAMEOVER!!!!!!!!!!!!!
            //RESET!
            /*player.setHP(3);
            this.lives = 3;
            this.score = 0;
            this.timeLeft = 200;*/
            Main.getInstance().gameOver();
            reset();
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
