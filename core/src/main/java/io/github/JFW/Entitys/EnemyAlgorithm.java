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
    //esquina izquierda arriba (1,14)
    //jugable desde (2,14)

    //esquina derecha abajo (16,1)
    //jugable desde (16,2)


    //esquina izquierda arriba (2,13)
    //esquina derecha abajo (16,1)

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


    //g cost = the distance between the current node and the start node
    //h cost = the distance from the current node to the goal node
    //f cost = the total cost (g+h) of the node


    public EnemyAlgorithm(Enemy enemy,Map currentMap){
        int col = 0;
        int row = 0;
        this.limit = 0;
        this.node = new Node[maxCol][maxRow];
        this.goalReached = false;

        //initializing array
        while (col<maxCol && row < maxRow){
            node[col][row] = new Node(col,row);
            row++;
            if (row == maxRow){
                row = 0;
                col ++;
            }
        }

        this.openList = new ArrayList<>();
        this.checkedList = new ArrayList<>();
        this.pathList = new ArrayList<>();
        this.enemy = enemy;
        this.enemyposition = enemy.getPosition();
        this.currentMap = currentMap;

        setNodes();
        viewingmatrix();
    }

    public stateEnemy.State optimalDirection(){

        if(pathList.isEmpty()){
            Gdx.app.error("Enemy","is Stuck");
            return stateEnemy.State.STUCK;

        }

        if (pathList.getFirst().getCol() < startNode.getCol() && pathList.getFirst().getRow() == startNode.getRow()){
            Gdx.app.error("Enemy","going left");
            this.state = stateEnemy.State.LEFT;
            horizontal();

        }

        else if(pathList.getFirst().getCol() > startNode.getCol() && pathList.getFirst().getRow() == startNode.getRow()){
            Gdx.app.error("Enemy","going right");
            this.state = stateEnemy.State.RIGHT;
            horizontal();

        }

        else if (pathList.getFirst().getRow() < startNode.getRow() && pathList.getFirst().getCol() == startNode.getCol()){
            Gdx.app.error("Enemy","going up");
            this.state = stateEnemy.State.UP;
            vertical();

        }

        else if (pathList.getFirst().getRow() > startNode.getRow() && pathList.getFirst().getCol() == startNode.getCol()){
            Gdx.app.error("Enemy","going down");
            this.state = stateEnemy.State.DOWN;
            vertical();

        }

        return this.state;
    }

    private void horizontal(){
        Vector2 currentposition = enemyposition;
        Vector2 nextposition = getActualCenterPosition(pathList.getFirst().getCol(),pathList.getFirst().getRow());
        if(!ValidDirection(state)){
            if(currentposition.y > nextposition.y){ //logica al reves
                this.state = stateEnemy.State.UP;
                Gdx.app.error("nvm","going up");
            }
            else if(currentposition.y < nextposition.y){ //logica al reves
                this.state = stateEnemy.State.DOWN;
                Gdx.app.error("nvm","going down");
            }
        }
    }

    private void vertical(){
        Vector2 currentposition = enemyposition;
        Vector2 nextposition = getActualCenterPosition(pathList.getFirst().getCol(),pathList.getFirst().getRow());
        if(!ValidDirection(this.state)){
            if(currentposition.x > nextposition.x){
                this.state = stateEnemy.State.LEFT;
                Gdx.app.error("nvm","going left");
            }
            else if(currentposition.x < nextposition.x){
                this.state = stateEnemy.State.RIGHT;
                Gdx.app.error("nvm","going right");
            }
        }
    }



    private Vector2 getActualCenterPosition(int centerTileX,int centerTileY){
        float x = (centerTileX * currentMap.getCollisionLayer().getTileWidth()) - (48/2.0f);
        float y = (centerTileY * currentMap.getCollisionLayer().getTileWidth()) - (48/2.0f);
        return new Vector2(x,y);
    }

    private boolean ValidDirection(stateEnemy.State nextDirection){
        float deltaTime = Gdx.graphics.getDeltaTime();
        switch(nextDirection){
            case LEFT:
                return !enemy.isCollision(enemyposition.x + -enemy.getSpeed() * deltaTime, enemyposition.y);
            case RIGHT:
                return !enemy.isCollision(enemyposition.x + enemy.getSpeed() * deltaTime, enemyposition.y);
            case DOWN:
                return !enemy.isCollision(enemyposition.x, enemyposition.y + -enemy.getSpeed() * deltaTime);
            case UP:
                return !enemy.isCollision(enemyposition.x, enemyposition.y + enemy.getSpeed() * deltaTime);
        }
        return false;
    }


    public stateEnemy.State getState(){
        return state;
    }

    public void setNodes(){
        //Start and Goal Nodes
        int[] EandPposition = enemy.getCenterPositions();
        EandPposition[1] = fixingY(EandPposition[1]);
        EandPposition[3] = fixingY(EandPposition[3]);
        setStartNode(EandPposition[0],EandPposition[1]);
        setGoalNode(EandPposition[2],EandPposition[3]);

        //Obstacles or Solid Nodes
        setAllSolidNodes();
        setCostNodes();

        //Search for a path
        autoSearch();
    }


    private int fixingY(int y){
        int maxY = 14;
        return maxY-y+1;
    }

    private boolean isDestructible(int x, int y){
        Rectangle tileRect = new Rectangle(
            x * currentMap.getCollisionLayer().getTileWidth(),
            y * currentMap.getCollisionLayer().getTileHeight(),
            currentMap.getCollisionLayer().getTileWidth(),
            currentMap.getCollisionLayer().getTileHeight()
        );

        for (RectangleMapObject obstacle : currentMap.getObstaclesMO()) {
            if (obstacle.getRectangle().overlaps(tileRect)) {
                if (Boolean.FALSE.equals(obstacle.getProperties().get("Indestructible"))){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIndestructible(int x, int y){
        Rectangle tileRect = new Rectangle(
            x * currentMap.getCollisionLayer().getTileWidth(),
            y * currentMap.getCollisionLayer().getTileHeight(),
            currentMap.getCollisionLayer().getTileWidth(),
            currentMap.getCollisionLayer().getTileHeight()
        );

        for (RectangleMapObject obstacle : currentMap.getObstaclesMO()) {
            if (obstacle.getRectangle().overlaps(tileRect)) {
                if (Boolean.TRUE.equals(obstacle.getProperties().get("Indestructible"))){
                    return true;
                }
            }
        }
        return false;
    }

    private void setStartNode(int col, int row){
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
        openList.add(currentNode);
    }

    private void setGoalNode(int col, int row){
        node[col][row].setAsGoal();
        goalNode = node[col][row];
    }

    private void setAllSolidNodes(){
        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                if (isIndestructible(i,ii)){
                    setSolidNode(fixingY(ii),i);
                }
                else if (isDestructible(i,ii)){
                    setSolidNode(fixingY(ii),i);;
                }
            }
        }
    }


    private void setSolidNode(int col, int row){
        node[col][row].setAsSolid();
    }

    private void setCostNodes(){
        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                getCost(node[ii][i]);
            }
        }
    }

    private void getCost(Node node){
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



    public void autoSearch(){
        while(goalReached == false){
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            //open the up node
            if(row-1>=0){
                openNode(node[col][row-1]);
            }
            //open the down node
            if(row+1<maxRow){
                openNode(node[col][row+1]);
            }
            //open the left node
            if (col-1>=0){
                openNode(node[col-1][row]);
            }
            //open the right node
            else if (col+1<maxCol){
                openNode(node[col+1][row]);
            }


            //find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for(int i=0; i<openList.size();i++){
                //Check if this node's f cost is better
                if(openList.get(i).fCost < bestNodefCost){
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                else if(openList.get(i).fCost == bestNodefCost){
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost){
                        bestNodeIndex = i;
                    }
                }
            }
            //get the best node
            if (openList.isEmpty()){
                break;
            }

            currentNode = openList.get(bestNodeIndex);
            if (currentNode == goalNode){
                goalReached = true;
                trackThePath();
            }

            limit++;
        }
    }


    private void openNode(Node node){
        if (node.open == false && node.checked == false && node.solid == false){
            //if the node is not opened yet, add it to the open list
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);

        }
    }

    private void trackThePath(){
        //Backtrack and draw the best path
        Node current = goalNode;

        while(current != startNode){
            pathList.addFirst(current);
            current = current.getParent();

        }
    }

    public void resetNodes(){
        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                node[ii][i].open = false;
                node[ii][i].checked = false;
                node[ii][i].solid = false;
                node[ii][i].start = false;
                node[ii][i].goal = false;

            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
        limit = 0;
    }

    private void viewingmatrix(){
        int[] EandPposition = enemy.getCenterPositions();
        EandPposition[1] = fixingY(EandPposition[1]);
        EandPposition[3] = fixingY(EandPposition[3]);
        setStartNode(EandPposition[0],EandPposition[1]);
        setGoalNode(EandPposition[2],EandPposition[3]);


        System.out.println("Player position "+EandPposition[2]+" "+EandPposition[3]);
        System.out.println("Enemy position "+EandPposition[0]+" "+EandPposition[1] );

        int[][] array = new int[maxCol][maxRow];

        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                array[i][ii] = 0;
            }
        }

        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                if (isIndestructible(i,ii)){
                    array[fixingY(ii)][i] = 1;
                }
                else if (isDestructible(i,ii)){
                    array[fixingY(ii)][i] = 3;
                }
            }
        }
        array[EandPposition[0]][EandPposition[1]] = 4;
        array[EandPposition[2]][EandPposition[3]] = 5;

        for (int i=0;i<maxCol;i++){
            for(int ii=0;ii<maxRow;ii++){
                System.out.print(array[i][ii]+" ");
            }
            System.out.println("\n");
        }
        System.out.println("End of matrix"+"\n");
    }
}
