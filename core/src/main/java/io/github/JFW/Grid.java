package io.github.JFW;

import com.badlogic.gdx.math.Vector2;

public class Grid {
    private int[][] grid;
    private int rows;
    private int colls;
    private int cellSize;

    public Grid(int rows, int cols, int cellSize) {
        this.rows = rows;
        this.colls = cols;
        this.cellSize = cellSize;
    }

    public Vector2 gridToWorld(int row, int coll){
        float x = 90+(0);
        float y = 45+(0);
        return new Vector2(x,y);
    }
}
