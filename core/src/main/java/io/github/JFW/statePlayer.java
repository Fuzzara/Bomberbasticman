package io.github.JFW;

public class statePlayer {
    public enum State {
        UP, DOWN, LEFT, RIGHT, DEAD, DOOR
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
