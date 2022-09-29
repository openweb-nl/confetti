package nl.openweb.confetti;

import lombok.Data;
import nl.openweb.confetti.dialog.GameNotification;
import nl.openweb.confetti.exception.GridOutOfBoundsException;
import nl.openweb.confetti.model.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;

@Data
public class GridManager {
    public static final int PLAYER_MOVE_DELAY = 500;
    private final List<GridCell> gridCells;

    private float gridStartX;
    private float gridStartY;
    private List<PlayerActor> players;
    private String activePlayerId;

    private boolean gameFinished;

    private static GridManager gridManager;

    public static GridManager getInstance() {
        if (gridManager == null) {
            gridManager = new GridManager();
        }
        return gridManager;
    }

    private GridManager() {
        this.gridCells = new ArrayList<>();
    }

    public void init(float centerX, float centerY) {
        this.gridStartX = centerX - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
        this.gridStartY = centerY - ((GRID_DIMENSION * GRID_CELL_SIZE) / 2f);
    }

    public void addCell(GridCell gridCell) {
        this.getGridCells().add(gridCell);
    }

    public GridCell getPlayerGridCell(Player player) {
        List<GridCell> gridCells = GridManager.getInstance().getGridCells();
        return gridCells.stream().filter(gridCell -> gridCell.getColumn() == player.getColumn() && gridCell.getRow() == player.getRow()).findFirst().get();
    }

    public void setActivePlayer(Player player) {
        this.activePlayerId = player.getId();
    }

    public PlayerActor getNextActivePlayer(boolean resetToStart) {
        int nextPlayerIndex = players.indexOf(getActivePlayer()) + 1;
        long playersAliveCount = players.stream().filter(PlayerActor::isAlive).count();
        long playersNoMovesCount = players.stream().filter(playerActor -> playerActor.getMoves().size() == 0).count();

        if(playersAliveCount > 1) {
            if (playersNoMovesCount == players.size()) {
                GameNotification.getInstance().showNotification("Next round!");
            }
            if (nextPlayerIndex < players.size()) {
                PlayerActor player = players.get(nextPlayerIndex);
                activePlayerId = player.getId();

                if (!player.isAlive()) {
                    return getNextActivePlayer(true);
                } else {
                    return player;
                }
            } else {
                if (resetToStart) {
                    return resetActivePlayer();
                } else {
                    return null;
                }
            }
        } else {
            Optional<PlayerActor> winningPlayerOpt = players.stream().filter(PlayerActor::isAlive).findFirst();
            if (winningPlayerOpt.isPresent()) {
                gameFinished = true;
                activePlayerId = winningPlayerOpt.get().getId();
                System.out.println("Only one player left, player has WON the game: " + winningPlayerOpt.get().getName());
                GameNotification.getInstance().showNotification("Winner " + winningPlayerOpt.get().getName(), true);
            }
            return null;
        }
    }

    public PlayerActor getNextActivePlayer() {
        return getNextActivePlayer(false);
    }

    public Point2D getCellCenterCoordinates(GridCoordinates gridCoordinates) {
        if (gridCoordinates.getRow() >= GRID_DIMENSION || gridCoordinates.getRow() < 0) throw new GridOutOfBoundsException("Requested cell row does not exist: " + gridCoordinates.getRow());
        if (gridCoordinates.getColumn() >= GRID_DIMENSION || gridCoordinates.getColumn() < 0) throw new GridOutOfBoundsException("Requested cell column does not exist: " + gridCoordinates.getColumn());

        final float cellCenterStartX = GridManager.getInstance().getGridStartX() + (gridCoordinates.getColumn() * GRID_CELL_SIZE) + (GRID_CELL_SIZE / 2f);
        final float cellCenterStartY = GridManager.getInstance().getGridStartY() + (gridCoordinates.getRow() * GRID_CELL_SIZE) + (GRID_CELL_SIZE / 2f);

        return new Point2D.Float(cellCenterStartX, cellCenterStartY);
    }

    public void performPlayerMove() {
        PlayerActor activePlayer = getActivePlayer();
        Move move = activePlayer.popMove();
        if (move != null) activePlayer.applyMove(move);
    }

    public PlayerActor resetActivePlayer() {
        PlayerActor player = players.stream().filter(PlayerActor::isAlive).findFirst().get();
        activePlayerId = player.getId();
        return player;
    }

    public PlayerActor getActivePlayer() {
        return this.players.stream().filter(player -> player.getId().equals(activePlayerId)).findAny().orElseThrow();
    }
}
