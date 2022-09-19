package nl.openweb.confetti;

import lombok.Data;
import nl.openweb.confetti.model.GridCell;
import nl.openweb.confetti.model.Player;

import java.util.ArrayList;
import java.util.List;

import static nl.openweb.confetti.model.GridCell.GRID_CELL_SIZE;
import static nl.openweb.confetti.model.GridCell.GRID_DIMENSION;

@Data
public class GridManager {
    private final List<GridCell> gridCells;

    private float gridStartX;
    private float gridStartY;
    private List<Player> players;
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

    public Player getNextActivePlayer() {
        int nextPlayerIndex = players.indexOf(getActivePlayer()) + 1;
        if(nextPlayerIndex < players.size()) {
            Player player = players.get(nextPlayerIndex);
            activePlayerId = player.getId();
            return player;
        } else {
            return null;
        }
    }

    public Player resetActivePlayer() {
        Player player = players.get(0);
        activePlayerId = player.getId();
        return player;
    }

    public Player getActivePlayer() {
        return this.players.stream().filter(player -> player.getId().equals(activePlayerId)).findAny().orElseThrow();
    }
}
