package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer {
    private Music music;
    private boolean isPlaying;
    public void playMusic(String path){
        music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
        isPlaying = true;
    }
    void stopMusic(){
        music.stop();
        isPlaying = false;
    }
    public void pauseMusic(){
        music.pause();
        isPlaying = false;
    }
    public void resumeMusic(){
        music.play();
        isPlaying = true;
    }
    boolean isPlaying(){
        return isPlaying;
    }

    public void dispose() {
        music.dispose();
    }
}

