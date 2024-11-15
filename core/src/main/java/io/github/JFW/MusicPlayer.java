package io.github.JFW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer {
    private Music music;
    private boolean isPlaying;
    void playMusic(String path){
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
}

