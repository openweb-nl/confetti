package nl.openweb.confetti.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.confetti.GridManager;
import nl.openweb.confetti.dialog.GameNotification;
import nl.openweb.confetti.exception.GridOutOfBoundsException;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.openweb.confetti.GridManager.PLAYER_MOVE_DELAY;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerActor extends Actor {
    public static final int AMOUNT_OF_MOVES = 5;

    private final String id;
    private final String name;
    private final Texture spriteTexture;
    private GridCoordinates gridCoordinates;
    private GridCoordinates targetGridCoordinates;
    private float x;
    private float y;
    private boolean alive;
    private List<Move> moves;
    private long elapsedMoveTime;

    public PlayerActor(String id, String name, Texture spriteTexture, int row, int column) {
        this.id = id;
        this.name = name;
        this.spriteTexture = spriteTexture;
        this.gridCoordinates = new GridCoordinates(row, column);
        this.targetGridCoordinates = new GridCoordinates(row, column);
        this.x = getCellXOffset(gridCoordinates);
        this.y = getCellYOffset(gridCoordinates);
        this.moves = new ArrayList<>();
        this.alive = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float targetStartX = getCellXOffset(targetGridCoordinates);
        float targetStartY = getCellYOffset(targetGridCoordinates);

        if (isAlive()) {
            if (elapsedMoveTime < PLAYER_MOVE_DELAY) {
                if (gridCoordinates.getRow() != targetGridCoordinates.getRow() || gridCoordinates.getColumn() != targetGridCoordinates.getColumn()) {
                    if (elapsedMoveTime == 0) {
                        //System.out.println("Start moving: " + getName());
                    }

                    float originalStartX = getCellXOffset(this.gridCoordinates);
                    float originalStartY = getCellYOffset(this.gridCoordinates);
                    float deltaX = targetStartX - originalStartX;
                    float deltaY = targetStartY - originalStartY;

                    float moveXPerFrame = ((deltaTime * 1000) / PLAYER_MOVE_DELAY) * deltaX;
                    float moveYPerFrame = ((deltaTime * 1000) / PLAYER_MOVE_DELAY) * deltaY;

                    x += moveXPerFrame;
                    y += moveYPerFrame;

                    elapsedMoveTime += (deltaTime * 1000);
                }
            } else {
                elapsedMoveTime = 0;
                gridCoordinates.setRow(targetGridCoordinates.getRow());
                gridCoordinates.setColumn(targetGridCoordinates.getColumn());

                x = getCellXOffset(gridCoordinates);
                y = getCellYOffset(gridCoordinates);

                //System.out.println("Stop moving: " + getName());
                PlayerActor nextActivePlayer = GridManager.getInstance().getNextActivePlayer(true);
                if (nextActivePlayer != null) {
                    System.out.println("Next active player: " + nextActivePlayer.getName());
                    GridManager.getInstance().performPlayerMove();
                } else {
                    System.out.println("Unable to determine next active player!");
                }
            }

            batch.draw(spriteTexture, x, y);
        }
    }

    private float getCellXOffset(GridCoordinates coordinates) {
        Point2D cellCenterCoordinates = GridManager.getInstance().getCellCenterCoordinates(coordinates);
        return (float) (cellCenterCoordinates.getX() - (spriteTexture.getWidth() / 2f));
    }

    private float getCellYOffset(GridCoordinates coordinates) {
        Point2D cellCenterCoordinates = GridManager.getInstance().getCellCenterCoordinates(coordinates);
        return (float) (cellCenterCoordinates.getY() - (spriteTexture.getHeight() / 2f));
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
        if (moves.size() > 0) {
            moves.remove(moves.size() - 1);
        }
    }

    public void applyMove(Move move) {
        GridCoordinates newGridCoordinates = targetGridCoordinates.applyMove(move);
        try{
            GridManager.getInstance().getCellCenterCoordinates(newGridCoordinates);
        } catch (GridOutOfBoundsException e) {
            setAlive(false);
            targetGridCoordinates.setCoordinates(gridCoordinates);

            Runnable executeFunction = () -> {
                GridManager.getInstance().getNextActivePlayer(true);
                GridManager.getInstance().performPlayerMove();
            };

            GameNotification.getInstance().showNotification(getName() + " DIED!", false, executeFunction);
        }

        Optional hitPlayer = hitPlayer();
    }

    private Optional hitPlayer() {
        PlayerActor deadPlayer = null;
        for (PlayerActor player : GridManager.getInstance().getPlayers()) {
            if (player.getGridCoordinates().getColumn() == getTargetGridCoordinates().getColumn() &&
                    player.getGridCoordinates().getRow() == getTargetGridCoordinates().getRow() &&
                    !player.getId().equals(getId())) {
                player.setAlive(false);
                //gameNotification.setText(player.getName() + " killed by " + activePlayer.getName());
                System.out.println(player.getName() + " killed by " + getName());
                deadPlayer = player;
            }
        }
        return Optional.ofNullable(deadPlayer);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        if (alive) {
            System.out.println(getName() + " is alive!");
        } else {
            this.moves.clear();
            System.out.println(getName() + " is DEAD!");
        }
    }
}
