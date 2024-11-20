package io.github.JFW.Graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpriteBatchHandler {
    private static SpriteBatch batch;

    private SpriteBatchHandler() {
    }

    // Devuelve el batch
    public static SpriteBatch getBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        return batch;
    }
}
