package nl.openweb.confetti.model;

public class GridCell {
    public static final int GRID_DIMENSION = 5;
    public static final int GRID_CELL_SIZE = 100;

    int column;
    int row;
    float startX;
    float startY;

    public GridCell(int column, int row, float startX, float startY) {
        this.column = column;
        this.row = row;
        this.startX = startX;
        this.startY = startY;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }
}
