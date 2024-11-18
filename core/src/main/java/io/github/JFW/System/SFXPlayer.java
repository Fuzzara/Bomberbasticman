package io.github.JFW.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class SFXPlayer {
    private ObjectMap<String, Sound> sounds;

    public SFXPlayer() {
        sounds = new ObjectMap<>();
    }

    public void playSFX(String path) {
        Sound sound = sounds.get(path);
        if (sound == null) {
            // Only load the sound if we haven't loaded it before
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sounds.put(path, sound);
        }
        sound.play(0.3f);
    }

    public void dispose() {
        // Dispose of all loaded sounds
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        sounds.clear();
    }
}
