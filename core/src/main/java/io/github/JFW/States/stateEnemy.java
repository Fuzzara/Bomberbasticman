package io.github.JFW.States;

public class stateEnemy {
    public enum State {
        UP, DOWN, LEFT, RIGHT, STUCK, DEADANIM, DEAD
    }
    private State currentState;

    public stateEnemy(){
        currentState = State.STUCK; //Estado inicial
    }
    public State getCurrentState(){
        return currentState;
    }
    public void setCurrentState(State state){
        currentState = state;
    }
}
