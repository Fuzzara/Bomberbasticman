package io.github.JFW.Entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import io.github.JFW.Entities.Actors;
import io.github.JFW.Entities.Items.BombManager;
import io.github.JFW.System.GlobalAccess;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.Graphics.Animator;
import io.github.JFW.System.InputHandler;
import com.badlogic.gdx.maps.MapObject;
import io.github.JFW.Audio.SFXPlayer;
import io.github.JFW.Graphics.SpriteBatchHandler;
import io.github.JFW.States.statePlayer;

import java.util.EnumSet;
import java.util.Set;

public class Player extends Actor {
    private static Player instance; // Singleton instance
    private Actors actors;

    // Constantes
    private static final int INITIAL_HP = 3;
    private static final float INITIAL_SPEED = 120;
    private static final Vector2 INITIAL_POSITION = new Vector2(96, 630);
    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 24;
    private static final int BOMB_LIMIT = 0;

    // Stats
    private int hp;
    private int bombLimit = BOMB_LIMIT;

    private Set<statePlayer.PowerUpType> activePowerUps = EnumSet.noneOf(statePlayer.PowerUpType.class);
    private boolean isFireProtectedTemp = false;
    private float fireProtectionTime = 60f;

    // Posiciones cosas
    private Vector2 position;
    private float speed;
    private InputHandler inputHandler;

    private Sprite bomberSprite;
    private SpriteBatch batch;

    // Bomb stuff
    private BombManager bombManager;

    // Animaciones
    private final Animator upAnimator;
    private final Animator downAnimator;
    private final Animator leftAnimator;
    private final Animator rightAnimator;
    private Animator currentAnimator;
    private Animator deathAnimator;
    private Animator winAnimator;

    // Colisiones
    private Rectangle boundingBox;
    private ShapeRenderer shapeRenderer;
    private Map currentMap;

    // Sonido
    private float walkSoundTime;
    private SFXPlayer sfx;

    private boolean isDead;
    private boolean isInvincible;
    private float invincibleTime;

    // States
    statePlayer state;

    private Player(Actors actors, Map currentMap) {
        this.actors = actors;
        this.currentMap = currentMap;
        this.hp = INITIAL_HP;
        this.speed = INITIAL_SPEED;
        this.position = new Vector2(INITIAL_POSITION);
        this.isDead = false;
        this.isInvincible = false;
        this.invincibleTime = 0f;
        this.state = new statePlayer();
        this.inputHandler = new InputHandler();
        this.batch = SpriteBatchHandler.getBatch();

        this.sfx = new SFXPlayer();

        // Sprite and rendering
        Texture bomberTexture = new Texture("bomberTexture.png");
        this.bomberSprite = new Sprite(bomberTexture);
        this.bomberSprite.setSize(SPRITE_WIDTH, SPRITE_HEIGHT);
        this.bomberSprite.setPosition(position.x, position.y);

        this.bombManager = new BombManager(actors, currentMap);

        this.boundingBox = new Rectangle(position.x - BOUNDING_BOX_OFFSET, position.y - BOUNDING_BOX_OFFSET, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        this.shapeRenderer = new ShapeRenderer();

        this.downAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 0, 2, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.rightAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 3, 5, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.upAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 6, 8, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.leftAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 9, 11, 0.5f, Animation.PlayMode.LOOP_PINGPONG);
        this.deathAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 12, 18, 0.1f, Animation.PlayMode.NORMAL);
        this.winAnimator = new Animator("bomberSpriteSheet.png", 29, 1, 19, 28, 0.2f, Animation.PlayMode.NORMAL);

        this.currentAnimator = downAnimator; // default
    }

    // Cambia el mapa actual
    public void setMap(Map map) {
        this.currentMap = map;
        this.bombManager.setMap(map);
    }

    // Le da un power-up al jugador
    public void applyPowerUp(statePlayer.PowerUpType type) {
        activePowerUps.add(type);
        switch (type) {
            case SUN: // AF
                // +2 Alcance de bomba
                break;
            case GOLDEN_BOMB: // AF
                // +1 Bomba
                bombLimit = BOMB_LIMIT + 1;
                break;
            case DETONATOR:
                // Spacebar para detonar
                break;
            case SKATES: // AF
                // 1.5x velocidad
                speed = INITIAL_SPEED * 1.5f;
                break;
            case STRIPPED_BOMB:
                // Atravesar bombas
                break;
            case STRIPPED_WALL:
                // Atravesar paredes
                break;
            case QUESTION_MARK:
                // Invulnerabilidad al fuego 60 segundos
                float delta = Gdx.graphics.getDeltaTime();
                isFireProtectedTemp = true;
                break;
            case FIRE_MAN:
                // Invulnerabilidad al fuego
                break;
        }
    }

    // Le quita un powerup al jugador
    public void removePowerUp(statePlayer.PowerUpType type) {
        activePowerUps.remove(type);
        switch (type) {
            case SUN:
                // AF, no se quita
                break;
            case GOLDEN_BOMB:
                // AF, no se quita
                break;
            case DETONATOR:
                // Remove Detonator effect
                break;
            case SKATES:
                // AF, no se quita
                break;
            case STRIPPED_BOMB:
                // Remove Stripped Bomb effect
                break;
            case STRIPPED_WALL:
                // Remove Stripped Wall effect
                break;
            case QUESTION_MARK:
                break;
            case FIRE_MAN:
                break;
        }
    }

    // Verifica si el jugador tiene algun powerup específico
    public boolean hasPowerUp(statePlayer.PowerUpType type) {
        return activePowerUps.contains(type);
    }

    // Define parametros del jugador como actors y el mapa
    public static Player getInstance(Actors actors, Map currentMap) {
        if (instance == null) {
            instance = new Player(actors, currentMap);
        }
        return instance;
    }

    // Obtiene la instancia del jugador (singleton)
    public static Player getInstance() {
        return instance;
    }

    // renderiza el jugador
    public void draw() {
        batch.begin();
        batch.draw(currentAnimator.getFrame(), position.x, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);
        batch.end();
    }

    // Maneja el input del jugador
    private void handleInput() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        statePlayer.State currentState = inputHandler.handlePlayerMovement();
        if (currentState != null) {
            state.setCurrentState(currentState);
            switch (currentState) {
                case LEFT:
                    move(-speed * deltaTime, 0);
                    currentAnimator = leftAnimator;
                    break;
                case RIGHT:
                    move(speed * deltaTime, 0);
                    currentAnimator = rightAnimator;
                    break;
                case UP:
                    move(0, speed * deltaTime);
                    currentAnimator = upAnimator;
                    break;
                case DOWN:
                    move(0, -speed * deltaTime);
                    currentAnimator = downAnimator;
                    break;
                case DEAD:
                    currentAnimator = deathAnimator;
                    break;
            }
        }

        if (currentState != null) {
            currentAnimator.getFrame();
            playSound();
        } else {
            currentAnimator.reset(0.5f);
            walkSoundTime = 0f;
        }

        bombManager.handleBombPlacement(position, Gdx.graphics.getDeltaTime());

        if (inputHandler.usedDetonator()) {
            actors.useDetonator();
        }
    }

    // Mueve al jugador
    private void move(float dx, float dy) {
        float newX = position.x + dx;
        float newY = position.y + dy;
        if (!isCollision(newX, newY)) {
            position.set(newX, newY);
            updateBoundingBox();
        }
    }

    // Verifica si hay colisión en la nueva posición
    private boolean isCollision(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(playerRect)) {
                    if (object.getProperties().containsKey("Pass-Through")) {
                        if (this.hasPowerUp(statePlayer.PowerUpType.STRIPPED_WALL)) {
                            return false; // Allow passing through the wall
                        } else {
                            return true; // Block the player
                        }
                    }
                    if (object.getProperties().containsKey("Bomb")) {
                        if (this.hasPowerUp(statePlayer.PowerUpType.STRIPPED_BOMB)) {
                            return false; // Allow passing through the bomb
                        } else {
                            return true; // Block the player
                        }
                    }
                    if (object.getProperties().containsKey("KYS")) {
                        Gdx.app.log("Player", "Player reached the door");
                        return false;
                    }
                    return true; // Block the player for non-pass-through walls
                }
            }
        }
        return false; // No collision detected
    }

    // Footsteps epicos
    private void playSound() {
        walkSoundTime += Gdx.graphics.getDeltaTime();
        if (walkSoundTime >= 0.5f) {
            sfx.playSFX("sound/Walking-1.mp3");
            walkSoundTime = 0f;
        }
    }

    // Ontas
    public Vector2 getPosition() {
        return position;
    }

    // Actualiza la caja de colisión del jugador
    private void updateBoundingBox() {
        boundingBox.setPosition(position.x - 24, position.y - 24);
    }

    // Obtiene boundingbox del jugador
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    // Obtiene la boundingbox del jugador para enemigos
    public Rectangle getBoundingBoxEnemy() {
        return new Rectangle(boundingBox.x + 12, boundingBox.y + 12, 48, 48);
    }

    // Obtiene la velocidad del jugador
    public float getSpeed() {
        return speed;
    }

    // hp?
    public int getHP() {
        return hp;
    }

    // hp -> this.hp ;)
    public void setHP(int hp) {
        this.hp = hp;
    }

    // NOOOO PORQUE SE MUERE BRO
    public void die(float delta) {
        if (isDead) {
            while (invincibleTime <= 3f) {
                invincibleTime += delta;
            }
            respawn();
            return;
        }

        isDead = true;
        this.hp--;
        this.currentAnimator = deathAnimator;
        removeNotAFPowerUps();
        sfx.playSFX("sound/dead.mp3");
        invincibleTime = 0f;
    }

    // WUU WIN GANOOO LETS GOO
    public void win() {
        this.currentAnimator = winAnimator;
        state.setCurrentState(statePlayer.State.DOOR);
    }

    // Respawnea al jugador
    public void respawn() {
        isDead = false;
        position.set(INITIAL_POSITION);
        this.currentAnimator = downAnimator;
        boundingBox.setPosition(INITIAL_POSITION.x - 24, INITIAL_POSITION.y - 24);
        isInvincible = true;
        invincibleTime = 0f;
    }

    // Elimina los powerups que no son AF
    public void removeNotAFPowerUps() {
        removePowerUp(statePlayer.PowerUpType.DETONATOR);
        removePowerUp(statePlayer.PowerUpType.STRIPPED_BOMB);
        removePowerUp(statePlayer.PowerUpType.STRIPPED_WALL);
        removePowerUp(statePlayer.PowerUpType.QUESTION_MARK);
        removePowerUp(statePlayer.PowerUpType.FIRE_MAN);
    }

    // Actualiza el estado del jugador
    public void update() {
        if (isInvincible) {
            if (GlobalAccess.getInstance().isBonusLevel()) {
                isInvincible = true;
            } else {
                invincibleTime += Gdx.graphics.getDeltaTime();
                if (invincibleTime >= 2f) {
                    isInvincible = false;
                    invincibleTime = 0f;
                }
            }
        }
        if (isFireProtectedTemp) {
            System.out.println("Fire protection time: " + fireProtectionTime);
            fireProtectionTime -= Gdx.graphics.getDeltaTime();
            if (fireProtectionTime <= 0) {
                removePowerUp(statePlayer.PowerUpType.QUESTION_MARK);
                isFireProtectedTemp = false;
            }
        }
        if (!isDead) {
            handleInput();
        }

        updateBoundingBox();
        draw();
    }

    // Invencible?
    public boolean getInvincible() {
        return isInvincible;
    }

    // Le da el mapa actual del jugador
    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    // Obtiene el límite de bombas del jugador
    public int getBombLimit() {
        return bombLimit;
    }
}
