package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.JFW.States.statePlayer;

public class InputHandler{

    // Maneja el input del jugador
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

    // Verifica si el apreto el boton de bomba
    public boolean canPlaceBomb(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
                return true;
        }
        return false;
    }

    // Verifica si el jugador apreto el boton de detonar
    public boolean usedDetonator(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            return true;
        }
        return false;
    }

    // Maneja el input del menu principal
    public String handleMainMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            return "start";
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return "exit";
        }
        return null;
    }

    // Maneja el input de la pnatalla gameover
    public boolean handleGameOverInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            return true;
        }
        return false;
    }

    // Para pausar y despausar
    public boolean handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            return true;
        }
        return false;
    }

    // Recargar el juego (esto no se usa esta bug)
    public boolean debugReload() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            return true;
        }
        return false;
    }
}
