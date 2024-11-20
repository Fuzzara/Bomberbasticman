package io.github.JFW.Entities.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.JFW.Entities.Player.Player;
import io.github.JFW.MapEnv.Map;
import io.github.JFW.System.Scoreboard;
import io.github.JFW.Graphics.Animator;
import io.github.JFW.Graphics.SpriteBatchHandler;
import io.github.JFW.States.stateEnemy;

import java.util.Random;

public class Enemy extends Actor {

    // Constantes
    private static final float SPRITE_WIDTH = 48;
    private static final float SPRITE_HEIGHT = 96;
    private static final float BOUNDING_BOX_SIZE = 34;
    private static final float BOUNDING_BOX_OFFSET = 23;
    private static final float DIRECTION_CHANGE_INTERVAL = 2.0f; // Change direction every 2 seconds

    // Atributos varios
    private final boolean noclip;
    private final int ai;
    private final Vector2 position;
    private final float speed;
    private final int score;

    // Sprites
    private final SpriteBatch batch;
    private Animator animation;

    // Colisiones
    private final Rectangle boundingBox;
    private final Player player;
    private Map currentMap;

    // IA
    private EnemyAlgorithm algo;
    private stateEnemy.State nextDirection;
    private int[] lastXY;

    // States y renderizacion
    private final stateEnemy state;
    private ShapeRenderer shapeRenderer;
    private Scoreboard scoreboard;
    private float directionChangeTimer = 0;

    public Enemy(float speed, float x, float y, int score, int ai, boolean noclip, Map currentMap, Animator animation) {
        this.speed = speed;
        this.position = new Vector2(x, y);
        this.score = score;
        this.ai = ai;
        this.noclip = noclip;
        this.currentMap = currentMap;
        this.animation = animation;
        this.batch = SpriteBatchHandler.getBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.boundingBox = new Rectangle(position.x - BOUNDING_BOX_OFFSET, position.y - BOUNDING_BOX_OFFSET, BOUNDING_BOX_SIZE, BOUNDING_BOX_SIZE);
        this.player = Player.getInstance();
        this.state = new stateEnemy();
        this.scoreboard = Scoreboard.getInstance();
        this.lastXY = new int[]{99, 99};
    }

    // Devuelve la caja de colisión del enemigo
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    // Verifica si hay colisión en las coordenadas dadas
    public boolean isCollision(float x, float y) {
        Rectangle enemyRect = new Rectangle(x, y, boundingBox.width, boundingBox.height);
        if (noclip) {
            return checkNoclipCollision(enemyRect);
        } else {
            return checkNormalCollision(enemyRect);
        }
    }

    // Dibuja el enemigo en la pantalla
    public void draw() {
        batch.begin();
        batch.draw(animation.getFrame(), position.x, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);
        batch.end();
    }

    // Actualiza el estado del enemigo
    public void update() {
        algorithm();
        updateBoundingBox();
        draw();
    }

    // Devuelve la posición del enemigo
    public Vector2 getPosition() {
        return position;
    }

    // Devuelve la velocidad del enemigo
    public float getSpeed() {
        return speed;
    }

    // Devuelve la puntuación del enemigo
    public int getScore() {
        return score;
    }

    // Devuelve las posiciones centrales del enemigo y del jugador
    public int[] getCenterPositions() {
        int centerTileX = (int) ((position.x + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        int centerTileY = (int) ((position.y + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        Vector2 playerPosition = player.getPosition();
        int centerTileXP = (int) ((playerPosition.x + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        int centerTileYP = (int) ((playerPosition.y + SPRITE_WIDTH / 2) / currentMap.getCollisionLayer().getTileWidth());
        return new int[]{centerTileX, centerTileY, centerTileXP, centerTileYP};
    }

    // Elige una dirección aleatoria para el enemigo
    private void choosingDirection() {
        Random rand = new Random();
        int randomDirection = rand.nextInt(4);
        switch (randomDirection) {
            case 0 -> state.setCurrentState(stateEnemy.State.DOWN);
            case 1 -> state.setCurrentState(stateEnemy.State.UP);
            case 2 -> state.setCurrentState(stateEnemy.State.RIGHT);
            case 3 -> state.setCurrentState(stateEnemy.State.LEFT);
        }
    }

    // Mueve al enemigo de manera aleatoria
    private void moveRandomly() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        directionChangeTimer += deltaTime;
        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL) {
            choosingDirection();
            directionChangeTimer = 0;
        }
        if (state.getCurrentState() == stateEnemy.State.STUCK) {
            choosingDirection();
        }
        switch (state.getCurrentState()) {
            case LEFT -> move(-speed * deltaTime, 0);
            case RIGHT -> move(speed * deltaTime, 0);
            case UP -> move(0, speed * deltaTime);
            case DOWN -> move(0, -speed * deltaTime);
        }
    }

    // Mueve al enemigo en las coordenadas dadas
    private void move(float dx, float dy) {
        float newX = position.x + dx;
        float newY = position.y + dy;
        if (!isCollision(newX, newY)) {
            position.set(newX, newY);
            updateBoundingBox();
        } else {
            state.setCurrentState(stateEnemy.State.STUCK);
            choosingDirection();
        }
    }

    // Mueve al enemigo en la siguiente direccion
    private void movetoNextDirection() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        switch (nextDirection) {
            case LEFT -> {
                move(-speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.LEFT);
            }
            case RIGHT -> {
                move(speed * deltaTime, 0);
                state.setCurrentState(stateEnemy.State.RIGHT);
            }
            case DOWN -> {
                move(0, -speed * deltaTime);
                state.setCurrentState(stateEnemy.State.DOWN);
            }
            case UP -> {
                move(0, speed * deltaTime);
                state.setCurrentState(stateEnemy.State.UP);
            }
            case STUCK -> moveRandomly();
        }
    }

    // Ejecuta el algoritmo de IA del enemigo
    private void algorithm() {
        algo = new EnemyAlgorithm(this, currentMap);
        this.nextDirection = algo.optimalDirection();
        movetoNextDirection();
    }

    // Actualiza la caja de colisión del enemigo
    private void updateBoundingBox() {
        boundingBox.setPosition(position.x - BOUNDING_BOX_OFFSET, position.y - 34);
    }

    // Verifica colisiones cuando el enemigo tiene noclip
    private boolean checkNoclipCollision(Rectangle enemyRect) {
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject rectObject) {
                if (Boolean.TRUE.equals(rectObject.getProperties().get("Bomb"))) {
                    Rectangle rect = rectObject.getRectangle();
                    if (rect.overlaps(enemyRect)) {
                        return true;
                    }
                }
                if (Boolean.TRUE.equals(rectObject.getProperties().get("Indestructible"))) {
                    Rectangle rect = rectObject.getRectangle();
                    if (rect.overlaps(enemyRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Verifica colisiones normales del enemigo
    private boolean checkNormalCollision(Rectangle enemyRect) {
        for (MapObject object : currentMap.getCollisionLayer().getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.overlaps(enemyRect)) {
                    return true;
                }
            }
        }
        if (enemyRect.overlaps(player.getBoundingBoxEnemy())) {
            scoreboard.removeLife();
        }
        return false;
    }
}
