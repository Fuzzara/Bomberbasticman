package io.github.JFW.States;

public class stateGameScreen {
    public enum State {
        running, paused, levelTransition
    }

    private State currentState;
    private float transitionTimer;
    private static final float TRANSITION_DURATION = 3.7f;

    public stateGameScreen() {
        currentState = State.running; // estado inicial
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State state) {
        currentState = state;
    }


    public void pauseGame() {
        setCurrentState(State.paused);
    }

    public void resumeGame() {
        setCurrentState(State.running);
    }
}
