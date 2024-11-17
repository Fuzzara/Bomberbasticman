package io.github.JFW;

public class Scoreboard {
    private int score;
    private int lives;

    public Scoreboard(){
        this.score = 0;
        this.lives = 3;
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

    public void reset(){
        this.score = 0;
        this.lives = 3;
    }
}
