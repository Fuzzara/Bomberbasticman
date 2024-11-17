package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SFXPlayer {
    private Sound sound;
    public void playSFX(String path){
        sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sound.play(0.3f);
    }
}
