package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputHandler{
    private long nextBombTime = 0;

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
                return true;
        }
        return false;
    }
   public String handleMainMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            return "start";
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return "exit";
        }
        return null;
    }

    public boolean handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            return true;
        }
        return false;
    }
    public boolean debugReload() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            return true;
        }
        return false;
    }
}
