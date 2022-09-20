package nl.openweb.confetti;

import lombok.Data;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Move;
import nl.openweb.confetti.model.Player;
import nl.openweb.confetti.model.PlayerActor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;

@Data
public class GridManager {
    private final List<GridCell> gridCells;

    private float gridStartX;
    private float gridStartY;
    private List<PlayerActor> players;
    private String activePlayerId;

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
        if(nextPlayerIndex < players.size()) {
            PlayerActor player = players.get(nextPlayerIndex);
            activePlayerId = player.getId();
            return player;
        } else {
            if (resetToStart) {
                return resetActivePlayer();
            } else {
                return null;
            }
        }
    }

    public PlayerActor getNextActivePlayer() {
        return getNextActivePlayer(false);
    }

    public Point2D getCellCenterCoordinates(int cellRow, int cellColumn) {
        if (cellRow > (GRID_DIMENSION - 1)) throw new IllegalArgumentException("Requested cell row does not exist");
        if (cellColumn > (GRID_DIMENSION - 1)) throw new IllegalArgumentException("Requested cell column does not exist");

        final float cellCenterStartX = GridManager.getInstance().getGridStartX() + (cellColumn * GRID_CELL_SIZE) + (GRID_CELL_SIZE / 2f);
        final float cellCenterStartY = GridManager.getInstance().getGridStartY() + (cellColumn * GRID_CELL_SIZE) + (GRID_CELL_SIZE / 2f);

        return new Point2D.Float(cellCenterStartX, cellCenterStartY);
    }

    public void performPlayerMove() {
        PlayerActor activePlayer = getActivePlayer();
       /* Move move = activePlayer.popMove();
        if (move != null) activePlayer.applyMove(move);*/
    }

    public PlayerActor resetActivePlayer() {
        PlayerActor player = players.get(0);
        activePlayerId = player.getId();
        return player;
    }

    public PlayerActor getActivePlayer() {
        return this.players.stream().filter(player -> player.getId().equals(activePlayerId)).findAny().orElseThrow();
    }
}
