package io.github.JFW.Entitys;

import com.badlogic.gdx.math.Vector2;
import io.github.JFW.stateEnemy;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import io.github.JFW.MapEnv.*;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

//A* Algorithm
public class EnemyAlgorithm {
    private int maxCol = 18;
    private int maxRow = 18;

    //Algorithm specific
    private Node[][] node;
    private Node startNode;
    private Node currentNode;
    private Node goalNode;
    private ArrayList<Node> openList;
    private ArrayList<Node> checkedList;
    private ArrayList<Node> pathList;

    private boolean goalReached;
    private int limit;

    //Game specific
    private Map currentMap;
    private Enemy enemy;
    private Vector2 enemyposition;
    private stateEnemy.State state;

    public EnemyAlgorithm(Enemy enemy, Map currentMap) {
        super();
        int col = 0;
        int row = 0;
        this.limit = 0;
        this.node = new Node[maxCol][maxRow];
        this.goalReached = false;

        //initializing array
        while (col < maxCol && row < maxRow) {
            node[col][row] = new Node(col, row);
            row++;
            if (row == maxRow) {
                row = 0;
                col++;
            }
        }

        this.openList = new ArrayList<Node>();
        this.checkedList = new ArrayList<Node>();
        this.pathList = new ArrayList<Node>();
        this.enemy = enemy;
        this.enemyposition = enemy.getPosition();
        this.currentMap = currentMap;

        setNodes();
    }

    public stateEnemy.State optimalDirection() {
        if (pathList.isEmpty()) {
            return stateEnemy.State.STUCK;
        }

        // Get the next node in the path
        Node nextNode = pathList.get(0);
        
        // Calculate direction based on current position and next node
        float tileWidth = currentMap.getCollisionLayer().getTileWidth();
        float tileHeight = currentMap.getCollisionLayer().getTileHeight();
        
        // Get current position in tile coordinates
        float currentTileX = enemyposition.x / tileWidth;
        float currentTileY = fixingY((int)(enemyposition.y / tileHeight));
        
        // Determine direction based on next node position
        if (Math.abs(nextNode.col - currentTileY) > Math.abs(nextNode.row - currentTileX)) {
            // Move vertically first
            if (nextNode.col < currentTileY) {
                this.state = stateEnemy.State.UP;
            } else {
                this.state = stateEnemy.State.DOWN;
            }
        } else {
            // Move horizontally first
            if (nextNode.row < currentTileX) {
                this.state = stateEnemy.State.LEFT;
            } else {
                this.state = stateEnemy.State.RIGHT;
            }
        }

        // If current direction is blocked, try alternate direction
        if (!ValidDirection(this.state)) {
            if (this.state == stateEnemy.State.UP || this.state == stateEnemy.State.DOWN) {
                // Try horizontal movement
                if (nextNode.row < currentTileX) {
                    this.state = stateEnemy.State.LEFT;
                } else {
                    this.state = stateEnemy.State.RIGHT;
                }
            } else {
                // Try vertical movement
                if (nextNode.col < currentTileY) {
                    this.state = stateEnemy.State.UP;
                } else {
                    this.state = stateEnemy.State.DOWN;
                }
            }
        }

        return this.state;
    }

    private boolean ValidDirection(stateEnemy.State nextDirection) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float speed = enemy.getSpeed() * deltaTime;
        
        // Add a small offset to better handle tile edges
        float offset = 2f;
        
        switch(nextDirection) {
            case LEFT:
                return !enemy.isCollision(enemyposition.x - speed - offset, enemyposition.y);
            case RIGHT:
                return !enemy.isCollision(enemyposition.x + speed + offset, enemyposition.y);
            case DOWN:
                return !enemy.isCollision(enemyposition.x, enemyposition.y - speed - offset);
            case UP:
                return !enemy.isCollision(enemyposition.x, enemyposition.y + speed + offset);
            default:
                return false;
        }
    }

    public stateEnemy.State getState() {
        return state;
    }

    private boolean isDestructible(int x, int y) {
        Rectangle tileRect = new Rectangle(
            x * currentMap.getCollisionLayer().getTileWidth(),
            y * currentMap.getCollisionLayer().getTileHeight(),
            currentMap.getCollisionLayer().getTileWidth(),
            currentMap.getCollisionLayer().getTileHeight()
        );

        for (RectangleMapObject obstacle : currentMap.getObstaclesMO()) {
            if (obstacle.getRectangle().overlaps(tileRect)) {
                Object prop = obstacle.getProperties().get("Indestructible");
                if (prop != null && prop instanceof Boolean && !((Boolean)prop)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIndestructible(int x, int y) {
        Rectangle tileRect = new Rectangle(
            x * currentMap.getCollisionLayer().getTileWidth(),
            y * currentMap.getCollisionLayer().getTileHeight(),
            currentMap.getCollisionLayer().getTileWidth(),
            currentMap.getCollisionLayer().getTileHeight()
        );

        for (RectangleMapObject obstacle : currentMap.getObstaclesMO()) {
            if (obstacle.getRectangle().overlaps(tileRect)) {
                Object prop = obstacle.getProperties().get("Indestructible");
                if (prop != null && prop instanceof Boolean && ((Boolean)prop)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setNodes() {
        int[] EandPposition = enemy.getCenterPositions();
        EandPposition[1] = fixingY(EandPposition[1]);
        EandPposition[3] = fixingY(EandPposition[3]);

        setStartNode(EandPposition[1], EandPposition[0]);
        setGoalNode(EandPposition[3], EandPposition[2]);

        setAllSolidNodes();
        setCostNodes();

        autoSearch();
    }

    private int fixingY(int y) {
        int maxY = 14;
        return maxY - y + 1;
    }

    private void setStartNode(int col, int row) {
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
        openList.add(currentNode);
    }

    private void setGoalNode(int col, int row) {
        node[col][row].setAsGoal();
        goalNode = node[col][row];
    }

    private void setAllSolidNodes() {
        for (int i = 0; i < maxCol; i++) {
            for (int ii = 0; ii < maxRow; ii++) {
                if (isIndestructible(i, ii)) {
                    setSolidNode(fixingY(ii), i);
                } else if (isDestructible(i, ii)) {
                    setSolidNode(fixingY(ii), i);
                }
            }
        }
    }

    private void setSolidNode(int col, int row) {
        if (col >= 0 && col < maxCol && row >= 0 && row < maxRow) {
            node[col][row].setAsSolid();
        }
    }

    private void setCostNodes() {
        for (int i = 0; i < maxCol; i++) {
            for (int ii = 0; ii < maxRow; ii++) {
                getCost(node[i][ii]);
            }
        }
    }

    private void getCost(Node node) {
        //Get g cost
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;

        //Get h cost
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;

        //Get f cost
        node.fCost = node.gCost + node.hCost;
    }

    public void autoSearch() {
        while (!goalReached && !openList.isEmpty()) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            // Check all four directions
            checkNode(col, row - 1); // up
            checkNode(col, row + 1); // down
            checkNode(col - 1, row); // left
            checkNode(col + 1, row); // right

            // Find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                } else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            if (!openList.isEmpty()) {
                currentNode = openList.get(bestNodeIndex);
                if (currentNode == goalNode) {
                    goalReached = true;
                    trackThePath();
                }
            }
        }
    }

    private void checkNode(int col, int row) {
        if (col >= 0 && col < maxCol && row >= 0 && row < maxRow) {
            openNode(node[col][row]);
        }
    }

    private void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);
        }
    }

    private void trackThePath() {
        Node current = goalNode;
        while (current != startNode) {
            pathList.add(0, current);
            current = current.getParent();
        }
    }
}
