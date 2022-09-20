package nl.openweb.confetti.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import lombok.Data;
import nl.openweb.confetti.GridManager;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerActor extends Actor {
    public static final int AMOUNT_OF_MOVES = 5;

    private final String id;
    private final String name;
    private final Texture spriteTexture;
    private int row;
    private int column;
    private boolean alive;
    private List<Move> moves;

    public PlayerActor(String id, String name, Texture spriteTexture, int row, int column) {
        this.id = id;
        this.name = name;
        this.spriteTexture = spriteTexture;
        this.row = row;
        this.column = column;
        this.moves = new ArrayList<>();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Point2D cellCenterCoordinates = GridManager.getInstance().getCellCenterCoordinates(row, column);
        batch.draw(spriteTexture, (float)cellCenterCoordinates.getX(), (float)cellCenterCoordinates.getY());
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
}
