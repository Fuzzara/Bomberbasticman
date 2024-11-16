package io.github.JFW;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Bomb extends Actor{

    private final Actors actors; //anti POO, debe de haber una mejor manera

    private final Vector2 position;
    private float width = 48;
    private float height = 48;

    private final SpriteBatch batch;
    private Animator idleAnimator;
    private Animator explosionAnimator;
    private Animator currentAnimator;

    private float detonationTime;
    private long currentTime;
    private boolean exploded;
    private float explosionTimer;

    private Map tiledMap;

    public Bomb(SpriteBatch batch, float x ,float y, Actors actors, Map map){
        this.batch = batch;
        this.position = new Vector2(x,y);
        this.exploded = false;
        this.explosionTimer = 0f;

        this.tiledMap = map;
        this.currentTime = System.nanoTime();
        this.detonationTime = currentTime + 2000000000; //2 segundos
        this.actors = actors;

        //Animations!
        this.idleAnimator = new Animator("bombSpriteSheet.png", 8, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionAnimator = new Animator("bombSpriteSheet.png", 8, 1, 3, 7, 0.25f, Animation.PlayMode.LOOP_PINGPONG);
        this.currentAnimator = idleAnimator;

        actors.updateBombs(this);
    }

    public void draw(){
        batch.begin();
        batch.draw(currentAnimator.getFrame(), position.x, position.y, width, height);
        //bombSprite.setPosition(position.x, position.y);
        //bombSprite.draw(batch);
        batch.end();

    }
    public void logic() {
        this.currentTime = System.nanoTime();
        if (currentTime >= detonationTime && !exploded) { //aqui detona mhmm~~~~~~
            exploded = true;
            currentAnimator = explosionAnimator;
        }
        if (exploded) {
            explosionTimer += Gdx.graphics.getDeltaTime();
            if (explosionTimer >= 2.2f) {
                //Gdx.app.error("Bomb", "EN TEORIA TUVO QUE DESTRUIR ESTO LMAOOO");
                Rectangle horizHB = new Rectangle(position.x*3, position.y*3, 48, 16);
                Rectangle vertHB = new Rectangle(position.x*3, position.y*3, 16, 48);
                Array<Rectangle> obstacles = tiledMap.getObstacles();
                for (Rectangle obstacle : obstacles) {
                    Gdx.app.error("BOMB", "OBSTACLE = " + obstacle.toString());
                    if (horizHB.overlaps(obstacle) || vertHB.overlaps(obstacle)) {
                    Gdx.app.error("BOMB", "ACCEPTED = " + obstacle.toString());
                        int tileX = (int) (obstacle.x / (tiledMap.getCollisionLayer().getTileWidth() * 3));
                        int tileY = (int) (obstacle.y / (tiledMap.getCollisionLayer().getTileHeight() * 3));
                        tiledMap.removeSingleCollision(tileX, tileY);
                        Gdx.app.error("Bomb", "EN TEORIA TUVO QUE DESTRUIR ESTO");
                    }
                }
                actors.removeBombs(this);
            }
        }
    }

    public void update(){
        draw();
        logic();
    }
}
