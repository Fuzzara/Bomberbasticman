package io.github.JFW.System;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteBatchHandler { //Singleton!!!
    private static SpriteBatch batch;

    private SpriteBatchHandler() {
    }

    public static SpriteBatch getBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        return batch;
    }

}
