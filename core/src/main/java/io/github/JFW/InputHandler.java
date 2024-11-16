package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


public class InputHandler{
    private statePlayer state;
    private long nextBombTime = 0;

    public InputHandler(statePlayer playerState) {
        this.state = playerState;
    }

    public statePlayer.State handlePlayerMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            return statePlayer.State.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            return statePlayer.State.RIGHT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            return statePlayer.State.DOWN;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            return statePlayer.State.UP;
        }

        return null;
    }
    public boolean canPlaceBomb(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            if (System.nanoTime() > nextBombTime) {
                nextBombTime = System.nanoTime() + 50000000; // Cooldown de 50ms
                return true;
            }
        }
        return false;
    }
}
