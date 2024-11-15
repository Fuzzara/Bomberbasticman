package io.github.JFW;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CollisionSystem {
    Array<Rectangle> obstacles = new Array<Rectangle>();

    public CollisionSystem() {
        Rectangle paredIzquierda = new Rectangle( 89, 0, 1, 672);
        Rectangle paredDerecha = new Rectangle( 774, 0, 1, 672);
        Rectangle paredArriba = new Rectangle( 0, 627, 864, 1);
        Rectangle paredAbajo = new Rectangle( 0, 43, 864, 1);

        obstacles.add(paredAbajo);
        obstacles.add(paredArriba);
        obstacles.add(paredIzquierda);
        obstacles.add(paredDerecha);
    }

    public boolean willCollide(float newX, float newY, float width, float height) {
        Rectangle newBoundingBox = new Rectangle(newX, newY, width, height);
        for (Rectangle obstacle : obstacles) {
            if (newBoundingBox.overlaps(obstacle)) {
                return true;
            }
        }
        return false;
    }

    public void  addObstacle(Rectangle obstacle) {
        obstacles.add(obstacle);
    }
}
