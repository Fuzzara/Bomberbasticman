package io.github.JFW;

public class statePlayer {
    public enum State {
        UP, DOWN, LEFT, RIGHT, DEAD, DOOR
    }
    //PowerUPS
    public enum PowerUpType {
        SUN, //0
        GOLDEN_BOMB, //1
        DETONATOR, //2
        SKATES, //3
        STRIPPED_BOMB, //4
        STRIPPED_WALL, //5
        QUESTION_MARK, //6
        FIRE_MAN //7
    }
    private PowerUpType currentPowerUp;

    public PowerUpType getCurrentPowerUp(){
        return currentPowerUp;
    }

    public void setCurrentPowerUp(PowerUpType powerUp){
        currentPowerUp = powerUp;
    }

    private State currentState;

    public statePlayer(){
        currentState = State.DOWN; //Estado inicial
    }

    public State getCurrentState(){
        return currentState;
    }

    public void setCurrentState(State state){
        currentState = state;
    }



}
