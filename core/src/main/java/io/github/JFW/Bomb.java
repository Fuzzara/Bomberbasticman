package io.github.JFW;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.graphics.Color;

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

    //DEBUG
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Rectangle horizHB;
    private Rectangle vertHB;
    private Rectangle destroyLocation;

    public Bomb(SpriteBatch batch, float x ,float y, Actors actors, Map map){
        this.batch = batch;
        this.position = new Vector2(x-24,y-31.5f);
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
        batch.draw(currentAnimator.getFrame(), position.x+24, position.y+30, width, height);
        //bombSprite.setPosition(position.x, position.y);
        //bombSprite.draw(batch);
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(100,100,100,100);
        if (horizHB != null && vertHB != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(horizHB.x, horizHB.y, horizHB.width, horizHB.height);
            Gdx.app.error("Bomb", "HORIZ = " + horizHB.toString()+ " VERT = " + vertHB.toString());
            shapeRenderer.rect(vertHB.x, vertHB.y, vertHB.width, vertHB.height);
        }
        shapeRenderer.end();*/
        if (exploded) {
            debugDraw();
        }
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
                Gdx.app.error("Bomb", "AYUDA");
                float tileWidth = tiledMap.getCollisionLayer().getTileWidth();
                float tileHeight = tiledMap.getCollisionLayer().getTileHeight();

                // Adjust the hitboxes to be smaller
                horizHB = new Rectangle(
                    position.x + width / 2 - tileWidth * 1.5f,
                    (position.y + height / 2 - tileHeight / 2) + 10,
                    tileWidth * 3,
                    tileHeight - 20
                );

                vertHB = new Rectangle(
                    (position.x + width / 2  - tileWidth / 2) + 10,
                    position.y + height / 2 - tileHeight * 1.5f,
                    tileWidth - 20,
                    tileHeight * 3
                );

                Gdx.app.error("Bomb", "horizHB = " + horizHB.toString());
                Gdx.app.error("Bomb", "vertHB = " + vertHB.toString());

                Array<Rectangle> obstacles = tiledMap.getObstacles();
                for (Rectangle obstacle : obstacles) {
                    if (horizHB.overlaps(obstacle) || vertHB.overlaps(obstacle)) {
                        Gdx.app.error("BOMB", "ACCEPTED = " + obstacle.toString());
                        int tileX = (int) ((obstacle.x) / (tiledMap.getCollisionLayer().getTileWidth()));
                        int tileY = (int) ((obstacle.y) / (tiledMap.getCollisionLayer().getTileHeight()));
                        destroyLocation = new Rectangle(tileX * tiledMap.getCollisionLayer().getTileWidth(),
                                                        tileY * tiledMap.getCollisionLayer().getTileHeight(),
                                                        tiledMap.getCollisionLayer().getTileWidth(),
                                                        tiledMap.getCollisionLayer().getTileHeight());
                        //debug tile location
                        Gdx.app.error("DESTROY", "X: " + tileX + " Y: " + tileY);
                        tiledMap.removeSingleCollision(tileX, tileY);
                        Gdx.app.error("Bomb", "EN TEORIA TUVO QUE DESTRUIR ESTO");
                    }
                }
                if (explosionTimer >= 4.2f) { // Wait an additional 2 seconds before removing the bomb
                    actors.removeBombs(this);
                }
            }
        }
    }

    public void debugDraw(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        /*shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(384.0f,480.0f,144,48.0f);*/
        if(horizHB != null)Gdx.app.error("Bomb", "HORIZ = " + horizHB.toString()+ " VERT = " + vertHB.toString());
        if (horizHB != null && vertHB != null) {
            //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            //shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(horizHB.x, horizHB.y, horizHB.width, horizHB.height);
            shapeRenderer.rect(vertHB.x, vertHB.y, vertHB.width, vertHB.height);
            if (destroyLocation != null) {
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(destroyLocation.x, destroyLocation.y, destroyLocation.width, destroyLocation.height);
            }
            Gdx.app.error("Bomb", "HORIZ = " + horizHB.toString()+ " VERT = " + vertHB.toString());
        }
        shapeRenderer.end();
    }

    public void update(){
        logic();
        draw();
    }
}
