package io.github.JFW.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer {
    private Music music;
    private boolean isPlaying;

    public void playMusic(String path){
        if (music != null) {
            music.dispose();
        }
        music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
        isPlaying = true;
    }

    public void stopMusic(){
        if (music != null) {
            music.stop();
            isPlaying = false;
        }
    }

    public void pauseMusic(){
        if (music != null) {
            music.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic(){
        if (music != null) {
            music.play();
            isPlaying = true;
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void dispose() {
        if (music != null) {
            music.dispose();
        }
    }
}
