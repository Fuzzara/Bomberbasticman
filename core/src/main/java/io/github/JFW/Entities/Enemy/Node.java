package io.github.JFW.Entities.Enemy;

public class Node {
    Node parent;
    int col;
    int row;
    int gCost;
    int hCost;
    int fCost;
    boolean start;
    boolean goal;
    boolean solid;
    boolean open;
    boolean checked;

    public Node (int col,int row){
        this.col = col;
        this.row = row;
        this.start = false;
        this.goal = false;
        this.solid = false;
        this.open = false;
        this.checked = false;
    }
    public Node getParent(){
        return parent;
    }
    public void setAsStart(){
        start = true;
    }
    public void setAsGoal(){
        goal = true;
    }
    public void setAsSolid(){
        solid = true;
    }
    public void setAsOpen(){
        open = true;
    }
    public void setAsChecked(){
        checked = true;
    }
    public int getCol(){
        return col;
    }
    public int getRow(){
        return row;
    }

}
