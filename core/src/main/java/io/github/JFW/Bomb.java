package io.github.JFW;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
public class Bomb extends Actor{

    private int EXPLOSION_RANGE;

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


    private SFXPlayer sfx;

    private Map tiledMap;

    //DEBUG
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Rectangle horizHB;
    private Rectangle vertHB;
    private Rectangle destroyLocation;

    private Rectangle explosionHitbox;
    private Rectangle hitbox;

    private Rectangle bombTileHitbox;
    private boolean playerInBomb = true;

    private Player player;

    public Bomb(SpriteBatch batch, float x ,float y, Actors actors, Map map){
        this.batch = batch;
        this.position = new Vector2(x-24,y-31.5f);
        this.exploded = false;
        this.explosionTimer = 0f;

        this.EXPLOSION_RANGE = 1;

        this.tiledMap = map;
        this.currentTime = System.nanoTime();
        this.detonationTime = currentTime + 2000000000; //2 segundos
        this.actors = actors;

        //Animations!
        this.idleAnimator = new Animator("bombSpriteSheet.png", 8, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.explosionAnimator = new Animator("bombSpriteSheet.png", 8, 1, 3, 7, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.currentAnimator = idleAnimator;

        bombTileHitbox = new Rectangle(
            position.x,
            position.y,
            width + 35,
            height + 35
        );

        this.sfx = new SFXPlayer();

        actors.updateBombs(this);

        player = Player.getInstance();
    }

    public void draw(){
        batch.begin();
        batch.draw(currentAnimator.getFrame(), position.x+24, position.y+30, width, height);
        if (exploded) {
            debugDraw();
        }

        batch.end();

    }


    public void logic() {
        this.currentTime = System.nanoTime();
        if (!bombTileHitbox.contains(player.getBoundingBox())) {
            playerInBomb = false;
        }
        int tileXX = (int) ((position.x + 48 / 2) / tiledMap.getCollisionLayer().getTileWidth());
        int tileYY = (int) ((position.y + 48) / tiledMap.getCollisionLayer().getTileHeight());
        /*if (!playerInBomb) {
            System.out.println("AFUERA DE LA PUTA BOMBA");
            tiledMap.addSingleCollision(tileXX,tileYY, "mhmm~~~");
        }*/
        if (currentTime >= detonationTime && !exploded) {
            exploded = true;
            sfx.playSFX("sound/explodeBomb.mp3");
            currentAnimator = explosionAnimator;
        }
        if(!exploded){
            if (!playerInBomb) {
                System.out.println("AFUERA DE LA PUTA BOMBA");
                tiledMap.addSingleCollision(tileXX,tileYY, "Bomb");
            }
        }

        if (exploded) {
            explosionTimer += Gdx.graphics.getDeltaTime();
            if (explosionTimer >= 1f) {
                float tileWidth = tiledMap.getCollisionLayer().getTileWidth();
                float tileHeight = tiledMap.getCollisionLayer().getTileHeight();

                int x = 7;

                horizHB = new Rectangle(
                    position.x + width / 2 - tileWidth * ((EXPLOSION_RANGE * 2) / 2.0f),
                    (position.y + height / 2 - tileHeight / 2) + 10,
                    tileWidth * EXPLOSION_RANGE * 2,
                    tileHeight - 20
                );


                vertHB = new Rectangle(
                    (position.x + width / 2 - tileWidth / 2) + 10,
                    position.y + height / 2 - tileHeight * ((EXPLOSION_RANGE * 2) / 2.0f),
                    tileWidth - 20,
                    tileHeight * EXPLOSION_RANGE * 2
                );

                /*explosionHitbox = new Rectangle(
                    position.x - tileWidth + 5,
                    position.y - tileHeight + 5,
                    (tileWidth * 3) -10,
                    (tileHeight * 3) - 10
                );*/

                if (horizHB.overlaps(player.getBoundingBox()) || vertHB.overlaps(player.getBoundingBox())) {
                    Gdx.app.error("Bomb", "Player hit by bomb at: " + position.toString());
                }

                // First get the bomb's center tile position
                int centerTileX = (int) ((position.x + width / 2) / tiledMap.getCollisionLayer().getTileWidth());
                int centerTileY = (int) ((position.y + 48 + height / 2) / tiledMap.getCollisionLayer().getTileHeight());

                // cross pattern
                //arreglar, tiene que ser mas espacios!
                for (int i = 0; i <= EXPLOSION_RANGE; i++) { //wut is bro doing
                    int[][] tilesToCheck = {
                        {centerTileX, centerTileY},     // Center
                        {centerTileX - i, centerTileY},   // Left
                        {centerTileX + i, centerTileY},   // Right
                        {centerTileX, centerTileY - i},   // Down
                        {centerTileX, centerTileY + i}    // Up
                    };

                    for (int[] tilePos : tilesToCheck) {
                        int tileX = tilePos[0];
                        int tileY = tilePos[1];

                        Rectangle tileRect = new Rectangle(
                            tileX * tiledMap.getCollisionLayer().getTileWidth(),
                            tileY * tiledMap.getCollisionLayer().getTileHeight(),
                            tiledMap.getCollisionLayer().getTileWidth(),
                            tiledMap.getCollisionLayer().getTileHeight()
                        );

                        // Check if this tile contains an obstacle
                        for (RectangleMapObject obstacle : tiledMap.getObstaclesMO()) {
                        /*if(obstacle.getProperties().get("Indestructible") != null) {
                            Gdx.app.error("IND", "SE ENCONTRO CON LA VARA INDESTRUCTIBLE");
                            if(obstacle.getProperties().get("Indestructible") == "true"){
                                return;
                            }
                        }*/
                            if (obstacle.getRectangle().overlaps(tileRect)) {
                                tiledMap.removeSingleCollision(tileX, tileY);
                                break;
                            }
                        }
                    }
                }

                if (explosionTimer >= 1.36f) { // espera a que la animacion termine
                    tiledMap.removeSingleCollision(tileXX, tileYY);
                    actors.removeBombs(this);
                }


            }
        }
    }


    public void debugDraw(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (horizHB != null && vertHB != null) {

            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(horizHB.x, horizHB.y, horizHB.width, horizHB.height);
            shapeRenderer.rect(vertHB.x, vertHB.y, vertHB.width, vertHB.height);
            if (destroyLocation != null) {
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(destroyLocation.x, destroyLocation.y, destroyLocation.width, destroyLocation.height);
            }

        }

        if(explosionHitbox != null){
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(explosionHitbox.x, explosionHitbox.y, explosionHitbox.width, explosionHitbox.height);
        }

        shapeRenderer.end();
    }

    public void update(){
        logic();
        draw();
    }
}
