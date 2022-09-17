package nl.openweb.confetti.model;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;
import java.awt.geom.Point2D;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;

public class Player {
    private final String name;
    private final Texture spriteTexture;
    private int row;
    private int column;

    public Player(String name, Texture spriteTexture, int row, int column) {
        this.name = name;
        this.spriteTexture = spriteTexture;
        this.row = row;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public Texture getSpriteTexture() {
        return spriteTexture;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public float getCellDrawXPosition(GridCell gridCell) {
        return gridCell.getStartX() + ((GRID_CELL_SIZE / 2f) - (this.spriteTexture.getWidth() / 2f));
    }

    public float getCellDrawYPosition(GridCell gridCell) {
        return gridCell.getStartY() + ((GRID_CELL_SIZE / 2f) - (this.spriteTexture.getHeight() / 2f));
    }
}
