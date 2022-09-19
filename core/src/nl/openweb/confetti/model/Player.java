package nl.openweb.confetti.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;

public class Player {
    private final String id;
    private final String name;
    private final Texture spriteTexture;
    private int row;
    private int column;
    private boolean dead;
    private List<Move> moves;
    public static final int AMOUNT_OF_MOVES = 5;

    private Music music;

    public Player(String id, String name, Texture spriteTexture, int row, int column) {
        this.id = id;
        this.name = name;
        this.spriteTexture = spriteTexture;
        this.row = row;
        this.column = column;
        this.moves = new ArrayList<>();
        this.music = Gdx.audio.newMusic((Gdx.files.internal("death.mp3")));
    }

    public String getName() {
        return name;
    }

    public Texture getSpriteTexture() {
        return spriteTexture;
    }

    public String getId() {
        return id;
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

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isAlive() {
        return !dead;
    }

    public float getCellDrawXPosition(GridCell gridCell) {
        return gridCell.getStartX() + ((GRID_CELL_SIZE / 2f) - (this.spriteTexture.getWidth() / 2f));
    }

    public float getCellDrawYPosition(GridCell gridCell) {
        return gridCell.getStartY() + ((GRID_CELL_SIZE / 2f) - (this.spriteTexture.getHeight() / 2f));
    }

    public void applyMove(Move move) {
        int newColumnPos = this.column + move.getDeltaX();
        int newRowPos = this.row + move.getDeltaY();

        if (newColumnPos < GRID_DIMENSION && newColumnPos >= 0) {
            this.column = newColumnPos;
        } else {
            dead = true;
        }

        if (newRowPos < GRID_DIMENSION && newRowPos >= 0) {
            this.row = newRowPos;
        } else {
            dead = true;
        }

        if(dead) {
            this.music.play();
        }
    }

    public void addMove(Move move) {
        if (moves.size() < AMOUNT_OF_MOVES) {
            moves.add(move);
        }
    }

    public Move popMove() {
        if (!moves.isEmpty())
            return moves.remove(0);
        return null;
    }
    
    public void revertMove() {
        if(moves.size() > 0) {
            moves.remove(moves.size() -1);
        }
    }

    public List<Move> getMoves() {
        return moves;
    }
}
